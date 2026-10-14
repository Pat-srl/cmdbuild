-- nav tree upgrade

SELECT _cm3_class_create('_NavTree', 'MODE: reserved'); 
SELECT _cm3_attribute_create('"_NavTree"', 'Data', 'jsonb', 'NOTNULL: true');
SELECT _cm3_attribute_notnull_set('"_NavTree"', 'Code', TRUE);
SELECT _cm3_attribute_index_unique_create('"_NavTree"', 'Code');

CREATE OR REPLACE FUNCTION _patch_aux_build_tree(_root_id bigint) RETURNS jsonb AS $$ DECLARE
    _record RECORD;
BEGIN
    SELECT INTO _record * FROM "_DomainTreeNavigation" WHERE "Status" = 'A' AND "Id" = _root_id;
    RETURN jsonb_build_object(
        '_id', _cm3_utils_random_id(), 
        'targetClass', _record."TargetClassName",
        'filter', _record."TargetFilter",
        'description', _record."TargetClassDescription",
        'direction', CASE _record."Direct" WHEN FALSE THEN 'inverse' ELSE 'direct' END,
        'domain', _record."DomainName",
        'enableRecursion', _record."EnableRecursion",
        'showOnlyOne', _record."BaseNode",
        'nodes', (SELECT COALESCE(jsonb_agg(_patch_aux_build_tree("Id")),'[]'::jsonb) FROM "_DomainTreeNavigation" WHERE "Status" = 'A' AND "IdParent" = _root_id)
    );
END $$ LANGUAGE PLPGSQL;

INSERT INTO "_NavTree" ("Code", "Description", "Data") SELECT "Type", "Description", _patch_aux_build_tree("Id") FROM "_DomainTreeNavigation" WHERE "Status" = 'A' AND "IdParent" IS NULL;

DROP TABLE "_DomainTreeNavigation" CASCADE;
DROP FUNCTION _patch_aux_build_tree(_root_id bigint);

--     "_id": 17135,
--     "parent": null,
--     "filter": null,
--     "targetClass": "Building",
--     "recursionEnabled": false,
--     "domain": null,
--     "direction": null, 
-- 
-- 
--   Id   |         IdClass         | Code | Description | Status | User |           BeginDate           | Notes | EndDate | CurrentId | IdTenant | IdParent | IdGroup |     Type      |  DomainName   | Direct | BaseNode | TargetClassName | TargetClassDescription | TargetFilter | EnableRecursion 
-- -------+-------------------------+------+-------------+--------+------+-------------------------------+-------+---------+-----------+----------+----------+---------+---------------+---------------+--------+----------+-----------------+------------------------+--------------+-----------------
--  17135 | "_DomainTreeNavigation" |      |             | A      |      | 2018-09-26 17:36:33.830265+02 |       |         |     17135 |          |          |         | gisnavigation |               | f      | f        | Building        | Building               |              | f
--  17136 | "_DomainTreeNavigation" |      |             | A      |      | 2018-09-26 17:36:33.838223+02 |       |         |     17136 |          |    17135 |         | gisnavigation | BuildingFloor | t      | f        | Floor           | Floor                  |              | f
--  17137 | "_DomainTreeNavigation" |      |             | A      |      | 2018-09-26 17:36:33.84652+02  |       |         |     17137 |          |    17136 |         | gisnavigation | FloorRoom     | t      | f        | Room            | Room                   |              | f
-- (3 righe)
-- 
-- --------------------------------
--  Id                     | bigint                   |             | not null        | _cm3_utils_new_card_id() | plain          |             | MODE: reserved
--  IdClass                | regclass                 |             | not null        |                          | plain          |             | MODE: reserved
--  Code                   | character varying(100)   |             |                 |                          | extended       |             | MODE: write|DESCR: Code|INDEX: 1|BASEDSP: true
--  Description            | character varying(250)   |             |                 |                          | extended       |             | MODE: write
--  Status                 | character(1)             |             |                 |                          | extended       |             | MODE: reserved
--  User                   | character varying(100)   |             |                 |                          | extended       |             | MODE: reserved
--  BeginDate              | timestamp with time zone |             | not null        | now()                    | plain          |             | MODE: reserved
--  Notes                  | text                     |             |                 |                          | extended       |             | MODE: write|DESCR: Notes|INDEX: 3
--  EndDate                | timestamp with time zone |             |                 |                          | plain          |             | MODE: reserved
--  CurrentId              | bigint                   |             | not null        |                          | plain          |             | MODE: reserved
--  IdTenant               | bigint                   |             |                 |                          | plain          |             | MODE: rescore
--  IdParent               | integer                  |             |                 |                          | plain          |             | MODE: write
--  IdGroup                | integer                  |             |                 |                          | plain          |             | MODE: write
--  Type                   | character varying        |             |                 |                          | extended       |             | MODE: write
--  DomainName             | character varying        |             |                 |                          | extended       |             | MODE: write
--  Direct                 | boolean                  |             |                 |                          | plain          |             | MODE: write
--  BaseNode               | boolean                  |             |                 |                          | plain          |             | MODE: write
--  TargetClassName        | character varying        |             |                 |                          | extended       |             | MODE: write
--  TargetClassDescription | character varying        |             |                 |                          | extended       |             | MODE: write
--  TargetFilter           | character varying        |             |                 |                          | extended       |             | MODE: write
--  EnableRecursion        | boolean                  |             |                 |                          | plain          |             | MODE: write
