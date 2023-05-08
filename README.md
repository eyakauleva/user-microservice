
##### 
[Description](#description)<br/>
[Semantic versioning](#version)<br/>
[How to authenticate using Istio and JWT](#istio)<br/>
[How to sync mongodb & elasticsearch (via mongodb change streams)](#sync_changestreams)
[Kafka connector to elasticsearch](#sync_kafka)


<a id="description"></a>
## Description

User service provides endpoints to receive **data related to users**.<br/>

This microservice is connected with another microservice via ***service registry pattern***. A key advantage of a microservices architecture is the ability to create new instances of each service to meet the current load, respond to failures and roll out upgrades. One side effect of this dynamic server-side environment is that IP address and port of a service instances change constantly. In order to route an API request to a particular service, the client or API gateway needs to find out the address of the service instance it should use. Whereas in the past it might have been feasible to record these locations in a config file, in a dynamic environment where instances are added and removed on the fly, an automated solution is needed. Service discovery provides a mechanism for keeping track of the available instances and distributing requests across them.

***Circuite breaker pattern*** is also implemented there. Use of the Circuit Breaker pattern can allow a microservice to continue operating when a related service fails, preventing the failure from cascading and giving the failing service time to recover. You wrap a protected function call in a circuit breaker object, which monitors for failures. Once the failures reach a certain threshold, the circuit breaker trips, and all further calls to the circuit breaker return with an error, without the protected call being made at all.


<a id="version"></a>
## Semantic versioning

Github action (docker.yml) creates release with tag via https://github.com/mathieudutour/github-tag-action
and then docker image with this generated tag is pushed to dockerhub. <br/>
When deploying app to kubernetes set image version in ```./src/infra/minikube.sh``` to ```TAG``` variable
<br/><br/>

<a id="istio"></a>
## How to authenticate using Istio and JWT

### If you don't have private and public keys:

1. Set your data to ```payload``` variable in ```./src/infra/istio/rsa-jwt-generator/generate-keys-and-tokens-using-python.py```
2. To generate private & public keys and token run 
```bash
python ./src/infra/istio/rsa-jwt-generator/generate-keys-and-tokens-using-python.py
```
3. Set issuer and generated public key to ```./src/infra/istio/request-auth.yaml```
4. Apply changed to kubernetes 
```bash
kubectl apply -f ./src/infra/istio/request-auth.yaml
```
5. Add ```Authorization header``` to your request with ```Bearer <generated_token>```


### If you have private and public keys:

1. Set your private & public keys in ```./src/infra/istio/rsa-jwt-generator/generate-token.py```
2. To generate token run
```bash
python ./src/infra/istio/rsa-jwt-generator/generate-token.py
```
3. Set issuer and generated public key to ```./src/infra/istio/request-auth.yaml```
4. Apply changed to kubernetes
```bash
kubectl apply -f ./src/infra/istio/request-auth.yaml
```
5. Add ```Authorization header``` to your request with ```Bearer <generated_token>```
<br/><br/>


<a id="sync_changestreams"></a>
## How to sync mongodb & elasticsearch (via mongodb change streams)
### Without docker:
1. Start mongodb replica set using ```./docker-compose.yaml``` <br/>
(To create replica set image created via ```./src/infra/mongo-rs/Dockerfile``` is used)
2. Start elasticsearch using ```./docker-compose.yaml```
3. Add ```127.0.0.1 mongodb``` line to the hosts file on your machine
4. Run ```npm instal``` in ```./src/infra/mongodb-elasticsearch```
5. Run ```node index.js``` in ```./src/infra/mongodb-elasticsearch```

### Using docker:

1. Start mongodb replica set, elasticsearch and synchronizer using ```./docker-compose.yaml``` <br/>

***CAUTION:*** without docker set mongodb and elasticsearch domains as localhost,
otherwise set their service names
<br/><br/>


<a id="sync_kafka"></a>
## Kafka connector to elasticsearch

1. Start kafka-connect container using ```./docker-compose.yaml``` <br/>
2. Create kafka-connector by sending a POST request to ```http://localhost:8083/connectors``` url <br/>
with body like in ```src/infra/kafka-connect-elastic/kafka-elastic-connector.json``` example <br/>
   (kafka topic and elasticsearch index will have the same name mentioned in ```topics``` field) <br/>
3. Send messages to kafka

To list all connectors make a GET request to ```http://localhost:8083/connectors``` <br/>
To delete a connecter make a DELETE-request to ```http://localhost:8083/connectors/<connector-name>``` <br/>
To get connector status make a GET request to ```http://localhost:8083/connectors/<connector-name>/tasks/0/status``` <br/>

***CAUTION:*** if you want to change your connector config (after you already created it), <br/>
you have to delete the existing one and create a new one with changed configs and
***!!! necessarily a new name !!!*** <br/>
(even if you have deleted kafka-connect container, new kafka-connector name is required!) 

