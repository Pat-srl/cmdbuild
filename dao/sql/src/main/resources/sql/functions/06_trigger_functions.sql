-- trigger functions
-- REQUIRE PATCH 3.0.0-03a_system_functions

--- TRIGGER HELPERS ---

CREATE OR REPLACE FUNCTION _cm3_reference_update(_class regclass, _attr varchar, _card bigint, _value bigint) RETURNS void AS $$ BEGIN
    RAISE DEBUG 'update reference, set value = % for card = %[%] attr = %', _value, _class, _card, _attr;
	IF _value IS NULL THEN
		EXECUTE format('UPDATE %s SET %I = NULL WHERE "Status" = ''A'' AND "Id" = %L AND %I IS NOT NULL', _class, _attr, _card, _attr);
	ELSE
		EXECUTE format('UPDATE %s SET %I = %L WHERE "Status" = ''A'' AND "Id" = %L AND %I IS DISTINCT FROM %L', _class, _attr, _value, _card, _attr, _value);
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_cascade_after_delete(_card bigint) RETURNS void AS $$ BEGIN
    UPDATE "Map" SET "Status" = 'N' WHERE "Status" = 'A' AND ( "IdObj1" = _card OR "IdObj2" = _card );
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.2.0-67_calendar_index
CREATE OR REPLACE FUNCTION _cm3_card_cascade_after_delete(_card bigint) RETURNS void AS $$ DECLARE
    _relation record;
    _attribute record;
    _cascade varchar;
    _has_other boolean;
BEGIN
    FOR _relation IN SELECT * FROM (SELECT "Id", "IdDomain", 'direct' direction, "IdObj2" other_card, "IdClass2" other_class FROM "Map" WHERE "Status" = 'A' AND "IdObj1" = _card UNION ALL SELECT "Id", "IdDomain", 'inverse' direction, "IdObj1" other_card, "IdClass1" other_class FROM "Map" WHERE "Status" = 'A' AND "IdObj2" = _card ) x LOOP
        _cascade = _cm3_domain_cascade_get(_relation."IdDomain", _relation.direction);
        RAISE DEBUG 'processing cascade after delete for card = % target = %[%], domain = %, direction = %, cascade action = %',
                _card, _cm3_utils_regclass_to_name(_relation.other_class), _relation.other_card, _cm3_utils_regclass_to_domain_name(_relation."IdDomain"), _relation.direction, _cascade;
        IF _cascade = 'restrict' THEN
            EXECUTE FORMAT('SELECT EXISTS (SELECT * FROM %s WHERE "Id" = %L AND "Status" = ''A'')', _relation.other_class,  _relation.other_card) INTO _has_other;
            IF _has_other THEN
                RAISE EXCEPTION 'CMO 205: reference integrity violation for delete of card = %: target card = %[%], domain = %, direction = %, cascade action = %',
                    _card, _cm3_utils_regclass_to_name(_relation.other_class), _relation.other_card, _cm3_utils_regclass_to_domain_name(_relation."IdDomain"), _relation.direction, _cascade;
            ELSE
                RAISE DEBUG 'other card already deleted, ignore restrict constraint';
            END IF;
        ELSEIF _cascade = 'delete' THEN
            RAISE DEBUG 'cascade delete to card = %[%]', _cm3_utils_regclass_to_name(_relation.other_class), _relation.other_card;
            EXECUTE FORMAT('UPDATE %s SET "Status" = ''N'' WHERE "Id" = %L AND "Status" = ''A''', _relation.other_class,  _relation.other_card);
        END IF;
        EXECUTE FORMAT('UPDATE %s SET "Status" = ''N'' WHERE "Id" = %L AND "Status" = ''A''', _relation."IdDomain", _relation."Id");
    END LOOP;
    UPDATE "_CalendarEvent" SET "Status" = 'N' WHERE "Card" = _card AND "Config"->>'onCardDeleteAction' = 'delete' AND "Status" = 'A';
    UPDATE "_CalendarEvent" SET "Card" = NULL WHERE "Card" = _card AND "Status" = 'A';
    FOR _attribute IN SELECT * FROM "_GisAttribute" WHERE "Status" = 'A' LOOP --TODO filter attrs for target class
--         EXECUTE FORMAT('DELETE FROM gis."Gis_%s_%s" WHERE "Master" = %s', _cm3_utils_regclass_to_name(_attribute."Owner"), _attribute."Code", _card); TODO fix this (when faulty gis attr info is present)
    END LOOP;
    --TODO propagate deletion to notification and/or reload cache, notification cache
END $$ LANGUAGE PLPGSQL;
-- REQUIRE PATCH 3.0.0-03a_system_functions

--- TRIGGER FUNCTIONS ---

