-- email meta
-- PARAMS: FORCE_IF_NOT_EXISTS=true

DO $$ BEGIN
    IF NOT (SELECT EXISTS (SELECT * FROM _cm3_attribute_list() WHERE owner = '"Email"'::regclass AND name = 'Meta')) THEN
        PERFORM _cm3_attribute_create('OWNER: Email|NAME: Meta|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb'); 
    END IF;
END $$;
