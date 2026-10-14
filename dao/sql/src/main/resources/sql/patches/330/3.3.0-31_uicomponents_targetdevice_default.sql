-- removing "any" value and changing default

DO $$ DECLARE
    _record record;
BEGIN    
    FOR _record IN SELECT * FROM "_UiComponent" LOOP 
        IF _record."TargetDevice" = 'any' THEN
            UPDATE "_UiComponent" SET "TargetDevice" = 'default' WHERE "Id" = _record."Id" AND "Status"='A';
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_attribute_modify('OWNER: _UiComponent|NAME: TargetDevice|NOTNULL: TRUE|TYPE: varchar|VALUES: default,mobile|DEFAULT: default');
