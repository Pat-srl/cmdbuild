#!/bin/bash

dockerimagename="cmdbuild-oda:latest"
src_file="$1"
target_file="$2"
target_file_name=$(basename $target_file)

echo "Starting $dockerimagename docker image"

containerid=$(docker run --entrypoint /bin/sh -itd $dockerimagename)

echo "Started container with id $containerid"
echo "Copying dwg file to container"

docker exec $containerid bash -c 'cd /tmp ; mkdir in_dir ; mkdir out_dir'
docker cp $src_file $containerid:/tmp/in_dir

echo "Starting dwg to dxf conversion"

docker exec $containerid bash -c 'xvfb-run -a -s "-screen 0 1600x1200x24+32" /usr/bin/ODAFileConverter /tmp/in_dir /tmp/out_dir ACAD2018 DXF 0 1'

echo "Conversion complete. Copying container file to local dir"

docker cp $containerid:/tmp/out_dir/$target_file_name $target_file

echo "Stopping and removing docker container"

docker stop $containerid
docker rm $containerid
