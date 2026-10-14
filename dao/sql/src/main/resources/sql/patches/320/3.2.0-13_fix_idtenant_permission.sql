--- fix idtenant permission

DO $$ DECLARE
    _class regclass;
BEGIN
    FOR _class IN SELECT x FROM _cm3_class_list_standard() x LOOP
        IF _cm3_attribute_features_get(_class,'IdTenant','MODE') = 'rescore' THEN 
            PERFORM _cm3_attribute_features_set(_class,'IdTenant','MODE','syshidden');
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

