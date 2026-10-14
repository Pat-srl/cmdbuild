-- waterway functions management

UPDATE "_Function" SET "Category" = 'system' WHERE "Category" IS NULL;

ALTER TABLE "_Function" ALTER COLUMN "Category" SET NOT NULL;

ALTER TABLE "_Function" ADD CONSTRAINT "_cm3_Category_check" CHECK ("Category" IN ('system', 'module'));
