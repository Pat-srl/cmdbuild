-- removing unused comment and setting superclass comment for table map

SELECT _cm3_class_comment_delete('"Map"', 'STATUS');
SELECT _cm3_class_comment_set('"Map"', 'SUPERCLASS', 'true');