CREATE OR REPLACE FUNCTION _cm3_trigger_simplecard_prepare_record() RETURNS trigger AS $$ BEGIN
	IF (TG_OP='UPDATE') THEN
		IF (NEW."Id" <> OLD."Id") THEN -- Id change
			RAISE EXCEPTION 'CMO 201: operation not allowed: cannot modify card id';
		END IF;
	ELSE
		NEW."IdClass" = TG_RELID;
	END IF;
	NEW."BeginDate" = now();
	NEW."User" = _cm3_utils_operation_context_get();
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_card_prepare_record() RETURNS trigger AS $$ BEGIN
    IF _cm3_class_is_standard(TG_RELID::regclass) OR _cm3_class_is_process(TG_RELID::regclass) THEN
        IF NEW."IdTenant" <= 0 THEN
            NEW."IdTenant" = NULL;
        END IF;
    END IF;
    IF TG_OP = 'UPDATE' THEN
        IF NEW."Id" <> OLD."Id" THEN
            RAISE EXCEPTION 'CMO 201: operation not allowed: cannot modify card id';
        END IF;
        IF NEW."Status" = 'N' AND OLD."Status" = 'N' THEN
            RAISE EXCEPTION 'CMO 201: operation not allowed: cannot modify this card because its "Status" is ''N''';
        END IF;
    ELSEIF TG_OP = 'INSERT' THEN
        IF NEW."Status" IS NULL THEN
            NEW."Status" = 'A';
        ELSEIF NEW."Status" = 'N' THEN
            RAISE EXCEPTION 'CMO 201: operation not allowed: cannot INSERT a card with status "N"';
        END IF;
        IF NEW."Id" IS NULL OR NEW."Id" <= 0 THEN
            NEW."Id" = _cm3_utils_new_card_id();
        END IF;
        IF _cm3_class_is_domain(TG_RELID::regclass) THEN
            NEW."IdDomain" = TG_RELID;
            IF NEW."IdClass1" IS NULL THEN
                NEW."IdClass1" = (SELECT "IdClass" FROM "Class" WHERE "Id" = NEW."IdObj1" AND "Status" = 'A');
            END IF;
            IF NEW."IdClass2" IS NULL THEN
                NEW."IdClass2" = (SELECT "IdClass" FROM "Class" WHERE "Id" = NEW."IdObj2" AND "Status" = 'A');
            END IF;
            PERFORM _cm3_domain_source_check(TG_RELID::regclass, NEW."IdClass1");
            PERFORM _cm3_domain_target_check(TG_RELID::regclass, NEW."IdClass2");
        ELSE
                NEW."IdClass" = TG_RELID;
        END IF;
    ELSEIF TG_OP = 'DELETE' AND OLD."Status" = 'N' THEN
        IF NOT _cm3_class_is_domain(TG_RELID::regclass) AND _cm3_class_comment_get(TG_RELID::regclass, 'FASTDEL') != 'true' THEN
            EXECUTE format('DELETE FROM "Map" WHERE ( "IdObj1" = %s OR "IdObj2" = %s ) AND "Status" = ''N''', OLD."Id", OLD."Id");
            EXECUTE format('DELETE FROM "Map" WHERE ( "IdObj1" = %s OR "IdObj2" = %s ) AND "Status" = ''U''', OLD."Id", OLD."Id");
        END IF;
        EXECUTE format('DELETE FROM "%s_history" WHERE "CurrentId" = %s', _cm3_utils_regclass_to_name(TG_RELID::regclass), OLD."Id");
        RETURN OLD;
    ELSE
        RAISE EXCEPTION 'CMO 201: operation not allowed: you cannot execute % on this table', TG_OP;
    END IF;
    IF NEW."Status" !~ '^[AND]$' THEN
        RAISE EXCEPTION 'CMO 201: operation not allowed: invalid card status = %', NEW."Status";
    END IF;
    NEW."CurrentId" = NEW."Id";
    NEW."BeginDate" = now();
    NEW."User" = _cm3_utils_operation_context_get();
    RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_superclass_forbid_operations() RETURNS trigger AS $$ BEGIN
	RAISE EXCEPTION 'CMO 201: operation not allowed: you cannot execute % on superclass table', TG_OP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_card_create_history() RETURNS trigger AS $$ BEGIN
	OLD."Id" = _cm3_utils_new_card_id();
	OLD."Status" = 'U';
	OLD."EndDate" = now();
	EXECUTE format('INSERT INTO "%s_history" (%s) VALUES ( (%L::%s).* )', _cm3_utils_regclass_to_name(TG_RELID::regclass), _cm3_attribute_list_agg(TG_RELID::regclass), OLD, TG_RELID::regclass);
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_card_cascade_delete_on_relations() RETURNS trigger AS $$ BEGIN --TODO rename trigger
	IF NEW."Status" = 'N' AND OLD."Status" = 'A' THEN
        PERFORM _cm3_card_cascade_after_delete(OLD."Id");
	END IF;
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_simplecard_cascade_delete_on_relations() RETURNS trigger AS $$ BEGIN --TODO rename trigger
    RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_card_enforce_foreign_key_for_target() RETURNS trigger AS $$ DECLARE --only for foreign key, not for references!
	_class regclass = TG_ARGV[0]::regclass;
	_attr varchar = TG_ARGV[1];
	_attribute_type varchar = coalesce(TG_ARGV[2], 'bigint');
    _attribute_path varchar = TG_ARGV[3];
    _cascade_action varchar;
    _attrtype_is_text boolean = _attribute_type ~ 'varchar|text'; --TODO improve attr by type
    _expr varchar;
    _exists boolean;
BEGIN
	IF ( TG_OP = 'UPDATE' AND NEW."Status" = 'N' ) OR TG_OP = 'DELETE' THEN
            _cascade_action = _cm3_utils_first_not_blank(_cm3_attribute_features_get(_class, _attr, 'CASCADE'), 'restrict');
            IF _attribute_path IS NOT NULL THEN
                IF _attrtype_is_text THEN
                    _expr = format('%I->>%L', _attr, _attribute_path);
                ELSE
                    _expr = format('(%I->>%L)::bigint', _attr, _attribute_path);
                END IF;
            ELSE
                _expr = format('%I', _attr);
            END IF;
            IF _cascade_action = 'delete' THEN
                IF _attrtype_is_text THEN
                    PERFORM _cm3_cards_delete(_class, format('%s = %L', _expr, OLD."Code"));
                ELSE
                    PERFORM _cm3_cards_delete(_class, format('%s = %L', _expr, OLD."Id"));
                END IF;
            ELSEIF _cascade_action = 'setnull' THEN
                IF _attrtype_is_text THEN
                    PERFORM _cm3_cards_update(_class, format('%s = NULL', _expr), format('%s = %L', _expr, OLD."Code"));
                ELSE
                    PERFORM _cm3_cards_update(_class, format('%s = NULL', _expr), format('%s = %L', _expr, OLD."Id"));
                END IF;
            ELSE
                IF _attrtype_is_text THEN
                    EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %s = %L AND "Status" = ''A'')', _class, _expr, OLD."Code") INTO _exists;
                ELSE
                    EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %s = %L AND "Status" = ''A'')', _class, _expr, OLD."Id") INTO _exists;
                END IF;
                IF _exists THEN
                    RAISE EXCEPTION 'CMO 203: reference integrity violation: found matching record in class = % with fk attr = %', _class, _attr;
                END IF;
            END IF;
        END IF;
        IF TG_OP = 'UPDATE' THEN
            RETURN NEW;
        ELSE
            RETURN OLD;
        END IF;
