-- new system tables

SELECT _cm3_system_login();


SELECT _cm3_class_create('_Session', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: Session|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Session"'::regclass, 'SessionId', 'varchar(32)', 'UNIQUE: true|NOTNULL: true|MODE: read|DESCR: Session id');
SELECT _cm3_attribute_create('"_Session"'::regclass, 'Data', 'jsonb', 'DEFAULT: ''{}''::jsonb|NOTNULL: true|MODE: read|DESCR: Data');
SELECT _cm3_attribute_create('"_Session"'::regclass, 'LastActiveDate', 'timestamp', 'NOTNULL: true|MODE: read');


SELECT _cm3_class_create('_Request', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: Request|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Request"', 'Timestamp', 'timestamp', 'NOTNULL: true|MODE: read');
SELECT _cm3_attribute_create('"_Request"', 'Path', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Request Path');
SELECT _cm3_attribute_create('"_Request"', 'Method', 'varchar(16)', 'NOTNULL: true|MODE: read|DESCR: Request Method');
SELECT _cm3_attribute_create('"_Request"', 'SessionId', 'varchar(50)', 'MODE: read|DESCR: Session id');
SELECT _cm3_attribute_create('"_Request"', 'SessionUser', 'varchar(40)', 'MODE: read|DESCR: Username');
SELECT _cm3_attribute_create('"_Request"', 'RequestId', 'varchar(50)', 'NOTNULL: true|MODE: read|DESCR: Request id');
SELECT _cm3_attribute_create('"_Request"', 'TrackingId', 'varchar(50)', 'NOTNULL: true|MODE: read|DESCR: Tracking id');
SELECT _cm3_attribute_create('"_Request"', 'ActionId', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Action Id');
SELECT _cm3_attribute_create('"_Request"', 'Client', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Client IP');
SELECT _cm3_attribute_create('"_Request"', 'UserAgent', 'varchar', 'NOTNULL: true|MODE: read|DESCR: User Agent');
SELECT _cm3_attribute_create('"_Request"', 'Query', 'varchar', 'MODE: read|DESCR: Request Query');
SELECT _cm3_attribute_create('"_Request"', 'Completed', 'boolean', 'NOTNULL: true|MODE: read|DESCR: Request is completed');
SELECT _cm3_attribute_create('"_Request"', 'Payload', 'text', 'MODE: read|DESCR: Request Payload');
SELECT _cm3_attribute_create('"_Request"', 'PayloadSize', 'integer', 'MODE: read|DESCR: Request Payload Size');
SELECT _cm3_attribute_create('"_Request"', 'Response', 'text', 'MODE: read|DESCR: Response Payload');
SELECT _cm3_attribute_create('"_Request"', 'ResponseSize', 'integer', 'MODE: read|DESCR: Response Payload Size');
SELECT _cm3_attribute_create('"_Request"', 'StatusCode', 'char(3)', 'MODE: read|DESCR: Response Status Code');
SELECT _cm3_attribute_create('"_Request"', 'ElapsedTime', 'integer', 'MODE: read');
SELECT _cm3_attribute_create('"_Request"', 'Errors', 'jsonb', 'DEFAULT: ''{}''::jsonb|MODE: read|DESCR: Request Processing Errors');
SELECT _cm3_attribute_create('"_Request"', 'NodeId', 'varchar', 'NOTNULL: true|MODE: immutable|DESCR: Cluster node id');
SELECT _cm3_attribute_create('"_Request"', 'PayloadContentType', 'varchar', 'MODE: read|DESCR: Request Payload Content Type');
SELECT _cm3_attribute_create('"_Request"', 'ResponseContentType', 'varchar', 'MODE: read|DESCR: Response Payload Content Type');


SELECT _cm3_class_create('_SystemConfig', '"Class"', 'MODE: reserved|TYPE: class|DESCR: System Configuration|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_SystemConfig"', 'Value', 'varchar', 'MODE: write|DESCR: Value');


SELECT _cm3_class_create('_Lock', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: Lock|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Lock"', 'SessionId', 'varchar(50)', 'NOTNULL: true|MODE: read|DESCR: Session id');
SELECT _cm3_attribute_create('"_Lock"', 'ItemId', 'varchar(50)', 'NOTNULL: true|UNIQUE: true|MODE: read|DESCR: Item id');
SELECT _cm3_attribute_create('"_Lock"', 'LastActiveDate', 'timestamp', 'NOTNULL: true|MODE: read');


SELECT _cm3_class_create('_UserConfig', '"Class"', 'MODE: reserved|TYPE: class|DESCR: User Configuration|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_UserConfig"', 'Data', 'jsonb', 'DEFAULT: ''{}''::jsonb|NOTNULL: true|MODE: read');
SELECT _cm3_domain_create('UserConfigUser', 'MODE: reserved|TYPE: domain|CLASS1: _UserConfig|CLASS2: User|DESCRDIR: |DESCRINV: |CARDIN: N:1');
SELECT _cm3_attribute_create('"_UserConfig"', 'Owner', 'bigint', 'NOTNULL: true|UNIQUE: true|MODE: sysread|DESCR: User|REFERENCEDOM: UserConfigUser|REFERENCEDIR: direct');

INSERT INTO "_UserConfig" ("Owner", "Data") SELECT "Id", jsonb_build_object('cm_user_multiGroup','true') FROM "User" u WHERE "Status" = 'A' 
	AND EXISTS (SELECT * FROM "Map_UserRole" mur WHERE mur."IdObj1" = u."Id" and mur."Status" = 'A' AND mur."DefaultGroup" = TRUE);


SELECT _cm3_class_create('_Document', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Documents|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Document"', 'FileName', 'varchar','NOTNULL: true|MODE: read|DESCR: File Name');
SELECT _cm3_attribute_create('"_Document"', 'MimeType', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Mime Type'); 
SELECT _cm3_attribute_create('"_Document"', 'Version', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Version Number');
SELECT _cm3_attribute_create('"_Document"', 'Category', 'varchar', 'MODE: read|DESCR: Category');
SELECT _cm3_attribute_create('"_Document"', 'Hash', 'varchar', 'NOTNULL: true|MODE: read|DESCR: File Hash');
SELECT _cm3_attribute_create('"_Document"', 'Size', 'integer', 'NOTNULL: true|MODE: read|DESCR: File Size');
SELECT _cm3_attribute_create('"_Document"', 'CardId', 'bigint', 'NOTNULL: true|MODE: read|DESCR: Attached to Card Id'); 
SELECT _cm3_attribute_create('"_Document"', 'CreationDate', 'timestamp', 'NOTNULL: true|MODE: read|DESCR: Creation Date');
SELECT _cm3_attribute_index_create('"_Document"', 'CardId');
SELECT _cm3_attribute_index_unique_create('"_Document"', 'CardId', 'FileName');


SELECT _cm3_class_create('_DocumentData', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: Document data|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_DocumentData"', 'DocumentId', 'bigint', 'UNIQUE: true|NOTNULL: true|MODE: read|DESCR: Document id');
SELECT _cm3_attribute_create('"_DocumentData"', 'Data', 'bytea', 'NOTNULL: true|MODE: read|DESCR: Data');


SELECT _cm3_class_create('_Plan', '"Class"', 'MODE: reserved|TYPE: class|DESCR: cmdbuild-river plan instances|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Plan"', 'ClassId', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Process Class Name');
SELECT _cm3_attribute_create('"_Plan"', 'Data', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Plan xpdl content');
SELECT _cm3_attribute_comment_set('"_Plan"', 'BeginDate', 'MODE', 'hidden');


SELECT _cm3_class_create('_SystemStatusLog', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: System Status Log|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'JavaMemoryUsed', 'integer', 'NOTNULL: true|MODE: read|DESCR: Memory Used by java (MB)');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'JavaMemoryAvailable', 'integer', 'NOTNULL: true|MODE: read|DESCR: Total Memory available for java (MB)');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'SystemMemoryUsed', 'integer', 'MODE: read|DESCR: Memory Used by host system (MB)');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'SystemMemoryAvailable', 'integer', 'MODE: read|DESCR: Total Memory available for host system (MB)');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'FilesystemMemoryUsed', 'integer', 'MODE: read|DESCR: Memory used on webapp filesystem (MB)');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'FilesystemMemoryAvailable', 'integer', 'MODE: read|DESCR: Total Memory available on webapp filesystem (MB)');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'LoadAvg', 'decimal', 'NOTNULL: true|MODE: read|DESCR: CPU Load Average');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'ActiveSessionCount', 'integer', 'NOTNULL: true|MODE: read|DESCR: Active User Sessions');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'Warnings', 'varchar', 'MODE: read|DESCR: Warning Messages');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'Pid', 'integer', 'NOTNULL: true|MODE: read|DESCR: Java Process ID');
SELECT _cm3_attribute_create('"_SystemStatusLog"', 'Hostname', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Hostname');
--TODO: I/O? other info? cache size?


SELECT _cm3_class_create('_Upload', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Uploaded files|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Upload"', 'Path', 'varchar[]', 'NOTNULL: true|UNIQUE: true|MODE: read|DESCR: Complete file path');
SELECT _cm3_attribute_create('"_Upload"', 'FileName', 'varchar', 'NOTNULL: true|MODE: read|DESCR: File Name');
SELECT _cm3_attribute_create('"_Upload"', 'MimeType', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Mime Type');
SELECT _cm3_attribute_create('"_Upload"', 'Hash', 'varchar', 'NOTNULL: true|MODE: read|DESCR: File Hash');
SELECT _cm3_attribute_create('"_Upload"', 'Size', 'integer', 'NOTNULL: true|MODE: read|DESCR: File Size');
SELECT _cm3_attribute_create('"_Upload"', 'Content', 'bytea', 'NOTNULL: true|MODE: read|DESCR: File content');


SELECT _cm3_class_create('_FormTrigger', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Class Form Triggers|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_FormTrigger"', 'Owner', 'regclass', 'NOTNULL: true|MODE: read|DESCR: Owner class');
SELECT _cm3_attribute_create('"_FormTrigger"', 'Active', 'boolean', 'DEFAULT: true|NOTNULL: true|MODE: read|DESCR: Is Active');
SELECT _cm3_attribute_create('"_FormTrigger"', 'Index', 'integer', 'NOTNULL: true|MODE: read');
SELECT _cm3_attribute_create('"_FormTrigger"', 'Script', 'text', 'NOTNULL: true|MODE: read');
SELECT _cm3_attribute_create('"_FormTrigger"', 'Bindings', 'varchar[]', 'NOTNULL: true|MODE: read');
SELECT _cm3_attribute_index_create('"_FormTrigger"', 'Owner'); 


SELECT _cm3_class_create('_ContextMenu', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Class Context Menu items|SUPERCLASS: false'); -- sysread mode is useful to read table content via ws
SELECT _cm3_attribute_create('"_ContextMenu"', 'Owner', 'regclass', 'NOTNULL: true|MODE: read|DESCR: Owner class');
SELECT _cm3_attribute_create('"_ContextMenu"', 'Active', 'boolean', 'DEFAULT: true|NOTNULL: true|MODE: read|DESCR: Is Active');
SELECT _cm3_attribute_create('"_ContextMenu"', 'Index', 'integer', 'NOTNULL: true|MODE: read');
SELECT _cm3_attribute_create('"_ContextMenu"', 'Type', 'varchar', 'MODE: read');
SELECT _cm3_attribute_create('"_ContextMenu"', 'ComponentId', 'varchar', 'MODE: read');
SELECT _cm3_attribute_create('"_ContextMenu"', 'Script', 'text', 'MODE: read');
SELECT _cm3_attribute_create('"_ContextMenu"', 'Config', 'text', 'MODE: read');
SELECT _cm3_attribute_create('"_ContextMenu"', 'Visibility', 'varchar', 'MODE: read');
SELECT _cm3_attribute_index_create('"_ContextMenu"', 'Owner'); 


SELECT _cm3_class_create('_Temp', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: Temp file storage');
SELECT _cm3_attribute_create('"_Temp"', 'TimeToLive', 'bigint', 'NOTNULL: true|MODE: immutable|DESCR: Time to live (seconds)');
SELECT _cm3_attribute_create('"_Temp"', 'Data', 'bytea', 'NOTNULL: true|MODE: immutable|DESCR: Binary data');


--- MISC ---

ALTER TABLE "User" DROP COLUMN "Privileged";
CREATE UNIQUE INDEX "_cm3_User_DefaultGroup_custom" ON "Map_UserRole" ("IdClass1", "IdObj1", NULLIF("DefaultGroup", FALSE)) WHERE "Status" = 'A';

SELECT _cm3_attribute_create('"Activity"', 'FlowData', 'jsonb', 'DEFAULT: ''{}''::jsonb|MODE: rescore');


CREATE TABLE _domain_tree_nav_aux AS SELECT * FROM "_DomainTreeNavigation";

DROP TABLE "_DomainTreeNavigation";

SELECT _cm3_class_create('_DomainTreeNavigation', '"Class"', 'MODE: reserved|SUPERCLASS: false|TYPE: class');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'IdParent', 'integer', 'MODE: write');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'IdGroup', 'integer', 'MODE: write');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'Type', 'character varying', 'MODE: write');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'DomainName', 'character varying', 'MODE: write');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'Direct', 'boolean', 'MODE: write');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'BaseNode', 'boolean', 'MODE: write');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'TargetClassName', 'character varying', 'MODE: write');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'TargetClassDescription', 'character varying', 'MODE: write');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'TargetFilter', 'character varying', 'MODE: write');
SELECT _cm3_attribute_create('"_DomainTreeNavigation"', 'EnableRecursion', 'boolean', 'MODE: write');

