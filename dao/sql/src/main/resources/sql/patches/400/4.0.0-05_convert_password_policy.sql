-- refactor configs

DO $$ BEGIN

    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.enable-password-change-management')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.validation.enabled', _cm3_system_config_get('org.cmdbuild.password.enable-password-change-management'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.enable-password-change-management');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.allow_password_change')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.change.enabled', _cm3_system_config_get('org.cmdbuild.password.allow_password_change'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.allow_password_change');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.differ-from-username')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.validation.differentFromUsername', _cm3_system_config_get('org.cmdbuild.password.differ-from-username'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.differ-from-username');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.differ-from-previous')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.validation.differentFromPrevious', _cm3_system_config_get('org.cmdbuild.password.differ-from-previous'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.differ-from-previous');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.differ-from-previous-count')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.validation.differentFromPreviousCount', _cm3_system_config_get('org.cmdbuild.password.differ-from-previous-count'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.differ-from-previous-count');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.require-digit')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.validation.requireDigit', _cm3_system_config_get('org.cmdbuild.password.require-digit'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.require-digit');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.require-lowercase')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.validation.requireLowercase', _cm3_system_config_get('org.cmdbuild.password.require-lowercase'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.require-lowercase');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.require-uppercase')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.validation.requireUppercase', _cm3_system_config_get('org.cmdbuild.password.require-uppercase'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.require-uppercase');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.min-length')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.validation.minLength', _cm3_system_config_get('org.cmdbuild.password.min-length'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.min-length');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.max-password-age-days')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.validation.maxAgeDays', _cm3_system_config_get('org.cmdbuild.password.max-password-age-days'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.max-password-age-days');
    END IF;
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.password.forewarning-days')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.password.forewarningDays', _cm3_system_config_get('org.cmdbuild.password.forewarning-days'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.password.forewarning-days');
    END IF;

END $$ LANGUAGE PLPGSQL;