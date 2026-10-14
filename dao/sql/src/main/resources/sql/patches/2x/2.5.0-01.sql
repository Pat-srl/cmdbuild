-- BIM module functions revision

DROP FUNCTION public._bim_create_function_for_export();
DROP FUNCTION public._bim_update_coordinates(character varying, character varying, character varying, character varying, character varying);
DROP FUNCTION public._bim_store_data(integer, character varying, character varying, character varying, character varying, character varying);
DROP FUNCTION public._bim_set_coordinates(character varying, character varying, character varying);
DROP FUNCTION public._bim_set_room_geometry(character varying, character varying, character varying, character varying);

ALTER TABLE public."_BimProject" DROP COLUMN "ExportMapping";
ALTER TABLE public."_BimProject" DROP COLUMN "ExportProjectId";
ALTER TABLE public."_BimProject" DROP COLUMN "ShapesProjectId";
ALTER TABLE public."_BimProject" DROP COLUMN "Synchronized";

ALTER TABLE public."_BimLayer" DROP COLUMN "Export";
ALTER TABLE public."_BimLayer" DROP COLUMN "Container";

CREATE OR REPLACE FUNCTION public._bim_carddata_from_globalid(IN globalid character varying, OUT "Id" integer, OUT "IdClass" integer, OUT "Description" character varying, OUT "ClassName" character varying)
  RETURNS record AS
$BODY$
DECLARE
	query varchar;
	table_name varchar;
	tables CURSOR FOR SELECT tablename FROM pg_tables WHERE schemaname = 'bim' ORDER BY tablename;
	
BEGIN
	query='';
	FOR table_record IN tables LOOP
		query= query || '
		SELECT	b."Master" as "Id" , 
			p."Description" AS "Description", 
			p."IdClass"::integer as "IdClass" ,
			replace(p."IdClass"::text,''"'','''') as "ClassName"
		FROM bim."' || table_record.tablename || '" AS b 
			JOIN public."' ||  table_record.tablename || '" AS p 
			ON b."Master"=p."Id" 
		WHERE p."Status"=''A'' AND b."GlobalId" = ''' || globalid || ''' UNION ALL';
	END LOOP;

	SELECT substring(query from 0 for LENGTH(query)-9) INTO query;
	RAISE NOTICE 'execute query : %', query;
	EXECUTE(query) INTO "Id","Description","IdClass","ClassName";
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
COMMENT ON FUNCTION public._bim_carddata_from_globalid(character varying) IS 'TYPE: function';

