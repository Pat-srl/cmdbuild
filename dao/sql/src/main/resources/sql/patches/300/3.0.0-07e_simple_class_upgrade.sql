-- simple class structure upgrade


DROP TABLE IF EXISTS "SimpleClass";
CREATE TABLE "SimpleClass" (
	"Id" BIGINT NOT NULL DEFAULT _cm3_utils_new_card_id(),
	"IdClass" REGCLASS NOT NULL,
	"User" VARCHAR(100),
	"BeginDate" TIMESTAMP NOT NULL DEFAULT now(),
	"IdTenant" BIGINT
);

COMMENT ON TABLE "SimpleClass" IS 'MODE: reserved|TYPE: simpleclass|SUPERCLASS: true';
COMMENT ON COLUMN "SimpleClass"."Id" IS 'MODE: reserved';
COMMENT ON COLUMN "SimpleClass"."IdClass" IS 'MODE: reserved';
COMMENT ON COLUMN "SimpleClass"."User" IS 'MODE: reserved';
COMMENT ON COLUMN "SimpleClass"."BeginDate" IS 'MODE: read|BASEDSP: true';
COMMENT ON COLUMN "SimpleClass"."IdTenant" IS 'MODE: reserved';

DO $$ DECLARE
	_class regclass;
BEGIN	
	FOR _class IN SELECT * FROM _cm3_class_list_simple() LOOP
        RAISE NOTICE 'upgrade simple class %', _class;
        PERFORM _cm3_utils_store_and_drop_dependant_views(_class);
		EXECUTE format('ALTER TABLE %s ALTER COLUMN "Id" SET DEFAULT 0', _class); --TODO modify only if column uses sequence
		EXECUTE format('ALTER TABLE %s 
            ALTER COLUMN "Id" TYPE bigint, 
            ADD COLUMN "IdTenant" BIGINT,
            ALTER COLUMN "User" TYPE VARCHAR(100),
            ALTER COLUMN "IdClass" SET NOT NULL,
            ALTER COLUMN "BeginDate" SET NOT NULL,
            ALTER COLUMN "BeginDate" SET DEFAULT now()', _class);
		EXECUTE format('ALTER TABLE %s INHERIT %s', _class, '"SimpleClass"'::regclass);
		EXECUTE format('COMMENT ON COLUMN %s."IdTenant" IS ''MODE: reserved''', _class);
		EXECUTE format('ALTER TABLE %s ALTER COLUMN "Id" SET DEFAULT _cm3_utils_new_card_id()', _class); --TODO modify only if column uses sequence
        PERFORM _cm3_utils_restore_dependant_views();
	END LOOP;
END $$ LANGUAGE PLPGSQL;
