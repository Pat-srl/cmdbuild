-- Fixes "_cm_comment_add_parts" function

CREATE OR REPLACE FUNCTION _cm_comment_add_parts(comment text, parts text[], missingOnly boolean) RETURNS text AS $$
DECLARE
	part text;
	key text;
	value text;
BEGIN
	RAISE INFO 'adding comment parts % inside "%" (missing only is %)', parts, comment, missingOnly;
	RAISE DEBUG 'comment (before): "%"', comment;
	-- we MUST use coalesce since "array_" functions handle empty array in some funny way
	FOR i IN COALESCE(array_lower(parts, 1),0) .. COALESCE(array_upper(parts, 1),-1) LOOP
		part = parts[i];
		RAISE DEBUG 'comment part: "%"', part;		
		key = split_part(part, ': ', 1);
		value = split_part(part, ': ', 2);
		IF NOT (missingOnly AND substring(comment, key || ': [^|]+') IS NOT NULL) THEN
			comment = concat_ws('|', comment, key || ': ' || value);
		ELSE
		END IF;
	END LOOP;
	RAISE DEBUG 'comment (after): "%"', comment;
	RETURN comment;
END
$$ LANGUAGE plpgsql VOLATILE;