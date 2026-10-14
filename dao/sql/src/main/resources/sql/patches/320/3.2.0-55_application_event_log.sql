-- application event log table

SELECT _cm3_class_create('NAME: _EventLog|TYPE: simpleclass|MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _EventLog|NAME: EventId|TYPE: varchar|NOTNULL: true|MODE: immutable|DEFAULT: _cm3_utils_random_id()');
SELECT _cm3_attribute_create('OWNER: _EventLog|NAME: Code|TYPE: varchar|NOTNULL: true|MODE: immutable');
SELECT _cm3_attribute_create('OWNER: _EventLog|NAME: Timestamp|TYPE: timestamp|NOTNULL: true|MODE: immutable');
SELECT _cm3_attribute_create('OWNER: _EventLog|NAME: RequestId|TYPE: varchar|MODE: immutable|DESCR: Request id');
SELECT _cm3_attribute_create('OWNER: _EventLog|NAME: SessionId|TYPE: varchar|MODE: immutable|DESCR: Session id');
SELECT _cm3_attribute_create('OWNER: _EventLog|NAME: SessionUser|TYPE: varchar|MODE: immutable|DESCR: Username');
SELECT _cm3_attribute_create('OWNER: _EventLog|NAME: Card|TYPE: bigint|MODE: immutable|DESCR: Related card/object id');
SELECT _cm3_attribute_create('OWNER: _EventLog|NAME: Data|TYPE: jsonb|NOTNULL: true|MODE: immutable|DEFAULT: ''{}''::jsonb|DESCR: Additional event data');
SELECT _cm3_attribute_create('OWNER: _EventLog|NAME: Errors|TYPE: jsonb|NOTNULL: true|MODE: immutable|DEFAULT: ''{}''::jsonb|DESCR: Errors'); 

-- CREATE INDEX "_cm3__EventLog_Code" ON "_EventLog" ("Code" ) --TODO fix index to match string prefix, if required
SELECT _cm3_attribute_index_create('"_EventLog"', 'Code');
SELECT _cm3_attribute_index_create('"_EventLog"', 'Card'); 

