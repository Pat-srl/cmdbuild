-- card form table

SELECT _cm3_class_create('_Form','MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _Form|NAME: Data|TYPE: jsonb|NOTNULL: true');

SELECT _cm3_attribute_notnull_set('"_Form"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_Form"', 'Code'); 
