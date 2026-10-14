-- Visibility of Email class and EmailActivity domain

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
DECLARE
	email_comment text := _cm_comment_for_class('Email');
	activity_email_comment text := _cm_comment_for_domain('ActivityEmail');

	new_email_comment text := regexp_replace(email_comment, 'MODE: [^|]+', 'MODE: user');
	new_activity_email_comment text := regexp_replace(activity_email_comment, 'MODE: [^|]+', 'MODE: user');
BEGIN
	RAISE INFO 'changing Email class comment from "%" to "%"', email_comment, new_email_comment;
	PERFORM cm_modify_class('Email', new_email_comment);

	RAISE INFO 'changing ActivityEmail domain comment from "%" to "%"', activity_email_comment, new_activity_email_comment;
	PERFORM cm_modify_domain('ActivityEmail', new_activity_email_comment);
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
