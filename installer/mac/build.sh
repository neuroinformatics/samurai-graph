#!/bin/sh
PACKAGE=samurai-graph
APPNAME="Samurai Graph"

BASEDIR=`dirname $0`
cd "$BASEDIR"
TOPDIR=../..
DISTDIR=$TOPDIR/dist
SRCDIR=$TOPDIR/src
RESOURCEDIR=$SRCDIR/resources
DOCDIR=$TOPDIR/../doc
USERSMANDIR=$DOCDIR/usersman-ja
HTMLDIR=$USERSMANDIR/html
PROPERTYFILE=$RESOURCEDIR/samurai-graph.properties
PROPERTY_STRING_VERSION="samurai-graph.version"

# get version string
VERSION=`grep "$PROPERTY_STRING_VERSION" $PROPERTYFILE | sed -e "s+^$PROPERTY_STRING_VERSION=++g" -e "s+.$++"`
MAJOR_VERSION=`echo $VERSION | \
     sed 's/\([[0-9]]*\).\([[0-9]]*\).\([[0-9]]*\).*/\1/'`
MINOR_VERSION=`echo $VERSION | \
     sed 's/\([[0-9]]*\).\([[0-9]]*\).\([[0-9]]*\).*/\2/'`
MICRO_VERSION=`echo $VERSION | \
     sed 's/\([[0-9]]*\).\([[0-9]]*\).\([[0-9]]*\).*/\3/'`

UNAME=`uname`
# error check
if [ $UNAME != "Darwin" ]; then
  echo "Error: running host is not MacOS X";
  exit 65;
fi

APPLICATION="$APPNAME.app"
APP_CONTENTS="$APPLICATION/Contents"
APP_MACOS="$APP_CONTENTS/MacOS"
APP_RESOURCES="$APP_CONTENTS/Resources"
APP_JAVA="$APP_RESOURCES/Java"
APP_JAVALIB="$APP_RESOURCES/Java/lib"
APP_JAVALIBVG="$APP_JAVALIB/vectorgraphics"
APP_JAVALIBFT="$APP_JAVALIB/foxtrot"
APP_JAVALIBNC="$APP_JAVALIB/netCDF"
APP_JAVALIBCJ="$APP_JAVALIB/cisd-jhdf5"
APP_JAVALIBJM="$APP_JAVALIB/jmatio"
APP_JAVALIBJN="$APP_JAVALIB/jna"
APP_JAVALIBCD="$APP_JAVALIB/juniversalchardet"
APP_JAVALIBJT="$APP_JAVALIB/joda-time"
APP_DOC="$APP_RESOURCES/Documents"
APP_DOCVG="$APP_DOC/vectorgraphics"
APP_DOCFT="$APP_DOC/foxtrot"
APP_DOCNC="$APP_DOC/netCDF"
APP_DOCCJ="$APP_DOC/cisd-jhdf5"
APP_DOCJM="$APP_DOC/jmatio"
APP_DOCJN="$APP_DOC/jna"
APP_DOCCD="$APP_DOC/juniversalchardet"
APP_DOCJT="$APP_DOC/joda-time"
BUNDLER="resources/bundler"
IMGFILE="$PACKAGE-mac-$VERSION.dmg"
IMGDIST="diskimage"
VECTORIMGDIST="diskimage_vector"
VECTORVERSION=`echo m$VERSION | sed -e 's+\.++g'`
VECTORIMGFILE="$PACKAGE-$VECTORVERSION.dmg"
MACOSXADAPTER_DISTDIR="$TOPDIR/macosx/lib"

# check dist directory
check_dist_directory () {
  if [ ! -d $DISTDIR ]; then
    echo "Error: $DISTDIR not found"
    exit 65;
  fi
  if [ ! -f $DISTDIR/samurai-graph.jar ]; then
    echo "Error: $DISTDIR/samurai-graph.jar not found"
    exit 65;
  fi
}

# check manual directory
check_html_directory () {
  if [ ! -d $HTMLDIR ]; then
    echo "Error: $HTMLDIR not found"
    exit 65;
  fi
  if [ ! -f $HTMLDIR/index.html ]; then
    echo "Error: $HTMLDIR/index.html not found"
    exit 65;
  fi
}

# delte Java bundler
delete_java_bundler () {
  echo -n "[Samurai Graph] delete java bundler application...  "
  rm -rf "$APPLICATION"
  echo 'done'
}

