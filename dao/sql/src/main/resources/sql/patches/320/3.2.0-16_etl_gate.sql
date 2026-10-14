-- add etl gate tables

SELECT _cm3_class_create('_EtlGate','MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: Template|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: AllowPublicAccess|TYPE: boolean|NOTNULL: true|DEFAULT: false');
SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: Script|TYPE: varchar');
SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: ProcessingMode|TYPE: varchar|NOTNULL: true|DEFAULT: realtime');
SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: Config|TYPE: jsonb|DEFAULT: ''{}''::jsonb');
SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: Enabled|TYPE: boolean|DEFAULT: true');
SELECT _cm3_attribute_notnull_set('"_EtlGate"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_EtlGate"', 'Code'); 
ALTER TABLE "_EtlGate" ADD CONSTRAINT "_cm3_ProcessingMode_check" CHECK ( "ProcessingMode" IN ('realtime','batch') );

SELECT _cm3_class_create('_EtlData','MODE: reserved|TYPE: simpleclass');
SELECT _cm3_attribute_create('OWNER: _EtlData|NAME: Gate|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlData|NAME: ContentType|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlData|NAME: Data|TYPE: bytea|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlData|NAME: Meta|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
 