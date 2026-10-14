-- chat (notification) messages 

SELECT _cm3_class_create('NAME: _ChatMessage|MODE: reserved|TYPE: class|DESCR: Chat/Notificaton Messages');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: MessageType|TYPE: varchar|NOTNULL: true|VALUES: outgoing,incoming|DEFAULT: incoming');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: MessageStatus|TYPE: varchar|NOTNULL: true|VALUES: new,archived|DEFAULT: new');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: Target|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: SourceName|TYPE: varchar|NOTNULL: true|DEFAULT: system');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: SourceDescription|TYPE: varchar');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: SourceType|TYPE: varchar|NOTNULL: true|VALUES: user,system|DEFAULT: system');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: Thread|TYPE: varchar');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: Timestamp|TYPE: timestamp|NOTNULL: true|DEFAULT: now()');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: Meta|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: Subject|TYPE: varchar');
SELECT _cm3_attribute_create('OWNER: _ChatMessage|NAME: Content|TYPE: varchar');

SELECT _cm3_attribute_notnull_set('"_ChatMessage"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_ChatMessage"', 'Code', 'MessageType'); 

SELECT _cm3_attribute_index_create('"_ChatMessage"', 'Target', 'MessageType'); 
SELECT _cm3_attribute_index_create('"_ChatMessage"', 'SourceName', 'SourceType', 'MessageType'); 
