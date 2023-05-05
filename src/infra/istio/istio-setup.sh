#! /bin/bash

istioctl install

minikube addons enable ingress

kubectl apply -f ingress.yaml
kubectl apply -f request-auth.yaml
kubectl apply -f auth-policy.yaml

minikube tunnel


#------

kubectl delete ingress app-ingress

kubectl delete authorizationpolicy require-jwt -n istio-system

kubectl delete requestauthentication req-auth-jwt -n istio-system