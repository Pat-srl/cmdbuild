-- fix email job config

UPDATE "_Job" SET "Config" = "Config" || jsonb_build_object('filter.regex.from',replace("Config"->>'filter.regex.from','&#124;','|')) WHERE "Config"->>'filter.regex.from' IS NOT NULL AND "Status" = 'A';
UPDATE "_Job" SET "Config" = "Config" || jsonb_build_object('filter.regex.subject',replace("Config"->>'filter.regex.subject','&#124;','|')) WHERE "Config"->>'filter.regex.subject' IS NOT NULL AND "Status" = 'A';

