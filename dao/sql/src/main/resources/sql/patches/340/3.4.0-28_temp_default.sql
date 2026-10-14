-- bump temp default ttl

ALTER TABLE "_Temp" ALTER COLUMN "TimeToLive" SET DEFAULT 86400; --24h
