-- comment upgrade


DO $$ DECLARE
	_class regclass;
	_attribute varchar;
	_fieldmode varchar;
	_mode varchar;
	_newmode varchar;
	_comment varchar;
	_comment_fixed varchar;
BEGIN 
	FOR _class IN SELECT _cm3_domain_list() UNION SELECT _cm3_class_list() LOOP
		_mode = _cm3_class_comment_get(_class,'MODE');
		IF _mode IN ('baseclass', 'read') OR _mode LIKE 'sys%' THEN
			_newmode = 'protected';
		ELSEIF _mode = 'reserved' THEN
			_newmode = _mode;
		ELSE
			_newmode = 'default';
		END IF;
		IF _newmode <> _mode THEN
			RAISE NOTICE 'fix class mode for class %, from % to %', _class, _mode, _newmode;
			PERFORM _cm3_class_comment_set(_class, 'MODE', _newmode);
		END IF;
		FOR _attribute IN SELECT _cm3_attribute_list(_class) LOOP
			_comment = _cm3_attribute_comment_get_raw(_class,_attribute);
			_comment_fixed = replace(_comment,'|GROUP: |','|');
			IF _comment <> _comment_fixed THEN
				RAISE NOTICE 'fix comment %.%', _class, _attribute;
				RAISE NOTICE 'update attr comment %.%, old comment : %', _class, _attribute, _comment;
				RAISE NOTICE 'update attr comment %.%, new comment : %', _class, _attribute, _comment_fixed;
				PERFORM _cm3_attribute_comment_set(_class, _attribute, _cm3_comment_to_jsonb(_comment_fixed));
			END IF;
			_fieldmode = COALESCE(_cm3_attribute_comment_get(_class,_attribute,'FIELDMODE'), 'write');
			_mode = COALESCE(_cm3_attribute_comment_get(_class,_attribute,'MODE'), 'write');		
			PERFORM _cm3_attribute_comment_delete(_class, _attribute, 'FIELDMODE');
			IF _mode LIKE 'sys%' THEN
				_mode = 'sysread';
			END IF;
			IF _mode IN ('reserved', 'sysread') THEN
				_newmode = _mode;
			ELSE
				_newmode = _fieldmode;
			END IF;
			IF _newmode NOT IN ('read','write','reserved','hidden','immutable','sysread') THEN
				_newmode = 'write';
				RAISE WARNING 'fix wf attr mode for attr = %.% change mode from % to %', _class, _attribute, _mode, _newmode;
			END IF;			
			PERFORM _cm3_attribute_comment_set( _class, _attribute, 'MODE', _newmode);

		END LOOP;
	END LOOP;
	FOR _class IN SELECT _cm3_class_list_descendants_and_self('"Activity"'::regclass) LOOP
		FOR _attribute IN SELECT * FROM (SELECT _cm3_attribute_list(_class) x) a WHERE a.x IN ('UniqueProcessDefinition','PrevExecutors','ActivityInstanceId','NextExecutor','ProcessCode','ActivityDefinitionId','FlowData','FlowStatus') LOOP
-- 			IF _cm3_attribute_comment_get(_class,_attribute,'MODE') <> 'reserved' THEN 
-- 				RAISE NOTICE 'fix wf attribute permission for %.%, set MODE = ''reserved''', _class, _attribute;
			PERFORM _cm3_attribute_comment_set(_class,_attribute,'MODE','rescore');
-- 			END IF;
		END LOOP;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
	_class regclass;
	_fun oid;
	_attr varchar;
	_value varchar;
