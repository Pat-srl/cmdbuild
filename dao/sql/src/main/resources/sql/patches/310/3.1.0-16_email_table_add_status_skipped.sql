-- add skip status to email table

ALTER TABLE "Email" DROP CONSTRAINT "_cm3_EmailStatus_check";
ALTER TABLE "Email" ADD CONSTRAINT "_cm3_EmailStatus_check" CHECK ( "EmailStatus" IN ('received','sent','outgoing','error','draft','skipped') );
