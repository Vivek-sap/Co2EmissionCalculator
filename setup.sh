#!/bin/bash

un=`pwd`
echo $un
cd $un


echo ""
echo "***** Working on branch $BRANCH *****"
echo ""

say() {
    if [ `uname -s` == "Darwin" ]; then
        # On Mac OS, notify via Growl
        which -s growlnotify && growlnotify --name Maven --sticky --message "Maven - Branch - $RESULT"
    fi
    if [ `uname -s` == "Linux" ]; then
        # On Linux, notify via notify-send
        which notify-send && notify-send "Maven - build" "$RESULT"
    fi
}


if [ -e "pom.xml" ]; then
   # For Maven projects we can assume a default:
   BUILD_CMD=${BUILD_COMMAND-"mvn clean install"}
   if [[ $# -eq 0 ]]; then 
      eval $BUILD_CMD
   else
      mvn "$@" 
   fi
else
   ${BUILD_COMMAND:?"Variable BUILD_COMMAND needs to be set for non-maven projects. No pom.xml detected."}
   eval BUILD_COMMAND
fi

if [ $? -eq 0 ]; then
  RESULT="Build SUCCESS"
  echo $RESULT    
  say
else
  RESULT="Build FAILURE"
  echo $RESULT
  say
  exit $?
fi


`nohup java -jar target/*.jar > nohup.out 2> nohup.err < /dev/null &`
