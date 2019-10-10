#!/bin/sh
PACKAGE=samurai-graph
APPNAME="Samurai Graph"

BASEDIR=`dirname $0`
cd "$BASEDIR"
TOPDIR=../..
DISTDIR=$TOPDIR/dist
SRCDIR=$TOPDIR/src
RESOURCEDIR=resources
DOCDIR=$TOPDIR/../doc
USERSMANDIR=$DOCDIR/usersman-ja
HTMLDIR=$USERSMANDIR/html
PROPERTYFILE=$SRCDIR/resources/samurai-graph.properties
PROPERTY_STRING_VERSION="samurai-graph.version"

# get version string
VERSION=`grep "$PROPERTY_STRING_VERSION" $PROPERTYFILE | sed -e "s+^$PROPERTY_STRING_VERSION=++g" -e "s+.$++g"`
MAJOR_VERSION=`echo $VERSION | \
     sed 's/\([[0-9]]*\).\([[0-9]]*\).\([[0-9]]*\).*/\1/'`
MINOR_VERSION=`echo $VERSION | \
     sed 's/\([[0-9]]*\).\([[0-9]]*\).\([[0-9]]*\).*/\2/'`
MICRO_VERSION=`echo $VERSION | \
     sed 's/\([[0-9]]*\).\([[0-9]]*\).\([[0-9]]*\).*/\3/'`

DIRIMG="$PACKAGE-bin-$VERSION"
DIRIMG_LIB="$DIRIMG/lib"
DIRIMG_LIBVG="$DIRIMG_LIB/vectorgraphics"
DIRIMG_LIBFT="$DIRIMG_LIB/foxtrot"
DIRIMG_LIBNC="$DIRIMG_LIB/netCDF"
DIRIMG_LIBCJ="$DIRIMG_LIB/cisd-jhdf5"
DIRIMG_LIBJM="$DIRIMG_LIB/jmatio"
DIRIMG_LIBJN="$DIRIMG_LIB/jna"
DIRIMG_LIBCD="$DIRIMG_LIB/juniversalchardet"
DIRIMG_DOC="$DIRIMG/doc"
DIRIMG_DOCVG="$DIRIMG_DOC/vectorgraphics"
DIRIMG_DOCFT="$DIRIMG_DOC/foxtrot"
DIRIMG_DOCNC="$DIRIMG_DOC/netCDF"
DIRIMG_DOCCJ="$DIRIMG_DOC/cisd-jhdf5"
DIRIMG_DOCJM="$DIRIMG_DOC/jmatio"
DIRIMG_DOCJN="$DIRIMG_DOC/jna"
DIRIMG_DOCCD="$DIRIMG_DOC/juniversalchardet"
DIRIMG_DOCJT="$DIRIMG_DOC/joda-time"
DIRIMG_EXAMPLES="$DIRIMG/examples"
IMGFILE="$DIRIMG.zip"

VECTORVERSION=`echo u$VERSION | sed -e 's+\.++g'`
VECTORDIRIMG="$PACKAGE-$VECTORVERSION"
VECTORDIRIMG_LIB="$VECTORDIRIMG/lib"
VECTORDIRIMG_LIBVG="$VECTORDIRIMG_LIB/vectorgraphics"
VECTORDIRIMG_LIBFT="$VECTORDIRIMG_LIB/foxtrot"
VECTORDIRIMG_LIBNC="$VECTORDIRIMG_LIB/netCDF"
VECTORDIRIMG_LIBCJ="$VECTORDIRIMG_LIB/cisd-jhdf5"
VECTORDIRIMG_LIBJM="$VECTORDIRIMG_LIB/jmatio"
VECTORDIRIMG_LIBJN="$VECTORDIRIMG_LIB/jna"
VECTORDIRIMG_LIBCD="$VECTORDIRIMG_LIB/juniversalchardet"
VECTORDIRIMG_DOC="$VECTORDIRIMG/doc"
VECTORDIRIMG_DOCVG="$VECTORDIRIMG_DOC/vectorgraphics"
VECTORDIRIMG_DOCFT="$VECTORDIRIMG_DOC/foxtrot"
VECTORDIRIMG_DOCNC="$VECTORDIRIMG_DOC/netCDF"
VECTORDIRIMG_DOCCJ="$VECTORDIRIMG_DOC/cisd-jhdf5"
VECTORDIRIMG_DOCJM="$VECTORDIRIMG_DOC/jmatio"
VECTORDIRIMG_DOCJN="$VECTORDIRIMG_DOC/jna"
VECTORDIRIMG_DOCCD="$VECTORDIRIMG_DOC/juniversalchardet"
VECTORDIRIMG_DOCJT="$VECTORDIRIMG_DOC/joda-time"
VECTORDIRIMG_EXAMPLES="$VECTORDIRIMG/examples"
VECTORIMGDIST_USERSMAN="$VECTORDIRIMG/usersmanual"
VECTORIMGDIST_USERSMANJA="$VECTORIMGDIST_USERSMAN/ja"
VECTORIMGDIST_USERSMANJAIMG="$VECTORIMGDIST_USERSMANJA/images"
VECTORIMGDIST_USERSMANIMG="$VECTORIMGDIST_USERSMAN/images"

