#! /bin/bash

#kubectl apply -f zookeeper-service.yaml
#kubectl apply -f zookeeper-statefulset.yaml
#
#kubectl apply -f kafka-service.yaml
#kubectl apply -f kafka-deployment.yaml
#
#kubectl apply -f mongodb-secrets.yaml
#kubectl apply -f mongodb-service.yaml
#kubectl apply -f mongodb-statefulset.yaml
#
#kubectl apply -f pg-secrets.yaml
#kubectl apply -f pg-service.yaml
#kubectl apply -f pg-statefulset.yaml
#
#kubectl apply -f redis-secrets.yaml
#kubectl apply -f redis-service.yaml
#kubectl apply -f redis-statefulset.yaml

#kubectl apply -f app-configmap.yaml
#kubectl apply -f app-service.yaml
#kubectl apply -f app-deployment.yaml
#
kubectl apply -f tickets-configmap.yaml
kubectl apply -f tickets-service.yaml
kubectl apply -f tickets-deployment.yaml

#kubectl apply -f ingress.yaml

#minikube tunnel