END $$	LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION _cm3_trigger_card_enforce_foreign_key_for_source() RETURNS trigger AS $$ DECLARE
	_attribute_name varchar = TG_ARGV[0];
	_class regclass = TG_ARGV[1]::regclass;
	_reference_id bigint;
	_reference_code varchar;
	_attribute_type varchar = coalesce(TG_ARGV[2],'bigint');
    _attribute_path varchar = TG_ARGV[3];
BEGIN
    IF NEW."Status" = 'A' THEN
        IF _attribute_type ~ 'varchar|text' THEN
            IF _attribute_path IS NOT NULL THEN
                EXECUTE format('SELECT ((%L::%s).%I)->>%L', NEW, TG_RELID::regclass, _attribute_name, _attribute_path) INTO _reference_code;
            ELSE
                EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _attribute_name) INTO _reference_code;
            END IF;
            IF _cm3_utils_is_not_blank(_reference_code) AND NOT _cm3_card_exists_with_code(_class, _reference_code, NEW."Status" = 'A') THEN
                RAISE 'CMO 206: error while inserting new % record: card not found for class = % code = % (referenced from attr %.% )', TG_RELID::regclass, _class, _reference_code, _cm3_utils_regclass_to_name(TG_RELID::regclass), _attribute_name;
            END IF;
        ELSE
            IF _attribute_path IS NOT NULL THEN
                EXECUTE format('SELECT (((%L::%s).%I)->>%L)::bigint', NEW, TG_RELID::regclass, _attribute_name, _attribute_path) INTO _reference_id;
            ELSE
                EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _attribute_name) INTO _reference_id;
            END IF;
            IF _reference_id IS NOT NULL AND NOT _cm3_card_exists_with_id(_class, _reference_id, NEW."Status" = 'A') THEN
                RAISE 'CMO 206: error while inserting new % record: card not found for class = % card_id = % (referenced from attr %.% )', TG_RELID::regclass, _class, _reference_id, _cm3_utils_regclass_to_name(TG_RELID::regclass), _attribute_name;
            END IF;
        END IF;
    END IF;
    RETURN NEW;
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION _cm3_trigger_simplecard_enforce_foreign_key_for_source() RETURNS trigger AS $$ DECLARE
	_attribute_name varchar = TG_ARGV[0];
	_class regclass = TG_ARGV[1]::regclass;
	_reference_id bigint;
	_reference_code varchar;
	_attribute_type varchar = coalesce(TG_ARGV[2],'bigint');
BEGIN
    IF _attribute_type ~ 'varchar|text' THEN
        EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _attribute_name) INTO _reference_code;
        IF _cm3_utils_is_not_blank(_reference_code) AND NOT _cm3_card_exists_with_code(_class, _reference_code, FALSE) THEN
            RAISE 'CMO 206: error while inserting new % record: card not found for class = % code = % (referenced from attr %.% )', TG_RELID::regclass, _class, _reference_code, _cm3_utils_regclass_to_name(TG_RELID::regclass), _attribute_name;
        END IF;
    ELSE
        EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _attribute_name) INTO _reference_id;
        IF _reference_id IS NOT NULL AND NOT _cm3_card_exists_with_id(_class, _reference_id, FALSE) THEN
            RAISE 'CMO 206: error while inserting new % record: card not found for class = % card_id = % (referenced from attr %.% )', TG_RELID::regclass, _class, _reference_id, _cm3_utils_regclass_to_name(TG_RELID::regclass), _attribute_name;
        END IF;
    END IF;
    RETURN NEW;
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION _cm3_trigger_relation_update_references() RETURNS trigger AS $$ DECLARE
	_attribute_name text = TG_ARGV[1];
	_class regclass = TG_ARGV[0]::regclass;
	_direction varchar = TG_ARGV[2];
	_card_column text = CASE WHEN _direction = 'direct' THEN 'IdObj1' ELSE 'IdObj2' END;
	_reference_column text = CASE WHEN _direction = 'direct' THEN 'IdObj2' ELSE 'IdObj1' END;
	_old_card_id bigint;
	_new_card_id bigint;
	_old_ref_value bigint;
	_new_ref_value bigint;
BEGIN
	IF NEW."Status" IN ('A','N') THEN

		RAISE DEBUG 'relation_update_references domain = % to attr = %.%', _cm3_utils_regclass_to_domain_name(TG_RELID::regclass), _cm3_utils_regclass_to_name(_class), _attribute_name;

		EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _card_column) INTO _new_card_id;

		IF NEW."Status" = 'A' THEN
			EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _reference_column) INTO _new_ref_value;
		END IF;

		IF TG_OP = 'UPDATE' THEN
			EXECUTE format('SELECT (%L::%s).%I', OLD, TG_RELID::regclass, _card_column) INTO _old_card_id;
			IF _old_card_id <> _new_card_id THEN
				PERFORM _cm3_reference_update(_class, _attribute_name, _old_card_id, NULL);
			ELSE
				EXECUTE format('SELECT (%L::%s).%I', OLD, TG_RELID::regclass, _reference_column) INTO _old_ref_value;
			END IF;
		END IF;

		RAISE DEBUG 'relation_update_references domain = %: old rel = % -> %, new rel = % -> % (direction = %)', _cm3_utils_regclass_to_domain_name(TG_RELID::regclass), _old_card_id, _old_ref_value, _new_card_id, _new_ref_value, _direction;

		IF _new_ref_value IS DISTINCT FROM _old_ref_value THEN
			PERFORM _cm3_reference_update( _class, _attribute_name, _new_card_id, _new_ref_value);
		END IF;

	END IF;
	RETURN NEW;