# build Java bundler
build_java_bundler () {
  echo -n "[Samurai Graph] build java bundler application...  "
  mkdir "$APPLICATION"
  mkdir "$APP_CONTENTS"
  mkdir "$APP_MACOS"
  mkdir "$APP_RESOURCES"
  mkdir "$APP_JAVA"
  mkdir "$APP_JAVALIB"
  mkdir "$APP_JAVALIBVG"
  mkdir "$APP_JAVALIBFT"
  mkdir "$APP_JAVALIBNC"
  mkdir "$APP_JAVALIBCJ"
  mkdir "$APP_JAVALIBJM"
  mkdir "$APP_JAVALIBJN"
  mkdir "$APP_JAVALIBCD"
  mkdir "$APP_JAVALIBJT"
  mkdir "$APP_DOC"
  mkdir "$APP_DOCVG"
  mkdir "$APP_DOCFT"
  mkdir "$APP_DOCNC"
  mkdir "$APP_DOCCJ"
  mkdir "$APP_DOCJM"
  mkdir "$APP_DOCJN"
  mkdir "$APP_DOCCD"
  mkdir "$APP_DOCJT"
# cp "$BUNDLER/Info.plist"         "$APP_CONTENTS"
  sed -e "s+@SAMURAIGRAPH_VERSION@+$VERSION+g" \
      -e "s+@SAMURAIGRAPH_MAJOR_VERSION@+$MAJOR_VERSION+g" \
      -e "s+@SAMURAIGRAPH_MINOR_VERSION@+$MINOR_VERSION+g" \
      -e "s+@SAMURAIGRAPH_MICRO_VERSION@+$MICRO_VERSION+g" \
      "$BUNDLER/Info.plist.in" > "$APP_CONTENTS/Info.plist"
  cp "$BUNDLER/PkgInfo"            "$APP_CONTENTS"
#  cp "$BUNDLER/version.plist"      "$APP_CONTENTS"
  sed -e "s+@SAMURAIGRAPH_VERSION@+$VERSION+g" \
      -e "s+@SAMURAIGRAPH_MAJOR_VERSION@+$MAJOR_VERSION+g" \
      -e "s+@SAMURAIGRAPH_MINOR_VERSION@+$MINOR_VERSION+g" \
      -e "s+@SAMURAIGRAPH_MICRO_VERSION@+$MICRO_VERSION+g" \
      "$BUNDLER/version.plist.in"  > "$APP_CONTENTS/version.plist"
#  cp "$BUNDLER/Samurai Graph"      "$APP_MACOS"
  cp "$BUNDLER/JavaApplicationStub"      "$APP_MACOS"
  chmod 544 "$APP_MACOS/JavaApplicationStub"
  cp "$BUNDLER/samurai-graph.icns" "$APP_RESOURCES"
  cp "$BUNDLER/samurai-graph-property.icns" "$APP_RESOURCES"
  cp "$BUNDLER/samurai-graph-archive.icns" "$APP_RESOURCES"
  cp "$BUNDLER/samurai-graph-script.icns" "$APP_RESOURCES"
  cp "$DISTDIR/samurai-graph.jar"  "$APP_JAVA"
  cp "$DISTDIR/LICENSE.txt"        "$APP_DOC"
  cp "$DISTDIR/COPYING.txt"        "$APP_DOC"
  cp "$DISTDIR/lib/vectorgraphics/freehep-export-2.1.1.jar"          "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphics2d-2.1.1.jar"      "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-2.1.1.jar"      "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-cgm-2.1-SNAPSHOT.jar"  "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-emf-2.1.1.jar"  "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-java-2.1.1.jar" "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-pdf-2.1.1.jar"  "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-ps-2.1.1.jar"   "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-svg-2.1.1.jar"  "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-swf-2.1.1.jar"  "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-io-2.0.2.jar"              "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-swing-2.0.3.jar"           "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-util-2.0.2.jar"            "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-xml-2.1.1.jar"             "$APP_JAVALIBVG"
  cp "$DISTDIR/lib/vectorgraphics/openide-lookup-1.9-patched-1.0.jar" "$APP_JAVALIBVG"
  cp "$DISTDIR/doc/vectorgraphics/LGPL.txt"                    "$APP_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/LICENSE.txt"                 "$APP_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/README.txt"                  "$APP_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/ReleaseNotes-2.1.1.html"     "$APP_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/SAMURAIGRAPH-ChangeLog.txt"  "$APP_DOCVG"
  cp "$DISTDIR/lib/foxtrot/foxtrot.jar"                        "$APP_JAVALIBFT"
  cp "$DISTDIR/doc/foxtrot/README"                             "$APP_DOCFT"
  cp "$DISTDIR/doc/foxtrot/LICENSE"                            "$APP_DOCFT"
  cp "$DISTDIR/doc/foxtrot/SAMURAIGRAPH-ChangeLog.txt"         "$APP_DOCFT"
  cp "$DISTDIR/lib/netCDF/jdom.jar"                            "$APP_JAVALIBNC"
  cp "$DISTDIR/lib/netCDF/log4j-1.2.15.jar"                    "$APP_JAVALIBNC"
  cp "$DISTDIR/lib/netCDF/netcdf-4.2.jar"                      "$APP_JAVALIBNC"
  cp "$DISTDIR/lib/netCDF/slf4j-api-1.5.6.jar"                 "$APP_JAVALIBNC"
  cp "$DISTDIR/lib/netCDF/slf4j-log4j12-1.5.6.jar"             "$APP_JAVALIBNC"
  cp "$DISTDIR/lib/netCDF/commons-codec-1.3.jar"               "$APP_JAVALIBNC"
  cp "$DISTDIR/lib/netCDF/commons-httpclient-3.1.jar"          "$APP_JAVALIBNC"
  cp "$DISTDIR/lib/netCDF/commons-logging-1.1.jar"             "$APP_JAVALIBNC"
  cp "$DISTDIR/doc/netCDF/README.txt"                          "$APP_DOCNC"
  cp "$DISTDIR/doc/netCDF/SAMURAIGRAPH-ChangeLog.txt"          "$APP_DOCNC"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-args4j.jar"                 "$APP_JAVALIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-base.jar"                   "$APP_JAVALIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5-batteries_included_lin_win_mac_sun.jar" "$APP_JAVALIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5-core.jar"             "$APP_JAVALIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5-tools.jar"            "$APP_JAVALIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5.jar"                  "$APP_JAVALIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/commons-io.jar"                  "$APP_JAVALIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/commons-lang.jar"                "$APP_JAVALIBCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/CONTENT"                         "$APP_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING"                         "$APP_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING-args4j"                  "$APP_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING-commons"                 "$APP_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING-hdf5"                    "$APP_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/LICENSE"                         "$APP_DOCCJ"
  cp "$DISTDIR/lib/jmatio/jamtio.jar"                          "$APP_JAVALIBJM"
  cp "$DISTDIR/doc/jmatio/license.txt"                         "$APP_DOCJM"
  cp "$DISTDIR/doc/jmatio/readme.txt"                          "$APP_DOCJM"  
  cp "$DISTDIR/lib/jna/jna.jar"                                "$APP_JAVALIBJN"
  cp "$DISTDIR/lib/jna/platform.jar"                           "$APP_JAVALIBJN"
  cp "$DISTDIR/doc/jna/license.txt"                            "$APP_DOCJN"
  cp "$DISTDIR/doc/jna/LGPL.txt"                               "$APP_DOCJN"
  cp "$DISTDIR/lib/juniversalchardet/juniversalchardet-1.0.3.jar"  "$APP_JAVALIBCD"
  cp "$DISTDIR/doc/juniversalchardet/license.txt"              "$APP_DOCCD"
  cp "$DISTDIR/doc/juniversalchardet/MPL.txt"                  "$APP_DOCCD"
  cp "$DISTDIR/lib/joda-time/joda-time-2.1.jar"                "$APP_JAVALIBJT"
  cp "$DISTDIR/doc/joda-time/LICENSE.txt"                      "$APP_DOCJT"
  cp "$DISTDIR/doc/joda-time/NOTICE.txt"                       "$APP_DOCJT"
  cp "$MACOSXADAPTER_DISTDIR/macosx-adapter.jar"               "$APP_JAVALIB"
  echo 'done'
}

