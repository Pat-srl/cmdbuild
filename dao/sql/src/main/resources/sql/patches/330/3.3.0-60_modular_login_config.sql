-- modular login configuration

DO $$ DECLARE
    _var varchar;
    _modules varchar = 'default';
BEGIN
    SELECT INTO _var COALESCE(_cm3_system_config_get('org.cmdbuild.auth.methods'), 'DBAuthenticator');
    IF _var NOT ILIKE '%DBAuthenticator%' THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.auth.default.enabled', 'false');
    END IF;
    IF _var ILIKE '%LdapAuthenticator%' THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.auth.ldap.enabled', 'true');
    END IF;
    IF _var ILIKE '%HeaderAuthenticator%' THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.auth.header.enabled', 'true');
    END IF;
    IF _var ILIKE '%CasAuthenticator%' THEN
        _modules = _modules || ',cas';
        PERFORM _cm3_system_config_set('org.cmdbuild.auth.module.cas.type', 'cas');
        UPDATE "_SystemConfig" SET "Code" = regexp_replace("Code", '^org.cmdbuild.auth.cas', 'org.cmdbuild.auth.module.cas') WHERE "Code" ~ '^org.cmdbuild.auth.cas.*' AND "Status" = 'A';
    END IF;
    IF _var ILIKE '%OauthAuthenticator%' THEN
        _modules = _modules || ',oauth';
        PERFORM _cm3_system_config_set('org.cmdbuild.auth.module.oauth.type', 'oauth');
        UPDATE "_SystemConfig" SET "Code" = regexp_replace("Code", '^org.cmdbuild.auth.oauth', 'org.cmdbuild.auth.module.oauth') WHERE "Code" ~ '^org.cmdbuild.auth.oauth.*' AND "Status" = 'A';
    END IF;
    IF _var ILIKE '%SamlAuthenticator%' THEN
        _modules = _modules || ',saml';
        PERFORM _cm3_system_config_set('org.cmdbuild.auth.module.saml.type', 'saml');
        UPDATE "_SystemConfig" SET "Code" = regexp_replace("Code", '^org.cmdbuild.auth.saml', 'org.cmdbuild.auth.module.saml') WHERE "Code" ~ '^org.cmdbuild.auth.saml.*' AND "Status" = 'A';
    END IF;
    IF _modules <> 'default' THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.auth.modules', _modules);
    END IF;    
    PERFORM _cm3_system_config_delete('org.cmdbuild.auth.methods');
END $$ LANGUAGE PLPGSQL;