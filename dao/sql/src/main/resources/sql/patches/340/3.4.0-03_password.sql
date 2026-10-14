-- improve password field management

SELECT _cm3_attribute_features_set('"User"','Password','cm_encrypt','false');
SELECT _cm3_attribute_features_set('"User"','RecoveryToken','cm_encrypt','false');
