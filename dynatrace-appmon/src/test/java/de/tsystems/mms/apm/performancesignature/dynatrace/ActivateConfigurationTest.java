/*
 * Copyright (c) 2014-2018 T-Systems Multimedia Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.tsystems.mms.apm.performancesignature.dynatrace;

import de.tsystems.mms.apm.performancesignature.dynatrace.util.TestUtils;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

public class ActivateConfigurationTest {

    @ClassRule
    public static final JenkinsRule j = new JenkinsRule();
    private static ListBoxModel dynatraceConfigurations;
    private FreeStyleProject project;

    @BeforeClass
    public static void setUp() throws Exception {
        dynatraceConfigurations = TestUtils.prepareDTConfigurations();
    }

    @Test
    public void testJenkinsConfiguration() throws Exception {
        project = j.createFreeStyleProject();
        project.getBuildersList().add(new PerfSigActivateConfiguration(dynatraceConfigurations.get(0).name, "ActivateConfigurationTest"));
        FreeStyleBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        String s = FileUtils.readFileToString(build.getLogFile());
        assertTrue(s.contains("activated configuration successfully on"));
    }

    @Test
    public void testFillConfigurationItems() {
        PerfSigActivateConfiguration.DescriptorImpl descriptor = new PerfSigActivateConfiguration.DescriptorImpl();
        ListBoxModel listBoxModel = descriptor.doFillConfigurationItems(project, dynatraceConfigurations.get(0).name);

        assertNotNull(listBoxModel);
        assertFalse(listBoxModel.isEmpty());
        assertTrue(TestUtils.containsOption(listBoxModel, "Default"));
        assertTrue(TestUtils.containsOption(listBoxModel, "ActivateConfigurationTest"));
    }

    @Test
    public void testCheckConfiguration() {
        PerfSigActivateConfiguration.DescriptorImpl descriptor = new PerfSigActivateConfiguration.DescriptorImpl();

        assertEquals(descriptor.doCheckConfiguration(project, "Default"), FormValidation.ok());
        assertEquals(descriptor.doCheckConfiguration(null, "ActivateConfigurationTest"), FormValidation.ok());
    }
}
