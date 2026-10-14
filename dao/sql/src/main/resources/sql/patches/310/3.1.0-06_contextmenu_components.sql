--  context menu components

SELECT _cm3_class_create('_ContextMenuComp', 'MODE: reserved');
SELECT _cm3_attribute_create('"_ContextMenuComp"', 'Data', 'bytea', 'NOTNULL: true');
SELECT _cm3_attribute_create('"_ContextMenuComp"', 'Active', 'boolean', 'DEFAULT: true|NOTNULL: true');

SELECT _cm3_attribute_notnull_set('"_ContextMenuComp"', 'Code');
SELECT _cm3_attribute_unique_set('"_ContextMenuComp"', 'Code');
