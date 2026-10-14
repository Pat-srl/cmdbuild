-- lookup table refactoring


SELECT _cm3_class_create('NAME: _LookupType|MODE: reserved|TYPE: class|DESCR: Lookup Type');
SELECT _cm3_attribute_create('OWNER: _LookupType|NAME: ParentType|TYPE: varchar|FKTARGETCLASS: _LookupType');
SELECT _cm3_attribute_create('OWNER: _LookupType|NAME: Access|TYPE: varchar|NOTNULL: true|DEFAULT: default|VALUES: default,system,protected');
SELECT _cm3_attribute_create('OWNER: _LookupType|NAME: Speciality|TYPE: varchar|NOTNULL: true|DEFAULT: default|VALUES: default,dmscategory');
SELECT _cm3_attribute_create('OWNER: _LookupType|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: {}');
SELECT _cm3_attribute_notnull_set('"_LookupType"', 'Code');
SELECT _cm3_attribute_unique_set('"_LookupType"', 'Code');

SELECT _cm3_class_create('NAME: _LookupValue|MODE: reserved|TYPE: class|DESCR: Lookup Value');
SELECT _cm3_attribute_create('OWNER: _LookupValue|NAME: Type|TYPE: varchar|NOTNULL: true|FKTARGETCLASS: _LookupType');
SELECT _cm3_attribute_create('OWNER: _LookupValue|NAME: ParentValue|TYPE: bigint|FKTARGETCLASS: _LookupValue');
SELECT _cm3_attribute_create('OWNER: _LookupValue|NAME: Index|TYPE: integer|NOTNULL: true|DEFAULT: -1');
SELECT _cm3_attribute_create('OWNER: _LookupValue|NAME: Active|TYPE: boolean|NOTNULL: true|DEFAULT: true');
SELECT _cm3_attribute_create('OWNER: _LookupValue|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: {}');
SELECT _cm3_attribute_notnull_set('"_LookupValue"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_LookupValue"', 'Code', 'Type');

SELECT _cm3_class_triggers_disable('"_LookupType"');
SELECT _cm3_class_triggers_disable('"_LookupValue"');

INSERT INTO "_LookupType" 
        ("Id", "IdClass", "Code", "Description", "Status", "User", "BeginDate", "EndDate", "CurrentId", "Notes", "ParentType", "Access", "Speciality")
    SELECT
        "Id", '"_LookupType"'::regclass, "Type", "Description", "Status", "User", "BeginDate", "EndDate", "CurrentId", "Notes", "ParentType", "AccessType", "Speciality"
    FROM "LookUp" l WHERE "Code" = 'org.cmdbuild.LOOKUPTYPE' AND "Status" IN ('A','N');

INSERT INTO "_LookupType_history" 
        ("Id", "IdClass", "Code", "Description", "Status", "User", "BeginDate", "EndDate", "CurrentId", "Notes", "ParentType", "Access", "Speciality")
    SELECT
        "Id", '"_LookupType"'::regclass, "Type", "Description", "Status", "User", "BeginDate", "EndDate", "CurrentId", "Notes", "ParentType", "AccessType", "Speciality"
    FROM "LookUp" l WHERE "CurrentId" IN (SELECT "Id" FROM ONLY "_LookupType") AND "Status" = 'U';

INSERT INTO "_LookupValue" 
        ("Id", "IdClass", "Code", "Description", "Status", "User", "BeginDate", "EndDate", "CurrentId", "Notes", "Type", "ParentValue", "Index", "Active", "Config")
    SELECT
        "Id", '"_LookupValue"'::regclass, "Code", "Description", "Status", "User", "BeginDate", "EndDate", "CurrentId", "Notes", "Type", "ParentId", "Index", "IsActive",
        "Config" || jsonb_build_object(
            'cm_is_default', "IsDefault",
            'cm_icon_type', "IconType",
            'cm_icon_image', "IconImage",
            'cm_icon_font', "IconFont",
            'cm_icon_color', "IconColor",
            'cm_text_color', "TextColor"
        )
    FROM "LookUp" WHERE "Code" <> 'org.cmdbuild.LOOKUPTYPE' AND "Status" IN ('A','N');

INSERT INTO "_LookupValue_history" 
        ("Id", "IdClass", "Code", "Description", "Status", "User", "BeginDate", "EndDate", "CurrentId", "Notes", "Type", "ParentValue", "Index", "Active", "Config")
    SELECT
        "Id", '"_LookupValue"'::regclass, "Code", "Description", "Status", "User", "BeginDate", "EndDate", "CurrentId", "Notes", "Type", "ParentId", "Index", "IsActive",
        "Config" || jsonb_build_object(
            'cm_is_default', "IsDefault",
            'cm_icon_type', "IconType",
            'cm_icon_image', "IconImage",
            'cm_icon_font', "IconFont",
            'cm_icon_color', "IconColor",
            'cm_text_color', "TextColor"
        )
    FROM "LookUp" h WHERE "CurrentId" IN (SELECT "Id" FROM ONLY "_LookupValue")  AND "Status" = 'U';

