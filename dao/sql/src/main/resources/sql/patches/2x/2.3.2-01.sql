-- Moves parameters from "_EmailAccount" table to "_TaskParameter" table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
DECLARE
	entry record;
BEGIN
	RAISE NOTICE 'moving values';
	FOR entry IN (
		SELECT
				p."Value" AS name,
				p."Owner" AS owner,
				a."InputFolder" AS incoming,
				a."ProcessedFolder" AS processed,
				a."RejectedFolder" AS rejected,
				CASE
					WHEN a."RejectNotMatching" IS TRUE THEN 'true'
					ELSE 'false'
				END AS reject
			FROM "_TaskParameter" AS p
			JOIN "_Task" AS t ON t."Id" = p."Owner" AND t."Status" = 'A'
			JOIN "_EmailAccount" AS a ON a."Code" = p."Value" AND a."Status" = 'A'
			WHERE p."Status" = 'A' AND p."Key" = 'account.name' AND t."Type" = 'emailService'
	) LOOP
		RAISE DEBUG 'inserting entry ''%''', entry;
		INSERT INTO "_TaskParameter"
			("Owner", "Key", "Value") VALUES
			(entry.owner, 'folder.incoming', entry.incoming),
			(entry.owner, 'folder.processed', entry.processed),
			(entry.owner, 'folder.rejected', entry.rejected),
			(entry.owner, 'filter.reject', entry.reject);
	END LOOP;

	RAISE NOTICE 'deleting columns';
	ALTER TABLE "_EmailAccount" DISABLE TRIGGER USER;
	UPDATE "_EmailAccount" SET
		"InputFolder" = NULL,
		"ProcessedFolder" = NULL,
		"RejectedFolder" = NULL,
		"RejectNotMatching" = NULL;
	ALTER TABLE "_EmailAccount" ENABLE TRIGGER USER;
	PERFORM cm_delete_class_attribute('_EmailAccount', 'InputFolder');
	PERFORM cm_delete_class_attribute('_EmailAccount', 'ProcessedFolder');
	PERFORM cm_delete_class_attribute('_EmailAccount', 'RejectedFolder');
	PERFORM cm_delete_class_attribute('_EmailAccount', 'RejectNotMatching');
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
