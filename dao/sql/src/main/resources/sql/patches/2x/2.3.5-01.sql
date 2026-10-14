-- Fixes "cm_modify_attribute" function

CREATE OR REPLACE FUNCTION cm_modify_attribute(tableid oid, attributename text, sqltype text, attributedefault text, attributenotnull boolean, attributeunique boolean, commentparts text[], classes text[]) RETURNS void AS $$
DECLARE
	OldComment text := _cm_comment_for_attribute(TableId, AttributeName);
	-- creates full new comment string for consistency checks
	NewComment text := _cm_comment_add_parts(_cm_comment_set_parts(OldComment, commentparts), commentparts, true);
	subClassId oid;
	_classes text[] := COALESCE(classes, ARRAY[]::text[]);
BEGIN
	IF COALESCE(_cm_read_reference_domain_comment(OldComment), '') IS DISTINCT FROM COALESCE(_cm_read_reference_domain_comment(NewComment), '')
		OR  _cm_read_reference_type_comment(OldComment) IS DISTINCT FROM _cm_read_reference_type_comment(NewComment)
		OR  COALESCE(_cm_get_fk_target_comment(OldComment), '') IS DISTINCT FROM COALESCE(_cm_get_fk_target_comment(NewComment), '')
	THEN
		RAISE EXCEPTION 'CM_FORBIDDEN_OPERATION';
	END IF;

	PERFORM _cm_check_attribute_comment_and_type(NewComment, SQLType);

	IF _cm_get_attribute_sqltype(TableId, AttributeName) <> trim(SQLType) THEN
		IF _cm_attribute_is_inherited(TableId, AttributeName) THEN
			RAISE NOTICE 'Not altering column type'; -- Fail silently
			--RAISE EXCEPTION 'CM_FORBIDDEN_OPERATION';
		ELSE
			EXECUTE 'ALTER TABLE '|| TableId::regclass ||' ALTER COLUMN '|| quote_ident(AttributeName) ||' TYPE '|| SQLType;
		END IF;
	END IF;

	PERFORM _cm_attribute_set_uniqueness(TableId, AttributeName, AttributeUnique);
	PERFORM _cm_attribute_set_notnull(TableId, AttributeName, AttributeNotNull);
	PERFORM _cm_set_attribute_default(TableId, AttributeName, AttributeDefault, FALSE);
	-- updates comment according to specified tables (empty means all)
	FOR subClassId IN SELECT _cm_subtables_and_itself(tableid) LOOP
		IF (COALESCE(array_length(_classes, 1),0) = 0) OR (_classes @> ARRAY[replace(subClassId::regclass::text, '"', '')]) THEN
			OldComment = _cm_comment_for_attribute(subClassId, AttributeName);
			NewComment = _cm_comment_add_parts(_cm_comment_set_parts(OldComment, commentparts), commentparts, true);
			EXECUTE 'COMMENT ON COLUMN '|| subClassId::regclass ||'.'|| quote_ident(AttributeName) ||' IS '|| quote_literal(NewComment);
		END IF;		
	END LOOP;
END;
$$ LANGUAGE plpgsql VOLATILE;