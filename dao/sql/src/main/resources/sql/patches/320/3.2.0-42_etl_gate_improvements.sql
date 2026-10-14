-- etl gate improvements
 
SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: Templates|TYPE: varchar[]|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]');
SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: Handler|TYPE: varchar|NOTNULL: true|DEFAULT: default');

SELECT _cm3_class_triggers_disable('"_EtlGate"');
UPDATE "_EtlGate" SET "Templates" = ARRAY["Template"] WHERE _cm3_utils_is_not_blank("Template");
SELECT _cm3_class_triggers_enable('"_EtlGate"');

ALTER TABLE "_EtlGate" DROP COLUMN "Template";
