-- Relation between e-mails and cards

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	RAISE INFO 'creating domain ClassEmail';
	PERFORM cm_create_domain('ClassEmail', 'MODE: reserved|TYPE: domain|CLASS1: Class|CLASS2: Email|DESCRDIR: |DESCRINV: |CARDIN: 1:N|STATUS: active');

	RAISE INFO 'creating attribute Card for class Email';
	PERFORM cm_create_class_attribute('Email', 'Card', 'integer', null, false, false, 'MODE: read|FIELDMODE: write|DESCR: Card|INDEX: 4|REFERENCEDOM: ClassEmail|REFERENCEDIRECT: false|REFERENCETYPE: restrict|STATUS: active');

	RAISE INFO 'copying old reference values into new one';
	ALTER TABLE "Email" DISABLE TRIGGER USER;
	UPDATE "Email" SET "Card" = "Activity";
	ALTER TABLE "Email" ENABLE TRIGGER USER;

	RAISE INFO 'copying old relations into new domain';
	ALTER TABLE "Map_ClassEmail" DISABLE TRIGGER USER;
	INSERT INTO "Map_ClassEmail"("IdDomain", "IdClass1", "IdObj1", "IdClass2", "IdObj2", "Status", "User", "BeginDate", "EndDate", "Id")
		SELECT '"Map_ClassEmail"'::regclass, "IdClass1", "IdObj1", "IdClass2", "IdObj2", "Status",  "User", "BeginDate", "EndDate", "Id"
			FROM ONLY "Map_ActivityEmail";
	INSERT INTO "Map_ClassEmail_history"("IdDomain", "IdClass1", "IdObj1", "IdClass2", "IdObj2", "Status", "User", "BeginDate", "EndDate", "Id")
		SELECT '"Map_ClassEmail"'::regclass, "IdClass1", "IdObj1", "IdClass2", "IdObj2", "Status",  "User", "BeginDate", "EndDate", "Id"
			FROM "Map_ActivityEmail_history";
	ALTER TABLE "Map_ClassEmail" ENABLE TRIGGER USER;

	RAISE INFO 'deleting old reference attribute';
	ALTER TABLE "Email" DISABLE TRIGGER USER;
	UPDATE "Email" SET "Activity" = null;
	ALTER TABLE "Email" ENABLE TRIGGER USER;
	PERFORM cm_delete_class_attribute('Email', 'Activity');

	RAISE INFO 'deleting old domain';
	ALTER TABLE "Map_ActivityEmail" DISABLE TRIGGER USER;
	TRUNCATE TABLE "Map_ActivityEmail", "Map_ActivityEmail_history";
	ALTER TABLE "Map_ActivityEmail" ENABLE TRIGGER USER;
	PERFORM cm_delete_domain('ActivityEmail');
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