END $$	LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION _cm3_trigger_card_update_relations() RETURNS trigger AS $$ DECLARE
	_attribute_name text = TG_ARGV[0];
	_domain regclass = TG_ARGV[1]::regclass;
	_direction varchar = lower(TG_ARGV[2]);
	_cardinality varchar;
    _query varchar;
	_target_id_old bigint;
	_target_id_new bigint;
	_source_id bigint = NEW."Id";
	_source_class regclass = TG_RELID::regclass;
	_target_class regclass = _cm3_utils_name_to_regclass(_cm3_class_comment_get(_domain, CASE _direction WHEN 'direct' THEN 'CLASS2' ELSE 'CLASS1' END));
    _exists boolean;
BEGIN
	IF TG_OP = 'UPDATE' THEN
		EXECUTE format('SELECT (%L::%s).%I', OLD, TG_RELID::regclass, _attribute_name) INTO _target_id_old;
	END IF;
	EXECUTE format('SELECT (%L::%s).%I', NEW, TG_RELID::regclass, _attribute_name) INTO _target_id_new;
    IF _target_id_new IS NOT NULL AND _cm3_class_is_superclass(_target_class) THEN
        EXECUTE format('SELECT "IdClass" FROM %s WHERE "Id" = %L AND "Status" = ''A''', _target_class, _target_id_new) INTO _target_class;
    END IF;
	RAISE DEBUG 'card_update_relations: direction = %, old target = %, new target = % %', _direction, _target_id_old, _target_class, _target_id_new;
	IF _target_id_new IS DISTINCT FROM _target_id_old AND (_target_id_new IS NOT NULL OR _target_id_old IS NOT NULL) THEN
		IF _target_id_old IS NULL THEN
			RAISE DEBUG 'card_update_relations: insert relation record = % -> % (%)', _source_id, _target_id_new, _direction;
            EXECUTE format(CASE _direction
                WHEN 'direct' THEN 'SELECT EXISTS (SELECT * FROM %s WHERE "IdClass1" = %L::regclass AND "IdObj1" = %L AND "IdClass2" = %L::regclass AND "IdObj2" = %L AND "Status" = ''A'')'
                    ELSE 'SELECT EXISTS (SELECT * FROM %s WHERE "IdClass2" = %L::regclass AND "IdObj2" = %L AND "IdClass1" = %L::regclass AND "IdObj1" = %L AND "Status" = ''A'')' END,
                    _domain, _source_class, _source_id, _target_class, _target_id_new) INTO _exists;
            IF _exists THEN
                RAISE DEBUG 'card_update_relations: relation exists already, skip insert';
            ELSE
                _cardinality = _cm3_class_comment_get(_domain, 'CARDIN');
                IF _direction = 'direct' THEN
                    _query = 'INSERT INTO %s ("IdDomain","IdClass1","IdObj1","IdClass2","IdObj2","Status") VALUES (%L,%L,%L,%L,%L,''A'')';
                    IF _cardinality LIKE '1:%' THEN
                        _query = _query || ' ON CONFLICT ("IdClass2","IdObj2") WHERE "Status" = ''A'' DO UPDATE SET "IdClass1" = excluded."IdClass1", "IdObj1" = excluded."IdObj1"';
                    END IF;
                ELSE
                    _query = 'INSERT INTO %s ("IdDomain","IdClass2","IdObj2","IdClass1","IdObj1","Status") VALUES (%L,%L,%L,%L,%L,''A'')';
                    IF _cardinality LIKE '%:1' THEN
                        _query = _query || ' ON CONFLICT ("IdClass1","IdObj1") WHERE "Status" = ''A'' DO UPDATE SET "IdClass2" = excluded."IdClass2", "IdObj2" = excluded."IdObj2"';
                    END IF;
                END IF;
                EXECUTE format(_query, _domain, _domain, _source_class, _source_id, _target_class, _target_id_new);
            END IF;
		ELSEIF _target_id_new IS NULL THEN
			RAISE DEBUG 'card_update_relations: delete relation record = % -> % (%)', _source_id, _target_id_old, _direction;
			IF _direction = 'direct' THEN
				EXECUTE format('UPDATE %s SET "Status" = ''N'' WHERE "Status" = ''A'' AND "IdObj1" = %L', _domain, _source_id);
			ELSE
				EXECUTE format('UPDATE %s SET "Status" = ''N'' WHERE "Status" = ''A'' AND "IdObj2" = %L', _domain, _source_id);
			END IF;
		ELSE
			RAISE DEBUG 'card_update_relations: update relation record = % -> % (%)', _source_id, _target_id_new, _direction;
			IF _direction = 'direct' THEN
				EXECUTE format('UPDATE %s SET "IdClass2" = %L, "IdObj2" = %L WHERE "IdObj1" = %L AND "Status" = ''A'' AND "IdObj2" <> %L', _domain, _target_class, _target_id_new, _source_id, _target_id_new);
			ELSE
				EXECUTE format('UPDATE %s SET "IdClass1" = %L, "IdObj1" = %L WHERE "IdObj2" = %L AND "Status" = ''A'' AND "IdObj1" <> %L', _domain, _target_class, _target_id_new, _source_id, _target_id_new);
			END IF;
		END IF;
	END IF;
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.3.0-32_lookup_access_type
CREATE OR REPLACE FUNCTION _cm3_trigger_lookup_view() RETURNS trigger AS $$ BEGIN
    RAISE NOTICE 'modify lookup ( view) with op = % record = %', TG_OP, NEW;
    IF TG_OP = 'INSERT' THEN
        IF NEW."Code" = 'org.cmdbuild.LOOKUPTYPE' THEN
            INSERT INTO "_LookupType" ("Code", "Description", "Notes", "ParentType", "Access", "Speciality")
                VALUES (NEW."Type", NEW."Description", NEW."Notes", NEW."ParentType", NEW."AccessType", NEW."Speciality");
        ELSE
            IF NOT EXISTS (SELECT * FROM "_LookupType" WHERE "Status" = 'A' AND "Code" = NEW."Type") THEN
                INSERT INTO "_LookupType" ("Code") VALUES (NEW."Type");
            END IF;
            INSERT INTO "_LookupValue" ("Type", "Code", "Description", "Notes", "ParentValue", "Index", "Active", "Config")
                VALUES (NEW."Type", NEW."Code", NEW."Description", NEW."Notes", NEW."ParentId", NEW."Index", NEW."IsActive", NEW."Config" || jsonb_build_object(
                    'cm_is_default', NEW."IsDefault",
                    'cm_icon_type', NEW."IconType",
                    'cm_icon_image', NEW."IconImage",
                    'cm_icon_font', NEW."IconFont",
                    'cm_icon_color', NEW."IconColor",
                    'cm_text_color', NEW."TextColor"
                ));
        END IF;
        RETURN (SELECT x FROM "_LookupView" x WHERE "Code" = NEW."Code" AND "Type" = NEW."Type" AND "Status" = 'A');
    ELSEIF TG_OP = 'UPDATE' THEN
        IF OLD."Code" = 'org.cmdbuild.LOOKUPTYPE' THEN
            UPDATE "_LookupType" SET
                "Description" = NEW."Description",
                "Notes" = NEW."Notes",
                "Access" = NEW."AccessType",
                "Speciality" = NEW."Speciality",
                "Status" = NEW."Status",
                "Code" = NEW."Type",
                "ParentType" = NEW."ParentType"
                WHERE "Id" = NEW."Id";
        ELSE
            UPDATE "_LookupValue" SET
                "Description" = NEW."Description",
                "Notes" = NEW."Notes",
                "ParentValue" = NEW."ParentId",
                "Index" = NEW."Index",
                "Active" = NEW."IsActive",
                "Status" = NEW."Status",
                "Code" = NEW."Code",
                "Type" = NEW."Type",
                "Config" = NEW."Config" || jsonb_build_object(
                    'cm_is_default', NEW."IsDefault",
                    'cm_icon_type', NEW."IconType",
                    'cm_icon_image', NEW."IconImage",
                    'cm_icon_font', NEW."IconFont",
                    'cm_icon_color', NEW."IconColor",
                    'cm_text_color', NEW."TextColor"
                ) WHERE "Id" = NEW."Id";
        END IF;
        RETURN NEW;
    ELSEIF TG_OP = 'DELETE' THEN
        RAISE 'CMO 202: delete not allowed';
    END IF;
    RETURN NEW;
