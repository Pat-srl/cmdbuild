-- create `workflow` user if not exists

DO $$ BEGIN
    IF NOT EXISTS (SELECT * FROM "User" WHERE "Username" = 'workflow') THEN
        BEGIN
            INSERT INTO "User" ("Username", "Description", "Service") VALUES ('workflow', 'workflow', TRUE);
            INSERT INTO "Map_UserRole" ("IdObj1", "IdObj2") VALUES ((SELECT "Id" FROM "User" WHERE "Username" = 'workflow' AND "Status" = 'A'), COALESCE (
                (SELECT "Id" FROM "Role" WHERE "Type" = 'admin' AND "Status" = 'A' AND "Code" ~* 'superuser' ORDER BY "BeginDate" ASC LIMIT 1),
                (SELECT "Id" FROM "Role" WHERE "Type" = 'admin' AND "Status" = 'A' ORDER BY "BeginDate" ASC LIMIT 1)));
        EXCEPTION WHEN OTHERS THEN
            RAISE WARNING 'unable to create `workflow` system user: %', SQLERRM;
        END;
    END IF;
END $$ LANGUAGE PLPGSQL;

