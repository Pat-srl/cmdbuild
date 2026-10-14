-- attribute structure upgrade

DO $$ DECLARE
	_class regclass;
	_attrs varchar[];
	_query text;
BEGIN
	FOR _class, _attrs IN WITH q AS (SELECT a.classe, a.attr FROM (SELECT c.x classe, _cm3_attribute_list(c.x) attr FROM (SELECT x FROM _cm3_class_list() x UNION SELECT x FROM _cm3_domain_list() x) c) a
            WHERE ( _cm3_attribute_is_reference(a.classe, a.attr) OR _cm3_attribute_is_lookup(a.classe, a.attr) OR _cm3_attribute_is_foreignkey(a.classe, a.attr) ) AND NOT _cm3_attribute_is_inherited(a.classe, a.attr) )
        SELECT classe, array_agg(attr) FROM q GROUP BY classe
	LOOP
		RAISE NOTICE 'upgrade class = % attrs = % (change type to bigint)', _cm3_utils_regclass_to_name(_class), _attrs;
		_query = _cm3_utils_prepare_class_triggers_query(_class);
		PERFORM _cm3_utils_drop_class_triggers(_class);
        PERFORM _cm3_utils_store_and_drop_dependant_views(_class);
		EXECUTE format('ALTER TABLE %s %s', _class, (SELECT string_agg(format('ALTER COLUMN %I TYPE bigint', attr), ', ') FROM unnest(_attrs) attr));
        PERFORM _cm3_utils_restore_dependant_views();
		EXECUTE _query;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

