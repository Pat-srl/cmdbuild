-- application data governance table

SELECT _cm3_class_create('NAME: _ActivityLog|TYPE: simpleclass|MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _ActivityLog|NAME: EventId|TYPE: varchar|NOTNULL: true|MODE: immutable|DEFAULT: _cm3_utils_random_id()');
SELECT _cm3_attribute_create('OWNER: _ActivityLog|NAME: Code|TYPE: varchar|NOTNULL: true|MODE: immutable');
SELECT _cm3_attribute_create('OWNER: _ActivityLog|NAME: Timestamp|TYPE: timestamp|NOTNULL: true|MODE: immutable');
SELECT _cm3_attribute_create('OWNER: _ActivityLog|NAME: RequestId|TYPE: varchar|MODE: immutable|DESCR: Request id');
SELECT _cm3_attribute_create('OWNER: _ActivityLog|NAME: SessionId|TYPE: varchar|MODE: immutable|DESCR: Session id');
SELECT _cm3_attribute_create('OWNER: _ActivityLog|NAME: SessionUser|TYPE: varchar|MODE: immutable|DESCR: Username');
SELECT _cm3_attribute_create('OWNER: _ActivityLog|NAME: Metadata|TYPE: jsonb|NOTNULL: true|MODE: immutable|DEFAULT: ''{}''::jsonb|DESCR: Additional metadata');

-- SELECT _cm3_attribute_index_create('"_ActivityLog"', 'Code');