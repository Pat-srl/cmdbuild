-- Creates the domain between "_Filter" and "Role" classes

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	RAISE INFO 'creating the "FilterRole" domain';
	PERFORM cm_create_domain('FilterRole', 'MODE: reserved|TYPE: domain|CLASS1: _Filter|CLASS2: Role|DESCRDIR: |DESCRINV: |CARDIN: N:N|STATUS: active');
	
	RAISE INFO 'updating the name of some columns of "_Filter" table';
	ALTER TABLE "_Filter" RENAME COLUMN "IdOwner" TO "UserId";
	ALTER TABLE "_Filter" RENAME COLUMN "IdSourceClass" TO "ClassId";
	ALTER TABLE "_Filter" RENAME COLUMN "Template" TO "Shared";
	
	ALTER TABLE "_Filter" DROP CONSTRAINT filter_name_table_unique;
	ALTER TABLE "_Filter" ADD CONSTRAINT filter_name_table_unique UNIQUE ("Code", "UserId", "ClassId");
	
	PERFORM _cm_set_attribute_comment('"_Filter"'::regclass::oid, 'Code',        'MODE: write|STATUS: active');
	PERFORM _cm_set_attribute_comment('"_Filter"'::regclass::oid, 'Description', 'MODE: write|STATUS: active');
	PERFORM _cm_set_attribute_comment('"_Filter"'::regclass::oid, 'UserId',      'MODE: write|STATUS: active');
	PERFORM _cm_set_attribute_comment('"_Filter"'::regclass::oid, 'Filter',      'MODE: write|STATUS: active');
	PERFORM _cm_set_attribute_comment('"_Filter"'::regclass::oid, 'ClassId',     'MODE: write|STATUS: active');
	PERFORM _cm_set_attribute_comment('"_Filter"'::regclass::oid, 'Shared',      'MODE: write|STATUS: active');

	UPDATE "_Filter" SET "UserId" = 0 WHERE "UserId" IS NULL;	
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
