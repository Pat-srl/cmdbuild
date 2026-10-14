

CREATE OR REPLACE FUNCTION test_setup() RETURNS void AS $$ BEGIN
    DROP TABLE IF EXISTS test CASCADE;
    CREATE TABLE test(my_id INT, description VARCHAR);
    INSERT INTO test(my_id, description) VALUES (100, 'test description');
END $$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION test_case_user_existance() RETURNS void AS $$
    DECLARE
        id INT;
    BEGIN
        SELECT my_id FROM test INTO id;
        perform test_assertNotNull('test id not found', id);
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION test_case_user_description_existance() RETURNS void AS $$
    DECLARE
        descr VARCHAR;
    BEGIN
        SELECT description FROM test INTO descr;
        perform test_assertNotNull('description not found', descr);
    END;
$$ LANGUAGE plpgsql;