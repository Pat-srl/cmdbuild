-- report table improvements
 
SELECT _cm3_attribute_create('OWNER: _Report|NAME: Sources|TYPE: varchar[]|MODE: rescore|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]');
SELECT _cm3_attribute_create('OWNER: _Report|NAME: Binaries|TYPE: bytea[]|MODE: rescore|NOTNULL: true|DEFAULT: ARRAY[]::bytea[]');
SELECT _cm3_attribute_create('OWNER: _Report|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb|MODE: rescore');

SELECT _cm3_class_triggers_disable('"_Report"');

UPDATE "_Report" SET "Binaries" = ARRAY["MainReport"] || "SubReports", "Sources" = "Report";

SELECT _cm3_class_triggers_enable('"_Report"');

ALTER TABLE "_Report" DROP COLUMN "MainReport";
ALTER TABLE "_Report" DROP COLUMN "SubReports";
ALTER TABLE "_Report" DROP COLUMN "Report";

ALTER TABLE "_Report" ADD CONSTRAINT "_cm3_Images_check" CHECK ( cardinality("Images") = cardinality("ImageNames") );
ALTER TABLE "_Report" ADD CONSTRAINT "_cm3_Report_check" CHECK ( ("Status" != 'A') OR ( ( cardinality("Sources") > 0 OR cardinality("Binaries") > 0 ) AND ( cardinality("Sources") = 0 OR cardinality("Binaries") = 0 OR cardinality("Sources") = cardinality("Binaries") ) ) );

