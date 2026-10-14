-- add params to gis ruleset

SELECT _cm3_attribute_create('OWNER: _GisStyleRules|NAME: Params|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');
