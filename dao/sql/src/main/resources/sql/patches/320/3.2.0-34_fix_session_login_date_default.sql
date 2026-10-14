-- fix session login date default

ALTER TABLE "_Session" ALTER COLUMN "LoginDate" SET DEFAULT now();
