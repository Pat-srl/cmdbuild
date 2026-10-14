-- menu target device

SELECT _cm3_attribute_create('OWNER: _Menu|NAME: TargetDevice|NOTNULL: TRUE|TYPE: varchar|VALUES: default,mobile|DEFAULT: default');

DROP INDEX "_cm3__Menu_GroupName";

SELECT _cm3_attribute_index_unique_create('"_Menu"', 'GroupName', 'TargetDevice'); 

SELECT _cm3_attribute_create('OWNER: _UiComponent|NAME: TargetDevice|NOTNULL: TRUE|TYPE: varchar|VALUES: any,default,mobile|DEFAULT: any');