END $$ LANGUAGE plpgsql;

-- REQUIRE PATCH 3.3.0-46_lookup_refresh_trigger_improvements_2
CREATE OR REPLACE FUNCTION _cm3_trigger_lookup_view() RETURNS trigger AS $$ BEGIN
    RAISE NOTICE 'modify lookup ( view) with op = % record = %', TG_OP, NEW;
    IF TG_OP = 'INSERT' THEN
        IF NEW."Code" = 'org.cmdbuild.LOOKUPTYPE' THEN
            INSERT INTO "_LookupType" ("Code", "Description", "Notes", "ParentType", "Access", "Speciality")
                VALUES (NEW."Type", NEW."Description", NEW."Notes", (SELECT "Id" FROM "_LookupType" WHERE "Status" = 'A' AND "Code" = NEW."ParentType"), NEW."AccessType", NEW."Speciality");
        ELSE
            IF NOT EXISTS (SELECT * FROM "_LookupType" WHERE "Status" = 'A' AND "Code" = NEW."Type") THEN
                INSERT INTO "_LookupType" ("Code") VALUES (NEW."Type");
            END IF;
            INSERT INTO "_LookupValue" ("Type", "Code", "Description", "Notes", "ParentValue", "Index", "Active", "Config")
                VALUES ((SELECT "Id" FROM "_LookupType" WHERE "Status" = 'A' AND "Code" = NEW."Type"), NEW."Code", NEW."Description", NEW."Notes", NEW."ParentId", NEW."Index", NEW."IsActive", NEW."Config" || jsonb_build_object(
                    'cm_is_default', NEW."IsDefault",
                    'cm_icon_type', NEW."IconType",
                    'cm_icon_image', NEW."IconImage",
                    'cm_icon_font', NEW."IconFont",
                    'cm_icon_color', NEW."IconColor",
                    'cm_text_color', NEW."TextColor"
                ));
        END IF;
        RETURN (SELECT x FROM "_LookupView" x WHERE "Code" = NEW."Code" AND "Type" = NEW."Type" AND "Status" = 'A');
    ELSEIF TG_OP = 'UPDATE' THEN
        IF OLD."Code" = 'org.cmdbuild.LOOKUPTYPE' THEN
            UPDATE "_LookupType" SET
                "Description" = NEW."Description",
                "Notes" = NEW."Notes",
                "Access" = NEW."AccessType",
                "Speciality" = NEW."Speciality",
                "Status" = NEW."Status",
                "Code" = NEW."Type",
                "ParentType" = CASE NEW."Status" WHEN 'A' THEN (SELECT "Id" FROM "_LookupType" WHERE "Status" = 'A' AND "Code" = NEW."ParentType") ELSE "ParentType" END
                WHERE "Id" = NEW."Id";
        ELSE
            UPDATE "_LookupValue" SET
                "Description" = NEW."Description",
                "Notes" = NEW."Notes",
                "ParentValue" = NEW."ParentId",
                "Index" = NEW."Index",
                "Active" = NEW."IsActive",
                "Status" = NEW."Status",
                "Code" = NEW."Code",
                "Config" = NEW."Config" || jsonb_build_object(
                    'cm_is_default', NEW."IsDefault",
                    'cm_icon_type', NEW."IconType",
                    'cm_icon_image', NEW."IconImage",
                    'cm_icon_font', NEW."IconFont",
                    'cm_icon_color', NEW."IconColor",
                    'cm_text_color', NEW."TextColor"
                ) WHERE "Id" = NEW."Id";
        END IF;
        RETURN NEW;
    ELSEIF TG_OP = 'DELETE' THEN
        RAISE 'CMO 202: delete not allowed';
    END IF;
    RETURN NEW;
