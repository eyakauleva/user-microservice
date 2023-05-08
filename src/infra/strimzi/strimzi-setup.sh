#! /bin/bash

kubectl create namespace strimzi

helm repo add strimzi https://strimzi.io/charts/
helm install strimzi-kafka strimzi/strimzi-kafka-operator --version 0.34.0 --namespace strimzi

kubectl apply -f kafka-persistent.yaml -n strimzi