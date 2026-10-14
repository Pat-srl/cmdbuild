-- system lookup (proteceted)
 
SELECT _cm3_attribute_create('OWNER: LookUp|NAME: IsSystem|TYPE: boolean|NOTNULL: true|DEFAULT: false');

UPDATE "LookUp" SET "IsSystem" = true WHERE "Status" = 'A' AND "Type" IN ('AlfrescoCategory','FlowStatus','CalendarCategory','CalendarEndType','CalendarEventStatus','CalendarFrequency','CalendarPriority') AND "Code" = 'org.cmdbuild.LOOKUPTYPE';
    