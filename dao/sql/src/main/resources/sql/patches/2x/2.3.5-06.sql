-- Updates the table "_TaskParameter" handling the new parameters for e-mail tasks 

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
DECLARE
	entry record;
BEGIN
	UPDATE "_TaskParameter"
		SET "Key" = regexp_replace("Key", 'filter.(\w+).regex', 'filter.regex.\1', 'g')
		WHERE "Key" like 'filter.%.regex' AND "Id" IN (
			SELECT  p."Id"
				FROM "_TaskParameter" AS p
				JOIN "_Task" AS t ON t."Id" = p."Owner" and t."Type" = 'emailService'
				where p."Status" = 'A'
			);

	FOR entry IN (
		SELECT * FROM "_Task" WHERE "Status" = 'A' AND "Type" = 'emailService'
	) LOOP
		INSERT INTO "_TaskParameter" ("Owner", "Key", "Value") VALUES (entry."Id", 'filter.type', 'regex');
	END LOOP;
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION apply_patch();