-- add offline datamodel

SELECT _cm3_class_create('NAME: _Offline|MODE: reserved|DESCR: Offline datamodel');

SELECT _cm3_attribute_create('OWNER: _Offline|NAME: Metadata|TYPE: jsonb|NOTNULL: true|DESCR: Offline metadata|DEFAULT: ''{}''::jsonb');
SELECT _cm3_attribute_create('OWNER: _Offline|NAME: Enabled|TYPE: boolean|DEFAULT: true|NOTNULL: true');

SELECT _cm3_attribute_notnull_set('"_Offline"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_Offline"', 'Code'); 