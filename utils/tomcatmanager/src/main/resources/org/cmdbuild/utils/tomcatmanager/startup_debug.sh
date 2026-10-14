#!/bin/bash

CATALINA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=CMDBUILD_DEBUG_PORT" bin/startup.sh

