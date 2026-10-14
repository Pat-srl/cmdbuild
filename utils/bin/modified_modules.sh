#!/bin/bash

t=$(mktemp)

git status --porcelain | cut -c4- | while read path x; do
	while ! [ -e "$path"/pom.xml ]; do
		path="$(dirname "$path")"
	done
	echo "$path" >> $t
done

t2=$(mktemp)

cat $t | sort | uniq | egrep -v '^[.]$|test|extras' > $t2

modules="$(cat $t2 | tr '\n' ',' | sed 's#,$##' )"
rm $t $t2

echo "$modules"


