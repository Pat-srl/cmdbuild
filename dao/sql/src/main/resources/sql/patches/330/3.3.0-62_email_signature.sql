-- email signature

SELECT _cm3_class_create('NAME: _EmailSignature|TYPE: class|MODE: reserved');
SELECT _cm3_attribute_create('OWNER: _EmailSignature|NAME: ContentHtml|TYPE: varchar');
SELECT _cm3_attribute_index_unique_create('"_EmailSignature"', 'Code'); 

SELECT _cm3_attribute_create('OWNER: _EmailTemplate|NAME: Signature|TYPE: bigint|FKTARGETCLASS: _EmailSignature');
SELECT _cm3_attribute_create('OWNER: Email|NAME: Signature|TYPE: bigint|FKTARGETCLASS: _EmailSignature');

-- INSERT INTO "_EmailSignature" ("Code","Description","ContentHtml") VALUES ('test', 'Firma di Test', 'Esempio di <i>firma</i> html');
