#!/bin/bash
echo "########### Setting up localstack profile ###########"
aws configure set aws_access_key_id access_key --profile=localstack
aws configure set aws_secret_access_key secret_key --profile=localstack
aws configure set region us-east-1 --profile=localstack

echo "########### Setting default profile ###########"
export AWS_DEFAULT_PROFILE=localstack

echo "########### Setting env variables ###########"
export HTTP_SUBSCRIBER=http://host.docker.internal:8080/spring-cloud-sns-topic-listener/subscribers/purchase-transactions
export SQS_DLQ=sqs-dlq
export SNS_TOPIC=purchase-transactions-topic

echo "########### Creating DLQ SQS  ###########"
aws --endpoint-url=http://localstack:4566 sqs create-queue --queue-name $SQS_DLQ

echo "########### ARN for Subscriber DLQ SQS ###########"
SQS_DLQ_ARN=$(aws --endpoint-url=http://localstack:4566 sqs get-queue-attributes\
                  --attribute-name QueueArn --queue-url=http://localhost:4566/000000000000/"$SQS_DLQ"\
                  |  sed 's/"QueueArn"/\n"QueueArn"/g' | grep '"QueueArn"' | awk -F '"QueueArn":' '{print $2}' | tr -d '"' | xargs)


echo "########### Creating SNS topic and getting arn  ###########"
SNS_TOPIC_ARN=$(aws --endpoint-url=http://localhost:4566 \
  sns create-topic --name=$SNS_TOPIC \
   --attributes RawMessageDelivery=false,\
DeliveryPolicy="\"{\\\"http\\\":{\\\"defaultHealthyRetryPolicy\\\":{\\\"minDelayTarget\\\":20,\\\"maxDelayTarget\\\":20,\\\"numRetries\\\":3,\\\"numMaxDelayRetries\\\":0,\\\"numNoDelayRetries\\\":0,\\\"numMinDelayRetries\\\":0,\\\"backoffFunction\\\":\\\"linear\\\"},\\\"disableSubscriptionOverrides\\\":false}}\"" \
  |  sed 's/"TopicArn"/\n"TopicArn"/g' | grep '"TopicArn"' | awk -F '"TopicArn":' '{print $2}' | tr -d '"' | xargs)


echo "########### List SNS topics ###########"
aws --endpoint-url=http://localhost:4566 sns  list-topics --starting-token=0 --max-items=3

echo "########### Get SNS topic attributes ###########"
aws --endpoint-url=http://localhost:4566\
 sns get-topic-attributes \
 --topic-arn="$SNS_TOPIC_ARN"


echo "########### Creating subscription for Spring Boot app  ###########"
aws --endpoint-url=http://localhost:4566 \
 sns subscribe \
--topic-arn="$SNS_TOPIC_ARN" \
--protocol=http \
--notification-endpoint="$HTTP_SUBSCRIBER" \
--attributes '{
     "RedrivePolicy": "{\"deadLetterTargetArn\": \"'"$SQS_DLQ_ARN"'\"}"}' \
--return-subscription-arn

echo "########### List subscriptions  ###########"
aws --endpoint-url=http://localhost:4566\
 sns list-subscriptions


