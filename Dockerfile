# SOURCE: certimg-nodejs12/blob/master/Dockerfile
FROM rbihdev.azurecr.io/certimg-openjdk11:latest
ADD src/main/resources/LRS-MP-spec.json /opt/app
ADD sslcert/* /opt/app/sslcert/
ADD target/*.jar /opt/app
