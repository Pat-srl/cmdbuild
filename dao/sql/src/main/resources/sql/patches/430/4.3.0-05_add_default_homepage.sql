-- Add Default Homepage

DO $$ BEGIN

    IF NOT EXISTS (SELECT 1 FROM "_Homepage" WHERE "Code" = 'default') THEN
        INSERT INTO "_Homepage" (
                                 "Code",
                                 "Description",
                                 "Active",
                                 "Status",
                                 "GroupName",
                                 "Config")
        VALUES (
                'default',
                'Default Homepage',
                true,
                'A',
                '_default',
                '{
                    "layout": {
                        "rows": []
                    },
                    "components" :[]
                }'
               );
    END IF;
END $$;