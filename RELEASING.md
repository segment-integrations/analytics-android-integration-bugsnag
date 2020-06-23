Releasing
========

This repository is set up for autorelease. The process involves pushing a new git tag.

 1. `git checkout master && git pull`; all the following commands should be performed while on the `master` branch.
 1. Update the `CHANGELOG.md` for the impending release.
 1. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version)
 1. `git tag -a X.Y.Z -m "Version X.Y.Z"` (where X.Y.Z is the new version)
 1. `git push && git push --tags`
 1. Update the `gradle.properties` to the next SNAPSHOT version.
 1. `git commit -am "Prepare next development version."`
 1. `git push`
