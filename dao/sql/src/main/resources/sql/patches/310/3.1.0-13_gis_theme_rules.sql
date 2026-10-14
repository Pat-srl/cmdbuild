-- gisValue style rules

SELECT _cm3_class_create('_GisStyleRules','MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _GisStyleRules|NAME: Owner|TYPE: regclass|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _GisStyleRules|NAME: Attribute|TYPE: bigint|FKTARGET: _GisAttribute|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _GisStyleRules|NAME: Function|TYPE: varchar');
SELECT _cm3_attribute_create('OWNER: _GisStyleRules|NAME: Rules|TYPE: jsonb|NOTNULL: true');

SELECT _cm3_attribute_notnull_set('"_GisStyleRules"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_GisStyleRules"', 'Owner', 'Code'); 

--rules: map of filter->json/string value