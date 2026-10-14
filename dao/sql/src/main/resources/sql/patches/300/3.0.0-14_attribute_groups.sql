-- attribute groups refactoring


SELECT _cm3_class_create('_AttributeGroup', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Attribute group|SUPERCLASS: false');
SELECT _cm3_attribute_notnull_set('"_AttributeGroup"', 'Code', true);
SELECT _cm3_attribute_index_unique_create('"_AttributeGroup"', 'Code');

CREATE TABLE _cmdb_patch_aux_0 (classe regclass, groupname varchar, attribute varchar);
CREATE TABLE _cmdb_patch_aux_1 (classe regclass, topclass regclass, groupname varchar,attributes varchar[]);
CREATE TABLE _cmdb_patch_aux_2 (classe regclass, groupname varchar);

CREATE OR REPLACE FUNCTION _cm3_patch_aux_top_parent_with_group(_classe regclass,_group varchar) RETURNS int AS $$
DECLARE
	_parent regclass;
BEGIN
	_parent = _cm3_class_parent_get(_classe);
	IF _parent IS NULL OR NOT EXISTS (SELECT * FROM _cmdb_patch_aux_1 WHERE classe = _parent AND groupname = _group) THEN
		RETURN _classe;
	ELSE
		RETURN _cm3_patch_aux_top_parent_with_group(_parent,_group);
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_patch_aux_rename_attr_group_in_class(_classe regclass,_attribute varchar,_old varchar,_new varchar) RETURNS VOID AS $$ BEGIN
	IF _cm3_attribute_comment_get(_classe, _attribute, 'GROUP') = _old THEN
		PERFORM _cm3_attribute_comment_set(_classe, _attribute, 'GROUP', _new);
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_patch_aux_rename_attr_group(_classe regclass,_old varchar,_new varchar) RETURNS VOID AS $$ DECLARE
	_attr_classe regclass;
	_attributes varchar[];
	_attribute varchar;
	_old_comment varchar;
	_new_comment varchar;
BEGIN
	RAISE NOTICE 'rename group ''%'' to ''%'' (with target class %)', _old, _new, _classe;
	FOR _attr_classe,_attributes IN SELECT classe,attributes FROM _cmdb_patch_aux_1 WHERE topclass = _classe AND groupname = _old LOOP
		FOREACH _attribute in ARRAY _attributes LOOP
			PERFORM _cm3_patch_aux_rename_attr_group_in_class(_attr_classe, _attribute, _old, _new);		
		END LOOP;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
	_classe regclass;
	_attr_classe regclass;
	_parent regclass;
	_attribute varchar;
	_attributes varchar[];
	_groupname varchar;
	_description varchar;
	_groups varchar;
	_metadata jsonb;
	_count int;
BEGIN
	FOR _classe IN 
		SELECT c.classe FROM (SELECT table_id::regclass classe,table_id::regclass::varchar class_name FROM _cm3_class_list() table_id) c ORDER BY c.class_name
	LOOP
		FOR _attribute IN
			SELECT attr FROM _cm3_attribute_list(_classe) attr ORDER BY attr
		LOOP
			_groupname = _cm3_attribute_comment_get(_classe, _attribute, 'GROUP');
			IF _cm3_utils_is_not_blank(_groupname) THEN
				RAISE NOTICE 'read from attr %.% group ''%''', _classe, _attribute, _groupname;
				INSERT INTO _cmdb_patch_aux_0 VALUES (_classe,_groupname,_attribute);
			END IF;
		END LOOP;		
	END LOOP;
	INSERT INTO _cmdb_patch_aux_1 (classe,groupname,attributes) SELECT classe,groupname,array_agg(attribute) FROM _cmdb_patch_aux_0 GROUP BY classe,groupname;
	UPDATE _cmdb_patch_aux_1 SET topclass = _cm3_patch_aux_top_parent_with_group(classe,groupname);
	FOR _classe,_groupname IN
		SELECT DISTINCT topclass,groupname FROM _cmdb_patch_aux_1 ORDER BY topclass, groupname
	LOOP
		RAISE NOTICE 'processing attr group ''%'' with target class %', _groupname, _classe;
		INSERT INTO _cmdb_patch_aux_2 VALUES(_classe,_groupname);
	END LOOP;
	FOR _classe,_groupname IN
		SELECT classe,groupname from _cmdb_patch_aux_2
	LOOP
		SELECT COUNT(*) FROM _cmdb_patch_aux_2 WHERE groupname = _groupname INTO _count;
		_description = _groupname;
		IF _count > 1 THEN
			_groupname = replace(_classe::varchar,'"','') || ' - ' || _groupname;
			PERFORM _cm3_patch_aux_rename_attr_group(_classe,_description,_groupname);
		END IF;
		INSERT INTO "_AttributeGroup" ("User","Code","Description") VALUES ('system',_groupname,_description);	
	END LOOP;
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
	_translation_id int;
	_classname varchar;
	_lang varchar;
	_class regclass;
	_attribute varchar;
	_groupname varchar;
	_translation_code varchar;
BEGIN
	FOR _translation_id,_classname,_attribute,_lang IN
		SELECT t."Id",t.c,t.a,t."Lang" FROM (
			SELECT "Id","Lang",regexp_replace("Code",'attributeclass[.]([^.]+)[.].*','\1') c,regexp_replace("Code",'attributeclass[.][^.]+[.]([^.]+)[.].*','\1') a FROM "_Translation" WHERE "Code" LIKE 'attributeclass.%.group' AND "Status" = 'A'
		) t ORDER BY t.c, t.a
	LOOP
		_class = _cm3_utils_name_to_regclass(_classname);
		IF _class IS NULL THEN
			UPDATE "_Translation" SET "Status" = 'N' WHERE "Id" = _translation_id;
		ELSE
			_groupname = _cm3_attribute_comment_get(_class, _attribute, 'GROUP');
			IF _cm3_utils_is_blank(_groupname) THEN
				UPDATE "_Translation" SET "Status" = 'N' WHERE "Id" = _translation_id;
			ELSE
				RAISE NOTICE 'update translation for group ''%'' from attr %.%', _groupname, _classname, _attribute;
				_translation_code = 'attributegroupclass.' || _classname || '.' || _groupname || '.description';
				IF EXISTS (SELECT 1 FROM "_Translation" WHERE "Code" = _translation_code AND "Status" = 'A' AND "Lang" = _lang) THEN
					UPDATE "_Translation" SET "Code" = _translation_code, "Status" = 'N' WHERE "Id" = _translation_id;
				ELSE
					UPDATE "_Translation" SET "Code" = _translation_code WHERE "Id" = _translation_id;
				END IF;
			END IF;
		END IF;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP FUNCTION _cm3_patch_aux_rename_attr_group_in_class(regclass,varchar,varchar,varchar);
DROP FUNCTION _cm3_patch_aux_top_parent_with_group(regclass,varchar);
DROP FUNCTION _cm3_patch_aux_rename_attr_group(regclass,varchar,varchar); 
DROP TABLE _cmdb_patch_aux_0; 
DROP TABLE _cmdb_patch_aux_1; 
DROP TABLE _cmdb_patch_aux_2;