# delete disk image file
delete_diskimage_file () {
  echo -n "[Samurai Graph] delete disk image file...  "
  rm -f "$PACKAGE-mac-"*.*.*".dmg"
  rm -f "$PACKAGE-mac-"*.*.*".dmg.gz"
  rm -rf "$IMGDIST"
  echo 'done'
}

# build disk image file
build_diskimage_file () {
  echo -n "[Samurai Graph] build disk image file...  "
  mkdir "$IMGDIST"
  mkdir "$IMGDIST/Example Data"
  # put the resource forks back together
  #/System/Library/CoreServices/FixupResourceForks -nodelete diskimage
  cp -R  "$APPLICATION" "$IMGDIST"
  cp     "$DISTDIR/COPYING.txt" "$IMGDIST"
  cp     "$DISTDIR/LICENSE.txt" "$IMGDIST"
  cp     "$TOPDIR/examples/data/"*.txt "$IMGDIST/Example Data"
  cp     "$TOPDIR/examples/data/"*.nc "$IMGDIST/Example Data"
  cp     "$TOPDIR/examples/data/"*.sgp "$IMGDIST/Example Data"
  cp     "resources/diskimage/_DS_Store" "$IMGDIST/.DS_Store"
  cp     "resources/diskimage/_DS_Store2" "$IMGDIST/Example Data/.DS_Store"
  hdiutil create -srcfolder "$IMGDIST" -volname "$APPNAME" "$IMGFILE" >/dev/null
  gzip "$IMGFILE"
  echo 'done'
}

