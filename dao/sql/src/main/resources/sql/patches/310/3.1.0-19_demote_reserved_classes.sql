-- switch some protected sys classes to reserved

-- note: leave user and role `protected`, otherwise breaks ready2use and other legacy code

-- SELECT _cm3_class_features_set('"User"','MODE','reserved');
-- SELECT _cm3_class_features_set('"Role"','MODE','reserved');

SELECT _cm3_class_features_set('"Email"','MODE','reserved');

