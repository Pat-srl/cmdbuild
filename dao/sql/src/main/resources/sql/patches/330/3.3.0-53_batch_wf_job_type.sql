-- batch wf job type
 
ALTER TABLE "_Job" DROP CONSTRAINT "_cm3_Type_check";
ALTER TABLE "_Job" ADD CONSTRAINT "_cm3_Type_check" CHECK ("Status" <> 'A' OR "Type" IN ('script','emailService','export_file','import_file','stored_function','workflow','etl','sendemail','wf_batch_task'));

