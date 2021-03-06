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
package org.mobicents.metadata.sip.jboss;

import java.util.List;

import org.jboss.metadata.javaee.spec.MessageDestinationsMetaData;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.javaee.spec.SecurityRolesMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.metadata.web.spec.ListenerMetaData;
import org.jboss.metadata.web.spec.SessionConfigMetaData;
import org.mobicents.metadata.sip.spec.ProxyConfigMetaData;
import org.mobicents.metadata.sip.spec.SipApplicationKeyMethodInfo;
import org.mobicents.metadata.sip.spec.SipLoginConfigMetaData;
import org.mobicents.metadata.sip.spec.SipSecurityConstraintMetaData;
import org.mobicents.metadata.sip.spec.SipServletSelectionMetaData;
import org.mobicents.servlet.sip.annotation.ConcurrencyControlMode;

/**
 * Extend the JBossWebMetaData from JBoss 5 to provide support for converged sip/http applications
 *
 * @author jean.deruelle@gmail.com
 *
 *         This class is based on the contents of org.mobicents.metadata.sip.jboss package from jboss-as7-mobicents project,
 *         re-implemented for jboss as10 (wildfly) by:
 * @author kakonyi.istvan@alerant.hu
 *
 */
public class JBossConvergedSipMetaData extends JBossWebMetaData {
    private static final long serialVersionUID = 1;

    private String applicationName;
    private SipServletSelectionMetaData sipServletSelection;
    private ProxyConfigMetaData proxyConfig;
    private List<SipSecurityConstraintMetaData> sipSecurityConstraints;
    private SessionConfigMetaData sipSessionConfig;
    private SipLoginConfigMetaData sipLoginConfig;
    private List<? extends ParamValueMetaData> sipContextParams;
    private List<ListenerMetaData> sipListeners;
    private JBossSipServletsMetaData sipServlets;
    private MessageDestinationsMetaData messageDestinations;
    private SecurityRolesMetaData securityRoles;
    // AS7 isolated class loading does not allow to fetch Method
    // so processors fill SipApplicationKeyMethodInfo and Method is fetched afterwards
    // private Method sipApplicationKeyMethod;
    private SipApplicationKeyMethodInfo sipApplicationKeyMethodInfo;

    private ConcurrencyControlMode concurrencyControlMode;

    /**
     * @param applicationName the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param sipServletSelection the sipServletSelection to set
     */
    public void setSipServletSelection(SipServletSelectionMetaData sipServletSelection) {
        this.sipServletSelection = sipServletSelection;
    }

    /**
     * @return the sipServletSelection
     */
    public SipServletSelectionMetaData getSipServletSelection() {
        return sipServletSelection;
    }

    /**
     * @param proxyConfig the proxyConfig to set
     */
    public void setProxyConfig(ProxyConfigMetaData proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    /**
     * @return the proxyConfig
     */
    public ProxyConfigMetaData getProxyConfig() {
        return proxyConfig;
    }

    /**
     * @param sipSecurityConstraints the sipSecurityConstraints to set
     */
    public void setSipSecurityConstraints(List<SipSecurityConstraintMetaData> sipSecurityConstraints) {
        this.sipSecurityConstraints = sipSecurityConstraints;
    }

    /**
     * @return the sipSecurityConstraints
     */
    public List<SipSecurityConstraintMetaData> getSipSecurityConstraints() {
        return sipSecurityConstraints;
    }

    /**
     * @param sipLoginConfig the sipLoginConfig to set
     */
    public void setSipLoginConfig(SipLoginConfigMetaData sipLoginConfig) {
        this.sipLoginConfig = sipLoginConfig;
    }

    /**
     * @return the sipLoginConfig
     */
    public SipLoginConfigMetaData getSipLoginConfig() {
        return sipLoginConfig;
    }

    /**
     * @param sipContextParams the sipContextParams to set
     */
    public void setSipContextParams(List<? extends ParamValueMetaData> sipContextParams) {
        this.sipContextParams = sipContextParams;
    }

    /**
     * @return the sipContextParams
     */
    public List<? extends ParamValueMetaData> getSipContextParams() {
        return sipContextParams;
    }

    /**
     * @param sipListeners the sipListeners to set
     */
    public void setSipListeners(List<ListenerMetaData> sipListeners) {
        this.sipListeners = sipListeners;
    }

    /**
     * @return the sipListeners
     */
    public List<ListenerMetaData> getSipListeners() {
        return sipListeners;
    }

    /**
     * @param sipServlets the sipServlets to set
     */
    public void setSipServlets(JBossSipServletsMetaData sipServlets) {
        this.sipServlets = sipServlets;
    }

    /**
     * @return the sipServlets
     */
    public JBossSipServletsMetaData getSipServlets() {
        return sipServlets;
    }

    /**
     * @param sipSessionConfig the sipSessionConfig to set
     */
    public void setSipSessionConfig(SessionConfigMetaData sipSessionConfig) {
        this.sipSessionConfig = sipSessionConfig;
    }

    /**
     * @return the sipSessionConfig
     */
    public SessionConfigMetaData getSipSessionConfig() {
        return sipSessionConfig;
    }

    // /**
    // * @param sipApplicationKeyMethod the sipApplicationKeyMethod to set
    // */
    // public void setSipApplicationKeyMethod(Method sipApplicationKeyMethod) {
    // this.sipApplicationKeyMethod = sipApplicationKeyMethod;
    // }
    //
    // /**
    // * @return the sipApplicationKeyMethod
    // */
    // public Method getSipApplicationKeyMethod() {
    // return sipApplicationKeyMethod;
    // }

    /**
     * @param sipApplicationKeyMethodInfo the sipApplicationKeyMethodInfo to set
     */
    public void setSipApplicationKeyMethodInfo(SipApplicationKeyMethodInfo sipApplicationKeyMethodInfo) {
        this.sipApplicationKeyMethodInfo = sipApplicationKeyMethodInfo;
    }

    /**
     * @return the sipApplicationKeyMethodInfo
     */
    public SipApplicationKeyMethodInfo getSipApplicationKeyMethodInfo() {
        return this.sipApplicationKeyMethodInfo;
    }

    /**
     * @param messageDestinations the messageDestinations to set
     */
    public void setSipMessageDestinations(MessageDestinationsMetaData messageDestinations) {
        this.messageDestinations = messageDestinations;
    }

    /**
     * @return the messagesDestinations
     */
    public MessageDestinationsMetaData getSipMessageDestinations() {
        return messageDestinations;
    }

    /**
     * @param securityRoles the securityRoles to set
     */
    public void setSipSecurityRoles(SecurityRolesMetaData securityRoles) {
        this.securityRoles = securityRoles;
    }

    /**
     * @return the securityRoles
     */
    public SecurityRolesMetaData getSipSecurityRoles() {
        return securityRoles;
    }

    /**
     * @param concurrencyControlMode the concurrencyControlMode to set
     */
    public void setConcurrencyControlMode(ConcurrencyControlMode ConcurrencyControlMode) {
        this.concurrencyControlMode = ConcurrencyControlMode;
    }

    /**
     * @return the concurrencyControlMode
     */
    public ConcurrencyControlMode getConcurrencyControlMode() {
        return concurrencyControlMode;
    }
}
