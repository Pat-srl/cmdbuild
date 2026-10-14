-- adding send email job type to _Job constraint

ALTER TABLE "_Job" DROP CONSTRAINT "_cm3_Type_check";
ALTER TABLE "_Job" ADD CONSTRAINT "_cm3_Type_check" CHECK ("Status" <> 'A' OR "Type" IN ('script','emailService','export_file','import_file','stored_function','workflow','etl','import_database','sendemail') );
