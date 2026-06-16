#!/bin/sh
cd `dirname $0`
PRODUCTXML=../../../web/htdocs/product.xml
CHANGELOGXSL=ChangeLog.xsl
CHANGELOGXML=../resources/ChangeLog.html

rm -f $CHANGELOGXML

xmllint -valid -noout $PRODUCTXML
if [ $? -ne 0 ]; then
  echo "Error : $PRODUCTXML - invalid format xml file "
  exit 1
fi
xsltproc -o $CHANGELOGXML $CHANGELOGXSL $PRODUCTXML
xmllint -html -valid -noout $CHANGELOGXML
if [ $? -ne 0 ]; then
  echo "Error : CHANGELOGXML - invalid format html  file "
  exit 1
fi

