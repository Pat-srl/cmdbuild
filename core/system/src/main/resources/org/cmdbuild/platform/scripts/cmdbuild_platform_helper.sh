#!/bin/bash

script_file="$0"

if ! [ "$1" == 'exec' ]; then

	tomcat_home="$1"
	command="$2"

	{
		echo "platform helper: prepare command = $command on tomcat = $tomcat_home"

		if ! [ -f "${tomcat_home}/bin/startup.sh" ]; then
			echo "error: startup script not found in tomcat home dir = $tomcat_home"
			exit 1
		fi

		if [ "$command" = 'upgrade' ]; then
			new_war="$4"
			unzip -tq "$4" || exit 1
		fi

		nohup bash "$0" 'exec' "$PPID" "$@" &>> "${tomcat_home}/logs/cmdbuild.log" &

		exit 0

	} &>> "${tomcat_home}/logs/cmdbuild.log"

fi

# exec

shift
tomcat_pid="$1"
shift
tomcat_home="$1"
command="$2"

sleep 5;

echo "platform helper: execute command $command on tomcat $tomcat_home"

if [ "$command" = 'stop' ] || [ "$command" = 'restart' ] || [ "$command" = 'upgrade' ]; then

	echo "platform helper: stop tomcat = $tomcat_home with pid = $tomcat_pid"

	"${tomcat_home}"/bin/shutdown.sh -force

	for i in {1..20}; do
		kill -0 $tomcat_pid &>/dev/null || break
		sleep 1
	done

	if kill -0 $tomcat_pid &>/dev/null; then

		kill $tomcat_pid

		for i in {1..20}; do
			kill -0 $tomcat_pid &>/dev/null || break
			sleep 1
		done

		if kill -0 $tomcat_pid &>/dev/null; then
			kill -9 $tomcat_pid
		fi

	fi

	sleep 1

	if kill -0 $tomcat_pid &>/dev/null; then
		echo "platform helper: unable to stop tomcat = $tomcat_home pid = $tomcat_pid"
		exit 1
	fi

fi

if [ "$command" = 'upgrade' ]; then

	webapp_home="$3"

	new_war="$4"

	echo "platform helper: upgrade webapp = $webapp_home with new war = $new_war"

	cd "$webapp_home"/.. || exit 1

	backup_file="$(basename "$webapp_home")_backup_$(date +%Y-%m-%d_%H-%M-%S).tar.gz"

	echo "platform helper: backup webapp = $webapp_home to file $backup_file"

	tar -czf "$backup_file" "$(basename "$webapp_home")" || exit 1

	rm -r "$webapp_home" || exit 1

	mkdir "$webapp_home" || exit 1

	unzip -q "$new_war" -d "$webapp_home" || exit 1

	rm "$new_war"

fi

if [ "$command" = 'restart' ] || [ "$command" = 'upgrade' ]; then

	echo "platform helper: start tomcat = $tomcat_home"

	"${tomcat_home}"/bin/startup.sh

fi

rm "$script_file"


