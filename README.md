
A simple sample of microservice on spring-boot.

##### 
[Description](#description)<br/>
[How to sync mongodb & elasticsearch](#sync)


<a id="description"></a>
## Description

User service provides endpoints to receive **data related to users**.<br/>

This microservice is connected with another microservice via ***service registry pattern***. A key advantage of a microservices architecture is the ability to create new instances of each service to meet the current load, respond to failures and roll out upgrades. One side effect of this dynamic server-side environment is that IP address and port of a service instances change constantly. In order to route an API request to a particular service, the client or API gateway needs to find out the address of the service instance it should use. Whereas in the past it might have been feasible to record these locations in a config file, in a dynamic environment where instances are added and removed on the fly, an automated solution is needed. Service discovery provides a mechanism for keeping track of the available instances and distributing requests across them.

***Circuite breaker pattern*** is also implemented there. Use of the Circuit Breaker pattern can allow a microservice to continue operating when a related service fails, preventing the failure from cascading and giving the failing service time to recover. You wrap a protected function call in a circuit breaker object, which monitors for failures. Once the failures reach a certain threshold, the circuit breaker trips, and all further calls to the circuit breaker return with an error, without the protected call being made at all.


<a id="sync"></a>
## How to sync mongodb & elasticsearch

1. Start mongodb replica set using ```./docker-compose.yaml```
2. Start elasticsearch using ```./src/infra/docker-elk/docker-compose.yaml```
3. Add ```127.0.0.1 mongodb``` to hosts file on your machine
4. Run ```npm instal``` in ```./src/infra/mongodb-elasticsearch```
5. Run ```node index.js``` in ```./src/infra/mongodb-elasticsearch```


