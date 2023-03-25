#! /bin/bash

#kubectl delete deployment.apps/kafka-deploy
#kubectl delete service/kafka-service
#
#kubectl delete deployment.apps/zookeeper-deploy
#kubectl delete service/zookeeper-service

kubectl delete deployment.apps/user-deploy
kubectl delete service/user-service
kubectl delete configmap user-configmap

#kubectl delete statefulset.apps/pg-stateful-users
#kubectl delete service/pg-service-users
#kubectl delete secret pg-secrets-users