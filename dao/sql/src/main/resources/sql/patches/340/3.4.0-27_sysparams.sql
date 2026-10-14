-- sys param table

SELECT _cm3_class_create('NAME: _SysParam|TYPE: class|MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _SysParam|NAME: Value|TYPE: varchar');
SELECT _cm3_attribute_create('OWNER: _SysParam|NAME: Meta|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb|MODE: hidden');
SELECT _cm3_attribute_notnull_set('"_SysParam"','Code');
SELECT _cm3_attribute_index_unique_create('"_SysParam"', 'Code'); 

