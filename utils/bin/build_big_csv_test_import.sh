#!/bin/bash

echo '"One","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten"'
for n in {1..1061932}; do
	echo "$n,$RANDOM,$RANDOM,$[$RANDOM/100],$[$RANDOM/100],$[$RANDOM/100],$[$RANDOM/10],$[$RANDOM/10],$[$RANDOM/10],$[$RANDOM/10]"
done

