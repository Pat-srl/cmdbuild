-- add _CoreComponents table 

SELECT _cm3_class_create('NAME: _CoreComponent|TYPE: class|MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _CoreComponent|NAME: Type|TYPE: varchar|NOTNULL: true|VALUES: script');
SELECT _cm3_attribute_create('OWNER: _CoreComponent|NAME: Active|TYPE: boolean|NOTNULL: true|DEFAULT: true');
SELECT _cm3_attribute_create('OWNER: _CoreComponent|NAME: Data|TYPE: varchar');
SELECT _cm3_attribute_notnull_set('"_CoreComponent"','Code');
SELECT _cm3_attribute_index_unique_create('"_CoreComponent"', 'Code'); 