VECTORIMGFILE="$VECTORDIRIMG.tar.gz"

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

# delte directory image
delete_dirimage () {
  echo -n "[Samurai Graph] delete distribution directory and file...  "
  rm -rf "$DIRIMG"
  rm -f  "$IMGFILE"
  echo 'done'
}

# build directory image
build_dirimage () {
  echo -n "[Samurai Graph] build distribution directory and file...  "
  mkdir "$DIRIMG"
  mkdir "$DIRIMG_LIB"
  mkdir "$DIRIMG_LIBVG"
  mkdir "$DIRIMG_LIBFT"
  mkdir "$DIRIMG_LIBNC"
  mkdir "$DIRIMG_LIBCJ"
  mkdir "$DIRIMG_LIBJM"
  mkdir "$DIRIMG_LIBJN"
  mkdir "$DIRIMG_DOC"
  mkdir "$DIRIMG_DOCVG"
  mkdir "$DIRIMG_DOCFT"
  mkdir "$DIRIMG_DOCNC"
  mkdir "$DIRIMG_DOCCJ"
  mkdir "$DIRIMG_DOCJM"
  mkdir "$DIRIMG_DOCJN"
  mkdir "$DIRIMG_EXAMPLES"
  cp "$RESOURCEDIR/samurai-graph"                   "$DIRIMG"
  cp "$RESOURCEDIR/samurai-graph.xpm"     			"$DIRIMG"
  cp "$RESOURCEDIR/samurai-graph-archive.xpm"		"$DIRIMG"
  cp "$RESOURCEDIR/samurai-graph-property.xpm"		"$DIRIMG"
  cp "$DISTDIR/samurai-graph.jar"                "$DIRIMG"
  cp "$DISTDIR/LICENSE.txt"                      "$DIRIMG"
  cp "$DISTDIR/COPYING.txt"                      "$DIRIMG"
  cp "$DISTDIR/lib/foxtrot/foxtrot.jar"                        "$DIRIMG_LIBFT"
  cp "$DISTDIR/doc/foxtrot/README"                             "$DIRIMG_DOCFT"
  cp "$DISTDIR/doc/foxtrot/LICENSE"                            "$DIRIMG_DOCFT"
  cp "$DISTDIR/doc/foxtrot/SAMURAIGRAPH-ChangeLog.txt"         "$DIRIMG_DOCFT"
  cp "$DISTDIR/lib/vectorgraphics/freehep-export-2.1.1.jar"          "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphics2d-2.1.1.jar"      "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-2.1.1.jar"      "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-cgm-2.1-SNAPSHOT.jar"  "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-emf-2.1.1.jar"  "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-java-2.1.1.jar" "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-pdf-2.1.1.jar"  "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-ps-2.1.1.jar"   "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-svg-2.1.1.jar"  "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-swf-2.1.1.jar"  "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-io-2.0.2.jar"              "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-swing-2.0.3.jar"           "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-util-2.0.2.jar"            "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-xml-2.1.1.jar"             "$DIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/openide-lookup-1.9-patched-1.0.jar" "$DIRIMG_LIBVG"
  cp "$DISTDIR/doc/vectorgraphics/LGPL.txt"                    "$DIRIMG_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/LICENSE.txt"                 "$DIRIMG_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/README.txt"                  "$DIRIMG_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/ReleaseNotes-2.1.1.html"     "$DIRIMG_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/SAMURAIGRAPH-ChangeLog.txt"  "$DIRIMG_DOCVG"
  cp "$DISTDIR/lib/netCDF/jdom.jar"                            "$DIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/log4j-1.2.15.jar"                    "$DIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/netcdf-4.2.jar"                      "$DIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/slf4j-api-1.5.6.jar"                 "$DIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/slf4j-log4j12-1.5.6.jar"             "$DIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/commons-codec-1.3.jar"               "$DIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/commons-httpclient-3.1.jar"          "$DIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/commons-logging-1.1.jar"             "$DIRIMG_LIBNC"
  cp "$DISTDIR/doc/netCDF/README.txt"                          "$DIRIMG_DOCNC"
  cp "$DISTDIR/doc/netCDF/SAMURAIGRAPH-ChangeLog.txt"          "$DIRIMG_DOCNC"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-args4j.jar"                 "$DIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-base.jar"                   "$DIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5.jar"                  "$DIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5-batteries_included_lin_win_mac_sun.jar"  "$DIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5-core.jar"             "$DIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5-tools.jar"            "$DIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/commons-io.jar"                  "$DIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/commons-lang.jar"                "$DIRIMG_LIBCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/CONTENT"                         "$DIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING"                         "$DIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING-args4j"                  "$DIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING-commons"                 "$DIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING-hdf5"                    "$DIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/LICENSE"                         "$DIRIMG_DOCCJ"
  cp "$DISTDIR/lib/jmatio/jamtio.jar"                          "$DIRIMG_LIBJM"
  cp "$DISTDIR/doc/jmatio/license.txt"                         "$DIRIMG_DOCJM"
  cp "$DISTDIR/doc/jmatio/readme.txt"                          "$DIRIMG_DOCJM"
  cp "$DISTDIR/lib/jna/jna.jar"                                "$DIRIMG_LIBJN"
  cp "$DISTDIR/lib/jna/platform.jar"                           "$DIRIMG_LIBJN"
  cp "$DISTDIR/doc/jna/license.txt"                            "$DIRIMG_DOCJN"
  cp "$DISTDIR/doc/jna/LGPL.txt"                               "$DIRIMG_DOCJN"
  cp "$DISTDIR/lib/juniversalchardet/juniversalchardet-1.0.3.jar"    "$DIRIMG_LIBCD"
  cp "$DISTDIR/doc/juniversalchardet/license.txt"              "$DIRIMG_DOCCD"
  cp "$DISTDIR/doc/juniversalchardet/MPL.txt"                  "$DIRIMG_DOCCD"
  cp "$DISTDIR/lib/joda-time/joda-time-2.1.jar"                "$DIRIMG_LIBJT"
  cp "$DISTDIR/doc/joda-time/LICENSE.txt"                      "$DIRIMG_DOCJT"
  cp "$DISTDIR/doc/joda-time/NOTICE.txt"                       "$DIRIMG_DOCJT"
  cp "$TOPDIR/examples/data/"*.txt  "$DIRIMG_EXAMPLES"
  cp "$TOPDIR/examples/data/"*.nc   "$DIRIMG_EXAMPLES"
  cp "$TOPDIR/examples/data/"*.sgp  "$DIRIMG_EXAMPLES"
  zip -9 -r "$IMGFILE" "$DIRIMG"
  echo 'done'
}

