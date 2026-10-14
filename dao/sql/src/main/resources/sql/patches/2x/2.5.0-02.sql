-- Add columns to User table for password expiration policy, fix User and Role columns visibility

SELECT * FROM cm_create_class_attribute('User', 'PasswordExpiration', 'timestamp without time zone', null, false, false, 'MODE: read|DESCR: Password expiration date|INDEX: 11|BASEDSP: true|STATUS: active');
SELECT * FROM cm_create_class_attribute('User', 'LastPasswordChange', 'timestamp without time zone', null, false, false, 'MODE: read|DESCR: Last password change date|INDEX: 12|BASEDSP: false|STATUS: active');
SELECT * FROM cm_create_class_attribute('User', 'LastExpiringNotification', 'timestamp without time zone', null, false, false, 'MODE: read|DESCR: Last expiring notification|INDEX: 13|BASEDSP: false|STATUS: active');

COMMENT ON COLUMN "User"."Id" IS 'MODE: reserved';
COMMENT ON COLUMN "User"."IdClass" IS 'MODE: reserved';
COMMENT ON COLUMN "User"."Code" IS 'MODE: hidden|DESCR: Code|INDEX: 1|BASEDSP: true|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "User"."Description" IS 'MODE: read|DESCR: Description|INDEX: 2|BASEDSP: true|STATUS: active|GROUP: |FIELDMODE: read';
COMMENT ON COLUMN "User"."Status" IS 'MODE: reserved';
COMMENT ON COLUMN "User"."User" IS 'MODE: reserved';
COMMENT ON COLUMN "User"."BeginDate" IS 'MODE: reserved';
COMMENT ON COLUMN "User"."Notes" IS 'MODE: read|DESCR: Notes|INDEX: 3';
COMMENT ON COLUMN "User"."Username" IS 'MODE: read|DESCR: Username|INDEX: 5|BASEDSP: true|STATUS: active|GROUP: |FIELDMODE: read';
COMMENT ON COLUMN "User"."Password" IS 'MODE: hidden|DESCR: Password|INDEX: 6|BASEDSP: false|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "User"."Email" IS 'MODE: read|DESCR: Email|INDEX: 7|BASEDSP: true|STATUS: active|GROUP: |FIELDMODE: read';
COMMENT ON COLUMN "User"."Active" IS 'MODE: read|DESCR: Active|INDEX: 8|BASEDSP: false|STATUS: active|GROUP: |FIELDMODE: read';
COMMENT ON COLUMN "User"."Service" IS 'MODE: hidden|DESCR: Service|INDEX: 9|BASEDSP: false|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "User"."Privileged" IS 'MODE: hidden|DESCR: Privileged|INDEX: 10|BASEDSP: false|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "User"."PasswordExpiration" IS 'MODE: hidden|DESCR: Password expiration date|INDEX: 11|BASEDSP: false|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "User"."LastPasswordChange" IS 'MODE: hidden|DESCR: Last password change date|INDEX: 12|BASEDSP: false|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "User"."LastExpiringNotification" IS 'MODE: hidden|DESCR: Last expiring notification|INDEX: 13|BASEDSP: false|STATUS: active|GROUP: |FIELDMODE: hidden';

COMMENT ON COLUMN "Role"."Id" IS 'MODE: reserved';
COMMENT ON COLUMN "Role"."IdClass" IS 'MODE: reserved';
COMMENT ON COLUMN "Role"."Code" IS 'MODE: read|DESCR: Code|INDEX: 1|BASEDSP: true|STATUS: active|GROUP: |FIELDMODE: read';
COMMENT ON COLUMN "Role"."Description" IS 'MODE: read|DESCR: Description|INDEX: 2|BASEDSP: true|STATUS: active|GROUP: |FIELDMODE: read';
COMMENT ON COLUMN "Role"."Status" IS 'MODE: reserved';
COMMENT ON COLUMN "Role"."User" IS 'MODE: reserved';
COMMENT ON COLUMN "Role"."BeginDate" IS 'MODE: reserved';
COMMENT ON COLUMN "Role"."Notes" IS 'MODE: read|DESCR: Notes|INDEX: 3';
COMMENT ON COLUMN "Role"."Administrator" IS 'MODE: hidden|DESCR: Administrator|INDEX: 5|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."startingClass" IS 'MODE: hidden|DESCR: Starting Class|INDEX: 6|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."Email" IS 'MODE: hidden|DESCR: Email|INDEX: 7|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."DisabledModules" IS  'MODE: hidden|DESCR: DisabledModules|INDEX: 8|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."DisabledCardTabs" IS  'MODE: hidden|DESCR: DisabledCardTabs|INDEX: 9|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."DisabledProcessTabs" IS  'MODE: hidden|DESCR: DisabledProcessTabs|INDEX: 10|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."HideSidePanel" IS  'MODE: hidden|DESCR: HideSidePanel|INDEX: 11|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."FullScreenMode" IS  'MODE: hidden|DESCR: FullScreenMode|INDEX: 12|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."SimpleHistoryModeForCard" IS  'MODE: hidden|DESCR: SimpleHistoryModeForCard|INDEX: 13|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."SimpleHistoryModeForProcess" IS  'MODE: hidden|DESCR: SimpleHistoryModeForProcess|INDEX: 14|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."ProcessWidgetAlwaysEnabled" IS  'MODE: hidden|DESCR: ProcessWidgetAlwaysEnabled|INDEX: 15|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."CloudAdmin" IS  'MODE: hidden|DESCR: CloudAdmin|INDEX: 16|STATUS: active|GROUP: |FIELDMODE: hidden';
COMMENT ON COLUMN "Role"."Active" IS  'MODE: read|DESCR: Active|INDEX: 17|BASEDSP: true|STATUS: active|GROUP: |FIELDMODE: read';
