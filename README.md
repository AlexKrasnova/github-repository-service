# Github repository service

The service is intended to provide information about user repositories and their branches.

# How to build and run

Project can be compiled with JDK 17 and above.

To compile just do `mvn clean package`.

Spring Boot Version: 3.1.2

## Prerequisites

* JAVA 17 should be installed
* Maven should be installed

To run the application execute the below command :

```
java -jar target/github-repository-service-*.jar -DGITHUB_TOKEN={your_token}
```

The VM option GITHUB_TOKEN is optional, but in this case Github limits number of request. 

The server will start at <http://localhost:8080>.

Once the application starts, The Swagger UI will open at : <http://localhost:8080/swagger-ui/index.html> .

We can now use the Swagger-UI to test our application.

## Exploring the Rest APIs

The application contains the following REST API

```
1. GET /repositories/by-user/alexkrasnova - Get the repositories for a given username
```

```
curl -X 'GET' \
  'http://localhost:8080/repositories/by-user/alexkrasnova' \
  -H 'accept: application/json'
```