# delete disk image file for vectordesign
delete_vector_diskimage_file () {
  echo -n "[Samurai Graph] delete disk image file for vectordesign...  "
  rm -f "$PACKAGE-m"*".dmg"
  rm -f "$PACKAGE-m"*".dmg.gz"
  rm -rf "$VECTORIMGDIST"
  echo 'done'
}

# build disk image file for vectordesign
build_vector_diskimage_file () {
  echo -n "[Samurai Graph] build disk image file for vectordesign...  "
  mkdir "$VECTORIMGDIST"
  mkdir "$VECTORIMGDIST/Example Data"
  mkdir "$VECTORIMGDIST/Users Manual"
  # put the resource forks back together
  #/System/Library/CoreServices/FixupResourceForks -nodelete $VECTORIMGDIST
  cp -R  "$APPLICATION" "$VECTORIMGDIST"
  cp     "$DISTDIR/COPYING.txt" "$VECTORIMGDIST"
  cp     "$DISTDIR/LICENSE.txt" "$VECTORIMGDIST"
#  cp     "resources/vector/README.txt" "$VECTORIMGDIST"
  sed -e "s+@SAMURAIGRAPH_VERSION@+$VERSION+g" \
      -e "s+@SAMURAIGRAPH_VECTORIMGFILE@+$VECTORIMGFILE+g" \
         "resources/vector/README.txt.in" > "$VECTORIMGDIST/README.txt"
  cp     "$TOPDIR/examples/data/"*.txt "$VECTORIMGDIST/Example Data"
  cp     "$TOPDIR/examples/data/"*.nc "$VECTORIMGDIST/Example Data"
  cp     "$TOPDIR/examples/data/"*.sgp "$VECTORIMGDIST/Example Data"
  cp -R  "$HTMLDIR" "$VECTORIMGDIST/Users Manual/ja"
  cp     "resources/vector/_DS_Store" "$VECTORIMGDIST/.DS_Store"
  cp     "resources/vector/_DS_Store2" "$VECTORIMGDIST/Example Data/.DS_Store"
  cp     "resources/vector/_DS_Store3" "$VECTORIMGDIST/Users Manual/.DS_Store"
  cp     "resources/vector/_DS_Store4" "$VECTORIMGDIST/Users Manual/ja/.DS_Store"
  hdiutil create -srcfolder "$VECTORIMGDIST" -volname "$APPNAME" "$VECTORIMGFILE" >/dev/null
  gzip "$VECTORIMGFILE"
  echo 'done'
}

case "$1" in
build)
  check_dist_directory
  delete_java_bundler
  delete_diskimage_file
  build_java_bundler
  build_diskimage_file
  ;;
vector)
  check_dist_directory
  check_html_directory
  delete_java_bundler
  delete_vector_diskimage_file
  build_java_bundler
  build_vector_diskimage_file
  ;;
clean)
  delete_java_bundler
  delete_diskimage_file
  delete_vector_diskimage_file
  ;;
*)
  echo "Usage: ${0##*/}: { build | vector | clean }" 2>&1
  exit 65
  ;;
esac

exit 0
