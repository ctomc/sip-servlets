/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2015, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.mobicents.io.undertow.servlet.core;

import java.security.AccessController;
import java.util.HashSet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.core.ApplicationListeners;
import io.undertow.servlet.core.SessionListenerBridge;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpSessionImpl;

/**
 * This class extends io.undertow.servlet.core.SessionListenerBridge to create ConvergedHttpSessions instead of plain HttpSessions.
 *
 * @author kakonyi.istvan@alerant.hu
 */
public class ConvergedSessionListenerBridge extends SessionListenerBridge{
    private final ApplicationListeners applicationListeners;
    private final ServletContext servletContext;
    private final SessionManager manager;
    private final ThreadSetupHandler.Action<Void, Session> destroyedAction;

    public ConvergedSessionListenerBridge(final Deployment deployment, final ApplicationListeners applicationListeners,
                                          final ServletContext servletContext, final SessionManager manager) {
        super(deployment, applicationListeners, servletContext);
        this.applicationListeners = applicationListeners;
        this.servletContext = servletContext;
        this.manager = manager;
        this.destroyedAction = deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, Session>() {
            @Override
            public Void call(HttpServerExchange exchange, Session session) throws ServletException {
                doDestroy(session);
                return null;
            }
        });
    }

    @Override
    public void sessionCreated(final Session session, final HttpServerExchange exchange) {
        final HttpSession httpSession = SecurityActions.forSession(session, servletContext, true, manager);
        applicationListeners.sessionCreated(httpSession);
    }

    @Override
    public void sessionDestroyed(final Session session, final HttpServerExchange exchange, final SessionDestroyedReason reason) {
        if (reason == SessionDestroyedReason.TIMEOUT) {
            try {
                //we need to perform thread setup actions
                destroyedAction.call(exchange, session);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            doDestroy(session);
        }

        ServletRequestContext current = SecurityActions.currentServletRequestContext();
        Session underlying = null;
        if (current != null && current.getSession() != null) {
            if (System.getSecurityManager() == null) {
                underlying = current.getSession().getSession();
            } else {
                underlying = AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(current.getSession()));
            }
        }

        if (current != null && underlying == session) {
            current.setSession(null);
        }
    }

    private void doDestroy(Session session) {
        final HttpSession httpSession = SecurityActions.forSession(session, servletContext, false, manager);
        applicationListeners.sessionDestroyed(httpSession);
        //we make a defensive copy here, as there is no guarantee that the underlying session map
        //is a concurrent map, and as a result a concurrent modification exception may be thrown
        HashSet<String> names = new HashSet<>(session.getAttributeNames());
        for (String attribute : names) {
            session.removeAttribute(attribute);
        }
    }

    @Override
    public void attributeAdded(final Session session, final String name, final Object value) {
        if(name.startsWith(IO_UNDERTOW)) {
            return;
        }
        final HttpSession httpSession = SecurityActions.forSession(session, servletContext, false, manager);
        applicationListeners.httpSessionAttributeAdded(httpSession, name, value);
        if (value instanceof HttpSessionBindingListener) {
            ((HttpSessionBindingListener) value).valueBound(new HttpSessionBindingEvent(httpSession, name, value));
        }
    }

    @Override
    public void attributeUpdated(final Session session, final String name, final Object value, final Object old) {
        if(name.startsWith(IO_UNDERTOW)) {
            return;
        }
        final HttpSession httpSession = SecurityActions.forSession(session, servletContext, false, manager);
        if (old != value) {
            if (old instanceof HttpSessionBindingListener) {
                ((HttpSessionBindingListener) old).valueUnbound(new HttpSessionBindingEvent(httpSession, name, old));
            }
            applicationListeners.httpSessionAttributeReplaced(httpSession, name, old);
        }
        if (value instanceof HttpSessionBindingListener) {
            ((HttpSessionBindingListener) value).valueBound(new HttpSessionBindingEvent(httpSession, name, value));
        }
    }

    @Override
    public void attributeRemoved(final Session session, final String name, final Object old) {
        if(name.startsWith(IO_UNDERTOW)) {
            return;
        }
        final HttpSession httpSession = SecurityActions.forSession(session, servletContext, false, manager);
        if (old != null) {
            applicationListeners.httpSessionAttributeRemoved(httpSession, name, old);
            if (old instanceof HttpSessionBindingListener) {
                ((HttpSessionBindingListener) old).valueUnbound(new HttpSessionBindingEvent(httpSession, name, old));
            }
        }
    }
}