END $$ LANGUAGE plpgsql;

-- REQUIRE PATCH 3.3.0-32_lookup_access_type
CREATE OR REPLACE FUNCTION _cm3_trigger_lookup_to_view() RETURNS trigger AS $$ BEGIN
    IF NEW."Config"->>'cm_view_refresh' = 'true' THEN
        NEW."Config" = NEW."Config" - 'cm_view_refresh';
        RETURN NEW;
    ELSEIF TG_OP = 'INSERT' THEN
        RAISE NOTICE 'modify lookup (table) with op = % record = %', TG_OP, NEW;
        INSERT INTO "_LookupView" VALUES (NEW."Id",NEW."IdClass",NEW."Code",NEW."Description",NEW."Status",NEW."User",NEW."BeginDate",NEW."Notes",NEW."EndDate",NEW."CurrentId",NEW."IdTenant",NEW."Type",NEW."ParentType",NEW."IsDefault",NEW."IconType",NEW."IconImage",NEW."IconFont",NEW."Index",NEW."IconColor",NEW."TextColor",NEW."IsActive",NEW."ParentId",NEW."Speciality",NEW."Config",NEW."AccessType");
        RETURN NULL;
    ELSEIF TG_OP = 'UPDATE' THEN
        RAISE NOTICE 'modify lookup (table) with op = % record = %', TG_OP, NEW;
        UPDATE "_LookupView" SET
                "Code" = NEW."Code",
                "Description" = NEW."Description",
                "Status" = NEW."Status",
                "Notes" = NEW."Notes",
                "CurrentId" = NEW."CurrentId",
                "IdTenant" = NEW."IdTenant",
                "Type" = NEW."Type",
                "ParentType" = NEW."ParentType",
                "IsDefault" = NEW."IsDefault",
                "IconType" = NEW."IconType",
                "IconImage" = NEW."IconImage",
                "IconFont" = NEW."IconFont",
                "Index" = NEW."Index",
                "IconColor" = NEW."IconColor",
                "TextColor" = NEW."TextColor",
                "IsActive" = NEW."IsActive",
                "ParentId" = NEW."ParentId",
                "Speciality" = NEW."Speciality",
                "Config" = NEW."Config",
                "AccessType" = NEW."AccessType"
            WHERE "Id" = NEW."Id";
        RETURN NULL;
    END IF;
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION _cm3_trigger_lookup_refresh() RETURNS trigger AS $$ BEGIN
    DELETE FROM "LookUp";
    INSERT INTO "LookUp"
            ("Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant","Type","ParentType","IsDefault","IconType","IconImage","IconFont","Index","IconColor","TextColor","IsActive","ParentId","Speciality","Config","AccessType")
        SELECT
            "Id","IdClass","Code","Description","Status","User","BeginDate","Notes","EndDate","CurrentId","IdTenant","Type","ParentType","IsDefault","IconType","IconImage","IconFont","Index","IconColor","TextColor","IsActive","ParentId","Speciality",
            "Config" || jsonb_build_object('cm_view_refresh', 'true'), "AccessType" FROM "_LookupView";
    RETURN NULL;
END $$ LANGUAGE plpgsql;

-- REQUIRE PATCH 3.1.0-01_attribute_groups

CREATE OR REPLACE FUNCTION _cm3_trigger_attribute_group() RETURNS trigger AS $$ DECLARE
    _sub_class regclass;
BEGIN
    FOR _sub_class IN SELECT _cm3_class_list_descendant_classes(NEW."Owner") LOOP
        IF NOT EXISTS (SELECT * FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _sub_class AND "Code" = NEW."Code") THEN
            INSERT INTO "_AttributeGroup" ("Code", "Description", "Index", "Owner") VALUES (NEW."Code", NEW."Description", COALESCE((SELECT MAX("Index")+1 FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _sub_class), 1), _sub_class);
            RAISE NOTICE 'copy attribute group = % from class = % to class = %', NEW."Code", NEW."Owner", _sub_class;
        ELSEIF TG_OP = 'UPDATE' THEN
            IF NEW."Description" <> OLD."Description" THEN
                UPDATE "_AttributeGroup" SET "Description" = NEW."Description" WHERE "Status" = 'A' AND "Owner" = _sub_class AND "Code" = NEW."Code";
            END IF;
            IF NEW."Index" <> OLD."Index" THEN
                UPDATE "_AttributeGroup" SET "Index" = NEW."Index" WHERE "Status" = 'A' AND "Owner" = _sub_class AND "Code" = NEW."Code"; --TODO improve index processing
            END IF;
        END IF;
    END LOOP;
    RETURN NULL;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.3.0-29_class_structure
CREATE OR REPLACE FUNCTION _cm3_trigger_attribute_group() RETURNS trigger AS $$ DECLARE
    _sub_class regclass;
