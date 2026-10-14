--- fix idclass permission

DO $$ DECLARE
    _class regclass;
BEGIN
    FOR _class IN SELECT x FROM _cm3_class_list() x UNION SELECT '"SimpleClass"'::regclass LOOP
        IF _cm3_attribute_features_get(_class,'IdClass','MODE') = 'reserved' THEN 
            PERFORM _cm3_attribute_features_set(_class,'IdClass','MODE','syshidden');
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

