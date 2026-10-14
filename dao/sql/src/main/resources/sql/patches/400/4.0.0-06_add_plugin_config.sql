-- add plugin configs

SELECT _cm3_class_create('NAME: _PluginConfig|MODE: reserved|DESCR: Plugin Config');


SELECT _cm3_attribute_create('OWNER: _PluginConfig|NAME: Key|TYPE: varchar|NOTNULL: true|DESCR: Plugin config key');
SELECT _cm3_attribute_create('OWNER: _PluginConfig|NAME: Value|TYPE: varchar|NOTNULL: true|DESCR: Plugin config value');
SELECT _cm3_attribute_create('OWNER: _PluginConfig|NAME: Access|TYPE: varchar|DEFAULT: private|NOTNULL: true|DESCR: Plugin config access');

SELECT _cm3_attribute_notnull_set('"_PluginConfig"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_PluginConfig"', 'Code', 'Key');

ALTER TABLE "_PluginConfig" ADD CONSTRAINT "_cm3_Access_check" CHECK ( "Access" IN ('public','private') );