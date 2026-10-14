-- upgrade email job

UPDATE "_Job" SET "Config" = "Config" || jsonb_build_object('action_attachments_mode','attach_to_card') 
    WHERE "Type" = 'emailService' AND "Status" = 'A' AND ("Config"->>'action_workflow_attachmentssave') ILIKE 'true' AND _cm3_utils_is_blank("Config"->>'action_attachments_mode');

