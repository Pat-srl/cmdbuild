-- menu description cleanup

WITH q AS (
        SELECT unnest(ARRAY['class','processclass']) _type, table_name _code, _cm3_utils_first_not_blank(features->>'DESCR', table_name) _descr FROM _cm3_class_list_detailed()
        UNION 
        SELECT 'dashboard' _type, "Code" _code, _cm3_utils_first_not_blank("Description", "Code") _descr FROM "_Dashboard" WHERE "Status" = 'A'
        UNION 
        SELECT 'custompage' _type, "Code" _code, _cm3_utils_first_not_blank("Description", "Code") _descr FROM "_UiComponent" WHERE "Type" = 'custompage' AND "Status" = 'A'
        UNION 
        SELECT unnest(ARRAY['reportcsv','reportpdf','reportodt','reportxml']) _type, "Code" _code, _cm3_utils_first_not_blank("Description", "Code") _descr FROM "_Report" WHERE "Status" = 'A'
        UNION 
        SELECT 'view' _type, "Code" _code, _cm3_utils_first_not_blank("Description", "Code") _descr FROM "_View" WHERE "Status" = 'A'
    )
UPDATE "_Menu" SET "Data" =  _cm3_utils_menu_from_list((select jsonb_agg(v) FROM (SELECT 
        CASE WHEN EXISTS (SELECT * FROM q WHERE _type = value->>'type' AND _code = value->>'target' AND _descr = value->>'description') THEN value - 'description' ELSE value END 
    v FROM (SELECT * FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data"))) q) a)) WHERE "Status" = 'A';

