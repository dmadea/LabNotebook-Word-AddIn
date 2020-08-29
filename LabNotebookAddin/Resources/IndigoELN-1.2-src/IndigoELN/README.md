## Indigo ELN customization

### Application

- "Clean" installation - without any external services (build with `mvn clean package`)

        ServiceLocator/src/main/resources/application.properties - change SERVICE_URL to necessary IndigoELN address (where the application will be deployed)
        WebApplication/src/main/resources/database/database.properties - change database connection parameters

- "Epam" installation - with external services provided by EPAM (build with `mvn clean package -P epam`)

        ServiceLocator/src/main/resources/application.properties - change SERVICE_URL to necessary IndigoELN address (where the application will be deployed)
        WebApplication/src/main/resources/database/database.properties - change database connection parameters
        EpamServices/src/main/resources/registration.properties - change CRS service address and credentials
        EpamServices/src/main/resources/signature.properties - change IndigoSignatureService service address and credentials

### Logging configuration

Indigo ELN uses SLF4J with Log4j 1.2 backend for logging. Also, SLF4J bridges (jcl-over-slf4j and jul-to-slf4j) are used.
To configure logging see `DesktopClient/src/main/resources/log4j.properties` and `WebApplication/src/main/resource/log4j.properties`.

### Security

Indigo ELN supports basic application security. This means that all application services (like Storage Service or Report Service) will be secured with password.
Here is the one restriction: Users should enter application username/password when downloading the application via JNLP
(Because application jars contains application username/password to access secured services).
To enable application security, just comment the line `<security:anonymous granted-authority="ROLE_SERVICE" enabled="true"/>`
in `WebApplication/src/main/webapp/WEB-INF/security-context.xml`
and change default application password in `ServiceLocator/src/main/resources/application.properties`.

### Database support

By default Indigo ELN supports PostgreSQL, but it is possible to run Indigo ELN with Oracle.
To run Indigo ELN with Oracle, change necessary properties in database.properties and:

- For runtime Oracle support place `ojdbc6.jar` into `WEB-INF/lib` folder in `indigoeln.war`
- For compile-time Oracle support install `ojdbc6.jar` into local Maven repository and add the dependency to `ServiceLocator/pom.xml`

### Database schema installation

Database schema installation process is described in `WebApplication/scripts/database/README.md`.

