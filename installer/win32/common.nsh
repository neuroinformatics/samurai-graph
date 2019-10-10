;
; NSIS common script for Samurai Graph
;

;;
;; create version number definition header from samurai-graph.properties file
;;
!define SG_TOOLS_DIR     "..\..\tools\win32"
!define SG_RESOURCE_DIR  "..\..\src\resources"
!system '${SG_TOOLS_DIR}\grep.exe "samurai-graph.version" ${SG_RESOURCE_DIR}\samurai-graph.properties | ${SG_TOOLS_DIR}\sed.exe -e "s+samurai-graph.version=+!define SG_FULLVERSION +g" -e "s+\x0d$++g" > version.nsh'
!system '${SG_TOOLS_DIR}\grep.exe "samurai-graph.version" ${SG_RESOURCE_DIR}\samurai-graph.properties | ${SG_TOOLS_DIR}\sed.exe -e "s+samurai-graph.version=+!define SG_VER_MAJOR +g" -e "s+\x0d$++g" -e "s/\([[0-9]]*\).\([[0-9]]*\).\([[0-9]]*\).*/\1/" >> version.nsh'
!system '${SG_TOOLS_DIR}\grep.exe "samurai-graph.version" ${SG_RESOURCE_DIR}\samurai-graph.properties | ${SG_TOOLS_DIR}\sed.exe -e "s+samurai-graph.version=+!define SG_VER_MINOR +g" -e "s+\x0d$++g" -e "s/\([[0-9]]*\).\([[0-9]]*\).\([[0-9]]*\).*/\2/" >> version.nsh'
!system '${SG_TOOLS_DIR}\grep.exe "samurai-graph.version" ${SG_RESOURCE_DIR}\samurai-graph.properties | ${SG_TOOLS_DIR}\sed.exe -e "s+samurai-graph.version=+!define SG_VER_MICRO +g" -e "s+\x0d$++g" -e "s/\([[0-9]]*\).\([[0-9]]*\).\([[0-9]]*\).*/\3/" >> version.nsh'

;;
;; load generated version definition file
;;

!include version.nsh

!define SG_VERSION   "${SG_VER_MAJOR}.${SG_VER_MINOR}.${SG_VER_MICRO}"

;;
;; check version definitions
;;

!ifndef SG_VERSION
  !error "SG_VERSION is not defined"
!endif
!ifndef SG_VER_MAJOR
  !error "SG_VER_MAJOR is not defined"
!endif
!ifndef SG_VER_MINOR
  !error "SG_VER_MINOR is not defined"
!endif
!ifndef SG_VER_MICRO
  !error "SG_VER_MICRO is not defined"
!endif

;;
;; define common variables
;;

!define SG_PRODUCT           "Samurai Graph"
!define SG_COMMENT           "Samurai Graph is a user-friendly graph ploter"
!define SG_URL               "http://samurai-graph.sourceforge.jp/"
!define SG_PUBLISHER         "RIKEN BSI Neuroinformatics Laboratory"
!define SG_PUBLISHER_URL     "http://www.ni.brain.riken.jp/"
!define SG_NAME              "${SG_PRODUCT} ${SG_VERSION}"
!define SG_COMPANY           "RIKEN Japan"
!define SG_COPYRIGHT         "(C) RIKEN Japan"
!define SG_PRODUCT_VERSION   "${SG_FULLVERSION}"

