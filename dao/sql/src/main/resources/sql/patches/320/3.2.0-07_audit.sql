-- improve audit table

ALTER TABLE "_Request" ALTER COLUMN "PayloadSize" TYPE bigint, ALTER COLUMN "ResponseSize" TYPE bigint;

