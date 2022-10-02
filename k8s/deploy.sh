#!/usr/bin/env bash

envsubst < config.$ENVIRONMENT.yml > k8s-config.yml
kubectl apply -f k8s-config.yml

envsubst < main.yml > k8s-main.yml
kubectl apply -f k8s-main.yml

kubectl rollout status deployments/$JOB_NAME -n $NAMESPACE