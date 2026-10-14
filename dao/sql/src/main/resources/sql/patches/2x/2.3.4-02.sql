-- Updates cm_delete_card function

DROP FUNCTION IF EXISTS cm_delete_card(integer, oid);

CREATE OR REPLACE FUNCTION cm_delete_card(CardId integer, TableId oid, UserName text) RETURNS void AS $$
DECLARE
	ClassComment text := _cm_comment_for_table_id(TableId);
	IsSimpleClass boolean := _cm_is_simpleclass_comment(ClassComment);
BEGIN
	IF IsSimpleClass THEN
		RAISE DEBUG 'deleting a card from a simple class';
		EXECUTE 'DELETE FROM ' || TableId::regclass || ' WHERE "Id" = ' || CardId;
	ELSE
		RAISE DEBUG 'deleting a card from a standard class';
		EXECUTE 'UPDATE ' || TableId::regclass || ' SET "User" = ''' || coalesce(UserName,'') || ''', "Status" = ''N'' WHERE "Id" = ' || CardId;
	END IF;
END;
$$ LANGUAGE plpgsql VOLATILE;
