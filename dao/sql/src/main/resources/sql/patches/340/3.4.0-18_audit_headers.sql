-- improve audit table, log request headers

SELECT _cm3_attribute_create('OWNER: _Request|NAME: RequestHeaders|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
SELECT _cm3_attribute_create('OWNER: _Request|NAME: ResponseHeaders|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');

UPDATE "_Request" SET "Errors" = COALESCE("Errors"->'data', '[]'::jsonb);
ALTER TABLE "_Request" ALTER COLUMN "Errors" SET DEFAULT '[]'::jsonb;

