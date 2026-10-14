-- legacy structure fix

COMMENT ON TABLE "Report" IS 'MODE: reserved|TYPE: simpleclass|DESCR: Report|SUPERCLASS: false'; 

DROP INDEX IF EXISTS "idx_map_userrole_defaultgroup";

