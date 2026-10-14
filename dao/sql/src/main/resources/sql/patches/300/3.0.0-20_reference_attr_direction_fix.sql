-- reference attr direction fix

DO $$ DECLARE
	_class regclass;
	_attr varchar;
	_domain regclass;
	_dir varchar;
BEGIN
	FOR _class, _attr IN SELECT owner, name FROM _cm3_attribute_list() WHERE _cm3_attribute_is_reference(owner, name) LOOP
		_domain = _cm3_attribute_reference_domain_get(_class, _attr);
		IF _cm3_class_comment_get(_domain, 'CARDIN') ILIKE '1:N' THEN
			_dir = 'inverse';
		ELSEIF _cm3_class_comment_get(_domain, 'CARDIN') ILIKE 'N:1' THEN
			_dir = 'direct';
		ELSE
			RAISE 'unsupported cardin for domain = % with reference', _domain;
		END IF;
		PERFORM _cm3_attribute_comment_set(_class, _attr, 'REFERENCEDIR', _dir);
		PERFORM _cm3_attribute_comment_delete(_class, _attr, 'REFERENCEDIRECT');
		PERFORM _cm3_attribute_comment_delete(_class, _attr, 'REFERENCETYPE');
	END LOOP;
END $$ LANGUAGE PLPGSQL;

