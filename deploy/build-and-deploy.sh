#!/bin/bash
# this script builds the project with a given profile and deploys it
# to Amazon ECR and AWS Fargate
if [[ $0 != ./deploy/* ]]; then
  echo "Please run this script from the root of our repository!"
  exit -1
fi
PROFILE="$1"
TRAVIS_BRANCH="$2"
if [ -n "${TRAVIS_BRANCH}" ]; then
  PROFILE="dev"
fi
if [ "${TRAVIS_BRANCH}" = "master" ]; then
  PROFILE="prod"
fi
if [ "${TRAVIS_BRANCH}" = "test" ]; then
  PROFILE="test"
fi
if [ -z ${PROFILE} ]; then
  echo "Please provide a valid profile e.g.: $0 dev"
  exit -1
fi
echo "Going to run maven build with profile: ${PROFILE}"
mvn --settings .travis.settings.xml --no-transfer-progress -P${PROFILE} clean install
if [ $? -ne 0 ]; then
    echo "Maven build failed!"
    exit -1
fi
AWS_CRED=($(aws sts assume-role --role-arn arn:aws:iam::347729458675:role/Admin --role-session-name travis-deployment --query 'Credentials.[AccessKeyId,SecretAccessKey,SessionToken]' --output text))
export AWS_ACCESS_KEY_ID=${AWS_CRED[0]}
export AWS_SECRET_ACCESS_KEY=${AWS_CRED[1]}
export AWS_SESSION_TOKEN=${AWS_CRED[2]}
$(aws ecr get-login --no-include-email --region eu-central-1)
mvn -P${PROFILE} dockerfile:push dockerfile:push@push-image-latest
if [ $? -ne 0 ]; then
    echo "Maven Docker push failed!"
    exit -1
fi
aws ecs list-tasks --cluster metadatamanagement-${PROFILE} --service metadatamanagement-${PROFILE} | jq -r ".taskArns[]" | awk '{print "aws ecs stop-task --cluster metadatamanagement-${PROFILE} --task \""$0"\""}' | sh
aws ecs list-tasks --cluster metadatamanagement-${PROFILE} --service metadatamanagement-worker | jq -r ".taskArns[]" | awk '{print "aws ecs stop-task --cluster metadatamanagement-${PROFILE} --task \""$0"\""}' | sh
if [ $? -ne 0 ]; then
    echo "Task redeployment failed!"
    exit -1
fi
