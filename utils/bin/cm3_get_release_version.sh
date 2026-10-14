#!/bin/bash

exec sed -n -r 's#.*[<]revision[>]([^<]+)[<]/revision[>].*#\1#p' parent/pom.xml  | head -n1
