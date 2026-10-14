-- domain inline, default closed source/target patch
 
DO $$ DECLARE
    _domain regclass;
    _cardin varchar;
BEGIN
    FOR _domain IN SELECT * FROM _cm3_domain_list() LOOP
        _cardin = _cm3_domain_cardin_get(_domain);
        IF _cardin ILIKE '%:N' THEN
            PERFORM _cm3_class_features_set(_domain, 'cm_show_inline_1', _cm3_class_features_get(_domain, 'cm_show_inline'));
            PERFORM _cm3_class_features_set(_domain, 'cm_show_inline_default_closed_1', _cm3_class_features_get(_domain, 'cm_show_inline_default_closed'));
        END IF;
        IF _cardin ILIKE 'N:%' THEN
            PERFORM _cm3_class_features_set(_domain, 'cm_show_inline_2', _cm3_class_features_get(_domain, 'cm_show_inline'));
            PERFORM _cm3_class_features_set(_domain, 'cm_show_inline_default_closed_2', _cm3_class_features_get(_domain, 'cm_show_inline_default_closed'));
        END IF;
        PERFORM _cm3_class_features_delete(_domain, 'cm_show_inline');
        PERFORM _cm3_class_features_delete(_domain, 'cm_show_inline_default_closed');
    END LOOP;
END $$ LANGUAGE PLPGSQL;

