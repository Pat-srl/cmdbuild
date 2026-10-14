-- add active flag for email template and email account
-- PARAMS: FORCE_IF_NOT_EXISTS=true

SELECT _cm3_attribute_create('OWNER: _EmailTemplate|NAME: Active|TYPE: boolean|NOTNULL: true|DEFAULT: true');
SELECT _cm3_attribute_create('OWNER: _EmailAccount|NAME: Active|TYPE: boolean|NOTNULL: true|DEFAULT: true');

