-- mobileApp notification messages 

SELECT _cm3_class_create('NAME: _MobileAppMessage|MODE: reserved|TYPE: class|DESCR: MobileAppNotificaton Messages');
SELECT _cm3_attribute_create('OWNER: _MobileAppMessage|NAME: MessageStatus|TYPE: varchar|NOTNULL: true|VALUES: new,sent,archived,error|DEFAULT: new');
SELECT _cm3_attribute_create('OWNER: _MobileAppMessage|NAME: Target|TYPE: varchar|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _MobileAppMessage|NAME: SourceName|TYPE: varchar|NOTNULL: true|DEFAULT: system');
SELECT _cm3_attribute_create('OWNER: _MobileAppMessage|NAME: SourceDescription|TYPE: varchar');
SELECT _cm3_attribute_create('OWNER: _MobileAppMessage|NAME: SourceType|TYPE: varchar|NOTNULL: true|VALUES: user,system|DEFAULT: system');
SELECT _cm3_attribute_create('OWNER: _MobileAppMessage|NAME: Timestamp|TYPE: timestamp|NOTNULL: true|DEFAULT: now()');
SELECT _cm3_attribute_create('OWNER: _MobileAppMessage|NAME: Meta|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
SELECT _cm3_attribute_create('OWNER: _MobileAppMessage|NAME: Subject|TYPE: varchar');
SELECT _cm3_attribute_create('OWNER: _MobileAppMessage|NAME: Content|TYPE: varchar');

SELECT _cm3_attribute_notnull_set('"_MobileAppMessage"', 'Code');

SELECT _cm3_attribute_index_create('"_MobileAppMessage"', 'Target'); 
SELECT _cm3_attribute_index_create('"_MobileAppMessage"', 'SourceName', 'SourceType'); 