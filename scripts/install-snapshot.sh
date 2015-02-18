#!/bin/bash
#
# Deploy a snapshot build to Sonatype.  Only non-pull requests will be deployed.
#

echo "Pull request: '${TRAVIS_PULL_REQUEST}' on branch '${TRAVIS_BRANCH}' with JDK '${TRAVIS_JDK_VERSION}'"
if [ "${TRAVIS_PULL_REQUEST}" = "false" ] && [ "${TRAVIS_BRANCH}" = "master" ] && [ "${TRAVIS_JDK_VERSION}" = "oraclejdk8" ]; then

    ./gradlew -PnexusUsername=${CI_DEPLOY_USERNAME} -PnexusPassword=${CI_DEPLOY_PASSWORD} clean assemble uploadArchives
fi