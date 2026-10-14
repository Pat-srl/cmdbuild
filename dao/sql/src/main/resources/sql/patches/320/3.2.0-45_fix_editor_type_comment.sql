-- fix editor type comment
 
DO $$ DECLARE
    _class regclass;
    _attr varchar;
BEGIN
    FOR _class, _attr IN SELECT owner, name FROM _cm3_attribute_list_detailed() WHERE comment->>'EDITORTYPE' ~* '^null$' LOOP
        PERFORM _cm3_attribute_comment_delete(_class, _attr, 'EDITORTYPE');
    END LOOP;
END $$ LANGUAGE PLPGSQL;

