-- wf data fix

SELECT _cm3_class_triggers_disable('"Activity"');
UPDATE "Activity" SET "FlowData" = "FlowData"->'d' WHERE "FlowData" IS NOT NULL AND "FlowData"->'d' IS NOT NULL;
SELECT _cm3_class_triggers_enable('"Activity"');