COMMENT ON COLUMN "_DomainTreeNavigation"."Description" IS 'MODE: write';

ALTER TABLE "_DomainTreeNavigation" DISABLE TRIGGER USER;

INSERT INTO "_DomainTreeNavigation" ("Id","CurrentId","IdClass","User","BeginDate","IdParent","IdGroup","Type","DomainName","Direct","BaseNode","TargetClassName","TargetClassDescription","Description","TargetFilter","EnableRecursion","Status")
	SELECT "Id","Id",'"_DomainTreeNavigation"'::regclass,"User","BeginDate","IdParent","IdGroup","Type","DomainName","Direct","BaseNode","TargetClassName","TargetClassDescription","Description","TargetFilter","EnableRecursion",'A' 
	FROM _domain_tree_nav_aux;

ALTER TABLE "_DomainTreeNavigation" ENABLE TRIGGER USER;

DROP TABLE _domain_tree_nav_aux;


TRUNCATE TABLE "_CustomPage"; --TODO: migrate data
TRUNCATE TABLE "_CustomPage_history"; --TODO: migrate data
SELECT _cm3_attribute_create('"_CustomPage"', 'Data', 'bytea', 'NOTNULL: true|MODE: write');


DO $$ DECLARE
    _filter_old_oid int;
BEGIN

    CREATE TABLE _patch_aux_filter AS SELECT "Id","User","BeginDate","Code","Description","UserId","Filter","ClassId","Shared" FROM "_Filter";

    _filter_old_oid = '"_Filter"'::regclass::oid::int;
    DROP TABLE "_Filter";

    PERFORM _cm3_class_create('_Filter', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Filter|SUPERCLASS: false');
    PERFORM _cm3_attribute_create('"_Filter"', 'UserId', 'int', 'MODE: write');
    PERFORM _cm3_attribute_create('"_Filter"', 'Filter', 'jsonb', 'MODE: write');
    PERFORM _cm3_attribute_create('"_Filter"', 'ClassId', 'regclass', 'NOTNULL: true|MODE: write');
    PERFORM _cm3_attribute_create('"_Filter"', 'Shared', 'boolean', 'DEFAULT: false|NOTNULL: true|MODE: write');
    PERFORM _cm3_attribute_notnull_set('"_Filter"', 'Code', true); 
    PERFORM _cm3_attribute_index_unique_create('"_Filter"', 'Code', 'UserId', 'ClassId');

    PERFORM _cm3_class_triggers_disable('"_Filter"');

    INSERT INTO "_Filter" ("Id","CurrentId","IdClass","User","BeginDate","Code","Description","UserId","Filter","ClassId","Shared","Status")
        SELECT "Id","Id",'"_Filter"'::regclass,"User","BeginDate","Code","Description","UserId","Filter"::jsonb,"ClassId","Shared",'A' FROM _patch_aux_filter;

    PERFORM _cm3_class_triggers_enable('"_Filter"');

    PERFORM _cm3_class_triggers_disable('"Map_FilterRole"');

    UPDATE "Map_FilterRole" SET "IdClass1" = '"_Filter"'::regclass WHERE "IdClass1" = _filter_old_oid;

    PERFORM _cm3_class_triggers_enable('"Map_FilterRole"');

END $$ LANGUAGE PLPGSQL;

-- ROLE TABLE

SELECT _cm3_attribute_create('"Role"', 'Config', 'jsonb', 'MODE: hidden');

ALTER TABLE "Role" DISABLE TRIGGER USER;

DO $$
DECLARE
	config jsonb;
	rec RECORD;
BEGIN
	FOR rec IN SELECT * FROM "Role" 
	LOOP
		BEGIN
			config = '{}'::jsonb;

			IF rec."startingClass" IS NOT NULL THEN
				config = jsonb_set(config,'{startingClass}'::text[], ('"'||replace(rec."startingClass"::text,'"','')||'"')::jsonb);
			END IF;

			IF rec."DisabledModules" IS NOT NULL AND rec."DisabledModules"[1] IS NOT NULL THEN
				config = jsonb_set(config,'{disabledModules}'::text[], to_jsonb(rec."DisabledModules"));
			END IF;

			IF rec."DisabledCardTabs" IS NOT NULL AND rec."DisabledCardTabs"[1] IS NOT NULL THEN
				config = jsonb_set(config,'{disabledCardTabs}'::text[], to_jsonb(rec."DisabledCardTabs"));
			END IF;

			IF rec."DisabledProcessTabs" IS NOT NULL AND rec."DisabledProcessTabs"[1] IS NOT NULL THEN
				config = jsonb_set(config,'{disabledProcessTabs}'::text[], to_jsonb(rec."DisabledProcessTabs"));
			END IF;

			IF rec."ProcessWidgetAlwaysEnabled" IS NOT NULL THEN
				config = jsonb_set(config,'{processWidgetAlwaysEnabled}'::text[], rec."ProcessWidgetAlwaysEnabled"::varchar::jsonb);
			END IF;

			UPDATE "Role" SET "Config" = config WHERE "Id" = rec."Id";
		EXCEPTION WHEN others THEN
			RAISE EXCEPTION 'error processing Role record %: %', rec."Id", SQLERRM;
		END;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

UPDATE "Role" SET "Config" = '{}'::jsonb WHERE "Config" IS NULL;
UPDATE "Role" SET "Administrator" = FALSE WHERE "Administrator" IS NULL;

ALTER TABLE "Role" ALTER COLUMN "Config" SET DEFAULT '{}'::jsonb;
ALTER TABLE "Role" ALTER COLUMN "Config" SET NOT NULL;
ALTER TABLE "Role" ALTER COLUMN "Administrator" SET NOT NULL;

-- ALTER TABLE "Role" ALTER COLUMN "Config" DROP NOT NULL;
-- UPDATE "Role" SET 
-- 	"startingClass" = NULL,
-- 	"DisabledModules" = NULL,
-- 	"DisabledCardTabs" = NULL,
-- 	"DisabledProcessTabs" = NULL,
-- 	"ProcessWidgetAlwaysEnabled" = NULL,
-- 	"HideSidePanel" = NULL,
-- 	"FullScreenMode" = NULL,
-- 	"SimpleHistoryModeForCard" = NULL,
-- 	"SimpleHistoryModeForProcess" = NULL,
-- 	"CloudAdmin" = NULL;

ALTER TABLE "Role" ENABLE TRIGGER USER;

ALTER TABLE "Role" DROP COLUMN "startingClass";
ALTER TABLE "Role" DROP COLUMN "DisabledModules";
ALTER TABLE "Role" DROP COLUMN "DisabledCardTabs";
ALTER TABLE "Role" DROP COLUMN "DisabledProcessTabs";
ALTER TABLE "Role" DROP COLUMN "ProcessWidgetAlwaysEnabled";
ALTER TABLE "Role" DROP COLUMN "HideSidePanel";
ALTER TABLE "Role" DROP COLUMN "FullScreenMode";
ALTER TABLE "Role" DROP COLUMN "SimpleHistoryModeForCard";
ALTER TABLE "Role" DROP COLUMN "SimpleHistoryModeForProcess";
ALTER TABLE "Role" DROP COLUMN "CloudAdmin"; 

SELECT _cm3_attribute_create('"Role"', 'Type', 'varchar', 'MODE: write|DESCR: Role Type');
SELECT _cm3_attribute_create('"Role"', 'Permissions', 'jsonb', 'DEFAULT: ''{}''::jsonb|MODE: write|DESCR: Role Permissions');

ALTER TABLE "Role" DISABLE TRIGGER USER;

UPDATE "Role" SET "Type" = 'admin' WHERE "Administrator" = true AND "Status" = 'A';
UPDATE "Role" SET "Type" = 'default' WHERE "Type" IS NULL AND "Status" = 'A';
UPDATE "Role" SET "Permissions" = '{}'::jsonb WHERE "Permissions" IS NULL;

ALTER TABLE "Role" DROP COLUMN "Administrator";

ALTER TABLE "Role" ENABLE TRIGGER USER;

SELECT _cm3_attribute_notnull_set('"Role"', 'Type', true);
SELECT _cm3_attribute_notnull_set('"Role"', 'Permissions', true);

DO $$ DECLARE
	_role record;
	_perms jsonb;
	_key varchar;
BEGIN
	ALTER TABLE "Role" DISABLE TRIGGER USER;

	FOR _role IN SELECT * FROM "Role" LOOP
		_perms = _role."Permissions";
		FOR _key IN SELECT jsonb_array_elements_text(_role."Config"->'disabledModules') LOOP
			_perms = jsonb_set(_perms, ARRAY[format('%s_access', lower(_key))], to_jsonb(false));
		END LOOP;
		FOR _key IN SELECT jsonb_array_elements_text(_role."Config"->'disabledCardTabs') LOOP
			_perms = jsonb_set(_perms, ARRAY[format('card_tab_%s_access', regexp_replace(lower(_key),'(^class)|(tab$)','','g'))], to_jsonb(false));
		END LOOP;
		FOR _key IN SELECT jsonb_array_elements_text(_role."Config"->'disabledProcessTabs') LOOP
			_perms = jsonb_set(_perms, ARRAY[format('flow_tab_%s_access', regexp_replace(lower(_key),'(^process)|(tab$)','','g'))], to_jsonb(false));
		END LOOP;

		UPDATE "Role" SET "Permissions" = _perms, "Config" = "Config" - 'disabledModules' - 'disabledCardTabs' - 'disabledProcessTabs' WHERE "Id" = _role."Id";
	END LOOP;
	
	ALTER TABLE "Role" ENABLE TRIGGER USER;
END $$ LANGUAGE PLPGSQL;
 

-- DisabledModules             | {bulkUpdate,changePassword,class,customPages,dashboard,dataView,exportCsv,importCsv,process,report}
-- DisabledCardTabs            | {classAttachmentTab,classDetailTab,classEmailTab,classHistoryTab,classNoteTab,classRelationTab}
-- DisabledProcessTabs         | {processAttachmentTab,processEmailTab,processHistoryTab,processNoteTab,processRelationTab}
-- -- 
-- UPDATE "Role" SET "DisabledProcessTabs" = '{processAttachmentTab,processEmailTab,processHistoryTab,processNoteTab,processRelationTab}' WHERE "Id" = 3893;
-- UPDATE "Role" SET "DisabledCardTabs" = '{classAttachmentTab,classDetailTab,classEmailTab,classHistoryTab,classNoteTab,classRelationTab}' WHERE "Id" = 3893;
-- UPDATE "Role" SET "DisabledModules" = '{bulkUpdate,changePassword,class,customPages,dashboard,dataView,exportCsv,importCsv,process,report}' WHERE "Id" = 3893;
-- 

-- JOBS TABLE --

SELECT _cm3_class_create('_Job', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Scheduled Jobs');
SELECT _cm3_attribute_create('"_Job"', 'CronExpression', 'varchar', '');
SELECT _cm3_attribute_create('"_Job"', 'Type', 'varchar', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_Job"', 'Enabled', 'boolean', 'DEFAULT: false|NOTNULL: true');
SELECT _cm3_attribute_create('"_Job"', 'Config', 'jsonb', 'DEFAULT: ''{}''::jsonb|NOTNULL: true');
--TODO set code unique, notnull

SELECT _cm3_class_create('_JobRun', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: Scheduled Jobs Execution Info');
SELECT _cm3_attribute_create('"_JobRun"', 'Job', 'varchar', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_JobRun"', 'JobStatus', 'varchar', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_JobRun"', 'Timestamp', 'timestamp', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_JobRun"', 'Completed', 'boolean', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_JobRun"', 'ElapsedTime', 'integer', 'MODE: write');
SELECT _cm3_attribute_create('"_JobRun"', 'Errors', 'jsonb', '');
ALTER TABLE "_JobRun" ADD CONSTRAINT "_cm3_JobStatus_check" CHECK ( "JobStatus" IN ('running','completed','failed') );

SELECT _cm3_class_triggers_disable('"_Job"');

INSERT INTO "_Job" (
		"Id",
		"CurrentId",
		"IdClass",
		"Code",
		"Description",
		"BeginDate",
		"Status",
		"CronExpression",
		"Type",
		"Enabled",
		"Config"
	) SELECT 
		"Id",
		"Id",
		'"_Job"',
		"Description",
		"Description",
		"BeginDate",
		'A',
		"CronExpression",
		"Type",
		"Running",
		(SELECT COALESCE(jsonb_object_agg("Key","Value"),'{}'::jsonb) from "_TaskParameter" where "Owner" = "_Task"."Id")
	FROM "_Task" WHERE "Status" = 'A';

SELECT _cm3_class_triggers_enable('"_Job"');

DROP TABLE "_TaskRuntime";
DROP TABLE "_TaskParameter_history";
DROP TABLE "_TaskParameter";
DROP TABLE "_Task_history";
DROP TABLE "_Task";


SELECT _cm3_class_triggers_disable('"_Job"');

UPDATE "_Job" SET "Config" = "Config" || jsonb_build_object('action.cronExpression', "CronExpression"), "CronExpression" = NULL WHERE NULLIF("CronExpression",'') IS NOT NULL;
SELECT _cm3_attribute_delete('"_Job"', 'CronExpression');

SELECT _cm3_class_triggers_enable('"_Job"');

-- EMAIL TABLES


CREATE TABLE _patch_aux (card_id bigint, email_status varchar);

INSERT INTO _patch_aux (card_id, email_status) SELECT e."Id",lower(l."Code") FROM "Email" e LEFT JOIN "LookUp" l ON e."EmailStatus" = l."Id" WHERE l."Status" = 'A';

UPDATE _patch_aux SET email_status = 'draft' WHERE NULLIF(email_status,'') IS NULL OR email_status = 'new';
UPDATE _patch_aux SET email_status = 'error' WHERE email_status NOT IN ('received','sent','outgoing','error','draft');

SELECT _cm3_class_triggers_disable('"Email"');

ALTER TABLE "Email" DROP COLUMN "EmailStatus";
SELECT _cm3_attribute_create('"Email"', 'EmailStatus', 'varchar', 'NOTNULL: true|DEFAULT: draft|MODE: write|DESCR: Email Status|INDEX: 5|BASEDSP: true');
UPDATE "Email" SET "EmailStatus" = email_status FROM _patch_aux WHERE card_id = "Id";

ALTER TABLE "Email" ADD CONSTRAINT "_cm3_EmailStatus_check" CHECK ( "EmailStatus" IN ('received','sent','outgoing','error','draft') );

SELECT _cm3_attribute_create('"Email"', 'ErrorCount', 'integer', 'NOTNULL: true|DEFAULT: 0|MODE: write|DESCR: Error Count');

SELECT _cm3_class_triggers_enable('"Email"');

DROP TABLE _patch_aux;

SELECT _cm3_attribute_create('"_EmailTemplate"', 'Data', 'jsonb', 'DEFAULT: ''{}''::jsonb|MODE: write|DESCR: Template Context Data');

