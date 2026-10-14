-- waterway config table improvements

DROP INDEX "_cm3__EtlConfig_Code_Version";
SELECT _cm3_attribute_index_unique_create('"_EtlConfig"', 'Code');  