-- improve email templates to handle different kinds of notifications


SELECT _cm3_attribute_create('OWNER: _EmailTemplate|NAME: NotificationType|TYPE: varchar|NOTNULL: true|VALUES: email,notify|DEFAULT: email'); 
SELECT _cm3_attribute_create('OWNER: _EmailTemplate|NAME: TimeToLive|TYPE: int'); 
SELECT _cm3_attribute_create('OWNER: _EmailTemplate|NAME: Participants|TYPE: varchar[]|NOTNULL: true|DEFAULT: ARRAY[]::varchar[]'); 

--NOTE: we shall use negative `delay` values to handle notifications before the event (es: calendar notifications)
