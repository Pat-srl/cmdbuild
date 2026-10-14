-- remove deprecated _EtlData table

DELETE FROM "_EtlData";
SELECT _cm3_class_delete('"_EtlData"'::regclass);