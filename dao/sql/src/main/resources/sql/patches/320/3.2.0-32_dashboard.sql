-- dashboard upgrade

ALTER TABLE "_Grant" DROP CONSTRAINT "_cm3_Type_check";
ALTER TABLE "_Grant" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('Class', 'CustomPage', 'Filter', 'View', 'Report', 'IETemplate', 'Dashboard') );

SELECT _cm3_class_create('_Dashboard', 'MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _Dashboard|NAME: Active|TYPE: boolean|NOTNULL: true|DEFAULT: true'); 
SELECT _cm3_attribute_create('OWNER: _Dashboard|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb'); 
SELECT _cm3_attribute_notnull_set('"_Dashboard"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_Dashboard"', 'Code'); 

SELECT _cm3_class_triggers_disable('"_Dashboard"');

INSERT INTO "_Dashboard" ("Id","CurrentId","BeginDate","IdClass","Status","User","Code","Description","Config") 
    SELECT
        "Id",
        "Id",
        "BeginDate",
        '"_Dashboard"'::regclass,
        'A',
        "User",
        ("Definition"::jsonb)->>'name',
        ("Definition"::jsonb)->>'description',
        ("Definition"::jsonb)-'name'-'description'-'groups'
    FROM "_Dashboards";

SELECT _cm3_class_triggers_enable('"_Dashboard"');

WITH q AS ( SELECT "Id" dashboard_id, jsonb_array_elements_text(("Definition"::jsonb)->'groups') group_name from "_Dashboards" )
    INSERT INTO "_Grant" ("IdRole","Type","Mode","ObjectId") 
        SELECT
            "Role"."Id",
            'Dashboard',
            'r',
            q.dashboard_id
        FROM q, "Role" WHERE q.group_name = "Role"."Code" AND "Role"."Status" = 'A';

DROP TABLE "_Dashboards";
