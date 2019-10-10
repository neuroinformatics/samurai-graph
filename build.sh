#!/bin/sh
PWD=`dirname $0`
cd $PWD

ANT_TARGET=

UNAME=`uname`
# error check
if [ $UNAME = "Darwin" ]; then
  ANT_TARGET=macosx
fi

case "$1" in
build)
  (cd contrib ; ./build.sh build)
  tools/ant.sh $ANT_TARGET
  ;;
rebuild)
  rm -f `find . -name .DS_Store`
  (cd contrib ; ./build.sh rebuild)
  tools/ant.sh $ANT_TARGET
  ;;
clean)
  rm -f `find . -name .DS_Store`
  (cd contrib ; ./build.sh clean)
  tools/ant.sh clean
  ;;
*)
  echo "Usage: ${0##*/}: { build | rebuild | clean }" 2>&1
  exit 65
  ;;
esac
