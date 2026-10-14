-- improve password field management

SELECT _cm3_attribute_features_set('"User"','Password','cm_password','true');

SELECT _cm3_attribute_create('OWNER: User|NAME: RecoveryToken|TYPE: varchar|MODE: hidden|cm_password: true');
