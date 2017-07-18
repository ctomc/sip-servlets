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

import org.jboss.as.controller.ReloadRequiredWriteAttributeHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.operations.validation.InetAddressValidator;
import org.jboss.as.controller.operations.validation.IntRangeValidator;
import org.jboss.as.controller.operations.validation.StringLengthValidator;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.as.controller.registry.ManagementResourceRegistration;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

/**
 * @author Tomaz Cerar
 * @created 22.2.12 15:03
 * @author josemrecio@gmail.com
 *
 *         This class is based on the contents of org.mobicents.as7 package from jboss-as7-mobicents project, re-implemented for
 *         jboss as10 (wildfly) by:
 * @author kakonyi.istvan@alerant.hu
 */
public class SipConnectorDefinition extends SimpleResourceDefinition {

    protected static final SimpleAttributeDefinition PROTOCOL =
            new SimpleAttributeDefinitionBuilder(Constants.PROTOCOL, ModelType.STRING)
                    .setXmlName(Constants.PROTOCOL)
                    .setAllowNull(false)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setValidator(new StringLengthValidator(1))
                    .build();

    protected static final SimpleAttributeDefinition SOCKET_BINDING =
            new SimpleAttributeDefinitionBuilder(Constants.SOCKET_BINDING, ModelType.STRING)
                    .setXmlName(Constants.SOCKET_BINDING)
                    .setAllowNull(false)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setValidator(new StringLengthValidator(1))
                    .build();

    protected static final SimpleAttributeDefinition SCHEME =
            new SimpleAttributeDefinitionBuilder(Constants.SCHEME, ModelType.STRING)
                    .setXmlName(Constants.SCHEME)
                    .setAllowNull(false)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setValidator(new StringLengthValidator(1))
                    .setDefaultValue(new ModelNode("http"))
                    .build();

    protected static final SimpleAttributeDefinition ENABLED =
            new SimpleAttributeDefinitionBuilder(Constants.ENABLED, ModelType.BOOLEAN)
                    .setXmlName(Constants.ENABLED)
                    .setAllowNull(true)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setDefaultValue(new ModelNode(true))
                    .build();

    protected static final SimpleAttributeDefinition USE_LOAD_BALANCER =
            new SimpleAttributeDefinitionBuilder(Constants.USE_LOAD_BALANCER, ModelType.BOOLEAN)
                    .setXmlName(Constants.USE_LOAD_BALANCER)
                    .setAllowNull(true)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setDefaultValue(new ModelNode(false))
                    .build();
    
    protected static final SimpleAttributeDefinition LOAD_BALANCER_ADDRESS =
            new SimpleAttributeDefinitionBuilder(Constants.LOAD_BALANCER_ADDRESS, ModelType.STRING)
                    .setXmlName(Constants.LOAD_BALANCER_ADDRESS)
                    .setAllowNull(true)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setValidator(new InetAddressValidator(true, false))
                    .setRequires(Constants.USE_LOAD_BALANCER)
                    .build();

    protected static final SimpleAttributeDefinition LOAD_BALANCER_RMI_PORT =
            new SimpleAttributeDefinitionBuilder(Constants.LOAD_BALANCER_RMI_PORT, ModelType.INT)
                    .setXmlName(Constants.LOAD_BALANCER_RMI_PORT)
                    .setAllowNull(true)
                    .setValidator(new IntRangeValidator(1, true))
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setRequires(Constants.USE_LOAD_BALANCER)
                    .build();

    protected static final SimpleAttributeDefinition LOAD_BALANCER_SIP_PORT =
            new SimpleAttributeDefinitionBuilder(Constants.LOAD_BALANCER_SIP_PORT, ModelType.INT)
                    .setXmlName(Constants.LOAD_BALANCER_SIP_PORT)
                    .setAllowNull(true)
                    .setValidator(new IntRangeValidator(1, true))
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setRequires(Constants.USE_LOAD_BALANCER)
                    .build();
    
    protected static final SimpleAttributeDefinition USE_STATIC_ADDRESS =
            new SimpleAttributeDefinitionBuilder(Constants.USE_STATIC_ADDRESS, ModelType.BOOLEAN)
                    .setXmlName(Constants.USE_STATIC_ADDRESS)
                    .setAllowNull(true)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setDefaultValue(new ModelNode(false))
                    .build();

