-- init patch structure


--- LOCK ---

SELECT pg_advisory_xact_lock(1313);


--- UTILITIES ---

CREATE TABLE IF NOT EXISTS "_SysLock" ("Code" varchar PRIMARY KEY);

CREATE OR REPLACE FUNCTION _cm3_system_lock_aquire_try(_name varchar) RETURNS boolean AS $$ DECLARE
    _aquired boolean;
BEGIN
    PERFORM pg_advisory_lock(1313); 
    IF EXISTS (SELECT * FROM "_SysLock" WHERE "Code" = _name) THEN
        _aquired = FALSE;
    ELSE
        INSERT INTO "_SysLock" ("Code") VALUES (_name);
        _aquired = TRUE;
    END IF;
    PERFORM pg_advisory_unlock(1313); 
    RETURN _aquired;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_lock_aquire(_name varchar) RETURNS VOID AS $$ DECLARE
    _aquired boolean;
BEGIN
    RAISE DEBUG 'aquiring system lock = %', _name;
    _aquired = _cm3_system_lock_aquire_try(_name);
    IF NOT _aquired THEN
        RAISE NOTICE 'unable to aquire system lock = %: lock already aquired by another thread; waiting for lock release', _name;
        WHILE NOT _aquired LOOP
            PERFORM pg_sleep(1);
            _aquired = _cm3_system_lock_aquire_try(_name);
        END LOOP;
    END IF;
    RAISE NOTICE 'aquired system lock = %', _name;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_lock_release(_name varchar) RETURNS VOID AS $$ BEGIN
    RAISE NOTICE 'release system lock = %', _name;
    DELETE FROM "_SysLock" WHERE "Code" = _name;
END $$ LANGUAGE PLPGSQL;


--- INIT PATCH TABLE ---

SELECT _cm3_system_lock_aquire('patch_init');

DO $$ DECLARE
	_version varchar;
BEGIN
	IF EXISTS (SELECT * FROM pg_class WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND relname = 'Patch') THEN
		_version = 'legacy_25';
	ELSEIF EXISTS (SELECT * FROM pg_class WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND relname = '_Patch') THEN
		SELECT INTO _version CASE WHEN description LIKE '%TYPE: class%' THEN 'legacy_30' ELSE regexp_replace(description, '.*VERSION: *([^ |]+).*', '\1') END FROM pg_description WHERE objoid = '"_Patch"'::regclass AND objsubid = 0;
	ELSE 
		_version = 'none';
	END IF;

	IF NOT _version = ANY(ARRAY['legacy_25','none','legacy_30','1','2']) THEN
		RAISE EXCEPTION 'unsupported patch table version = %', _version;
	END IF;

	IF NOT _version = '2' THEN
		RAISE NOTICE 'upgrade patch table: found existing patch table = %', _version;
	END IF;

	IF _version = ANY(ARRAY['legacy_25','legacy_30']) THEN
		CREATE TABLE _patch_aux (id bigint, code varchar,des varchar,cat varchar,phash varchar, content text,begindate timestamp with time zone default now());
	END IF;

	IF _version = 'legacy_25' THEN
		INSERT INTO _patch_aux (id,code,des,begindate,cat) SELECT "Id", "Code", "Description", COALESCE("BeginDate", now()), COALESCE("Category", 'core') FROM "Patch" WHERE "Status" = 'A';
		DROP TABLE "Patch_history";
		DROP TABLE "Patch";
	ELSEIF _version = 'legacy_30' THEN
		INSERT INTO _patch_aux SELECT "Id", "Code", "Description", "Category", "Hash", "Content", "BeginDate" FROM "_Patch" WHERE "Status" = 'A';
		DROP TABLE "_Patch_history";
		DROP TABLE "_Patch";
	END IF;

	IF _version NOT IN ('1','2') THEN -- TODO improve this, version order
		CREATE TABLE "_Patch" (
			"Id" bigserial, 
			"IdClass" regclass not null, 
			"User" varchar not null default 'system',
			"BeginDate" timestamp with time zone not null default now(),
			"IdTenant" bigint,
			"Code" varchar unique not null, 
			"Description" varchar, 
			"Notes" varchar,
			"Category" varchar, 
			"Hash" varchar,
			"Content" text);
		ALTER TABLE "_Patch" ALTER COLUMN "IdClass" SET DEFAULT '"_Patch"'::regclass;
		COMMENT ON TABLE "_Patch" IS 'TYPE: special|VERSION: 1';
	END IF;

	IF _version = ANY(ARRAY['legacy_25','legacy_30']) THEN
		INSERT INTO "_Patch" ("BeginDate","Code","Description","Category","Hash","Content") SELECT begindate,code,des,cat,phash,content FROM _patch_aux ORDER BY id;
		DROP TABLE _patch_aux;
	END IF;

    IF NOT EXISTS (SELECT * FROM pg_class WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND relname = '_Function') THEN
		CREATE TABLE "_Function" (
			"Id" bigserial, 
			"IdClass" regclass not null, 
			"User" varchar not null default 'system',
			"BeginDate" timestamp with time zone not null default now(),
			"IdTenant" bigint,
			"Code" varchar unique not null, 
			"Description" varchar, 
			"Notes" varchar,
			"Category" varchar, 
			"Hash" varchar,
			"Revision" varchar,
			"Content" text);
		ALTER TABLE "_Function" ALTER COLUMN "IdClass" SET DEFAULT '"_Function"'::regclass;
		COMMENT ON TABLE "_Function" IS 'TYPE: special|VERSION: 1';
	END IF;

	IF _version <> '2' THEN -- TODO improve this, version order
        ALTER TABLE "_Patch" ADD COLUMN "Status" varchar NOT NULL DEFAULT 'A';
        ALTER TABLE "_Patch" ADD COLUMN "Meta" jsonb NOT NULL DEFAULT '{}'::jsonb;
        ALTER TABLE "_Function" ADD COLUMN "Status" varchar NOT NULL DEFAULT 'A';
        ALTER TABLE "_Function" ADD COLUMN "Meta" jsonb NOT NULL DEFAULT '{}'::jsonb;

		COMMENT ON TABLE "_Patch" IS 'TYPE: special|VERSION: 2';
		COMMENT ON TABLE "_Function" IS 'TYPE: special|VERSION: 2';
    END IF;

	RAISE NOTICE 'patch table ready';

END $$ LANGUAGE PLPGSQL;

SELECT _cm3_system_lock_release('patch_init'); 
