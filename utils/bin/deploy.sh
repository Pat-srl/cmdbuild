#!/bin/bash

tomcat="$CMDBUILD_DEPLOY_TARGET"
proj=`pwd`

for name in "$@"; do
	if [ -d "$name" ]; then
		tomcat="$name"
	fi
done

echo "tomcat dir: $tomcat"
echo "project dir: $proj"


stopped=$(mktemp)

{
	for dir in $tomcat; do
		cd "$dir"
		bin/shutdown.sh 30 -force
		while kill $(cat bin/catalina.pid) &>/dev/null; do sleep 1; done; 
		rm bin/catalina.pid
		cd -
	done

	rm $stopped
} &


cd "$proj"

function fail(){
	notify-send 'error' 'error building cmdbuild'
	exit 1;
}

if echo "$@" | grep -q 'clean'; then
	mvn -nsu -am -pl cmdbuild -Dorg.cmdbuild.test.skipSlow=true clean install || fail
else
	project_list="$(modified_modules.sh),cmdbuild"
	mvn -nsu -pl $project_list -Dorg.cmdbuild.test.skipSlow=true clean install || fail
fi

echo "wait for tomcat shutdown"
while [ -e $stopped ]; do
	sleep .1;
done

for dir in $tomcat; do
	t=`mktemp -d`
	unzip -q -d $t ./cmdbuild/target/cmdbuild.war
	rsync -crvP --delete $t/ "$dir"/webapps/cmdbuild/
	rm -r $t
done

if ! (echo "$@" | grep -q  'nostart'); then

	for dir in $tomcat; do
		cd "$dir"
		bin/startup.sh
		cd -
	done

	cmdbuild selftest cli || exit 1

	echo "wait for tomcat startup"

        for dir in $tomcat; do
		cd "$dir"
		for n in {1..60}; do
			status="$(cmdbuild restws -username system status 2>&1)"
			echo "$status" | egrep -qi 'READY|ERROR|wait' && break;
			sleep 1
		done
		cd -
	done

	notify-send 'DEPLOY COMPLETED' "\\n\\nthe deploy is completed\\n\\n${status}\\n\\n" -i ~/svn/cmdbuild_30/ui/resources/images/logo.png

fi


