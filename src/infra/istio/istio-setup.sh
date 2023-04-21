istioctl install

minikube addons enable ingress

kubectl apply -f istio/ingress.yaml
kubectl apply -f istio/request-auth.yaml
kubectl apply -f istio/auth-policy.yaml

minikube tunnel