BEGIN
    FOR _sub_class IN SELECT _cm3_class_list_descendant_classes(_cm3_utils_name_to_regclass(NEW."Owner")) LOOP
        IF NOT EXISTS (SELECT * FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _cm3_utils_regclass_to_name(_sub_class) AND "Code" = NEW."Code") THEN
            INSERT INTO "_AttributeGroup" ("Code", "Description", "Index", "Owner") VALUES (NEW."Code", NEW."Description", COALESCE((SELECT MAX("Index")+1 FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _cm3_utils_regclass_to_name(_sub_class)), 1), _cm3_utils_regclass_to_name(_sub_class));
            RAISE NOTICE 'copy attribute group = % from class = % to class = %', NEW."Code", NEW."Owner", _sub_class;
        ELSEIF TG_OP = 'UPDATE' THEN
            IF NEW."Description" <> OLD."Description" THEN
                UPDATE "_AttributeGroup" SET "Description" = NEW."Description" WHERE "Status" = 'A' AND "Owner" = _cm3_utils_regclass_to_name(_sub_class) AND "Code" = NEW."Code";
            END IF;
            IF NEW."Index" <> OLD."Index" THEN
                UPDATE "_AttributeGroup" SET "Index" = NEW."Index" WHERE "Status" = 'A' AND "Owner" = _cm3_utils_regclass_to_name(_sub_class) AND "Code" = NEW."Code"; --TODO improve index processing
            END IF;
        END IF;
    END LOOP;
    RETURN NULL;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.1.0-13_gis_theme_rules

CREATE OR REPLACE FUNCTION _cm3_trigger_filter_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'filter' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    --TODO default filter cleanup from classes ??
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_ietemplate_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'etlemplate' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    --TODO trigger config cleanup? other cleanup?
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_view_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'view' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    UPDATE "_Menu" SET "Data" = _cm3_utils_menu_from_list((SELECT jsonb_agg(x) FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x WHERE NOT (x->>'type' = 'view' AND x->>'target' = OLD."Code"))) WHERE "Status" = 'A' AND EXISTS (SELECT x FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x
        WHERE x->>'type' = 'view' AND x->>'target' = OLD."Code");
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_report_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'report' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    UPDATE "_Menu" SET "Data" = _cm3_utils_menu_from_list((SELECT jsonb_agg(x) FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x WHERE NOT (x->>'type' ILIKE 'report%' AND x->>'target' = OLD."Code"))) WHERE "Status" = 'A' AND EXISTS (SELECT x FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x
        WHERE x->>'type' ILIKE 'report%' AND x->>'target' = OLD."Code");
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_custompage_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'custompage' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    UPDATE "_Menu" SET "Data" = _cm3_utils_menu_from_list((SELECT jsonb_agg(x) FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x WHERE NOT (x->>'type' = 'custompage' AND x->>'target' = OLD."Code"))) WHERE "Status" = 'A' AND EXISTS (SELECT x FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x
        WHERE x->>'type' = 'custompage' AND x->>'target' = OLD."Code");
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_dashboard_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'dashboard' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    UPDATE "_Menu" SET "Data" = _cm3_utils_menu_from_list((SELECT jsonb_agg(x) FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x WHERE NOT (x->>'type' = 'dashboard' AND x->>'target' = OLD."Code"))) WHERE "Status" = 'A' AND EXISTS (SELECT x FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x
        WHERE x->>'type' = 'dashboard' AND x->>'target' = OLD."Code");
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_etlgate_cleanup() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" = 'etlgate' AND "Status" = 'A' AND "ObjectId" = OLD."Id" LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    --TODO menu cleanup? other cleanup?
	RETURN NEW;
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION _cm3_trigger_menu_validation() RETURNS trigger AS $$ DECLARE
    _to_process jsonb;
    _element jsonb;
    _codes varchar[];
    _duplicates varchar[];
BEGIN
    _to_process = NEW."Data"->'children';
    _codes = ARRAY[]::varchar[];
    WHILE jsonb_array_length(_to_process) > 0 LOOP
        _element = _to_process->0;
        _codes = array_append(_codes, (_element->>'code')::varchar);
        _to_process = ( _to_process - 0 ) || ( _element->'children' );
    END LOOP;
    SELECT array_agg(_code) INTO _duplicates FROM (SELECT x _code, count(*) _count FROM unnest(_codes) x GROUP BY x) q WHERE _count > 1;
    IF cardinality(_duplicates) > 0 THEN
        RAISE EXCEPTION 'CMO 207: invalid menu data for record = %, duplicate node ids = %', NEW."Id", _duplicates;
    END IF;

    RAISE NOTICE 'menu data is OK for menu = %', NEW."Id";
    RETURN NEW;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.2.0-55_application_event_log

CREATE OR REPLACE FUNCTION _cm3_trigger_session_events() RETURNS trigger AS $$ DECLARE
    _record RECORD;
