-- report upgrade

SELECT _cm3_class_create('_Report', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Report|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Report"', 'Active', 'boolean', 'DEFAULT: true|NOTNULL: true|MODE: read|DESCR: Is Active');
SELECT _cm3_attribute_create('"_Report"', 'Query', 'text', 'MODE: read|DESCR: Query');
SELECT _cm3_attribute_create('"_Report"', 'MainReport', 'bytea', 'NOTNULL: true|MODE: rescore');
SELECT _cm3_attribute_create('"_Report"', 'SubReports', 'bytea[]', 'NOTNULL: true|MODE: rescore');
SELECT _cm3_attribute_create('"_Report"', 'ImageNames', 'varchar[]', 'NOTNULL: true|MODE: rescore');
SELECT _cm3_attribute_create('"_Report"', 'Images', 'bytea[]', 'NOTNULL: true|MODE: rescore');
SELECT _cm3_attribute_notnull_set('"_Report"', 'Code', true);
SELECT _cm3_attribute_index_unique_create('"_Report"', 'Code');

CREATE OR REPLACE FUNCTION _patch_30039_aux(sourcebytes IN bytea,length_array IN integer[]) RETURNS bytea[] AS $$ DECLARE
	len integer;
	res bytea[];
	start_index integer;
BEGIN
	res = array[]::bytea[];
	start_index = 1;
	FOREACH len IN ARRAY length_array LOOP
		res = array_append(res, substring(sourcebytes FROM start_index FOR len));
		start_index = start_index + len;
	END LOOP;
	RETURN res;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_disable('"_Report"');

WITH _old AS ( SELECT *, _patch_30039_aux("RichReport","ReportLength") _report_array, 
	CASE WHEN "Images" IS NULL OR "ImagesLength" IS NULL THEN '{}'::bytea[] ELSE _patch_30039_aux("Images","ImagesLength") END _images_array,
	COALESCE("ImagesName", '{}'::varchar[]) _image_names_array
	FROM "Report" WHERE "Status" = 'A' AND "RichReport" IS NOT NULL AND "ReportLength" IS NOT NULL AND cardinality("ReportLength") > 0
) INSERT INTO "_Report" (
	"Id",
	"CurrentId",
	"IdClass",
	"Code",
	"Description",
	"Status",
	"User",
	"BeginDate",
	"Query",
	"Active",
	"MainReport",
	"SubReports",
	"ImageNames",
	"Images"
) SELECT 
	"Id",
	"Id",
	'"_Report"'::regclass,
	"Code",
	"Description",
	'A',
	"User",
	"BeginDate",
	"Query",
	true,
	_report_array[1],
	_report_array[2:array_length(_report_array, 1)],
	_image_names_array,
	_images_array
FROM _old;

UPDATE "Menu" SET "IdElementClass" = '"_Report"'::regclass where "IdElementClass" = '"Report"'::regclass AND "Status" = 'A';

INSERT INTO "_Grant" ("IdRole","Mode","Type","ObjectId") 
	SELECT (SELECT "Id" FROM "Role" WHERE "Code" = r.role_name AND "Status" = 'A'),'r','Report',r.report_id FROM --TODO handle case where role is not found for name
		(SELECT unnest("Groups") role_name, "Id" report_id FROM "Report" WHERE "Status" = 'A' ) r WHERE EXISTS (SELECT "Id" FROM "Role" WHERE "Code" = r.role_name AND "Status" = 'A');

SELECT _cm3_class_triggers_enable('"_Report"');
DROP FUNCTION  _patch_30039_aux(sourcebytes IN bytea,length_array IN integer[]);
DROP TABLE "Report";
