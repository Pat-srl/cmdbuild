-- attribute group validation

ALTER TABLE "_AttributeGroup" ADD CONSTRAINT "_cm3_Code_notblank" CHECK ( "Status" <> 'A' OR _cm3_utils_is_not_blank("Code") );
