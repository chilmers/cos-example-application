# Getting Started

### Prerequisites
You will need Java and Maven to run this application.
The application is tested to run on Java 17.

### Scope and intended use
**IMPORTANT!** The scope of this application is not to show how to create a secure and production ready application.
You need to design and implement security mechanisms appropriate for the needs of your application.  
The intention of this example application is to give you an idea of how you can get up and running quickly and start to work with the FHIR APIs in COS.
The patterns used in the application are just examples and similar results can be achieved in different ways, using various libraries and programming languages.

### Quick Start
`mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DCOS_API_KEY=CHANGE_ME -DCOS_OAUTH_CLIENT_ID=CHANGE_ME -DCOS_OAUTH_CLIENT_SECRET=CHANGE_ME"`

### Configure the application
The application requires an API Key and a set of Client ID and Client Secret for the OAuth2.0 Authorization server.
These can be given as parameters at startup or by altering the values in the src/main/resources/application.yaml file.

The application.yaml holds substitution variables and default values for these configuration values.
The substitution variables are:
* COS_API_KEY - The API key (subscription key) for Cambio Open Services
* COS_OAUTH_CLIENT_ID - The client id used for authenticating against the auth server
* COS_OAUTH_CLIENT_SECRET - The client secret used for authenticating against the auth server

Spring will look in environment variables as well as in system properties for values to substitute.
The application will be started in a process separate from Maven. 
Thus, to provide system properties you must tell Spring Boot to add them when starting the server.
I.e. it will not work to give them directly to the Maven process.

To tell Spring Boot to add system properties when starting the server you will need to wrap them inside a system property to the Maven process, 
spring-boot.run.jvmArguments.
E.g. `mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DCOS_API_KEY=CHANGE_ME -DCOS_OAUTH_CLIENT_ID=CHANGE_ME -DCOS_OAUTH_CLIENT_SECRET=CHANGE_ME"` 


### Start the application
If you added your credentials directly in the application.yaml file you can go on and start with the application using the following command: 

`mvn spring-boot:run`

If you want to provide your credentials on the command line you can use the following approach:

`mvn spring-boot:run -Dspring-boot.run.jvmArguments="-DCOS_API_KEY=CHANGE_ME -DCOS_OAUTH_CLIENT_ID=CHANGE_ME -DCOS_OAUTH_CLIENT_SECRET=CHANGE_ME"`

### Send a request to your application
The application will try to start using port 8080. It has one hard coded user with username: user and password: password.
To try out a request and search fpr a patient you can use curl, for example:

`curl -v -u user:password http://localhost:8080/patient\?pnr\=191212121212`


### Reference Documentation
For further reference, please consider the following sections:

* [Cambio Open Services](https://developer.openservices.cambio.se)
* [HAPI FHIR Client](https://hapifhir.io/hapi-fhir/docs/client/introduction.html) 
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.6/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.6/maven-plugin/reference/html/#build-image)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.6.6/reference/htmlsingle/#boot-features-security)
* [OAuth2 Client](https://docs.spring.io/spring-boot/docs/2.6.6/reference/htmlsingle/#boot-features-security-oauth2-client)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.6.6/reference/htmlsingle/#boot-features-developing-web-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

