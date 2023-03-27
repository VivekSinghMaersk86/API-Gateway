# governmentservice

[![Java CI - Build, Vulnerability check and  Push to Registry](https://github.com/CTO-RBIH/governmentservice/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/CTO-RBIH/governmentservice/actions/workflows/ci-cd.yml)


## How to work with this project scaffold
In this readme you will find instructions specific to working with this Java Microservice scaffold based on Spring Boot. 

### File Structure
```
├── .gitignore - Common git ignore patterns.
├── .gtihub/workflows/ci.yml - Used by github Actions to execute the build pipeline 
├── .gtihub/workflows/ci-cd.yml - Used by github Actions to execute the build pipeline and for continous deployment
├── chart/ - The Helm chart used to deploy the application to Kubernetes or OpenShift.
│   ├── values.yaml - The main K8s configuration file
├── Dockerfile - Used by Docker to build the app container image.
├── pom.xml - Maven Project definition file.
└── src/
    ├── main/
    │   ├── java/in.rbihub
    │   │   └── Application.java - Simple Spring Boot application.  
    │   │   ├── java/in.rbihub/config
    │   │   │   └── ApplicationConfig.java - Application configuration for application.
    │   │   │   └── SecurityConfig.java - Application Security configuration.
    │   │   ├── java/in.rbihub/controller
    │   │   │   └── Controller.java - Application controller and the endpoint apis. 
    │   │   ├── java/in.rbihub/utils  
    │   │   │   └── CommonUtil.java -  Application common response Implementation as response object  
    │   │   │   └── ICommonMethods.java - Application common response interface as response object  
    │   │   │   └── RSA.java - RSA utils methods
    │   │   ├── java/in.rbihub/service 
    │   │       └── <>Service.java - Application Service to apply  business logic                        
    │   ├── /resources/
    │       ├── application.yaml     - Used by Spring Boot to configure default application
    │       │                          configuration.  This is initialized with core
    │       │                          application capabilities such as metrics and health
    │       │                          via Spring Actuator.
    │       └── logback.xml      - Default logback logging configuration.
    └── test/java/in.rbihub
        ├── java/in.rbihub
            └── Application/test.java - Simple test class

```

## Customizing your microservices

 - Fix the name of the microservice in the following files
 - pom.xml
 - chart/values.yaml
 - chart/Chart.yaml
 - change your provider url in application.yml under ''src/main/resources'' 
 - If you have changed  application.yml under ''src/main/resources'' ensure any secrets to 
   be created in chart/templates/deployment.yaml file look for ''- name: secret''


## Microservice Features Summary

| # | Features |  Underlying Technology | Capability |  Concept |
| ------| ------ | ------|-------- | ------ |
| 1 | [ DevSecOps for Java Microservices ](.github/workflows/ci-cd.yml) |  Github Actions, Gitops based deployment, Security scans – Trivy vulnerability scanner, Code build, Dockerized Images, Azure Registry (Can be configured to any registry), RedHat OpenShift or Kubernetes on AWS cloud or On Prem Vm Infrastructure | Prebuild with  DevSecOps secured practices to support customer’s application modernization journey. This includes building and deployment of applications on Kubernetes and OCP along with  certified containers and using DevSecOps secured practices |   Enable Applications to build and deploy with DevSecOps secured practices on Kubernetes or OCP on premise or AWS /Azure cloud.|
| 2 | [ Deployment charts for Microservices](chart/) |  Helm Charts for microservices to be deployed on RedHat OpenShift or Kubernetes on AWS cloud or On Prem Vm Infrastructure  | Prebuild charts that will help is deployment for any kind of Gitops or manual deployment with ease. |  Ease of deploying applications on Kubernetes or OCP on premise or AWS / Azure Cloud |
| 3 | [ Java microservices code using Springboot ](pom.xml) | Springboot framework application with logging framework, maven based code build and package integrated with Docker image   | Prebuild code structure (config, dao, entity, services and controllers) and code that takes care of complete CRUD operation. Rest api exposed for CRUD operation and application properties.   |  Ease of Applications development with all coding standard /structure followed. |
| 4 | [ Certified Container Support ](Dockerfile) | Certfied Container using Kyndryl and Redhat best practices | Prebuild certfied base images for java microservices free from image vunerability  | Ease of deploying and building images for application|
| 5 | [ Application Metrics Support ](src/main/java/in.rbihub/service) | Prometheus based metrics with custom metrics| Prebuild custom metrics integrated inot the code for all CRUD operations of services  | Ease of monitoring application|


### Running Application locally

If we need to run the application locally use the below command

First verify the application.yml file under ``src/main/resources/`` to change username or password and also set the port number
then run the below command

```
mvn spring-boot:run

```

Check the application running status based on the port number using the simple url in your browser as below

    http://localhost:8090/

It will ask for username and password , use the one set in application.yaml file

This will show a message ``Greetings from Rbih - microservice!``

Check the application health, metric apis using the below url path
``http://localhost:8090/actuator`` and click on respective url like ``http://localhost:8090/actuator/prometheus``  for prmoetheus metric

In case you want to access the api to create,retrive for the microservice use the below

### To check the application use swagger with below

http://localhost:8090/swagger-ui/#/controller/retrieveMPPersonUsingGET
    
### To the run the application with postman use the below 


TYPE : GET
    http://localhost:8090/landOwner/{version}/{lang}
    for example
    
    version is 1.0
    lang is set to en

	Request Header should have the below

	clientId is set like 112
     api_key to set 24 char alfanumeric 234134134143WWER23123122
     txncode to 345245699785
     state is for madyapradesh can be set to 23
	consent value will be y/n
	consentId value will be like 123433
	
	For MP
	Provide the below details
	khasraId set to  118040200107207000995
	distId set to numeric value like 18
	tehId set to value like 04
    
    http://localhost:8090/landowner/1.0/en
    
Set the username and password in Authorization block based on what is set in application.yml file

In the header provide the below

	clientId *
	string
	(header)

	clientid can be set as 1112    

	consent *
	string
	(header)

	consent can be set as y

	consentId *
	string
	(header)

	consentId can be set as 2232322

	distId *
	integer($int32)
	(header)

	distId can be set as 18

	khasraId *
	string
	(header)

	khasraId  can be set as 118040200107207000995


	tehId *
	string
	(header)

	tehId  can be set as 118040200107207000995



Use the get response as below 


	   {
		"result": {
		    "errorcode": "E000",
		    "status": "success",
		    "info": ""
		},
		"data": {
		    "ownerDetail": {
                	"aadhartkn": "01001187VKWJWPj37A8DTkQ9Hl6EdmtE9gZLhODSlfDmhDtfy3qYCD85SpzHPrXIGJ6Zohhk",
                	"fanel": "भारतसिंह",
                	"caste": "सामान्य",
                	"heirpcshr": "1",
                	"addr3": "malkhena taraana ujjain madhya pradesh",
                	"heirtyp": "भूमि स्वामी",
                	"flne": "premsingh",
                	"flnel": "प्रेमसिंह  "
            	     }
		},
		"signature": "mcFVILhm8Iz8qJHytK/Ilygz8/eb65m0IQy0NvinRlvA3bI6HYErF2HOMTCpEZCWAKibW0wFZ8UtoBJ1uPZhWZEyzx5EzzP9Zn9KDvlZlWxoT99Tl6u2HNvrD2iDhDmng2R7ncN+/qya53v8sFy4Qu/rJ3qCAcpzK6KdHjAvYhL3sPwTf5NID6SJI9Lbxqvy61S7CvQ4DC6McxowBIFGR+BOLINqQ8PnX0MgNqfimflL9uzAu/1qtXJKHnGBtjIFgMXcT6Lcad75UCDSpjykMUJO/E0lFvLIwVui1hmhDf9EQsBLqiPu7YtYDq7eM4cIQLLVhJm3ZNzveibhJayZXA==",
		"meta": {
		    "txncode": "345245699785",
		    "ver": "1.0",
		    "ts": "2023-02-26T17:27:47+0530"
		},
		"hmac": "uNSvpy5fuauyYEzm9V5ENDVuNwbPfVTvuA9Fym6T7ksSMJ7Tsqz6TaN9S2PKWLBH03//etg0sUHCtaYiS7vEKw=="
		"publicKey": "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEJEljFdZo1UhzcRnycepaKGCkWuAhC/wKEMVxyfXVvkFpf4ZkzyYsreCeQ+IJLXVJSSXBh0xUjuswqzXAliuLlg==",
    	"algorithm": "SHA256withECDSA"
	    }
        
### Running Application Using docker image

In order to run docker image you need to either build the docker image locally or using the image build and uploaded in ``rbih.jfrog.io/docker-trial``


we have set the user and password as ``admin`` in case you have set different please update the ``appilcation.yml`` accordingly
Or override the env variables based in deployemnt

In order the docker needs to create a TCP connection from inside docker to postgresql in host you need to overwrite url as below

```
docker run -p 8090:8090  -e APP_TRANSLITERATIONURL=http://127.0.0.1:8098/   --detach --name governmentservice rbih.jfrog.io/docker-trial/governmentservice:main-1253965
```
Check the logs using the container id as below

```
docker logs 728197a7eb964b9c9a62ccdbaa3d946cb3d41c9bd5102d26276c92ddd60fdc6a
exec java -XX:+ExitOnOutOfMemoryError -cp . -jar /opt/app/governmentservice-0.0.1-SNAPSHOT.jar

```

In case you want to see all properties set dumped logs then set the env properties while running docker image

```
docker run -p 8090:8090  -e APP_PROPERTIESDUMP=true --detach --name governmentservice rbih.jfrog.io/docker-trial/governmentservice:main-1253965
```

### The CI and CD process can be carried out by Github actions
For CI process ensure the below secrets are created in org level

```
 REGISTRY - the registry used to store the images (eg :  rbih.jfrog.io/docker-trial)
 REGISTRY_USERNAME -  The registry  user that can be used to push image 
 REGISTRY_PASSWAORD - The token for the registry user 

```

For Gitops based CD ensure the below secrets are created

#### Pre-Req
 Ensure that below secrets are created 

```
 REGISTRY - the registry used to store the images (eg :  rbih.jfrog.io/docker-trial)
 GITOPS_ORG - The org  where the gitops repo is present (eg :  CTO-RBIH)
 GITOPS_REPO - The gitops repo (eg :  gitops-rbih-deployment )
 GITOPS_DEPLOYMENT_DIR - The directory where charts/ manifest needs to be created per env (dev/stage) etc (eg : inventories/environments/blr-dev-k8s-apps)
 GITOPS_USERNAME -  The github user that can be used to checkin the code
 GITOPS_TOKEN - The token for the github user

```

### Prerequisites for Kubernetes or Redhat OCP Deployment

In case we are deploying with secret values for username and passwords for database and application

as defined in ``application.yaml`` under ``src/main/resources/``

First set the uncomment secretName in ``chart\values.yaml``

        # secretName: governmentservice-secret

So the secret name will be ``governmentservice-secret``

Define any more secrets you want based on ``application.yaml`` under ``src/main/resources/`` in ``deployment.yaml`` under ``chart\templates`` folder

Look for key like ``APP-APIUSER`` in ``deployment.yaml`` under ``chart\templates`` folder
Check for the correct path for each key with properties in ``application.yaml`` under ``src/main/resources/``

Create the secret as ``governmentservice-secret`` for example in kubernetes as below

Make sure to set namespace based on your application deployment

```
kubectl create secret generic governmentservice-secret -n apps --from-literal=APP-APIUSER=admin --from-literal=APP-APIPASSWORD=pass

```
