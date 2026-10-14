-- removing duplicate indexes on code

DROP INDEX IF EXISTS "_Unique__EmailTemplate_Code";
SELECT _cm3_attribute_index_unique_create('"_EmailTemplate"', 'Code');

DROP INDEX IF EXISTS "_Unique__EmailAccount_Code";
SELECT _cm3_attribute_index_unique_create('"_EmailAccount"', 'Code');

DROP INDEX IF EXISTS "_Unique_User_Username";
SELECT _cm3_attribute_index_unique_create('"User"', 'Username');