-- Creates _graph_get_related_classes function

CREATE OR REPLACE FUNCTION _graph_get_related_classes(IN "DomainName" character varying, IN "ClassName" character varying, IN "CardId" integer, OUT type character varying, OUT total integer)
  RETURNS SETOF record AS $$
BEGIN
RETURN QUERY
SELECT
	case when _cm_cmtable("IdClass2")::varchar=$2 then _cm_cmtable("IdClass1")::varchar else _cm_cmtable("IdClass2")::varchar end "type",
	count(*)::integer as "total"  FROM "Map"
	WHERE "Status" = 'A'
		and _cm_cmtable("IdDomain")::varchar=$1
		and (("IdObj1"=$3 and _cm_cmtable("IdClass1")::varchar=$2)
		or ("IdObj2"=$3 and _cm_cmtable("IdClass2")::varchar=$2))
	group by 1;
END
$$ LANGUAGE PLPGSQL;
COMMENT ON FUNCTION _graph_get_related_classes(character varying, character varying, integer) IS 'TYPE: function';
