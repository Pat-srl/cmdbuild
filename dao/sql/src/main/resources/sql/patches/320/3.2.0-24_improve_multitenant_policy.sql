--- imporve multitenant policy performance on superclass

SELECT _cm3_multitenant_superclass_policy_update(c) FROM _cm3_class_list() c WHERE _cm3_class_is_superclass(c);

