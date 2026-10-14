-- improved attribute groups

CREATE TRIGGER "_cm3_trigger_attribute_group" AFTER INSERT OR UPDATE ON "_AttributeGroup" FOR EACH ROW WHEN ( NEW."Status" = 'A' ) EXECUTE PROCEDURE _cm3_trigger_attribute_group();
