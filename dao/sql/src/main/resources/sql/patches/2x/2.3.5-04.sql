-- Updates the table "_EmailAccount" adding the columns needed for STARTTLS management 

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS VOID AS $$
BEGIN
	PERFORM cm_create_class_attribute('_EmailAccount', 'SmtpStartTls', 'boolean', null, false, false, 'MODE: write|DESCR: SMTP STARTTLS|STATUS: active');
	PERFORM cm_create_class_attribute('_EmailAccount', 'ImapStartTls', 'boolean', null, false, false, 'MODE: write|DESCR: IMAP STARTTLS|STATUS: active');
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION apply_patch();