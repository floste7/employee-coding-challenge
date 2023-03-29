# Employee API - Coding Challenge

![build-and-test](https://github.com/floste7/employee-coding-challenge/actions/workflows/build-and-test.yml/badge.svg)

# Overview

This repository contains my solution to the Employee API - Coding Challenge. Following features are supported by the
application (```backend```):

- :heavy_check_mark: REST API for retrieving, creating, updating and deleting employees.
- :heavy_check_mark: Employees must have unique e-mail addresses.
- :heavy_check_mark: Domain events are sent to a Kafka topic when employees are created, updated or deleted.
- :heavy_check_mark: ```POST```, ```PUT``` and ```DELETE``` endpoints are protected with ```oAuth2```
  , ```spring-boot-security``` and Keycloak
- :heavy_check_mark: A Swagger-UI is included to test the authentication and REST endpoints.
- :heavy_check_mark: PostgreSQL is used to persist the employees.

# Run the application

The simplest way to run the application with all required services is to use ```docker-compose```. Clone the repository
and run:

```shell
cd employee-coding-challenge
docker-compose up
```

Since this command is downloading and building docker containers for all required services, it may take a while for all
services to start up (depending on your internet connection approx. 15 minutes).

Following services are available after successful start-up:

| Service        | Description                                                                                                     | URL (front-end)                               |
|----------------|-----------------------------------------------------------------------------------------------------------------|-----------------------------------------------|
| backend        | Contains the business logic                                                                                     | http://localhost:8080/swagger-ui/index.html   |
| backend-read   | Exposes REST API for reading employees with an [alternative architecture](#additional-thoughts-on-architecture) | http://localhost:8081/swagger-ui/index.html   |
| zookeeper      | Zookeeper for managing the Kafka cluster                                                                        | -                                             |
| broker         | Kafka Broker                                                                                                    | -                                             |
| akhq           | Frontend for managing and viewing Kafka clusters                                                                | http://localhost:9080                         |
| keycloak       | Used for authentication (IAM) and user management                                                               | http://localhost:8180                         |
| postgres       | Relational database for storing employees                                                                       | -                                             | 

# Use the application
Go to the swagger-ui of the ```backend``` service (http://localhost:8080/swagger-ui/index.html) and try out the REST APIs.

### Authentication
As mentioned above, you need to be authenticated for using the  ```POST```, ```PUT```, ```DELETE``` endpoints.
This repository comes with a sample Keycloak realm containing a sample user. Click on the swagger-ui's "Authorize" button
and provide the following user credentials:

- username: ```user```
- password: ```user```

# Build the application

The project can be built with ```maven```. Run the following command at the root level of the repository to build all 
modules and execute unit and integration tests:

```shell
mvn clean install
```

# Additional Thoughts on Architecture

While the ```backend``` service solves the challenge with a traditional monolithic approach, the ```backend-read``` service 
splits the application in a microservice based system and introduces two additional architectural concepts:

- Event Souring
- CQRS (Command Query Responsibility Segregation)

### Event Sourcing
The ```backend-read``` service consumes the domain events produced by the ```backend``` service, and instead of keeping
the employee's state by updating a database entity, it commits the domain events to an append-only changelog stream.
The last domain event of a specific employee therefore represents the employee's current state. In order to achieve this,
the ```backend-read``` service uses Kafka's ```KTable``` abstraction and materializes the changelog stream to Kafka's
internal ```RocksDB``` (or in-memory cache).

### CQRS (Command Query Responsibility Segregation)
The ```backend-read``` service only exposes APIs for reading an employee. The only way to create, update or delete an employee
is via the ```backend``` service. With Kafka sitting in between those two service, it acts as an event-handler that can be used
to synchronise two different data models: one for commands (create, update and delete) and one for querying (get).
One benefit of this concept is, that it allows the system to scale out independently for write and read operations.
The ```backend-read``` service utilizes and exposes Kafka's ```InteractiveQueryService``` to query the state store. 

### Outlook
The following graph shows both concepts and its current and planed implementation in this project:

![Architecture](/docs/architecture.png "Architecture")
