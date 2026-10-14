#!/bin/bash

# header size is set from builder process
header_size=000000000000
resources_archive_size=000000000000
jvm_args='JVM_ARGS_PLACEHOLDER'

# these parameters are used by this code AND by builder process; checksum is sha1
java_archive_url=https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_linux-x64_bin.tar.gz
java_archive_checksum=5eb8f3861af16cd67cd4dae01a84c540445e72be
java_archive_filename=openjdk-17_linux-x64_bin.tar.gz

java_version_regex='version "1[7-9]([.][0-9]+)*"'

if ! java -version 2>&1 | egrep -q "$java_version_regex"; then
	jdk_temp_dir="/tmp/cm_$(echo "cmdbuild_cli_java_${java_archive_checksum}_$(whoami)" | md5sum | cut -d' ' -f1)"
	if ! [ -e "${jdk_temp_dir}/ok" ]; then
		echo "java binary (17 or later) not found; preparing embedded java runtime..." >&2
		rm -rf "${jdk_temp_dir}"
		mkdir -p "${jdk_temp_dir}"
		dd bs=1M if="$0" iflag=skip_bytes,count_bytes skip=${header_size} count=${resources_archive_size} status=none | tar -xO "$java_archive_filename" | tar -C "${jdk_temp_dir}" -xz || { rm -rf "${jdk_temp_dir}"; exit 1; }
		touch "${jdk_temp_dir}/ok"
	fi
	java_binary="$(find "${jdk_temp_dir}" -name java | sort | head -n1)"
	$java_binary -version 2>&1 | egrep -q "$java_version_regex" || exit 1
else
	java_binary="`which java`"
fi

checksum=$(echo "`stat -c %Y "$0"`_`du -b "$0"`_`whoami`" | md5sum | cut -d' ' -f1)
tdir="/tmp/cm_${checksum}";
tempfile="${tdir}/file.jar"
okfile="${tdir}/ok"

if ! [ -e "${okfile}" ]; then
	echo -n "loading war ..." >&2
	mkdir -p "$tdir"
	dd bs=1M if="$0" of="${tempfile}" iflag=skip_bytes skip=$[header_size+resources_archive_size] status=none || exit 1
	unzip -tqq "${tempfile}" || exit 1
	touch "${okfile}"
	echo " done" >&2
fi

#echo "using java $java_binary $( $java_binary -version 2>&1 | head -n1 )" >&2

exec "$java_binary" $jvm_args -Xmx8G -jar "$tempfile" "$@"

# === after this marker there will be some empty lines, and then binary data === #


