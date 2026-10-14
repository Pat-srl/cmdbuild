#!/bin/bash

mvn clean depgraph:aggregate || exit 1

dot target/dependency-graph.dot -Tsvg | display

#t=$(mktemp)
#dot target/dependency-graph.dot -Tsvg > $t && gimp $t

