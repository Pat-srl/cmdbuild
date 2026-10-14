
how to configure cmdbuild (recommented):

* create cmdbuild conf directory in ${catalina.base}/conf/<cmdbuild_webapp_context>:

	mkdir -p <tomcat_home>/conf/<cmdbuild_webapp_context>

* copy default config files, modify at will:

	cp <tomcat_home>/webapps/<cmdbuild_webapp_context>/WEB-INF/conf/database.conf_example <tomcat_home>/conf/<cmdbuild_webapp_context>/database.conf
	
	vim <tomcat_home>/conf/<cmdbuild_webapp_context>/database.conf

done! 

for example:

	cd /opt/my_tomcat/
	mkdir conf/my_cmdbuild/
	cp webapps/my_cmdbuild/WEB-INF/conf/database.conf_example conf/my_cmdbuild/database.conf
	vim conf/my_cmdbuild/database.conf # edit config

you should not keep config files inside <tomcat_home>/webapps/<cmdbuild_webapp_context>
otherwise you risk losing them if you forgot to copy them in the event of a webapp upgrade.

have a nice day :)
