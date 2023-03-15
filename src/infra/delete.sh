#! /bin/bash

kubectl delete deployment.apps/kafka-deploy
kubectl delete service/kafka-service

kubectl delete deployment.apps/zookeeper-deploy
kubectl delete service/zookeeper-service

#kubectl delete configmap app-configmap