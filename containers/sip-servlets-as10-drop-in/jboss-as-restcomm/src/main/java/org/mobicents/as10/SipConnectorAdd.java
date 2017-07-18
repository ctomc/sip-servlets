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
package org.mobicents.as10;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.mobicents.as10.Constants.HOSTNAMES;
import static org.mobicents.as10.Constants.LOAD_BALANCER_ADDRESS;
import static org.mobicents.as10.Constants.LOAD_BALANCER_RMI_PORT;
import static org.mobicents.as10.Constants.LOAD_BALANCER_SIP_PORT;
import static org.mobicents.as10.Constants.STATIC_SERVER_ADDRESS;
import static org.mobicents.as10.Constants.STATIC_SERVER_PORT;
import static org.mobicents.as10.Constants.STUN_SERVER_ADDRESS;
import static org.mobicents.as10.Constants.STUN_SERVER_PORT;
import static org.mobicents.as10.SipConnectorDefinition.CONNECTOR_ATTRIBUTES;

import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.registry.Resource;
import org.jboss.as.network.SocketBinding;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController.Mode;

/**
 * {@code OperationHandler} responsible for adding a sip connector.
 *
 * @author Emanuel Muckenhuber
 *
 *         This class is based on the contents of org.mobicents.as7 package from jboss-as7-mobicents project, re-implemented for
 *         jboss as10 (wildfly) by:
 * @author kakonyi.istvan@alerant.hu
 */
class SipConnectorAdd extends AbstractAddStepHandler {


    private SipConnectorAdd() {
        super(CONNECTOR_ATTRIBUTES);
    }

    static final SipConnectorAdd INSTANCE = new SipConnectorAdd();


    @Override
    protected void performRuntime(OperationContext context, ModelNode operation, ModelNode model)
            throws OperationFailedException {
        final String name = context.getCurrentAddressValue();

        ModelNode fullModel = Resource.Tools.readModel(context.readResource(PathAddress.EMPTY_ADDRESS));
        final String bindingRef = SipConnectorDefinition.SOCKET_BINDING.resolveModelAttribute(context, fullModel).asString();

        final boolean enabled = SipConnectorDefinition.ENABLED.resolveModelAttribute(context, fullModel).asBoolean();
        final String protocol = SipConnectorDefinition.PROTOCOL.resolveModelAttribute(context, fullModel).asString();
        final String scheme = SipConnectorDefinition.SCHEME.resolveModelAttribute(context, fullModel).asString();

        final boolean useLoadBalancer = SipConnectorDefinition.USE_LOAD_BALANCER.resolveModelAttribute(context, fullModel).asBoolean();
        final String loadBalancerAddress = operation.hasDefined(LOAD_BALANCER_ADDRESS) ? SipConnectorDefinition.LOAD_BALANCER_ADDRESS.resolveModelAttribute(context, fullModel).asString() : null;
        final int loadBalancerRmiPort = operation.hasDefined(LOAD_BALANCER_RMI_PORT) ? SipConnectorDefinition.LOAD_BALANCER_RMI_PORT.resolveModelAttribute(context, fullModel).asInt() : -1;
        final int loadBalancerSipPort = operation.hasDefined(LOAD_BALANCER_SIP_PORT) ? SipConnectorDefinition.LOAD_BALANCER_SIP_PORT.resolveModelAttribute(context, fullModel).asInt() : -1;
        
        final boolean useStaticAddress = SipConnectorDefinition.USE_STATIC_ADDRESS.resolveModelAttribute(context, fullModel).asBoolean();        
        final String staticServerAddress = operation.hasDefined(STATIC_SERVER_ADDRESS) ? SipConnectorDefinition.STATIC_SERVER_ADDRESS.resolveModelAttribute(context, fullModel).asString() : null;
        final int staticServerPort = operation.hasDefined(STATIC_SERVER_PORT) ? SipConnectorDefinition.STATIC_SERVER_PORT.resolveModelAttribute(context, fullModel).asInt() : -1;

        final boolean useStun = SipConnectorDefinition.USE_STUN.resolveModelAttribute(context, fullModel).asBoolean();        
        final String stunServerAddress = operation.hasDefined(STUN_SERVER_ADDRESS) ? SipConnectorDefinition.STUN_SERVER_ADDRESS.resolveModelAttribute(context, fullModel).asString() : null;
        final int stunServerPort = operation.hasDefined(STUN_SERVER_PORT) ? SipConnectorDefinition.STUN_SERVER_PORT.resolveModelAttribute(context, fullModel).asInt() : -1;
        
        final String hostNames = operation.hasDefined(HOSTNAMES) ? SipConnectorDefinition.HOSTNAMES.resolveModelAttribute(context, fullModel).asString() : null;

        final SipConnectorService service = new SipConnectorService(protocol, scheme, useLoadBalancer, loadBalancerAddress, loadBalancerRmiPort, loadBalancerSipPort, useStaticAddress, staticServerAddress, staticServerPort, useStun, stunServerAddress, stunServerPort, hostNames);

        final ServiceBuilder<SipConnectorListener> serviceBuilder = context.getServiceTarget()
                .addService(SipSubsystemServices.JBOSS_SIP_CONNECTOR.append(name), service)
                .addDependency(SipSubsystemServices.JBOSS_SIP, SipServer.class, service.getServer())
                .addDependency(SocketBinding.JBOSS_BINDING_NAME.append(bindingRef), SocketBinding.class, service.getBinding());
        //kakonyii: set initialMode to PASSIVE to prevent the connector to receive messages. Connector will be enabled later by UndertowSipConnectorActivate service after all sip deployments finished:
        serviceBuilder.setInitialMode(enabled ? Mode.PASSIVE : Mode.NEVER);
        serviceBuilder.install();

    }
}
