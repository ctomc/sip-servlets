/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.web.tomcat.service.session.distributedcache.spi;

import java.util.HashSet;
import java.util.Set;

import org.mobicents.servlet.sip.core.session.SipApplicationSessionKey;
import org.mobicents.servlet.sip.core.session.SipSessionKey;

/**
 * @author jean.deruelle@gmail.com
 *
 */
public class DistributableSipApplicationSessionMetadata extends
		DistributableSessionMetadata {
	private SipApplicationSessionKey sipApplicationSessionKey;
	private Set<SipSessionKey> sipSessionKeys;
	private Set<String> httpSessionIds;
	
	public DistributableSipApplicationSessionMetadata() {
		sipSessionKeys = new HashSet<SipSessionKey>();
		httpSessionIds = new HashSet<String>();
	}

	/**
	 * @param sipApplicationSessionKey the sipApplicationSessionKey to set
	 */
	public void setSipApplicationSessionKey(SipApplicationSessionKey sipApplicationSessionKey) {
		this.sipApplicationSessionKey = sipApplicationSessionKey;
	}

	/**
	 * @return the sipApplicationSessionKey
	 */
	public SipApplicationSessionKey getSipApplicationSessionKey() {
		return sipApplicationSessionKey;
	}

	public void addSipSessionKey(SipSessionKey sipSessionKey) {
		sipSessionKeys.add(sipSessionKey);
	}
	
	public void addHttpSessionId(String httpSessionId) {
		httpSessionIds.add(httpSessionId);
	}
	
	public Set<SipSessionKey> getSipSessionKeys() {
		return sipSessionKeys;
	}
	
	public Set<String> getHttpSessionIds() {
		return httpSessionIds;
	}
}
