-- adding waterway message table

SELECT _cm3_class_create('NAME: _EtlMessage|MODE: reserved|TYPE: class|DESCR: Etl Messages');
SELECT _cm3_attribute_create('OWNER: _EtlMessage|NAME: Queue|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlMessage|NAME: Storage|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlMessage|NAME: MessageStatus|TYPE: varchar|NOTNULL: true|VALUES: draft,queued,processing,processed,error,failed,forwarded,completed');
SELECT _cm3_attribute_create('OWNER: _EtlMessage|NAME: NodeId|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlMessage|NAME: TransactionId|TYPE: int|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlMessage|NAME: Meta|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
SELECT _cm3_attribute_create('OWNER: _EtlMessage|NAME: Attachments|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
SELECT _cm3_attribute_create('OWNER: _EtlMessage|NAME: Errors|TYPE: jsonb|NOTNULL: true|DEFAULT: ''[]''::jsonb');
SELECT _cm3_attribute_create('OWNER: _EtlMessage|NAME: Logs|TYPE: varchar');

SELECT _cm3_attribute_notnull_set('"_EtlMessage"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_EtlMessage"', 'Code'); 

--TODO unique index for transaction id (?)