# delete disk image file for vectordesign
delete_vector_dirimage () {
  echo -n "[Samurai Graph] delete distribution directory and file for vectordesign...  "
  rm -rf "$VECTORDIRIMG"
  rm -f  "$VECTORIMGFILE"
  echo 'done'
}

# build disk image file for vectordesign
build_vector_dirimage () {
  echo -n "[Samurai Graph] build distribution directory and file for vectordesign...  "
  mkdir "$VECTORDIRIMG"
  mkdir "$VECTORDIRIMG_LIB"
  mkdir "$VECTORDIRIMG_LIBVG"
  mkdir "$VECTORDIRIMG_LIBFT"
  mkdir "$VECTORDIRIMG_LIBNC"
  mkdir "$VECTORDIRIMG_LIBCJ"
  mkdir "$VECTORDIRIMG_LIBJM"
  mkdir "$VECTORDIRIMG_LIBJN"
  mkdir "$VECTORDIRIMG_DOC"
  mkdir "$VECTORDIRIMG_DOCVG"
  mkdir "$VECTORDIRIMG_DOCFT"
  mkdir "$VECTORDIRIMG_DOCNC"
  mkdir "$VECTORDIRIMG_DOCCJ"
  mkdir "$VECTORDIRIMG_DOCJM"
  mkdir "$VECTORDIRIMG_DOCJN"
  mkdir "$VECTORDIRIMG_EXAMPLES"
  mkdir "$VECTORIMGDIST_USERSMAN"
  mkdir "$VECTORIMGDIST_USERSMANJA"
  mkdir "$VECTORIMGDIST_USERSMANJAIMG"
  mkdir "$VECTORIMGDIST_USERSMANJAIMG/etc"

  cp "$RESOURCEDIR/samurai-graph"     "$VECTORDIRIMG"
  cp "$RESOURCEDIR/samurai-graph.xpm"     			"$VECTORDIRIMG"
  cp "$RESOURCEDIR/samurai-graph-archive.xpm"		"$VECTORDIRIMG"
  cp "$RESOURCEDIR/samurai-graph-property.xpm"		"$VECTORDIRIMG"
  cp "$DISTDIR/samurai-graph.jar"  "$VECTORDIRIMG"
  cp "$DISTDIR/LICENSE.txt"        "$VECTORDIRIMG"
  cp "$DISTDIR/COPYING.txt"        "$VECTORDIRIMG"
  cp "$DISTDIR/lib/foxtrot/foxtrot.jar"                        "$VECTORDIRIMG_LIBFT"
  cp "$DISTDIR/doc/foxtrot/README"                             "$VECTORDIRIMG_DOCFT"
  cp "$DISTDIR/doc/foxtrot/LICENSE"                            "$VECTORDIRIMG_DOCFT"
  cp "$DISTDIR/doc/foxtrot/SAMURAIGRAPH-ChangeLog.txt"         "$VECTORDIRIMG_DOCFT"
  cp "$DISTDIR/lib/vectorgraphics/freehep-export-2.1.1.jar"          "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphics2d-2.1.1.jar"      "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-2.1.1.jar"      "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-cgm-2.1-SNAPSHOT.jar"  "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-emf-2.1.1.jar"  "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-java-2.1.1.jar" "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-pdf-2.1.1.jar"  "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-ps-2.1.1.jar"   "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-svg-2.1.1.jar"  "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-graphicsio-swf-2.1.1.jar"  "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-io-2.0.2.jar"              "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-swing-2.0.3.jar"           "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-util-2.0.2.jar"            "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/freehep-xml-2.1.1.jar"             "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/lib/vectorgraphics/openide-lookup-1.9-patched-1.0.jar" "$VECTORDIRIMG_LIBVG"
  cp "$DISTDIR/doc/vectorgraphics/LGPL.txt"                    "$VECTORDIRIMG_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/LICENSE.txt"                 "$VECTORDIRIMG_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/README.txt"                  "$VECTORDIRIMG_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/ReleaseNotes-2.1.1.html"     "$VECTORDIRIMG_DOCVG"
  cp "$DISTDIR/doc/vectorgraphics/SAMURAIGRAPH-ChangeLog.txt"  "$VECTORDIRIMG_DOCVG"
  cp "$DISTDIR/lib/netCDF/jdom.jar"                            "$VECTORDIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/log4j-1.2.15.jar"                    "$VECTORDIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/netcdf-4.2.jar"                      "$VECTORDIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/slf4j-api-1.5.6.jar"                 "$VECTORDIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/slf4j-log4j12-1.5.6.jar"             "$VECTORDIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/commons-codec-1.3.jar"               "$VECTORDIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/commons-httpclient-3.1.jar"          "$VECTORDIRIMG_LIBNC"
  cp "$DISTDIR/lib/netCDF/commons-logging-1.1.jar"             "$VECTORDIRIMG_LIBNC"
  cp "$DISTDIR/doc/netCDF/README.txt"                          "$VECTORDIRIMG_DOCNC"
  cp "$DISTDIR/doc/netCDF/SAMURAIGRAPH-ChangeLog.txt"          "$VECTORDIRIMG_DOCNC"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-args4j.jar"                 "$VECTORDIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-base.jar"                   "$VECTORDIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5.jar"                  "$VECTORDIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5-batteries_included_lin_win_mac_sun.jar"  "$VECTORDIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5-core.jar"             "$VECTORDIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/cisd-jhdf5-tools.jar"            "$VECTORDIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/commons-io.jar"                  "$VECTORDIRIMG_LIBCJ"
  cp "$DISTDIR/lib/cisd-jhdf5/commons-lang.jar"                "$VECTORDIRIMG_LIBCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/CONTENT"                         "$VECTORDIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING"                         "$VECTORDIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING-args4j"                  "$VECTORDIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING-commons"                 "$VECTORDIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/COPYING-hdf5"                    "$VECTORDIRIMG_DOCCJ"
  cp "$DISTDIR/doc/cisd-jhdf5/LICENSE"                         "$VECTORDIRIMG_DOCCJ"
  cp "$DISTDIR/lib/jmatio/jamtio.jar"                          "$VECTORDIRIMG_LIBJM"
  cp "$DISTDIR/doc/jmatio/license.txt"                         "$VECTORDIRIMG_DOCJM"
  cp "$DISTDIR/doc/jmatio/readme.txt"                          "$VECTORDIRIMG_DOCJM"
  cp "$DISTDIR/lib/jna/jna.jar"                                "$VECTORDIRIMG_LIBJN"
  cp "$DISTDIR/lib/jna/platform.jar"                           "$VECTORDIRIMG_LIBJN"
  cp "$DISTDIR/doc/jna/license.txt"                            "$VECTORDIRIMG_DOCJN"
  cp "$DISTDIR/doc/jna/LGPL.txt"                               "$VECTORDIRIMG_DOCJN"
  cp "$DISTDIR/lib/juniversalchardet/juniversalchardet-1.0.3.jar"  "$VECTORDIRIMG_LIBCD"
  cp "$DISTDIR/doc/juniversalchardet/license.txt"              "$VECTORDIRIMG_DOCCD"
  cp "$DISTDIR/doc/juniversalchardet/MPL.txt"                  "$VECTORDIRIMG_DOCCD"
  cp "$DISTDIR/lib/joda-time/joda-time-2.1.jar"                "$VECTORDIRIMG_LIBJT"
  cp "$DISTDIR/doc/joda-time/LICENSE.txt"                      "$VECTORDIRIMG_DOCJT"
  cp "$DISTDIR/doc/joda-time/NOTICE.txt"                       "$VECTORDIRIMG_DOCJT"
  cp "$TOPDIR/examples/data/"*.txt  "$VECTORDIRIMG_EXAMPLES"
  cp "$TOPDIR/examples/data/"*.nc  "$VECTORDIRIMG_EXAMPLES"
  cp "$TOPDIR/examples/data/"*.sgp  "$VECTORDIRIMG_EXAMPLES"
  cp "$HTMLDIR"/*.html "$VECTORIMGDIST_USERSMANJA"
  cp "$HTMLDIR/images"/*.gif "$VECTORIMGDIST_USERSMANJAIMG"
  cp "$HTMLDIR/images/etc"/*.gif "$VECTORIMGDIST_USERSMANJAIMG/etc"
  sed -e "s+@SAMURAIGRAPH_VERSION@+$VERSION+g" \
      -e "s+@SAMURAIGRAPH_VECTORIMGFILE@+$VECTORDIRIMG+g" \
      "resources/vector/README.txt.in"  > "$VECTORDIRIMG/README.txt"
  tar zcf "$VECTORIMGFILE" "$VECTORDIRIMG"
  echo 'done'
}

case "$1" in
build)
  check_dist_directory
  check_html_directory
  delete_dirimage
  delete_vector_dirimage
  build_dirimage
  build_vector_dirimage
  ;;
clean)
  delete_dirimage
  delete_vector_dirimage
  ;;
*)
  echo "Usage: ${0##*/}: { build | clean }" 2>&1
  exit 65
  ;;
esac

exit 0
