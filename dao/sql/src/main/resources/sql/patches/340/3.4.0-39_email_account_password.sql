-- remove size limit from mail account limit

ALTER TABLE "_EmailAccount" ALTER COLUMN "Password" TYPE VARCHAR;
