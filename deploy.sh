#!/bin/bash
set -e

# --------------------------------------------------------
# Before using this script, need to ensure
# ~/.gnupg/ contains key corresponding to KEYNAME, and
# ~/.m2/settings.xml contains OSS Sonatype credentials
# https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven
# https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide
# --------------------------------------------------------

SCRIPT_PATH=$0
SCRIPT_DIR=${SCRIPT_PATH%/*}

if [ "$1" != '' ]; then
	PASSPHRASE=-Dgpg.passphrase=$1
else
	PASSPHRASE=''
fi

if [ "$2" != '' ]; then
	KEYNAME=-Dgpg.keyname=$2
else
	KEYNAME=-Dgpg.keyname=CCD1D109
fi

pushd ${SCRIPT_DIR}/duel-mvc
mvn clean deploy -U -DperformRelease=true ${KEYNAME} ${PASSPHRASE}
popd

pushd ${SCRIPT_DIR}/duel-mvc-archetype
mvn clean deploy -U -DperformRelease=true ${KEYNAME} ${PASSPHRASE}
popd
