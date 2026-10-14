-- allow duplicate relations in N:N domains
-- PARAMS: FORCE_IF_NOT_EXISTS=true

DO $$ DECLARE
    _domain regclass;
BEGIN
    FOR _domain IN SELECT * FROM _cm3_domain_list() d WHERE _cm3_domain_cardin_get(d) = 'N:N' LOOP
        PERFORM _cm3_domain_composite_index_rebuild(_domain);
    END LOOP;
END; $$ LANGUAGE PLPGSQL;
