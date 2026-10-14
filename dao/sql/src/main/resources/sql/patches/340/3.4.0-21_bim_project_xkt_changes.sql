-- added xkt binary column

SELECT _cm3_attribute_create('"_BimProject"', 'XktFile', 'bytea', 'DESCR: Xkt file');

SELECT _cm3_class_create('NAME: _BimProjectIfc|MODE: reserved|DESCR: Bim project ifc table|TYPE: simpleclass');
SELECT _cm3_attribute_create('"_BimProjectIfc"', 'ProjectId', 'varchar', 'DESCR: Bim project id');
SELECT _cm3_attribute_create('"_BimProjectIfc"', 'IfcFile', 'bytea', 'DESCR: Ifc file');

DO $$ DECLARE
BEGIN
    IF _cm3_system_config_get('org.cmdbuild.bim.enabled') <> '' THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.bim.bimserver.enabled', _cm3_system_config_get('org.cmdbuild.bim.enabled'));
    END IF;
    IF _cm3_system_config_get('org.cmdbuild.bim.password') <> '' THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.bim.bimserver.password', _cm3_system_config_get('org.cmdbuild.bim.password'));
	PERFORM _cm3_system_config_delete('org.cmdbuild.bim.password');
    END IF;
    IF _cm3_system_config_get('org.cmdbuild.bim.username') <> '' THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.bim.bimserver.username', _cm3_system_config_get('org.cmdbuild.bim.username'));
	PERFORM _cm3_system_config_delete('org.cmdbuild.bim.username');
    END IF;
    IF _cm3_system_config_get('org.cmdbuild.bim.url') <> '' THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.bim.bimserver.url', _cm3_system_config_get('org.cmdbuild.bim.url'));
	PERFORM _cm3_system_config_delete('org.cmdbuild.bim.url');
    END IF;
    IF _cm3_system_config_get('org.cmdbuild.bim.deleteBeforeUpload') <> '' THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.bim.bimserver.deleteBeforeUpload', _cm3_system_config_get('org.cmdbuild.bim.deleteBeforeUpload'));
	PERFORM _cm3_system_config_delete('org.cmdbuild.bim.deleteBeforeUpload');
    END IF;
END $$ LANGUAGE PLPGSQL;