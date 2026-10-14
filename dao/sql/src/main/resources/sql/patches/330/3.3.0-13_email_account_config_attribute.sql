-- added config field for email accounts

SELECT _cm3_attribute_create('OWNER: _EmailAccount|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');

