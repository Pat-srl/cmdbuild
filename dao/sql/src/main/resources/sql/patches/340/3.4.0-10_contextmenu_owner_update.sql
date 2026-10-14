-- update onwer type for context menu components

ALTER TABLE "_ContextMenu" ALTER COLUMN "Owner" TYPE varchar USING _cm3_utils_regclass_to_name("Owner");
SELECT _cm3_attribute_create('OWNER: _ContextMenu|NAME: OwnerType|TYPE: varchar|DEFAULT: class|NOTNULL: true|VALUES: class,view');
SELECT _cm3_attribute_index_unique_create('"_ContextMenu"', 'Code', 'Owner', 'OwnerType');