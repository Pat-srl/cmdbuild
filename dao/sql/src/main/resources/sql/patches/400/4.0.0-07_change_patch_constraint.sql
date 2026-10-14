-- change patch constraint

ALTER TABLE "_Patch" DROP CONSTRAINT "_Patch_Code_key";
ALTER TABLE "_Patch" ADD CONSTRAINT "_Patch_Code_Category_key" UNIQUE ("Code", "Category");
SELECT _cm3_attribute_index_delete('"_Patch"', 'Code', 'Category');
SELECT _cm3_attribute_index_unique_create('"_Patch"', 'Code', 'Category');