SELECT _cm3_class_triggers_enable('"_LookupType"');
SELECT _cm3_class_triggers_enable('"_LookupValue"');

CREATE OR REPLACE VIEW "_LookupView" AS SELECT * FROM (SELECT
        "Id", _cm3_utils_name_to_regclass('LookUp') AS "IdClass", 'org.cmdbuild.LOOKUPTYPE' AS "Code", "Description", "Status", "User", "BeginDate", "Notes", "EndDate", "CurrentId", "IdTenant", 
        "Code" AS "Type", 
        "ParentType",
        FALSE AS "IsDefault",
        'none'::varchar AS "IconType",
        NULL::varchar AS "IconImage",
        NULL::varchar AS "IconFont",
        -1::integer AS "Index",
        NULL::varchar AS "IconColor",
        NULL::varchar AS "TextColor",
        TRUE AS "IsActive",
        NULL::bigint AS "ParentId",
        "Speciality",
        '{}' AS "Config",
        "Access" AS "AccessType"
    FROM "_LookupType" UNION ALL SELECT
        _value."Id", _cm3_utils_name_to_regclass('LookUp') AS "IdClass", _value."Code", _value."Description", _value."Status", _value."User", _value."BeginDate", _value."Notes", _value."EndDate", _value."CurrentId", _value."IdTenant", 
        _type."Code" AS "Type", 
        _type."ParentType",
        COALESCE((_value."Config"->>'cm_is_default'), 'FALSE')::boolean AS "IsDefault",
        COALESCE((_value."Config"->>'cm_icon_type'), 'none')::varchar AS "IconType",
        (_value."Config"->>'cm_icon_image')::varchar AS "IconImage",
        (_value."Config"->>'cm_icon_font')::varchar AS "IconFont",
        "Index",
        (_value."Config"->>'cm_icon_color')::varchar AS "IconColor",
        (_value."Config"->>'cm_text_color')::varchar AS "TextColor",
        _value."Active" AS "IsActive",
        "ParentValue" AS "ParentId",
        "Speciality",
        _value."Config" - 'cm_is_default' - 'cm_icon_type' - 'cm_icon_image' - 'cm_icon_font' - 'cm_icon_color' - 'cm_text_color' AS "Config",
        "Access" AS "AccessType"
    FROM "_LookupValue" _value JOIN "_LookupType" _type ON _value."Type" = _type."Code" WHERE _type."Status" = 'A' ) x ORDER BY "Type", "Index";

ALTER VIEW "_LookupView" ALTER COLUMN "IsActive" SET DEFAULT true;
ALTER VIEW "_LookupView" ALTER COLUMN "Index" SET DEFAULT -1;
ALTER VIEW "_LookupView" ALTER COLUMN "Config" SET DEFAULT '{}'::jsonb;

CREATE TRIGGER _cm3_lookup_view_trigger INSTEAD OF INSERT OR UPDATE OR DELETE ON "_LookupView" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_lookup_view();

--TODO change lookup to a `SPECIAL` table, like patch and stuff
DROP TRIGGER IF EXISTS _cm3_card_cascade_delete_on_relations ON "LookUp";
DROP TRIGGER IF EXISTS _cm3_card_create_history ON "LookUp";
DROP TRIGGER IF EXISTS _cm3_card_prepare_record ON "LookUp";
DROP TRIGGER IF EXISTS _cm3_lookup_trigger ON "LookUp";
DROP TRIGGER IF EXISTS _cm3_trigger_card_enforce_foreign_key_for_source ON "LookUp";
DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk_ParentId" ON "LookUp";

TRUNCATE TABLE "LookUp_history";

CREATE TRIGGER _cm3_lookup_view_trigger BEFORE INSERT OR UPDATE ON "LookUp" FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_lookup_to_view();

CREATE TRIGGER _cm3_lookup_refresh_trigger AFTER INSERT OR UPDATE ON "_LookupType" EXECUTE PROCEDURE _cm3_trigger_lookup_refresh();
CREATE TRIGGER _cm3_lookup_refresh_trigger AFTER INSERT OR UPDATE ON "_LookupValue" EXECUTE PROCEDURE _cm3_trigger_lookup_refresh();

--TODO check this, fix these:
ALTER TABLE "LookUp" DROP CONSTRAINT "_cm3_IconColor_check";
ALTER TABLE "LookUp" DROP CONSTRAINT "_cm3_IconType_check";
ALTER TABLE "LookUp" DROP CONSTRAINT "_cm3_TextColor_check";
