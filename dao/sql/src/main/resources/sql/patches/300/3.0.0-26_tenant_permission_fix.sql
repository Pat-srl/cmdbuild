-- tenant permission fix

SELECT _cm3_attribute_comment_set(x.c, 'IdTenant', 'MODE', 'rescore') FROM (SELECT c FROM _cm3_class_list() c UNION SELECT '"SimpleClass"'::regclass c) x;

