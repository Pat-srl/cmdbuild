-- dropping old system view using deprecated field

DROP VIEW IF EXISTS system_attributecatalog;
DROP FUNCTION IF EXISTS _cm_get_attribute_default(oid, text);