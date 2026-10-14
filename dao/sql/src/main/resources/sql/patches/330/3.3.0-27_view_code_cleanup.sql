-- removing unsupported chars and whitespaces from _View and adapting menu

UPDATE "_Menu" SET "Data" =  _cm3_utils_menu_from_list((select jsonb_agg(v) FROM (SELECT 
CASE WHEN value->>'type' = 'view' THEN 
    value || jsonb_build_object('target', regexp_replace(replace(value->>'target', ' ',''), '[^A-Za-z0-9áéíóúüÁÉÍÓÚÜ¡¿!?\-:]', '', 'g')) 
ELSE value END
    v FROM (SELECT * FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data"))) q) a)) WHERE "Status" = 'A';

DO $$ DECLARE
    _view record;
BEGIN
FOR _view IN SELECT * FROM "_View" LOOP
     UPDATE "_View" SET "Code"=regexp_replace(replace(_view."Code", ' ',''), '[^A-Za-z0-9áéíóúüÁÉÍÓÚÜ¡¿!?\-:]', '', 'g') WHERE "Status"='A' AND "Id"=_view."Id";
END LOOP;
END $$ LANGUAGE PLPGSQL;