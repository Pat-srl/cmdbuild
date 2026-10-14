-- adding report column to contain xml field

DO $$ BEGIN
	IF NOT EXISTS (SELECT * FROM _cm3_attribute_list_detailed('"_Report"') WHERE "name" = 'Report') THEN
		ALTER TABLE "_Report" ADD COLUMN "Report" VARCHAR[] NOT NULL DEFAULT array[]::varchar[];
		PERFORM _cm3_attribute_comment_set('"_Report"','Report','MODE','rescore');
	END IF;
END $$ LANGUAGE PLPGSQL;