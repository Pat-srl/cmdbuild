@ECHO OFF

set WAR_DIR=%~dp0

java -cp "%WAR_DIR%" org.cmdbuild.webapp.cli.Main startedFromExplodedWar %WAR_DIR% %*
