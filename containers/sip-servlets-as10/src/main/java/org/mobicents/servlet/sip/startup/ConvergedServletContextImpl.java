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
package org.mobicents.servlet.sip.startup;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.spec.ServletContextImpl;
import io.undertow.util.AttachmentKey;
import org.mobicents.io.undertow.server.session.ConvergedInMemorySessionManager;
import org.mobicents.io.undertow.servlet.core.ServletContextWrapper;
import org.mobicents.io.undertow.servlet.spec.ConvergedHttpSessionFacade;
import org.mobicents.servlet.sip.core.MobicentsSipServlet;
import org.mobicents.servlet.sip.core.session.SipRequestDispatcher;
import org.mobicents.servlet.sip.undertow.SipContextImpl;
import org.mobicents.servlet.sip.undertow.SipServletImpl;

/**
 * Facade object which masks the internal <code>ApplicationContext</code> object from the web application.
 *
 * @author Remy Maucherat
 * @author Jean-Francois Arcand
 * @version $Id: ApplicationContextFacade.java 1002556 2010-09-29 10:07:10Z markt $
 *
 *          This class is based on org.mobicents.servlet.sip.startup.ConvergedApplicationContextFacade class from
 *          sip-servlet-as7 project, re-implemented for jboss as10 (wildfly) by:
 * @author kakonyi.istvan@alerant.hu
 */
public final class ConvergedServletContextImpl extends ServletContextWrapper implements ServletContext {


    private final AttachmentKey<ConvergedHttpSessionFacade> sessionAttachmentKey = AttachmentKey.create(ConvergedHttpSessionFacade.class);

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new instance of this class, associated with the specified Context instance.
     *
     * @param context The associated Context instance
     */
    public ConvergedServletContextImpl(ServletContextImpl context) {
        super(context);
        this.context = context;
    }

    public void addSipContext(SipContextImpl sipContext){
        this.sipContext = sipContext;
    }


    // ----------------------------------------------------- Instance Variables

    /**
     * Wrapped application context.
     */
    private ServletContextImpl context = null;
    private SipContextImpl sipContext = null;

    // ------------------------------------------------- ServletContextImpl Methods
    public void initDone() {
        context.initDone();
    }

    public HttpSession getSession(final String sessionId) {
        final SessionManager sessionManager = context.getDeployment().getSessionManager();
        Session session = sessionManager.getSession(sessionId);
        if (session != null) {
            return SecurityActions.forSession(session, this, false, sessionManager);
        }
        return null;
    }

    public HttpSession getSession(final ServletContextImpl originalServletContext, final HttpServerExchange exchange, boolean create) {
        SessionConfig c = originalServletContext.getSessionConfig();
        ConvergedHttpSessionFacade httpSession = exchange.getAttachment(sessionAttachmentKey);
        if (httpSession != null && httpSession.isValidIntern()) {
            exchange.removeAttachment(sessionAttachmentKey);
            httpSession = null;
        }
        if (httpSession == null) {
            final SessionManager sessionManager = context.getDeployment().getSessionManager();
            Session session = sessionManager.getSession(exchange, c);
            if (session != null) {
                httpSession = (ConvergedHttpSessionFacade) SecurityActions.forSession(session, this, false, sessionManager);
                exchange.putAttachment(sessionAttachmentKey, httpSession);
            } else if (create) {

                String existing = c.findSessionId(exchange);
                if (originalServletContext != this.context) {
                    //this is a cross context request
                    //we need to make sure there is a top level session
                    originalServletContext.getSession(originalServletContext, exchange, true);
                } else if (existing != null) {
                    c.clearSession(exchange, existing);
                }

                final Session newSession = sessionManager.createSession(exchange, c);
                httpSession = (ConvergedHttpSessionFacade) SecurityActions.forSession(newSession, this, true, sessionManager);
                //call access after creation to set LastAccessTime at sipAppSession.
                httpSession.access();
                //add delegate to InMemorySession to call sipAppSession.access(), when necessary:
                ((ConvergedInMemorySessionManager)sessionManager).addConvergedSessionDeletegateToSession(newSession.getId(), httpSession.getConvergedSessionDelegate());

                exchange.putAttachment(sessionAttachmentKey, httpSession);
            }
        }
        return httpSession;
    }


    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        // Validate the name argument
        if (name == null)
            return (null);

        // Create and return a corresponding request dispatcher
        MobicentsSipServlet servlet = sipContext.findSipServletByName(name);

        if (servlet == null)
            return context.getNamedDispatcher(name);
        // return (null);

        if (servlet instanceof SipServletImpl) {
            return new SipRequestDispatcher((SipServletImpl) servlet);
        } else {
            return context.getNamedDispatcher(name);
        }
    }
    public Deployment getDeployment(){
        return context.getDeployment();
    }


    public ServletContextImpl getDelegatedContext() {
        return context;
    }

    public void destroy() {
        context.destroy();
    }
}
