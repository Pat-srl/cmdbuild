-- translation helper functions
-- REQUIRE PATCH 3.0.0-17_lookups

CREATE OR REPLACE FUNCTION _cm3_translation_get(_key varchar, _lang varchar, _default varchar) RETURNS varchar AS $$ DECLARE
    _value VARCHAR;
BEGIN
    SELECT "Value" FROM "_Translation" WHERE "Code" = _key AND "Lang" = _lang AND "Status" = 'A' INTO _value;
    IF _value IS NULL AND _lang <> 'default' THEN
        SELECT "Value" FROM "_Translation" WHERE "Code" = _key AND "Lang" = 'default' AND "Status" = 'A' INTO _value;
    END IF;
    RETURN COALESCE(_value, _default);
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_translation_get(_key varchar, _lang varchar) RETURNS varchar AS $$ 
    SELECT "Value" FROM "_Translation" WHERE "Code" = _key AND "Lang" = _lang AND "Status" = 'A';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_translation_lookup_get(_id bigint, _lang varchar) RETURNS varchar AS $$ DECLARE
    _type varchar;
    _code varchar;
    _desc varchar;
BEGIN
    SELECT "Type", "Code", "Description" INTO _type, _code, _desc FROM "LookUp" WHERE "Id" = _id AND "Status" = 'A';
    RETURN _cm3_translation_get(format('lookup.%s.%s.description', _type, _code), _lang, _desc);
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_translation_class_get(_class regclass, _lang varchar) RETURNS varchar AS $$ BEGIN 
    RETURN _cm3_translation_get(format('class.%s.description', _cm3_utils_regclass_to_name(_class)), _lang, _cm3_class_description_get(_class));
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_translation_attribute_get(_class regclass, _attribute varchar, _lang varchar) RETURNS varchar AS $$ DECLARE
    _descr varchar;
    _ancestors regclass[];
    _ancestor regclass;
    _translation varchar;
BEGIN
	_ancestors := ARRAY(SELECT * FROM _cm3_class_list_ancestors(_class));
	_ancestors = _cm3_utils_array_reverse(_ancestors);
	_descr = _cm3_attribute_features_get(_class, _attribute, 'DESCR');
	_translation = _cm3_translation_get(format('attributeclass.%s.%s.description', _cm3_utils_regclass_to_name(_class), _attribute), _lang);
	IF _translation IS NOT NULL THEN
		RETURN _translation;
	ELSE
		FOREACH _ancestor IN ARRAY _ancestors LOOP
			_translation = _cm3_translation_get(format('attributeclass.%s.%s.description', _cm3_utils_regclass_to_name(_ancestor), _attribute), _lang);
			IF _translation IS NOT NULL THEN
				RETURN _translation;
			END IF;
		END LOOP;
		RETURN _descr;
	END IF;
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_translation_class_get(_class regclass) RETURNS varchar AS $$ BEGIN
    RETURN _cm3_translation_class_get(_class, _cm3_utils_lang_get());
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_translation_lookup_get(_id bigint) RETURNS varchar AS $$ BEGIN
    RETURN _cm3_translation_lookup_get(_id, _cm3_utils_lang_get());
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_translation_attribute_get(_class regclass, _attribute varchar) RETURNS varchar AS $$ BEGIN
    RETURN _cm3_translation_attribute_get(_class, _attribute, _cm3_utils_lang_get());
END $$ LANGUAGE PLPGSQL STABLE;
