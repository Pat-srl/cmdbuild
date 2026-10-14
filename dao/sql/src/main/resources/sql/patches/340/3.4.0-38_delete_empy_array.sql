-- added check data on arrays
-- PARAMS: FORCE_IF_NOT_EXISTS=true

CREATE OR REPLACE FUNCTION public._cm3_attribute_has_data(
        _class regclass,
        _attr character varying)
    RETURNS boolean
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
 DECLARE
    _has_data boolean;
BEGIN
    IF _cm3_class_is_simple(_class) THEN
        EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I IS NOT NULL AND %I::text <> '''')', _class, _attr, _attr) INTO _has_data;
    ELSE
        EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I IS NOT NULL AND %I::text <> '''' AND %I::text <> ''{}'' AND "Status" = ''A'')', _class, _attr, _attr, _attr) INTO _has_data;
    END IF;
    RETURN _has_data;
END 
$BODY$;
