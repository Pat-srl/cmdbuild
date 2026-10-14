--- fix notes permission

DO $$ DECLARE
    _class regclass;
BEGIN
    FOR _class IN SELECT x FROM _cm3_class_list_standard() x LOOP
        IF _cm3_attribute_features_get(_class,'Notes','MODE') = 'write' THEN 
            PERFORM _cm3_attribute_features_set(_class,'Notes','MODE','protected');
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

