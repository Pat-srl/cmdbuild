-- adding menu type column

SELECT _cm3_attribute_create('OWNER: _Menu|NAME: Type|TYPE: varchar|NOTNULL: true|VALUES: navmenu,gismenu|DEFAULT: navmenu');

CREATE OR REPLACE FUNCTION _cm3_trigger_menu_code() RETURNS trigger AS $$ BEGIN
    IF NEW."Type" = 'gismenu' THEN
            NEW."Code" = 'gismenu';
	ELSE 
            NEW."Code" = NEW."GroupName" || '_' || NEW."TargetDevice";
	END IF;
    RETURN NEW;
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "_Menu" ADD CONSTRAINT "_cm3_TargetDevice_Type_check" CHECK (("Type" = 'gismenu' AND "Code" = 'gismenu' AND "TargetDevice" = 'default') OR ("Type" = 'navmenu' AND "Code" = "GroupName" || '_' || "TargetDevice"));