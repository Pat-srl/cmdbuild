-- move default email account to system config

DO $$ BEGIN
    IF EXISTS (SELECT * FROM "_EmailAccount" WHERE "Status" = 'A' AND "IsDefault" = TRUE) THEN
        INSERT INTO "_SystemConfig" ("Code","Value") VALUES ( 'org.cmdbuild.email.accountDefault', (SELECT "Code" FROM "_EmailAccount" WHERE "Status" = 'A' AND "IsDefault" = TRUE) );
    END IF;
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "_EmailAccount" DROP COLUMN "IsDefault";
