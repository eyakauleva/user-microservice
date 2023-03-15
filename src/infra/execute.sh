#! /bin/bash

kubectl apply -f zookeeper-service.yaml
kubectl apply -f zookeeper-deployment.yaml

kubectl apply -f kafka-service.yaml
kubectl apply -f kafka-deployment.yaml

#kubectl apply -f app-configmap.yaml

#minikube service lawoffice