    protected static final SimpleAttributeDefinition STATIC_SERVER_ADDRESS =
            new SimpleAttributeDefinitionBuilder(Constants.STATIC_SERVER_ADDRESS, ModelType.STRING)
                    .setXmlName(Constants.STATIC_SERVER_ADDRESS)
                    .setAllowNull(true)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setValidator(new InetAddressValidator(true, false))
                    .setRequires(Constants.USE_STATIC_ADDRESS)
                    .build();

    protected static final SimpleAttributeDefinition STATIC_SERVER_PORT =
            new SimpleAttributeDefinitionBuilder(Constants.STATIC_SERVER_PORT, ModelType.INT)
                    .setXmlName(Constants.STATIC_SERVER_PORT)
                    .setAllowNull(true)
                    .setValidator(new IntRangeValidator(1, true))
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setRequires(Constants.USE_STATIC_ADDRESS)
                    .build();

    protected static final SimpleAttributeDefinition USE_STUN =
            new SimpleAttributeDefinitionBuilder(Constants.USE_STUN, ModelType.BOOLEAN)
                    .setXmlName(Constants.USE_STUN)
                    .setAllowNull(true)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setDefaultValue(new ModelNode(false))
                    .build();

    protected static final SimpleAttributeDefinition STUN_SERVER_ADDRESS =
            new SimpleAttributeDefinitionBuilder(Constants.STUN_SERVER_ADDRESS, ModelType.STRING)
                    .setXmlName(Constants.STUN_SERVER_ADDRESS)
                    .setAllowNull(true)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setValidator(new InetAddressValidator(true, false))
                    .setRequires(Constants.USE_STUN)
                    .build();

    protected static final SimpleAttributeDefinition STUN_SERVER_PORT =
            new SimpleAttributeDefinitionBuilder(Constants.STUN_SERVER_PORT, ModelType.INT)
                    .setXmlName(Constants.STUN_SERVER_PORT)
                    .setAllowNull(true)
                    .setValidator(new IntRangeValidator(1, true))
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setRequires(Constants.USE_STUN)
                    .build();

    protected static final SimpleAttributeDefinition HOSTNAMES =
            new SimpleAttributeDefinitionBuilder(Constants.HOSTNAMES, ModelType.STRING)
                    .setXmlName(Constants.HOSTNAMES)
                    .setAllowNull(true)
                    .setFlags(AttributeAccess.Flag.RESTART_ALL_SERVICES)
                    .setValidator(new StringLengthValidator(1))
                    .build();

    protected static final SimpleAttributeDefinition[] CONNECTOR_ATTRIBUTES = {
        // NAME, // name is read-only
        // IMPORTANT -- keep these in xsd order as this order controls marshalling
        PROTOCOL,
        SCHEME,
        SOCKET_BINDING,
        ENABLED,
        USE_LOAD_BALANCER,
        LOAD_BALANCER_ADDRESS,
        LOAD_BALANCER_RMI_PORT,
        LOAD_BALANCER_SIP_PORT,
        USE_STATIC_ADDRESS,
        STATIC_SERVER_ADDRESS,
        STATIC_SERVER_PORT,
        USE_STUN,
        STUN_SERVER_ADDRESS,
        STUN_SERVER_PORT,
        HOSTNAMES
    };

    public static final SipConnectorDefinition INSTANCE = new SipConnectorDefinition();

    private SipConnectorDefinition() {
        super(SipExtension.CONNECTOR_PATH, SipExtension.getResourceDescriptionResolver(Constants.CONNECTOR),
                SipConnectorAdd.INSTANCE, SipConnectorRemove.INSTANCE);
    }

    @Override
    public void registerAttributes(ManagementResourceRegistration connectors) {
        for (SimpleAttributeDefinition def : CONNECTOR_ATTRIBUTES) {
            connectors.registerReadWriteAttribute(def, null, new ReloadRequiredWriteAttributeHandler(def));
        }

        for (final SimpleAttributeDefinition def : SipConnectorMetrics.ATTRIBUTES) {
            connectors.registerMetric(def, SipConnectorMetrics.INSTANCE);
        }
    }
}
