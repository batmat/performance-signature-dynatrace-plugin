package de.tsystems.mms.apm.performancesignature.dynatracesaas;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.google.common.collect.ImmutableSet;
import de.tsystems.mms.apm.performancesignature.dynatracesaas.util.DynatraceUtils;
import hudson.Extension;
import hudson.model.TaskListener;
import hudson.security.Permission;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.util.Set;

public class DynatraceSessionStep extends Step {
    private final String envId;
    private final String testCase;

    @DataBoundConstructor
    public DynatraceSessionStep(final String envId, final String testCase) {
        this.envId = envId;
        this.testCase = testCase;
    }

    public String getEnvId() {
        return envId;
    }

    public String getTestCase() {
        return testCase;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new DynatraceSessionStepExecution(this, stepContext);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "recordDynatraceSession";
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "record Dynatrace Saas/Managed session";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(TaskListener.class);
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillEnvIdItems(@QueryParameter final String envId) {
            if (!Jenkins.getInstance().hasPermission(Permission.CONFIGURE)) {
                return new StandardListBoxModel().includeCurrentValue(envId);
            }
            return DynatraceUtils.listToListBoxModel(DynatraceUtils.getDynatraceConfigurations());
        }

        @Nonnull
        @Restricted(NoExternalUse.class)
        public ListBoxModel doFillDynatraceProfileItems(@QueryParameter final String dynatraceProfile) {
            if (!Jenkins.getInstance().hasPermission(Permission.CONFIGURE)) {
                return new StandardListBoxModel().includeCurrentValue(dynatraceProfile);
            }
            return DynatraceUtils.listToListBoxModel(DynatraceUtils.getDynatraceConfigurations());
        }
    }
}
