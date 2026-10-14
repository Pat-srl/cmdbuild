-- add cache table

SELECT _cm3_class_create('NAME: _Cache|TYPE: simpleclass|MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _Cache|NAME: Code|TYPE: varchar|UNIQUE: true|NOTNULL: true|MODE: immutable');
SELECT _cm3_attribute_create('OWNER: _Cache|NAME: TimeToLive|TYPE: bigint|MODE: immutable|DEFAULT: 43200|DESCR: Time to live (seconds)');
SELECT _cm3_attribute_create('OWNER: _Cache|NAME: Data|TYPE: varchar|MODE: immutable');