BEGIN
    IF TG_OP = 'INSERT' THEN
        PERFORM _cm3_event_log('cm_session_create',
            'username', _cm3_utils_check_not_blank(NEW."Data"#>>'{operationUserStack,0,authenticatedUser,username}'),
            'groups', (SELECT string_agg(x, ', ') FROM jsonb_array_elements_text(NEW."Data" #> '{operationUserStack,0,privilegeContext,groups}') AS x),
            'activeTenantIds', (SELECT string_agg(x, ', ') FROM jsonb_array_elements_text(NEW."Data" #> '{operationUserStack,0,userTenantContext,activeTenantIds}') AS x),
            'ignoreTenantPolicies', NEW."Data" #>> '{operationUserStack,0,userTenantContext,ignoreTenantPolicies}',
            'sessionId', _cm3_utils_check_not_blank(NEW."SessionId"));
    ELSEIF TG_OP = 'UPDATE' THEN
        PERFORM _cm3_event_log('cm_session_update',
            'username', _cm3_utils_check_not_blank(NEW."Data"#>>'{operationUserStack,0,authenticatedUser,username}'),
            'groups', (SELECT string_agg(x, ', ') FROM jsonb_array_elements_text(NEW."Data" #> '{operationUserStack,0,privilegeContext,groups}') AS x),
            'activeTenantIds', (SELECT string_agg(x, ', ') FROM jsonb_array_elements_text(NEW."Data" #> '{operationUserStack,0,userTenantContext,activeTenantIds}') AS x),
            'ignoreTenantPolicies', NEW."Data" #>> '{operationUserStack,0,userTenantContext,ignoreTenantPolicies}',
            'sessionId', _cm3_utils_check_not_blank(NEW."SessionId"));
    ELSEIF TG_OP = 'DELETE' THEN
        PERFORM _cm3_event_log('cm_session_delete', 'username', _cm3_utils_check_not_blank(OLD."Data"#>>'{operationUserStack,0,authenticatedUser,username}'), 'sessionId', _cm3_utils_check_not_blank(OLD."SessionId"));
        DELETE FROM "_Lock" WHERE "SessionId" = OLD."SessionId";
    END IF;
    RETURN NULL;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.3.0-34_embedded_items

CREATE OR REPLACE FUNCTION _cm3_item_prepare_record(_item jsonb, _class regclass, _attr varchar, _card bigint) RETURNS jsonb AS $$ DECLARE
    _temp_id bigint;
    _id bigint;
BEGIN
    RAISE NOTICE 'prepare item =< % >', _item;
    IF _cm3_utils_is_blank(_item->>'Id') THEN
        INSERT INTO "_Items" ("Type", "OwnerClass", "Attr", "Card") VALUES (_item->>'IdClass', _class, _attr, _card) RETURNING "Id" INTO _id;
        _item = _item || jsonb_build_object('Id', _id);
    ELSEIF (_item->>'Id')::bigint < 0 THEN -- temp id
        _temp_id = (_item->>'Id')::bigint;
        INSERT INTO "_Items" ("Type", "OwnerClass", "Attr", "Card") VALUES (_item->>'IdClass', _class, _attr, _card) RETURNING "Id" INTO _id;
        _item = _item || jsonb_build_object('Id', _id);
        RAISE NOTICE 'map temp item id =< % > to actual id =< % >', _temp_id, _id;
        PERFORM set_config('cmdbuild.items.temp_id_to_actual_id', (current_setting('cmdbuild.items.temp_id_to_actual_id')::jsonb || jsonb_build_object(_temp_id::varchar, _id))::varchar, TRUE);
    ELSEIF NOT EXISTS ( SELECT * FROM "_Items" WHERE "Id" = (_item->>'Id')::bigint ) THEN
        INSERT INTO "_Items" ("Id", "Type", "OwnerClass", "Attr", "Card") VALUES ( (_item->>'Id')::bigint, _item->>'IdClass', _class, _attr, _card) RETURNING "Id" INTO _id;
    ELSEIF NOT EXISTS ( SELECT * FROM "_Items" WHERE "Id" = (_item->>'Id')::bigint AND "Type" = _item->>'IdClass' AND "OwnerClass" = _class AND "Attr" = _attr AND "Card" = _card) THEN
        RAISE 'invalid item =< % > with owner = %[%].%', _item, _class, _card, _attr;
    END IF;
    --TODO validate model structure, etc
    RETURN _item;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_trigger_items() RETURNS trigger AS $$ DECLARE
	_class regclass = TG_RELID::regclass;
	_attr varchar = TG_ARGV[0];
    _ref varchar;
BEGIN
    BEGIN
        PERFORM current_setting('cmdbuild.items.temp_id_to_actual_id'); --TODO cleanup ??
    EXCEPTION WHEN undefined_object THEN
        SET SESSION cmdbuild.items.temp_id_to_actual_id = '{}';
    END;
    IF _attr <> 'Items' THEN
        RAISE 'unsupported items attr name = %', _attr; -- unfortunately custom attrs are not supported yet
    END IF;
    IF TG_OP IN ('INSERT','UPDATE') AND COALESCE(NEW."Status", 'A') = 'A' THEN
        RAISE NOTICE 'prepare items, raw data =< % >', NEW."Items";
        NEW."Items" = COALESCE((SELECT jsonb_agg(i) FROM (SELECT _cm3_item_prepare_record(value, _class, _attr, NEW."Id") i FROM (SELECT * FROM jsonb_array_elements(NEW."Items")) z) x),'[]'::jsonb);
        RAISE NOTICE 'prepare items, processed data =< % >', NEW."Items";
        FOR _ref IN SELECT name FROM _cm3_attribute_list_detailed(_class) WHERE sql_type ='_int8' LOOP --TODO improve this, filter for reference and ref array types, filter noactive elements
            NEW = jsonb_populate_record(NEW, jsonb_build_object(_ref, (SELECT COALESCE( array_agg( CASE WHEN x.e::bigint < 0 THEN COALESCE(((current_setting('cmdbuild.items.temp_id_to_actual_id')::jsonb)->>x.e)::bigint, x.e::bigint) ELSE x.e::bigint END ), ARRAY[]::bigint[])::varchar FROM (SELECT jsonb_array_elements_text(to_jsonb(NEW)->_ref) e) x)));
        END LOOP;
        DELETE FROM "_Items" WHERE "OwnerClass" = _class AND "Attr" = _attr AND "Card" = NEW."Id" AND "Id" NOT IN (SELECT (value->>'Id')::bigint FROM jsonb_array_elements(NEW."Items") x);
    ELSEIF NEW."Status" = 'N' OR TG_OP = 'DELETE' THEN
        DELETE FROM "_Items" WHERE "OwnerClass" = _class AND "Attr" = _attr AND "Card" = OLD."Id";
    END IF;
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    ELSE
        RETURN NEW;
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_attribute_notnull() RETURNS trigger AS $$ DECLARE
    _attrname varchar = TG_ARGV[0];
BEGIN
    IF to_jsonb(NEW)->_attrname = 'null' AND NEW."Status" = 'A' THEN
        RAISE EXCEPTION 'CMO 203: % cannot be null', _attrname;
    END IF;
    RETURN NEW;
END $$ LANGUAGE PLPGSQL;