--  stored filters improvements 

DROP INDEX "_cm3_Map_FilterRole_DefaultFor";

SELECT _cm3_attribute_modify('"Map_FilterRole"', 'DefaultFor', 'regclass', 'NOTNULL: true|DOMAINKEY: true');

