#! /bin/bash

kubectl delete deployment.apps/user-deploy
kubectl delete service/user-service
kubectl delete configmap user-configmap

kubectl delete deployment.apps/tickets-deploy
kubectl delete service/tickets-service
kubectl delete configmap tickets-configmap

kubectl delete deployment.apps/kafka-deploy
kubectl delete service/kafka-service

kubectl delete statefulset.apps/zookeeper-stateful
kubectl delete service/zookeeper-service

kubectl delete statefulset.apps/redis-stateful
kubectl delete service/redis-service
kubectl delete secret redis-secrets

kubectl delete statefulset.apps/pg-stateful-common
kubectl delete service/pg-service-common
kubectl delete secret pg-secrets-common

kubectl delete statefulset.apps/mongodb-stateful-common
kubectl delete service/mongodb-service-common
kubectl delete secret mongodb-secrets-common

kubectl delete ingress app-ingress
kubectl delete requestauthentications req-auth-jwt -n istio-system
kubectl delete authorizationpolicies require-jwt -n istio-system
