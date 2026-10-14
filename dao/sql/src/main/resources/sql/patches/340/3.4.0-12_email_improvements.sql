-- email model improvements

ALTER TABLE "_EmailTemplate" DROP COLUMN "TimeToLive" CASCADE;
ALTER TABLE "_EmailTemplate" DROP COLUMN "NotificationType" CASCADE;
ALTER TABLE "_EmailTemplate" DROP COLUMN "Participants" CASCADE;
ALTER TABLE "_EmailTemplate" DROP COLUMN "Trigger" CASCADE;

SELECT _cm3_attribute_create('OWNER: _EmailTemplate|NAME: NotificationProvider|TYPE: varchar|NOTNULL: true|DEFAULT: email');