BEGIN
	FOR _class IN SELECT * FROM _cm3_class_list() UNION SELECT * FROM _cm3_domain_list() LOOP
		_value = lower(_cm3_class_comment_get(_class,'STATUS'));
		IF _value = 'noactive' THEN
			PERFORM _cm3_class_comment_set(_class, 'ACTIVE', 'false');
		ELSEIF _value <> 'active' AND _value ~ '.+' THEN
			RAISE 'unsupported STATUS value found for class = % value = %', _class, _value;
		END IF;
		PERFORM _cm3_class_comment_delete(_class, 'STATUS');
		FOR _attr IN SELECT * FROM _cm3_attribute_list(_class) LOOP
			_value = lower(_cm3_attribute_comment_get(_class, _attr, 'STATUS'));
			IF _value = 'noactive' THEN
				PERFORM _cm3_attribute_comment_set(_class, _attr, 'ACTIVE', 'false');
			ELSEIF _value <> 'active' AND _value ~ '.+' THEN
				RAISE 'unsupported STATUS value found for attr = %.% value = %', _class, _attr, _value;
			END IF;
			PERFORM _cm3_attribute_comment_delete(_class, _attr, 'STATUS');
		END LOOP;
	END LOOP;
	FOR _fun IN SELECT * FROM _cm3_function_list() LOOP
		_value = lower(_cm3_function_comment_get(_fun,'STATUS'));
		IF _value = 'noactive' THEN
			PERFORM _cm3_function_comment_set(_fun, 'ACTIVE', 'false');
		ELSEIF _value <> 'active' AND _value ~ '.+' THEN
			RAISE 'unsupported STATUS value found for function = % value = %', _cm3_function_definition_get(_fun), _value;
		END IF;
		PERFORM _cm3_function_comment_delete(_fun, 'STATUS');
	END LOOP;
END $$ LANGUAGE PLPGSQL;


DO $$ DECLARE
	_record RECORD;
BEGIN 
        FOR _record IN SELECT * FROM (
                       SELECT y.function_name function_name,  jsonb_object_keys(y.com) commentvalue FROM (SELECT  x function_name ,_cm3_function_comment_get_jsonb(x) com FROM _cm3_function_list() AS x) AS y) AS z 
                       WHERE commentvalue NOT IN ('CATEGORIES','MASTERTABLE','TAGS','ACTIVE','MODE','TYPE') LOOP
			RAISE NOTICE 'removing invalid function comment = %.%', _record.function_name, _record.commentvalue;
			PERFORM _cm3_function_comment_delete(_record.function_name,_record.commentvalue);
        END LOOP;
        FOR _record IN SELECT * FROM (
                       SELECT x.class_name class_name, jsonb_object_keys(_cm3_attribute_comment_get(x.class_name, x.attribute_name)) commentvalue, x.attribute_name attribute_name FROM (SELECT owner class_name, name attribute_name FROM _cm3_attribute_list()) AS  x) AS y 
                       WHERE commentvalue NOT IN ('DESCR','BASEDSP','CLASSORDER','EDITORTYPE','GROUP','INDEX','LOOKUP','REFERENCEDOM','REFERENCEDIR','FKTARGETCLASS','FILTER','IP_TYPE','ACTIVE','MODE') LOOP
			RAISE NOTICE 'removing invalid attribute comment = %.%.%', _record.class_name, _record.attribute_name, _record.commentvalue;
			PERFORM _cm3_attribute_comment_delete(_record.class_name,_record.attribute_name,_record.commentvalue);
        END LOOP;
        FOR _record IN SELECT * FROM (
                       SELECT y.class_name class_name,  jsonb_object_keys(y.com) commentvalue FROM (SELECT  x class_name ,_cm3_class_comment_get_jsonb(x) com FROM _cm3_class_list() AS x) AS y) AS z 
                       WHERE commentvalue NOT IN ('DESCR','SUPERCLASS','TYPE','MTMODE','USERSTOPPABLE','WFSTATUSATTR','WFSAVE','ATTACHMENT_TYPE_LOOKUP','ATTACHMENT_DESCRIPTION_MODE','ACTIVE','MODE')
                       UNION
                       SELECT * FROM (
                       SELECT y.dom class_name,  jsonb_object_keys(y.com) commentvalue FROM (SELECT  x dom ,_cm3_class_comment_get_jsonb(x) com FROM _cm3_domain_list() AS x) AS y) AS z 
                       WHERE commentvalue NOT IN ('LABEL','CLASS1','CLASS2','TYPE','DESCRDIR','DESCRINV','CARDIN','MASTERDETAIL','MDLABEL','MDFILTER','DISABLED1','DISABLED2','INDEX1','INDEX2','ACTIVE','MODE') LOOP
			RAISE NOTICE 'removing invalid class comment = %.%', _record.class_name, _record.commentvalue;
			PERFORM _cm3_class_comment_delete(_record.class_name,_record.commentvalue);
        END LOOP;
END $$ LANGUAGE PLPGSQL;

