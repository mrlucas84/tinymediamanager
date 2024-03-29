#!/bin/sh
#####################################################################################
# This is a "kickstarter" for OSX; we need to do some logic here, because in OSX
# there is no way to provide an updater and the app itself inside one app.
# There is exactly one "entry point" per .app which is defined in the info.plist.
# In our case this is JavaApplicationStub which is a simple shellscript that launches
# this shellscript. Here we do differend checks
#
# a) search the right Java JVM
# b) decide whether we need to launch the updater or launch tmm
#
#####################################################################################

# By default Mac OS X LC_ALL is set to "C", which means files with special characters will not be found.
export LC_ALL="en_US.UTF-8"

# search for the right JVM - priority is java 7/8
if [ -x "/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home/bin/java" ]; then
  JAVA_HOME="/Library/Internet Plug-Ins/JavaAppletPlugin.plugin/Contents/Home"
  export JAVA_HOME
elif [ -x /usr/libexec/java_home ]; then
  JAVA_HOME="`/usr/libexec/java_home`"
  export JAVA_HOME  
fi
JAVACMD="${JAVA_HOME}/bin/java"

if [ ! -f "$JAVACMD" -o ! -x "$JAVACMD" ]; then
  # display error message with applescript
  osascript -e "tell application \"System Events\" to display dialog \"ERROR launching tinyMediaManager!\n\nYou need to have JAVA installed on your Mac!\nVisit http://java.com for more information...\" with title \"tinyMediaManager\" buttons {\" OK \"} default button 1 with icon path to resource \"tmm.icns\" in bundle (path to me)"
  
  # and open java.com
  open http://java.com
  
  # exit with error
  exit 1
fi

# have a look if we need to launch the updater or tmm directly
if [ -f tmm.jar ]; then
  ARGS="-Dsilent=noupdate"
else
  ARGS="-Xdock:name=`tinyMediaManager updater`"
  ARGS="$ARGS -Xdock:icon=../tmm.icns"
fi

ARGS="$ARGS -Djava.net.preferIPv4Stack=true"

# execute it :)
exec "$JAVACMD" ${ARGS} -jar getdown.jar .      
