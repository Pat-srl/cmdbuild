-- Updating not null domain attribute restriction behavior

CREATE OR REPLACE FUNCTION _temp_attribute_notnull_get(_class regclass, _attr varchar) RETURNS boolean AS $$ DECLARE
	_is_notnull boolean;
BEGIN
    SELECT pg_attribute.attnotnull OR c.oid IS NOT NULL
    FROM pg_attribute
    LEFT JOIN pg_constraint AS c ON c.conrelid = pg_attribute.attrelid AND c.conname::text = format('_cm3_%s_notnull', pg_attribute.attname)
    WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr INTO _is_notnull;
    RETURN _is_notnull;
END $$ LANGUAGE PLPGSQL; 

DO $$ DECLARE
    _domain regclass;
    _attribute varchar;
BEGIN
    FOR _domain IN SELECT _cm3_domain_list() LOOP
        FOR _attribute IN SELECT _cm3_attribute_list(_domain) LOOP
		IF _temp_attribute_notnull_get(_domain, _attribute) AND _attribute NOT IN (SELECT _cm3_utils_domain_reserved_attributes()) THEN
			RAISE NOTICE 'not null attribute % for dom %', _attribute, _domain;
			EXECUTE format('ALTER TABLE %s ALTER COLUMN "%s" DROP NOT NULL', _domain, _attribute);
			EXECUTE format('DROP TRIGGER IF EXISTS _cm3_%s_notnull_trigger ON public.%s', _attribute, _domain);
                        EXECUTE format('CREATE TRIGGER _cm3_%s_notnull_trigger
					AFTER INSERT OR UPDATE
					ON public.%s
					FOR EACH ROW
					EXECUTE PROCEDURE _cm3_domain_attribute_notnull(''%s'')', _attribute, _domain, _attribute);
		END IF;
        END LOOP;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP FUNCTION IF EXISTS _temp_attribute_notnull_get(regclass, varchar);