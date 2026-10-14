-- embedded items

SELECT _cm3_class_create('NAME: _Items|MODE: reserved|TYPE: simpleclass|DESCR: Embedded Items Index');
SELECT _cm3_attribute_create('OWNER: _Items|NAME: Type|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _Items|NAME: OwnerClass|TYPE: regclass|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _Items|NAME: Attr|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _Items|NAME: Card|TYPE: bigint|NOTNULL: true');
