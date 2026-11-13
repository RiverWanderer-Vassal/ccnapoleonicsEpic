#!/usr/bin/env bash
# Publish (or update) one GitHub Release from a module file on the VASSAL Module Library.
# Usage: publish-release.sh <version> <vassal-file-url> <target-commit-or-tag>
# Notes come from release-notes/<version>.md; the checksum printed there must match the file.
set -euo pipefail
ver="$1"; url="$2"; target="$3"; tag="v$ver"
notes="release-notes/$ver.md"
[ -f "$notes" ] || { echo "::error::$notes not found"; exit 1; }
mkdir -p out; vmod="out/CCNapoEpic4-$ver.vmod"
curl -fsSL -o "$vmod" "$url"
ck=$(python3 -c "import zlib,sys;print('%08x'%(zlib.crc32(open(sys.argv[1],'rb').read())&0xffffffff))" "$vmod")
ver_in=$(unzip -p "$vmod" moduledata | sed -n 's:.*<version>\(.*\)</version>.*:\1:p')
echo "Downloaded $url: version $ver_in, checksum $ck"
[ "$ver_in" = "$ver" ] || { echo "::error::file reports version $ver_in, expected $ver"; exit 1; }
grep -q "checksum \*\*$ck\*\*" "$notes" || { echo "::error::checksum $ck is not the one in $notes"; exit 1; }
title="Epic & Grand Battles $ver"
if gh release view "$tag" >/dev/null 2>&1; then
  gh release edit "$tag" --title "$title" --notes-file "$notes"
  gh release upload "$tag" "$vmod#CCNapoEpic4.vmod ($ver)" --clobber
else
  gh release create "$tag" "$vmod#CCNapoEpic4.vmod ($ver)" --title "$title" --notes-file "$notes" --target "$target" --latest=false
fi
