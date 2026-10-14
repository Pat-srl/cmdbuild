-- etl gate example

DO $$ BEGIN
    IF (SELECT COUNT(*) FROM "_EtlGate") = 0 THEN
    INSERT INTO "_EtlGate" ("Code", "Description", "AllowPublicAccess", "ProcessingMode", "Config", "Enabled", "Handlers") VALUES (
        'Test', 'Example etl gate', TRUE, 'realtime', '{}'::jsonb, FALSE, 
        '[{"type":"script","script":"pack25gyqakzqw4mbeqrr0af2bsiv44b65dsxvbcqmj3zxbxm7m4uwlytihnlcgrge3ewp0fwakbeiy35igo0zvk4m67knfejj4g3r07elxx9wgy0e3afcwwvkgatzxtx3s3kmr7kmyis1j0mhw7nmrdbkfyya639tvyc660ckzcmang6o1w5cb45w6f0q3n83kcap"}]'::jsonb);
    END IF;
END $$ LANGUAGE PLPGSQL;
