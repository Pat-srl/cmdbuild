-- Replaces system functions for handling user when a relation is created/updated 

CREATE OR REPLACE FUNCTION _cm_trigger_update_reference() RETURNS trigger AS $$
DECLARE
	AttributeName text := TG_ARGV[0];
	TableId oid := TG_ARGV[1]::regclass::oid;
	CardColumn text := TG_ARGV[2]; -- Domain column name for the card id
	RefColumn text := TG_ARGV[3];  -- Domain column name for the reference id

	OldCardId integer;
	NewCardId integer;
	OldRefValue integer;
	NewRefValue integer;
BEGIN
	RAISE DEBUG 'Trigger % on %', TG_NAME, TG_TABLE_NAME;
	IF (NEW."Status"='A') THEN
		EXECUTE 'SELECT (' || quote_literal(NEW) || '::' || TG_RELID::regclass || ').' || quote_ident(RefColumn) INTO NewRefValue;
	ELSIF (NEW."Status"<>'N') THEN
		-- Ignore history rows
		RETURN NEW;
	END IF;

	EXECUTE 'SELECT (' || quote_literal(NEW) || '::' || TG_RELID::regclass || ').' || quote_ident(CardColumn) INTO NewCardId;

	IF (TG_OP='UPDATE') THEN
		EXECUTE 'SELECT (' || quote_literal(OLD) || '::' || TG_RELID::regclass || ').' || quote_ident(CardColumn) INTO OldCardId;
		IF (OldCardId <> NewCardId) THEN -- If the non-reference side changes...
			PERFORM _cm_update_reference(NEW."User", TableId, AttributeName, OldCardId, NULL);
			-- OldRefValue is kept null because it is like a new relation
		ELSE
			EXECUTE 'SELECT (' || quote_literal(OLD) || '::' || TG_RELID::regclass || ').' || quote_ident(RefColumn) INTO OldRefValue;
		END IF;
	END IF;

	IF ((NewRefValue IS NULL) OR (OldRefValue IS NULL) OR (OldRefValue <> NewRefValue)) THEN
		PERFORM _cm_update_reference(NEW."User", TableId, AttributeName, NewCardId, NewRefValue);
	END IF;

	RETURN NEW;
END;
$$ LANGUAGE plpgsql;


DROP FUNCTION IF EXISTS _cm_update_reference(oid, text, integer, integer);
CREATE OR REPLACE FUNCTION _cm_update_reference(username text, tableid oid, attributename text, cardid integer, referenceid integer) RETURNS void AS $$
BEGIN
	EXECUTE 'UPDATE ' || TableId::regclass ||
		' SET ' || quote_ident(AttributeName) || ' = ' || coalesce(ReferenceId::text, 'NULL') ||
		', "User" = ' || coalesce(quote_literal(UserName),'NULL') ||
		' WHERE "Status"=''A'' AND "Id" = ' || CardId::text ||
		' AND coalesce(' || quote_ident(AttributeName) || ', 0) <> ' || coalesce(ReferenceId, 0)::text;
END;
$$ LANGUAGE plpgsql;
