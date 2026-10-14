-- improved lookup type and value refresh triggers 2
 
DROP TRIGGER IF EXISTS _cm3_lookup_refresh_trigger ON "_LookupType";
DROP TRIGGER IF EXISTS _cm3_lookup_refresh_trigger ON "_LookupValue";

CREATE TRIGGER _cm3_lookup_refresh_trigger AFTER INSERT OR UPDATE ON "_LookupType" EXECUTE PROCEDURE _cm3_trigger_lookup_refresh();
CREATE TRIGGER _cm3_lookup_refresh_trigger AFTER INSERT OR UPDATE ON "_LookupValue" EXECUTE PROCEDURE _cm3_trigger_lookup_refresh();

DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk_LookUp_ParentId" ON "LookUp";


CREATE TABLE _patch_aux AS SELECT * FROM "_LookupValue";

TRUNCATE TABLE "_LookupValue";
TRUNCATE TABLE "_LookupValue_history";

ALTER TABLE "_LookupValue" DROP COLUMN "Type" CASCADE;
DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk_Type" ON "_LookupValue";
DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk__LookupValue_Type" ON "_LookupType";

SELECT _cm3_attribute_create('OWNER: _LookupValue|NAME: Type|TYPE: bigint|NOTNULL: true|FKTARGETCLASS: _LookupType');
SELECT _cm3_attribute_index_unique_create('"_LookupValue"', 'Code', 'Type');

SELECT _cm3_class_triggers_disable('"_LookupValue"');

INSERT INTO "_LookupValue" ("Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant","Type","ParentValue","Index","Active","Config") 
    SELECT "Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant",(SELECT "CurrentId" FROM "_LookupType" t WHERE t."Code" = p."Type" ORDER BY "BeginDate" DESC, "Status" LIMIT 1),"ParentValue","Index","Active","Config"
    FROM _patch_aux p WHERE "Status" <> 'U';

INSERT INTO "_LookupValue_history" ("Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant","Type","ParentValue","Index","Active","Config") 
    SELECT "Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant",(SELECT "CurrentId" FROM "_LookupType" t WHERE t."Code" = p."Type" ORDER BY "BeginDate" DESC, "Status" LIMIT 1),"ParentValue","Index","Active","Config" 
    FROM _patch_aux p WHERE "Status" = 'U';

SELECT _cm3_class_triggers_enable('"_LookupValue"');

DROP TABLE _patch_aux;


CREATE TABLE _patch_aux AS SELECT * FROM "_LookupType";

TRUNCATE TABLE "_LookupType";
TRUNCATE TABLE "_LookupType_history";

ALTER TABLE "_LookupType" DROP COLUMN "ParentType" CASCADE;
DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk_ParentType" ON "_LookupType";
DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk__LookupType_ParentType" ON "_LookupType";

SELECT _cm3_attribute_create('OWNER: _LookupType|NAME: ParentType|TYPE: bigint|FKTARGETCLASS: _LookupType');

SELECT _cm3_class_triggers_disable('"_LookupType"');

INSERT INTO "_LookupType" ("Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant","ParentType","Access","Speciality","Config") 
    SELECT "Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant",(SELECT "CurrentId" FROM "_patch_aux" t WHERE t."Code" = p."ParentType" ORDER BY "BeginDate" DESC, "Status" LIMIT 1),"Access","Speciality","Config"
    FROM _patch_aux p WHERE "Status" <> 'U';

INSERT INTO "_LookupType_history" ("Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant","ParentType","Access","Speciality","Config") 
    SELECT "Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant",(SELECT "CurrentId" FROM "_patch_aux" t WHERE t."Code" = p."ParentType" ORDER BY "BeginDate" DESC, "Status" LIMIT 1),"Access","Speciality","Config" 
    FROM _patch_aux p WHERE "Status" = 'U';

SELECT _cm3_class_triggers_enable('"_LookupType"');

DROP TABLE _patch_aux;

CREATE OR REPLACE VIEW "_LookupView" AS SELECT * FROM (SELECT
    t."Id", _cm3_utils_name_to_regclass('LookUp') AS "IdClass", 'org.cmdbuild.LOOKUPTYPE' AS "Code", t."Description", t."Status", t."User", t."BeginDate", t."Notes", t."EndDate", t."CurrentId", t."IdTenant", 
    t."Code" AS "Type", 
    p."Code" AS "ParentType",
    FALSE AS "IsDefault",
    'none'::varchar AS "IconType",
    NULL::varchar AS "IconImage",
    NULL::varchar AS "IconFont",
    -1::integer AS "Index",
    NULL::varchar AS "IconColor",
    NULL::varchar AS "TextColor",
    TRUE AS "IsActive",
    NULL::bigint AS "ParentId",
    t."Speciality",
    '{}' AS "Config",
    t."Access" AS "AccessType"
FROM "_LookupType" t LEFT JOIN "_LookupType" p ON t."ParentType" = p."Id" UNION ALL SELECT
    _value."Id", _cm3_utils_name_to_regclass('LookUp') AS "IdClass", _value."Code", _value."Description", _value."Status", _value."User", _value."BeginDate", _value."Notes", _value."EndDate", _value."CurrentId", _value."IdTenant", 
    _type."Code" AS "Type", 
    _parent."Code" AS "ParentType",
    COALESCE((_value."Config"->>'cm_is_default'), 'FALSE')::boolean AS "IsDefault",
    COALESCE((_value."Config"->>'cm_icon_type'), 'none')::varchar AS "IconType",
    (_value."Config"->>'cm_icon_image')::varchar AS "IconImage",
    (_value."Config"->>'cm_icon_font')::varchar AS "IconFont",
    "Index",
    (_value."Config"->>'cm_icon_color')::varchar AS "IconColor",
    (_value."Config"->>'cm_text_color')::varchar AS "TextColor",
    _value."Active" AS "IsActive",
    "ParentValue" AS "ParentId",
    _type."Speciality",
    _value."Config" - 'cm_is_default' - 'cm_icon_type' - 'cm_icon_image' - 'cm_icon_font' - 'cm_icon_color' - 'cm_text_color' AS "Config",
    _type."Access" AS "AccessType"
FROM "_LookupValue" _value JOIN "_LookupType" _type ON _value."Type" = _type."Id" LEFT JOIN "_LookupType" _parent ON _type."ParentType" = _parent."Id" ) x ORDER BY "Type", "Index";

ALTER VIEW "_LookupView" ALTER COLUMN "IsActive" SET DEFAULT true;
ALTER VIEW "_LookupView" ALTER COLUMN "Index" SET DEFAULT -1;
ALTER VIEW "_LookupView" ALTER COLUMN "Config" SET DEFAULT '{}'::jsonb;

CREATE TRIGGER _cm3_lookup_view_trigger INSTEAD OF INSERT OR UPDATE OR DELETE ON "_LookupView" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_lookup_view();

UPDATE "_LookupValue" SET "Code" = "Code" WHERE "Id" = (SELECT "Id"  FROM "_LookupValue" WHERE "Status" = 'A' LIMIT 1);
