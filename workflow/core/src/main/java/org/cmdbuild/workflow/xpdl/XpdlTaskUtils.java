/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.xpdl;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.workflow.model.TaskAttributeImpl;
import org.cmdbuild.workflow.model.TaskMetadata;
import org.cmdbuild.workflow.model.TaskAttribute;

public class XpdlTaskUtils {

    public static final String VARIABLE_PREFIX = "VariableToProcess_", METADATA_PREFIX = "Metadata_";

    @Nullable
    public static TaskAttribute taskVariableFromXpdlKeyValue(@Nullable String key, @Nullable String value) {
        if (!isVariableKey(key) || isBlank(value)) {
            return null;
        } else {
            TaskVariableMetadata type = getVariableMetadata(key);
            return TaskAttributeImpl.builder()
                    .withName(value)
                    .withAction(type.isAction())
                    .withWritable(type.isWritable())
                    .withMandatory(type.isMandatory())
                    .build();
        }
    }

    public static boolean isVariableKey(@Nullable String key) {
        return !isBlank(key) && key.startsWith(VARIABLE_PREFIX);
    }

    public static TaskVariableMetadata getVariableMetadata(String key) {
        String suffix = key.substring(VARIABLE_PREFIX.length());
        return TaskVariableMetadata.valueOf(suffix);
    }

    @Nullable
    public static TaskMetadata taskMetadataFromXpdlKeyValue(@Nullable String key, @Nullable String value) {
        if (!isMetadata(key) || isBlank(value)) {
            return null;
        } else {
            return new TaskMetadata(extractMetadataKey(key), value);
        }
    }

    private static boolean isMetadata(String key) {
        return key.startsWith(METADATA_PREFIX);
    }

    private static String extractMetadataKey(String key) {
        return key.substring(METADATA_PREFIX.length());
    }

    public enum TaskVariableMetadata {

        VIEW(false, false, false),
        UPDATE(true, false, false),
        UPDATEREQUIRED(true, true, false),
        ACTION(true, true, true);

//		VIEW(false, false, false, "READ_ONLY"),
//		UPDATE(true, false, false, "READ_WRITE"),
//		UPDATEREQUIRED(true, true, false, "READ_WRITE_REQUIRED"),
//		ACTION(true, true, true, "ACTION");
        private final boolean writable, mandatory, action;
//		private final String legacy;

//		private TaskVariableMetadata(boolean writable, boolean mandatory, boolean action, String legacy) {
        private TaskVariableMetadata(boolean writable, boolean mandatory, boolean action) {
            this.writable = writable;
            this.mandatory = mandatory;
//			this.legacy = legacy;
            this.action = action;
        }

        public boolean isAction() {
            return action;
        }

        public boolean isWritable() {
            return writable;
        }

        public boolean isMandatory() {
            return mandatory;
        }

//		@Deprecated
//		public String getLegacy() {
//			return legacy;
//		}
    }

}
