-- improve validation of role table data
 
ALTER TABLE "Role" DROP CONSTRAINT "_cm3_Type_check"; 

ALTER TABLE "Role" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('admin','admin_limited','admin_users','admin_readonly','default') ); 

