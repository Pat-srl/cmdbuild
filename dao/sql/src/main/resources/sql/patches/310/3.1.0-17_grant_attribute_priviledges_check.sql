-- added grant attributepriviledge constraint

DO $$ DECLARE
    element jsonb;
    _record record;
BEGIN
	FOR element IN (SELECT "AttributePrivileges" FROM "_Grant" WHERE "AttributePrivileges" NOT IN ('{}')) LOOP
		FOR _record in (select key,value from jsonb_each(element) WHERE value NOT IN ('"write"', '"read"', '"none"')) LOOP
			RAISE 'CM: GrantAttributePrivilege % for Grant % is not valid', _record.value, _record.key;
		END LOOP;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "_Grant" ADD CONSTRAINT _cm3_AttributePrivileges_check CHECK ( _cm3_grant_attribute_priviledges_check("AttributePrivileges") );

