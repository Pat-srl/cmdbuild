-- refactoring of _Translation table

CREATE TABLE "_TranslationAUX" ("Id" integer,"User" varchar,"BeginDate" timestamp without time zone,"Code" varchar,"Lang" varchar,"Value" varchar,"Status" varchar);

INSERT INTO "_TranslationAUX" ("Id","User","BeginDate","Code","Lang","Value","Status") SELECT "Id","User","BeginDate","Element","Lang","Value",'A' FROM "_Translation";

DROP TABLE "_Translation";

DO $$
DECLARE
	_code varchar;
	_lang varchar;
BEGIN
	
	FOR _code, _lang IN
		SELECT r."Code",r."Lang" FROM (SELECT "Code","Lang",COUNT(*) c FROM "_TranslationAUX" GROUP BY "Code","Lang") r WHERE r.c > 1
	LOOP
		RAISE NOTICE 'multiple translations detected for code = % lang = %, keeping only the most recent', _code, _lang;

		UPDATE "_TranslationAUX" SET "Status" = 'N' WHERE "Id" IN ( SELECT "Id" FROM "_TranslationAUX" WHERE "Code" = _code AND "Lang" = _lang ORDER BY "BeginDate" DESC OFFSET 1 );
	END LOOP;

END;
$$ LANGUAGE PLPGSQL;

SELECT _cm3_class_create('_Translation', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Translations|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Translation"', 'Lang', 'varchar', 'NOTNULL: true|MODE: read|DESCR: Lang');
SELECT _cm3_attribute_create('"_Translation"', 'Value', 'varchar', 'MODE: read|DESCR: Value');
ALTER TABLE "_Translation" ALTER COLUMN "Code" SET NOT NULL;

SELECT _cm3_class_triggers_disable('"_Translation"');

INSERT INTO "_Translation" ("Id","CurrentId","IdClass","User","BeginDate","Code","Lang","Value","Status") SELECT "Id","Id",'"_Translation"'::regclass,"User","BeginDate","Code","Lang","Value","Status" FROM "_TranslationAUX";

SELECT _cm3_class_triggers_enable('"_Translation"');

SELECT _cm3_attribute_index_unique_create('"_Translation"', 'Code', 'Lang');

DROP TABLE "_TranslationAUX";
