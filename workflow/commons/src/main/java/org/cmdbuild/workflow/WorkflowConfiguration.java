package org.cmdbuild.workflow;

public interface WorkflowConfiguration {

    boolean returnNullPlanOnPlanError();

    boolean isEnabled();

    boolean enableCardCacheForReferenceMigration();

    boolean hideSaveButton();

    boolean isSynchronizationOfMissingVariablesDisabled();

    boolean isUserTaskParametersValidationEnabled();

    String getDefaultUserForWfJobs();

    boolean enableAddAttachmentOnClosedActivities();

    boolean isBulkAbortEnabledDefault();

    WorkflowApiErrorManagementMode getApiErrorManagementMode();

    default boolean enableSaveButton() {
        return !hideSaveButton();
    }

}
