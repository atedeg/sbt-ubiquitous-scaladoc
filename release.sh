#!/bin/bash

branch=$1
nextReleaseVersion=$2
if [[ $branch == "main" ]]; then
    sbt +publishSigned || exit 1
    sbt sonatypeBundleRelease || exit 2
    echo "Published on Maven Central"
fi
#git tag -a -f "$nextReleaseVersion" "$nextReleaseVersion" -F CHANGELOG.md
#git push --force origin "$nextReleaseVersion"
echo "Pushed annotated tag: $nextReleaseVersion"
