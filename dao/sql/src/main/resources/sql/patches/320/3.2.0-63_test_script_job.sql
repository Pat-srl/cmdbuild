-- add script job (test example)
 
INSERT INTO "_Job" ("Code","Enabled","Type","Config") values ('_script_test', false, 'script', jsonb_build_object('cronExpression','* * * * ?','script','logger.info("\n\ntest job run\n");'));

