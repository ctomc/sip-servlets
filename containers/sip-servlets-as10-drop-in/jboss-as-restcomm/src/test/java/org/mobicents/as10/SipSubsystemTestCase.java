package org.mobicents.as10;

import java.io.IOException;

import org.jboss.as.controller.RunningMode;
import org.jboss.as.subsystem.test.AbstractSubsystemBaseTest;
import org.jboss.as.subsystem.test.AdditionalInitialization;

/**
 * @author Tomaz Cerar (c) 2017 Red Hat Inc.
 */
public class SipSubsystemTestCase extends AbstractSubsystemBaseTest {

    public SipSubsystemTestCase() {
        super(SipExtension.SUBSYSTEM_NAME, new SipExtension());
    }


    @Override
    protected String getSubsystemXml() throws IOException {
        return readResource("sip-1.0.xml");
    }

    @Override
    protected AdditionalInitialization createAdditionalInitialization() {
        return new AdditionalInitialization() {
            @Override
            protected RunningMode getRunningMode() {
                return RunningMode.NORMAL;
            }
        };
    }
}
