#!/usr/bin/env bash

if [[ $# -ne 1 ]]; then
  echo "Usage: ${0} <jogetplugin.jar>"
  exit
fi

if [[ ! -f ${1} ]]; then
  echo "[!] Could not access file ${1}."
  exit
fi

TEMPDIR=$(mktemp -d)

# Unpack everything
cp ${1} ${TEMPDIR}/${1%.*}.zip
mkdir -p ${TEMPDIR}/new/
unzip ${TEMPDIR}/${1%.*}.zip -d ${TEMPDIR}/new/

# Actual fix
sed -i 's/;version="\[3.0,4)"//g' ${TEMPDIR}/new/META-INF/MANIFEST.MF

# Pack everything up
pushd ${TEMPDIR}/new/ >/dev/null
zip -r ${TEMPDIR}/new.zip .
popd >/dev/null
cp ${TEMPDIR}/new.zip ./${1%.*}-patched.jar

echo "[>] Saved to ${TEMPDIR}/${1%.*}-patched.jar"
