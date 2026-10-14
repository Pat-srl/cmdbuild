-- bim tables refactoring

CREATE TABLE _patch_aux_bimlayer (id bigint, usr varchar, begindate timestamp, classname varchar, root boolean , active boolean, rootreference varchar);
INSERT INTO _patch_aux_bimlayer (id,usr,begindate,classname,root,active,rootreference) SELECT "Id","User","BeginDate","ClassName","Root","Active","RootReference" FROM "_BimLayer";
DROP TABLE "_BimLayer";

SELECT _cm3_class_create('_BimLayer', '"Class"'::regclass, 'MODE: reserved|TYPE: class|DESCR: Bim Layers|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_BimLayer"'::regclass, 'OwnerClassId', 'regclass', 'NOTNULL: true|UNIQUE: true');
SELECT _cm3_attribute_create('"_BimLayer"'::regclass, 'Root', 'boolean', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_BimLayer"'::regclass, 'Active', 'boolean', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_BimLayer"'::regclass, 'RootReference', 'varchar','DESCR: attribute of owner class that references bim root class');

ALTER TABLE "_BimLayer" DISABLE TRIGGER USER;
INSERT INTO "_BimLayer" ("Id","CurrentId","IdClass","User","BeginDate","OwnerClassId","Root","Active","RootReference","Status") 
	SELECT id,id,'"_BimLayer"'::regclass,usr,begindate,_cm3_utils_name_to_regclass(classname),root,active,rootreference,'A' FROM _patch_aux_bimlayer;
ALTER TABLE "_BimLayer" ENABLE TRIGGER USER;
DROP TABLE _patch_aux_bimlayer;

SELECT _cm3_class_create('_BimObject', '"Class"'::regclass, 'MODE: reserved|TYPE: class|DESCR: Bim Objects');
SELECT _cm3_attribute_create('"_BimObject"'::regclass, 'OwnerClassId', 'regclass', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_BimObject"'::regclass, 'OwnerCardId', 'bigint', 'NOTNULL: true'); 
SELECT _cm3_attribute_create('"_BimObject"'::regclass, 'ProjectId', 'varchar',''); --TODO fix import query below, mark projectid as not null
SELECT _cm3_attribute_create('"_BimObject"'::regclass, 'GlobalId', 'varchar','');

DO $$ DECLARE 
	_owner_class regclass; 
	_domain regclass;
	_bim_class regclass;
    _reference_attr varchar; 
    _is_root boolean;
    _query varchar;
BEGIN
	FOR _owner_class, _reference_attr IN SELECT "OwnerClassId", "Root", "RootReference" FROM "_BimLayer" WHERE "Root" = TRUE LOOP		
		_bim_class = format('bim.%s',_owner_class)::regclass;
        _domain = _cm3_utils_name_to_regclass(format('Map_%s_BimProject',_cm3_utils_regclass_to_name(_owner_class)));
        EXECUTE format('INSERT INTO "_BimObject" ("OwnerClassId","OwnerCardId","ProjectId","GlobalId") 
            SELECT %L,m."IdObj1",p."ProjectId",b."GlobalId" FROM "_BimProject" p JOIN %s m on p."Id" = m."IdObj2" and m."Status" = ''A'' LEFT JOIN %s b ON m."IdObj1" = b."Master"', _owner_class, _domain, _bim_class);
        EXECUTE format('DROP TABLE %s CASCADE', _domain);
		EXECUTE format('DROP TABLE %s', _bim_class);	
		EXECUTE format('DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk_%s_Master" ON %s', _cm3_utils_regclass_to_name(_owner_class), _owner_class); --TODO check this
	END LOOP;
	FOR _owner_class, _is_root, _reference_attr IN SELECT "OwnerClassId", "Root", "RootReference" FROM "_BimLayer" WHERE "Root" = FALSE LOOP		
		_bim_class = format('bim.%s',_owner_class)::regclass;
        _domain = _cm3_attribute_reference_domain_get(_owner_class, _reference_attr);
        _query = CASE _cm3_attribute_reference_direction_get(_owner_class, _reference_attr) WHEN 'direct' 
            THEN format('SELECT "ProjectId" FROM "_BimObject" WHERE "OwnerCardId" = (SELECT "IdObj2" FROM %s WHERE "IdObj1" = _bim_table."Master" AND "Status" = ''A'')', _domain)
            ELSE format('SELECT "ProjectId" FROM "_BimObject" WHERE "OwnerCardId" = (SELECT "IdObj1" FROM %s WHERE "IdObj2" = _bim_table."Master" AND "Status" = ''A'')', _domain) 
        END;
        EXECUTE format('INSERT INTO "_BimObject" ("OwnerClassId","OwnerCardId","ProjectId","GlobalId") SELECT %L,"Master",(%s),"GlobalId" FROM %s _bim_table', _owner_class, _query, _bim_class);
		EXECUTE format('DROP TABLE %s', _bim_class);	
		EXECUTE format('DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk_%s_Master" ON %s', _cm3_utils_regclass_to_name(_owner_class), _owner_class); --TODO check this
	END LOOP;
	DROP SCHEMA IF EXISTS bim;
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
    _record record;
BEGIN
    IF EXISTS (WITH q AS (SELECT COUNT("Id") c FROM "_BimObject" WHERE "GlobalId" IS NOT NULL GROUP BY "GlobalId") SELECT 1 FROM q WHERE c > 1) THEN
        FOR _record IN WITH q AS (SELECT "GlobalId",COUNT("Id") c FROM "_BimObject" WHERE "GlobalId" IS NOT NULL GROUP BY "GlobalId") SELECT b.* FROM "_BimObject" b JOIN q ON b."GlobalId" = q."GlobalId" WHERE c > 1 LOOP
            RAISE WARNING 'duplicate global id record found = %', _record;
        END LOOP;
        RAISE 'invalid bim data, duplicate records found (es: %)', (WITH q AS (SELECT "GlobalId",COUNT("Id") c FROM "_BimObject" WHERE "GlobalId" IS NOT NULL GROUP BY "GlobalId") SELECT b FROM "_BimObject" b JOIN q ON b."GlobalId" = q."GlobalId" WHERE c > 1 LIMIT 1);
    END IF;
    PERFORM _cm3_attribute_unique_set('"_BimObject"', 'GlobalId');
END $$ LANGUAGE PLPGSQL;

CREATE TABLE _patch_aux_bimproject (id bigint, usr varchar, begindate timestamp,projectid varchar,active boolean,lastcheckin timestamp,importmapping text,code varchar, descr varchar);
INSERT INTO _patch_aux_bimproject(id,usr,begindate,projectid,active,lastcheckin,importmapping,code,descr) SELECT "Id","User","BeginDate","ProjectId","Active","LastCheckin","ImportMapping","Code","Description" FROM "_BimProject";
DROP TABLE "_BimProject";

SELECT _cm3_class_create('_BimProject', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Bim Projects');
SELECT _cm3_attribute_create('"_BimProject"', 'ProjectId', 'varchar', 'NOTNULL: true|UNIQUE: true|DESCR: Project ID');
SELECT _cm3_attribute_create('"_BimProject"', 'Active', 'boolean', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_BimProject"', 'LastCheckin', 'timestamp', 'DESCR: Last Checkin');
SELECT _cm3_attribute_create('"_BimProject"', 'ImportMapping', 'text', '');

ALTER TABLE "_BimProject" DISABLE TRIGGER USER;
INSERT INTO "_BimProject" ("Id","CurrentId","IdClass","User","BeginDate","ProjectId","Active","LastCheckin","ImportMapping","Status","Code","Description") 
	SELECT id,id,'"_BimLayer"'::regclass,usr,begindate,projectid,active,lastcheckin,importmapping,'A',code,descr FROM _patch_aux_bimproject;
ALTER TABLE "_BimProject" ENABLE TRIGGER USER;
DROP TABLE _patch_aux_bimproject;

