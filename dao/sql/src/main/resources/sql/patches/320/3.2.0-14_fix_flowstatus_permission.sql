--- fix flowstatus permission

DO $$ DECLARE
    _class regclass;
    _record record;
BEGIN
    FOR _class IN SELECT x FROM _cm3_class_list_descendant_classes_and_self('"Activity"'::regclass) x LOOP
        IF _cm3_attribute_features_get(_class,'FlowStatus','MODE') = 'rescore' THEN 
            PERFORM _cm3_attribute_features_set(_class,'FlowStatus','MODE','syshidden');
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

