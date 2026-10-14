-- migrate widget table

CREATE TABLE _patch_aux ("Code" varchar,"Definition" varchar,"Description" varchar);
INSERT INTO _patch_aux ("Code","Definition","Description") SELECT "Code","Definition","Description" FROM "_Widget" WHERE "Status" = 'A';

DROP TABLE "_Widget_history";
DROP TABLE "_Widget";

SELECT _cm3_class_create('_Widget', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Widget|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Widget"', 'Owner', 'regclass', 'NOTNULL: true|MODE: read|DESCR: Owner class');
SELECT _cm3_attribute_create('"_Widget"', 'Active', 'boolean', 'DEFAULT: true|NOTNULL: true|MODE: read|DESCR: Is Active');
SELECT _cm3_attribute_create('"_Widget"', 'Type', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Type');
SELECT _cm3_attribute_create('"_Widget"', 'Data', 'jsonb', 'NOTNULL: true|MODE: read|DESCR: Data');
SELECT _cm3_attribute_index_create('"_Widget"', 'Owner'); 

INSERT INTO "_Widget" (
	"IdClass",
	"Owner",
	"Description",
	"Code",
	"Type",
	"Data",
	"Active"
) SELECT 
	'"_Widget"'::regclass,
	_cm3_utils_name_to_regclass("Code"),
	"Definition"::jsonb->>'label',
	"Definition"::jsonb->>'id',
	lower(substring("Description" from 2)),
	"Definition"::jsonb,
	coalesce("Definition"::jsonb->>'active','true')::boolean
FROM _patch_aux;

DROP TABLE _patch_aux;

SELECT _cm3_class_triggers_disable('"_Widget"'); 

UPDATE "_Widget" SET "Code" = 'widget_'||"Id" WHERE "Code" IS NULL;

SELECT _cm3_class_triggers_enable('"_Widget"'); 

SELECT _cm3_attribute_notnull_set('"_Widget"', 'Code', true);
SELECT _cm3_attribute_index_unique_create('"_Widget"', 'Owner', 'Code');

--- WIDGET DATA UPGRADE ---

SELECT _cm3_class_triggers_disable('"_Widget"'); 

DO $$ DECLARE
	_record record;
	_data jsonb;
	_key varchar;
	_type varchar;
BEGIN
	FOR _record IN SELECT * FROM "_Widget" WHERE "Status" = 'A' LOOP
		_data = _record."Data";
		_type = _record."Type";
		IF _type = 'openreport' THEN
			_type = 'createReport';
			_data = _data - 'reportCode' || jsonb_build_object('ReportCode', _data->>'reportCode');
			_data = _data - 'alwaysenabled' || jsonb_build_object('AlwaysEnabled', _data->>'alwaysenabled'); --TODO check this
			_data = _data - 'readOnlyAttributes' || jsonb_build_object('ReadOnlyAttributes', (SELECT coalesce(string_agg(a,','),'') FROM (SELECT jsonb_array_elements_text(coalesce(_data->'readOnlyAttributes','[]'::jsonb)) a) x));
			IF NULLIF(_data->>'forceFormat','') IS NOT NULL THEN
				IF _data->>'forceFormat' ILIKE 'pdf' THEN
					_data = _data || jsonb_build_object('ForcePDF', 'true');
				ELSEIF _data->>'forceFormat' ILIKE 'csv' THEN				
					_data = _data || jsonb_build_object('ForceCSV', 'true');
				ELSE
					RAISE WARNING 'unsupported report format = % (will remove config param)', _data->>'forceFormat';
				END IF;
			END IF;
			_data = _data - 'forceFormat';
			FOR _key IN SELECT jsonb_object_keys(coalesce(_data->'preset','{}'::jsonb)) LOOP
				_data = _data || jsonb_build_object(_key, _data->'preset'->>_key);
			END LOOP;
			_data = _data - 'preset' - 'type' - 'active' - 'label' - 'id';
		END IF;
		UPDATE "_Widget" SET "Data" = _data, "Type" = _type WHERE "Id" = _record."Id" AND "Status" = 'A';
	END LOOP;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_Widget"'); 

