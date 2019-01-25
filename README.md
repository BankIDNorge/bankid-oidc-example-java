# BankID Test Application for BankID Norge AS

This is a Java based test application for authenticating a user with BankID
OpenID Connect Server (BID OIDC) developed for BankID Norge AS.
When the user is authenticated you will see the result from a call to UserInfo
RESTful service and the contents of the `id_token` which was returned from BID OIDC.

## Note

This client uses the stable CURRENT environment, which is set up with the BankID Preprod user-database.
New test users can be created self-serviced at https://ra-preprod.bankidnorge.no/


## Process flow

The application exemplifies all steps of the OIDC authentication process.

1. Fetch configuration from OIDC. GET to a non protected URL, such as
   https://oidc-current.bankidapis.no/auth/realms/current/.well-known/openid-configuration
   The configuration contains information such as relevant endpoints, and public key for the `id_token` (JWT).

2. Redirect to the authentication URL.

3. Handle the callback from OIDC. The callback contains an attribute `access_code` which needs to be exchanged with the `access_token` (POST to OIDC)

4. Fetch user info. Finally we use the `access_token` to fetch a protected resource, in this case the user info provided by BID OIDC.

## Build and run

To be able to run the application you will need to edit the `client_id` and
`client_secret` in `Configuration.java`.
See https://confluence.bankidnorge.no/confluence/pdoidcl/release-notes/provisioning for details on how to receive this.


```java
class Configuration {

    /**
     * Client_id and secret must be inserted here for the application to work.
     *
     */
    public static final String CLIENT_ID = "<insert client_id>";
    public static final String CLIENT_SECRET = "<insert client_secret>";
}
```

Run with [Maven](https://maven.apache.org) and [jetty](https://www.eclipse.org/jetty/):

```
mvn clean install
mvn jetty:run
```


## Dependencies

This example application uses the following libraries:

**javax.servlet-api** for web GUI

**jersey-client** used in all integration

**oauth2-client** OAuth2 support in Jersey

**org.json.json** for JSON parsing

**nimbus-jose-jwt** for handling the JWT (JSON Web Token)

**jetty-maven-plugin** for running the application locally with a dynamically created SSL certificate.
