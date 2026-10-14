-- doc info

-- SELECT _cm3_attribute_create('OWNER: DmsModel|NAME: FileName|TYPE: varchar|MODE: sysread|NOTNULL: true');
-- SELECT _cm3_attribute_create('OWNER: DmsModel|NAME: Version|TYPE: varchar|MODE: sysread|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: DmsModel|NAME: FileName|TYPE: varchar|MODE: syshidden'); --TODO NOTNULL !!
SELECT _cm3_attribute_create('OWNER: DmsModel|NAME: Version|TYPE: varchar|MODE: syshidden'); --TODO NOTNULL !!
