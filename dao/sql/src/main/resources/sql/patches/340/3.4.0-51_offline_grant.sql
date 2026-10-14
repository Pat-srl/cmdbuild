-- add offline grants

ALTER TABLE IF EXISTS public."_Grant" DROP CONSTRAINT IF EXISTS "_cm3_ObjectClassObjectIdObjectCode_check";

ALTER TABLE IF EXISTS public."_Grant"
    ADD CONSTRAINT "_cm3_ObjectClassObjectIdObjectCode_check" CHECK (
            ("Type"::text = ANY (ARRAY['class'::character varying::text, 'process'::character varying::text]))
                AND "ObjectClass" IS NOT NULL
                AND "ObjectId" IS NULL
                AND "ObjectCode" IS NULL
            OR
            ("Type"::text = ANY (ARRAY['etlgate'::character varying::text, 'etltemplate'::character varying::text, 'offline'::character varying::text]))
                AND "ObjectClass" IS NULL
                AND "ObjectId" IS NULL
                AND _cm3_utils_is_not_blank("ObjectCode")
            OR
            ("Type"::text <> ALL (ARRAY['class'::character varying::text, 'process'::character varying::text, 'etlgate'::character varying::text, 'etltemplate'::character varying::text, 'offline'::character varying::text]))
                AND "ObjectClass" IS NULL
                AND "ObjectId" IS NOT NULL
                AND "ObjectCode" IS NULL
            );

ALTER TABLE IF EXISTS public."_Grant" DROP CONSTRAINT IF EXISTS "_cm3_Type_check";

ALTER TABLE IF EXISTS public."_Grant"
    ADD CONSTRAINT "_cm3_Type_check" CHECK (
            "Type"::text = ANY (ARRAY[
                'class'::character varying::text,
                'process'::character varying::text,
                'custompage'::character varying::text,
                'filter'::character varying::text,
                'view'::character varying::text,
                'report'::character varying::text,
                'etltemplate'::character varying::text,
                'etlgate'::character varying::text,
                'dashboard'::character varying::text,
                'offline'::character varying::text])
            );