-- Patch: Rename 'charts' to 'components' in _Dashboard.Config

-- Step 1: Update constraint (if exists, drop and will be re-created at the end of the patch)
ALTER TABLE "_Dashboard" DROP CONSTRAINT IF EXISTS "_cm3_config_check";
ALTER TABLE "_Dashboard" DROP CONSTRAINT IF EXISTS "_cm3_Config_check";

DO $$ DECLARE
    _record record;
    _config jsonb;
    _components jsonb;
    _layout jsonb;
BEGIN
    -- Step 2: Rename 'charts' to 'components' in all active dashboards
    FOR _record IN SELECT * FROM "_Dashboard" WHERE "Status" = 'A' LOOP
        _config = _record."Config";
        -- Ensure _charts is a valid object, otherwise use empty object
        IF _config ? 'charts' THEN
            _components = _config->'charts';
            _layout = _config->'layout';

            -- Rebuild layout with renamed charts to components in columns
            _layout := jsonb_set(
                _layout,
                '{rows}',
                (
                    SELECT jsonb_agg(
                        jsonb_set(
                            row_obj,
                            '{columns}',
                            (
                                SELECT jsonb_agg(
                                    CASE
                                        WHEN col_obj ? 'charts'
                                        THEN jsonb_set(col_obj, '{components}', col_obj->'charts') - 'charts'
                                        ELSE col_obj
                                    END
                                )
                                FROM jsonb_array_elements(row_obj->'columns') col_obj
                            ),
                            true
                        )
                    )
                    FROM jsonb_array_elements(_layout->'rows') row_obj
                ),
                true
            );

            -- Build new config with 'components' instead of 'charts'
            _config = jsonb_build_object('components', _components, 'layout', _layout);

            UPDATE "_Dashboard" SET "Config" = _config WHERE "Id" = _record."Id";
        END IF;
    END LOOP;

   -- RAISE NOTICE 'Updated % dashboards with new config structure', (SELECT COUNT(*) FROM "_Dashboard" WHERE "Status" = 'A' AND "Config" ? 'components');
END $$ LANGUAGE PLPGSQL;

-- Step 3: Update the validation function to check for 'components' instead of 'charts'
CREATE OR REPLACE FUNCTION _cm3_dashboard_config_check(_config jsonb) RETURNS boolean AS $$ DECLARE
    _val varchar;
    _res boolean;
BEGIN
    _res = TRUE;
    IF _config IS NULL OR _config->'components' IS NULL OR _config->'layout' IS NULL THEN
        RAISE WARNING 'invalid dashboard config structure';
        RETURN false;
    END IF;
    FOR _val IN WITH
            _component_ids AS (SELECT _component->>'_id' _id FROM jsonb_array_elements(_config->'components') _component),
            _rows AS (SELECT r _row FROM jsonb_array_elements(_config->'layout'->'rows') r),
            _cols AS (SELECT jsonb_array_elements(_row->'columns') _col FROM _rows),
            _layout_components_ids AS (SELECT jsonb_array_elements_text(_col->'components') _id FROM _cols)
        SELECT _id FROM _component_ids WHERE _id NOT IN (SELECT _id FROM _layout_components_ids) UNION SELECT _id FROM _layout_components_ids WHERE _id NOT IN (SELECT _id FROM _component_ids) LOOP
        RAISE WARNING 'found mismatching dashboard component id = %', _val;
        _res = FALSE;
    END LOOP;
    RETURN _res;
END $$ LANGUAGE PLPGSQL IMMUTABLE SET search_path = public;

-- Step 4: Validate all dashboards with the new check function
DO $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "_Dashboard" WHERE "Status" = 'A' AND NOT _cm3_dashboard_config_check("Config") LOOP
            RAISE WARNING 'Disabling invalid dashboard config = %', _record."Id";
            UPDATE "_Dashboard" SET "Status" = 'N' WHERE "Id" = _record."Id";
        END LOOP;
END $$ LANGUAGE PLPGSQL;

-- Step 5: Re-create the constraint to ensure future inserts/updates are validated with the new function
ALTER TABLE "_Dashboard" ADD CONSTRAINT "_cm3_Config_check"
    CHECK ( "Status" <> 'A' OR _cm3_dashboard_config_check("Config") );
