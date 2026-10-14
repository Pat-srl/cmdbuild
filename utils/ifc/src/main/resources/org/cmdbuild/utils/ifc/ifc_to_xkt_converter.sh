#!/bin/bash

dockerimagename="cmdbuild-xeokit:latest"
src_file="$1"
target_file="$2"
target_file_name=$(basename $target_file)

echo "Starting $dockerimagename docker image"

containerid=$(docker run --entrypoint /bin/sh -itd $dockerimagename)

echo "Started container with id $containerid"
echo "Copying ifc file to container"

docker exec $containerid bash -c 'cd /tmp ; mkdir working_dir'
docker cp $src_file $containerid:/tmp/working_dir

echo "Starting ifc to xkt conversion"

docker exec $containerid bash -c 'cd /tmp/working_dir; ls ; node /usr/app/xeokit-convert/convert2xkt.js -s '${src_file##*/}' -o '$target_file_name' -l'

echo "Conversion complete. Copying container file to local dir"

docker cp $containerid:/tmp/working_dir/$target_file_name $target_file

echo "Stopping and removing docker container"

docker stop $containerid
docker rm $containerid
