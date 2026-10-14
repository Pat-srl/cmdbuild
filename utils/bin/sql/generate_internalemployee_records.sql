


DO $$ DECLARE
	_id bigint;
BEGIN

	PERFORM _cm3_class_triggers_disable('"InternalEmployee"');
	FOR i IN 1..1000000 LOOP
		_id = _cm3_utils_new_card_id();
		INSERT INTO "InternalEmployee" ("Status","BeginDate","IdClass","Id","CurrentId","LastName","Description") 
			VALUES ('A',now()-format('%s seconds',floor(random()*10000))::interval,'"InternalEmployee"',_id,_id,_cm3_utils_random_id(),'employee');
	END LOOP;
	PERFORM _cm3_class_triggers_enable('"InternalEmployee"');

END $$ LANGUAGE PLPGSQL;

