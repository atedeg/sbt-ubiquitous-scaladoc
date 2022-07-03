#!/usr/bin/env bash

branch=$(git rev-parse --abbrev-ref HEAD)
nextReleaseVersion=$1

echo "Current branch: $branch"

if [[ $branch == "main" ]]; then
    sbt +publishSigned || exit 1
    sbt sonatypeBundleRelease || exit 2
    echo "Published on Maven Central"
fi
