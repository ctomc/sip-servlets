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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.as.controller.parsing.ParseUtils.missingRequired;
import static org.jboss.as.controller.parsing.ParseUtils.requireNoNamespaceAttribute;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedAttribute;
import static org.jboss.as.controller.parsing.ParseUtils.unexpectedElement;
import static org.mobicents.as10.Constants.CONNECTOR;
import static org.mobicents.as10.Constants.NAME;

import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.jboss.staxmapper.XMLExtendedStreamWriter;

/**
 * The web subsystem parser.
 *
 * @author Emanuel Muckenhuber
 * @author Brian Stansberry
 * @author Tomaz Cerar
 * @author josemrecio@gmail.com
 *
 *         This class is based on the contents of org.mobicents.as7 package from jboss-as7-mobicents project, re-implemented for
 *         jboss as10 (wildfly) by:
 * @author kakonyi.istvan@alerant.hu
 */
class SipSubsystemParser implements XMLStreamConstants, XMLElementReader<List<ModelNode>>,
        XMLElementWriter<SubsystemMarshallingContext> {

    private static final SipSubsystemParser INSTANCE = new SipSubsystemParser();

    static SipSubsystemParser getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeContent(XMLExtendedStreamWriter writer, SubsystemMarshallingContext context) throws XMLStreamException {

        context.startSubsystemElement(Namespace.CURRENT.getUriString(), false);

        ModelNode node = context.getModelNode();
        SipDefinition.INSTANCE_ID.marshallAsAttribute(node, false, writer);
        SipDefinition.APPLICATION_ROUTER.marshallAsAttribute(node, false, writer);
        SipDefinition.SIP_STACK_PROPS.marshallAsAttribute(node, false, writer);
        SipDefinition.SIP_PATH_NAME.marshallAsAttribute(node, false, writer);
        SipDefinition.SIP_APP_DISPATCHER_CLASS.marshallAsAttribute(node, false, writer);
        SipDefinition.CONCURRENCY_CONTROL_MODE.marshallAsAttribute(node, false, writer);
        SipDefinition.PROXY_TIMER_SERVICE_IMPEMENTATION_TYPE.marshallAsAttribute(node, false, writer);
        SipDefinition.SAS_TIMER_SERVICE_IMPEMENTATION_TYPE.marshallAsAttribute(node, false, writer);
        SipDefinition.CONGESTION_CONTROL_INTERVAL.marshallAsAttribute(node, true, writer);
        SipDefinition.CONGESTION_CONTROL_POLICY.marshallAsAttribute(node, false, writer);
        SipDefinition.USE_PRETTY_ENCODING.marshallAsAttribute(node, false, writer);
        SipDefinition.ADDITIONAL_PARAMETERABLE_HEADERS.marshallAsAttribute(node, false, writer);
        SipDefinition.BASE_TIMER_INTERVAL.marshallAsAttribute(node, false, writer);
        SipDefinition.T2_INTERVAL.marshallAsAttribute(node, false, writer);
        SipDefinition.T4_INTERVAL.marshallAsAttribute(node, false, writer);
        SipDefinition.TIMER_D_INTERVAL.marshallAsAttribute(node, false, writer);
        SipDefinition.GATHER_STATISTICS.marshallAsAttribute(node, false, writer);
        SipDefinition.CALL_ID_MAX_LENGTH.marshallAsAttribute(node, false, writer);
        SipDefinition.TAG_HASH_MAX_LENGTH.marshallAsAttribute(node, false, writer);
        SipDefinition.DIALOG_PENDING_REQUEST_CHECKING.marshallAsAttribute(node, false, writer);
        SipDefinition.DNS_SERVER_LOCATOR_CLASS.marshallAsAttribute(node, false, writer);
        SipDefinition.DNS_TIMEOUT.marshallAsAttribute(node, false, writer);
        SipDefinition.DNS_RESOLVER_CLASS.marshallAsAttribute(node, false, writer);
        SipDefinition.CANCELED_TIMER_TASKS_PURGE_PERIOD.marshallAsAttribute(node, false, writer);
        SipDefinition.MEMORY_THRESHOLD.marshallAsAttribute(node, false, writer);
        SipDefinition.BACK_TO_NORMAL_MEMORY_THRESHOLD.marshallAsAttribute(node, false, writer);
        SipDefinition.OUTBOUND_PROXY.marshallAsAttribute(node, false, writer);
        if (node.hasDefined(CONNECTOR)) {
            for (final Property connector : node.get(CONNECTOR).asPropertyList()) {
                final ModelNode config = connector.getValue();
                writer.writeStartElement(Element.CONNECTOR.getLocalName());
                writer.writeAttribute(NAME, connector.getName());
                for (SimpleAttributeDefinition attr : SipConnectorDefinition.CONNECTOR_ATTRIBUTES) {
                    attr.marshallAsAttribute(config, false, writer);
                }
                writer.writeEndElement();
            }
        }
        writer.writeEndElement();
    }

    /** {@inheritDoc} */
    @Override
    public void readElement(XMLExtendedStreamReader reader, List<ModelNode> list) throws XMLStreamException {
        PathAddress address = PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, SipExtension.SUBSYSTEM_NAME));

        final ModelNode subsystem = new ModelNode();
        subsystem.get(OP).set(ADD);
        subsystem.get(OP_ADDR).set(address.toModelNode());
        final int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {
            requireNoNamespaceAttribute(reader, i);
            final String value = reader.getAttributeValue(i);
            final Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
            switch (attribute) {
                case INSTANCE_ID:
                case APPLICATION_ROUTER:
                case SIP_STACK_PROPS:
                case SIP_APP_DISPATCHER_CLASS:
                case SIP_PATH_NAME:
                case ADDITIONAL_PARAMETERABLE_HEADERS:
                case BASE_TIMER_INTERVAL:
                case T2_INTERVAL:
                case T4_INTERVAL:
                case TIMER_D_INTERVAL:
                case GATHER_STATISTICS:
                case CALL_ID_MAX_LENGTH:
	            case TAG_HASH_MAX_LENGTH:
                case DIALOG_PENDING_REQUEST_CHECKING:
                case DNS_SERVER_LOCATOR_CLASS:
                case DNS_TIMEOUT:
                case DNS_RESOLVER_CLASS:
                case CANCELED_TIMER_TASKS_PURGE_PERIOD:
                case PROXY_TIMER_SERVICE_IMPEMENTATION_TYPE:
                case SAS_TIMER_SERVICE_IMPEMENTATION_TYPE:
                case CONGESTION_CONTROL_INTERVAL:
                case CONGESTION_CONTROL_POLICY:
                case MEMORY_THRESHOLD:
                case BACK_TO_NORMAL_MEMORY_THRESHOLD:
                case OUTBOUND_PROXY:
                case CONCURRENCY_CONTROL_MODE:
                case USE_PRETTY_ENCODING:
                    subsystem.get(attribute.getLocalName()).set(value);
                    break;
                default:
                    throw unexpectedAttribute(reader, i);
            }
        }
        list.add(subsystem);

        // elements
        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            switch (Namespace.forUri(reader.getNamespaceURI())) {
                case SIP_1_0: {
                    final Element element = Element.forName(reader.getLocalName());
                    switch (element) {
                        case CONNECTOR: {
                            parseConnector(reader, address, list);
                            break;
                        }
                        default: {
                            throw unexpectedElement(reader);
                        }
                    }
                    break;
                }
                default: {
                    throw unexpectedElement(reader);
                }
            }
        }
    }

    static void parseConnector(XMLExtendedStreamReader reader, PathAddress parent, List<ModelNode> list)
            throws XMLStreamException {
        String name = null;
        String bindingRef = null;
        final ModelNode connector = new ModelNode();

        final int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {
            requireNoNamespaceAttribute(reader, i);
            final String value = reader.getAttributeValue(i);
            final Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
            switch (attribute) {
                case NAME:
                    name = value;
                    break;
                case SOCKET_BINDING:
                    bindingRef = value;
                    SipConnectorDefinition.SOCKET_BINDING.parseAndSetParameter(value, connector, reader);
                    break;
                case SCHEME:
                    SipConnectorDefinition.SCHEME.parseAndSetParameter(value, connector, reader);
                    break;
                case PROTOCOL:
                    SipConnectorDefinition.PROTOCOL.parseAndSetParameter(value, connector, reader);
                    break;
                case ENABLED:
                    SipConnectorDefinition.ENABLED.parseAndSetParameter(value, connector, reader);
                    break;
                case USE_LOAD_BALANCER:
                	SipConnectorDefinition.USE_LOAD_BALANCER.parseAndSetParameter(value, connector, reader);
                	break;
                case LOAD_BALANCER_ADDRESS:
                	SipConnectorDefinition.LOAD_BALANCER_ADDRESS.parseAndSetParameter(value, connector, reader);
                	break;
                case LOAD_BALANCER_RMI_PORT:
                	SipConnectorDefinition.LOAD_BALANCER_RMI_PORT.parseAndSetParameter(value, connector, reader);
                	break;
                case LOAD_BALANCER_SIP_PORT:
                	SipConnectorDefinition.LOAD_BALANCER_SIP_PORT.parseAndSetParameter(value, connector, reader);
                	break;
                case USE_STATIC_ADDRESS:
                    SipConnectorDefinition.USE_STATIC_ADDRESS.parseAndSetParameter(value, connector, reader);
                    break;
                case STATIC_SERVER_ADDRESS:
                    SipConnectorDefinition.STATIC_SERVER_ADDRESS.parseAndSetParameter(value, connector, reader);
                    break;
                case STATIC_SERVER_PORT:
                    SipConnectorDefinition.STATIC_SERVER_PORT.parseAndSetParameter(value, connector, reader);
                    break;
                case USE_STUN:
                    SipConnectorDefinition.USE_STUN.parseAndSetParameter(value, connector, reader);
                    break;
                case STUN_SERVER_ADDRESS:
                    SipConnectorDefinition.STUN_SERVER_ADDRESS.parseAndSetParameter(value, connector, reader);
                    break;
                case STUN_SERVER_PORT:
                    SipConnectorDefinition.STUN_SERVER_PORT.parseAndSetParameter(value, connector, reader);
                    break;
                case HOSTNAMES:
                    SipConnectorDefinition.HOSTNAMES.parseAndSetParameter(value, connector, reader);
                    break;
                default:
                    throw unexpectedAttribute(reader, i);
            }
        }
        if (name == null) {
            throw missingRequired(reader, Collections.singleton(Attribute.NAME));
        }
        if (bindingRef == null) {
            throw missingRequired(reader, Collections.singleton(Attribute.SOCKET_BINDING));
        }
        connector.get(OP).set(ADD);
        PathAddress address = PathAddress.pathAddress(parent, PathElement.pathElement(CONNECTOR, name));
        connector.get(OP_ADDR).set(address.toModelNode());
        list.add(connector);

        while (reader.hasNext() && reader.nextTag() != END_ELEMENT) {
            switch (Namespace.forUri(reader.getNamespaceURI())) {
                case SIP_1_0: {
                    final Element element = Element.forName(reader.getLocalName());
                    switch (element) {
                        default:
                            throw unexpectedElement(reader);
                    }
                    // break;
                }
                default:
                    throw unexpectedElement(reader);
            }
        }
    }
}
