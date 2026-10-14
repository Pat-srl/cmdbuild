-- fix system tables

ALTER TABLE "User" DROP COLUMN IF EXISTS "PasswordExpiration";
ALTER TABLE "User" DROP COLUMN IF EXISTS "LastPasswordChange";
ALTER TABLE "User" DROP COLUMN IF EXISTS "LastExpiringNotification";

SELECT _cm3_attribute_notnull_set('"_PluginConfig"', 'Key');
SELECT _cm3_attribute_notnull_set('"_PluginConfig"', 'Value');
