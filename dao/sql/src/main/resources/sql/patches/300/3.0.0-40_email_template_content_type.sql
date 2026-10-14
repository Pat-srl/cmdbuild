-- email template content type

SELECT _cm3_attribute_create('"_EmailTemplate"', 'ContentType', 'varchar', 'NOTNULL: true|DEFAULT: text/plain|MODE: write');

UPDATE "_EmailTemplate" SET "ContentType" = 'text/html' WHERE "Status" = 'A' AND "ContentType" = 'text/plain' AND ( "Body" LIKE '<%' OR "Body" ILIKE '%<p>%' OR "Body" ILIKE '%<div>%' );

SELECT _cm3_class_triggers_disable('"Email"');
UPDATE "Email" SET "ContentType" = 'text/html' WHERE "Status" = 'A' AND "ContentType" = 'text/plain' AND "EmailStatus" NOT IN ('received') AND ( "Content" LIKE '<%' OR "Content" ILIKE '%<p>%' OR "Content" ILIKE '%<div>%' );
SELECT _cm3_class_triggers_enable('"Email"');	

