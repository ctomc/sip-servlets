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

import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.handlers.ServletRequestContext;

/**
 * This class is based on protected class io.undertow.servlet.core.ServletRequestContextThreadSetupAction.
 *
 * @author kakonyi.istvan@alerant.hu
 */
class ServletRequestContextThreadSetupAction implements ThreadSetupHandler {

    static final ServletRequestContextThreadSetupAction INSTANCE = new ServletRequestContextThreadSetupAction();

    private ServletRequestContextThreadSetupAction() {

    }

    @Override
    public <T, C> Action<T, C> create(final Action<T, C> action) {
        return new Action<T, C>() {
            @Override
            public T call(HttpServerExchange exchange, C context) throws Exception {
                if (exchange == null) {
                    return action.call(null, context);
                } else {
                    ServletRequestContext servletRequestContext = exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
                    final ServletRequestContext old = ServletRequestContext.current();
                    SecurityActions.setCurrentRequestContext(servletRequestContext);
                    try {
                        return action.call(exchange, context);
                    } finally {
                        ServletRequestContext.setCurrentRequestContext(old);
                    }
                }
            }
        };
    }
}
