package no.bankid.oidc;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.json.JSONObject;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.UUID;

import static java.net.URLEncoder.encode;
import static no.bankid.oidc.Configuration.*;

public class BankIdOIDCClient {

    private final String authorizationEndpoint;
    private final String token_endpoint;
    private final String userinfo_endpoint;
    private final JWTHandler JWTHandler;

    private static BankIdOIDCClient bankIdOIDCClient;

    public static BankIdOIDCClient getInstance() {
        if (bankIdOIDCClient == null) {
            bankIdOIDCClient = new BankIdOIDCClient();
        }
        return bankIdOIDCClient;
    }

    /**
     * On initialization the OIDC configuration is fetched from CONFIG_URL.
     * This contains relevant endpoints and information about the jwt (id_token) key.
     */
    private BankIdOIDCClient() {
        // Fetch .well-known-configuration
        Client client = ClientBuilder.newClient();
        Response response = client.target(CONFIG_URL).request().get();

        JSONObject configuration = new JSONObject(response.readEntity(String.class));

        this.authorizationEndpoint = configuration.getString("authorization_endpoint");
        this.token_endpoint = configuration.getString("token_endpoint");
        this.userinfo_endpoint = configuration.getString("userinfo_endpoint");

        // JWTHandler fetches the key from jwks_uri
        JWTHandler = new JWTHandler(configuration.getString("jwks_uri"));
    }

    /**
     * Builds the authentication url, where the user shall be redirected upon starting of the authentication process.
     */
    public String createAuthenticationUrl() {
        // state is a value used to maintain state between the request and the callback. Actually not used in this application.
        String state = UUID.randomUUID().toString();

        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s&nonce=%s&login_hint=%s&acr_values=%s",
                authorizationEndpoint, CLIENT_ID, encoded(CALLBACK_URL), "code", encoded(SCOPE), encoded(state),
                "somecorrelationnonce",encoded(LOGIN_HINT),encoded(ACR_VALUES));
    }

    /**
     * After callback from oidc, the authentication_code must be exchanged with the access_token.
     * <p>
     * This will be done with a POST against the token_endpoint.
     * the 'code' is attached in the body (x-www-form-urlencoded)
     * The endpoint requires basic auth.
     * https://confluence.bankidnorge.no/confluence/pdoidcl/technical-documentation/rest-api/token
     * <p>
     * Finally, we put the access_token and id_token in a User object. It may typically be stored on the session.
     */
    public User endAuthentication(String code) {
        Client client = ClientBuilder.newClient();

        WebTarget target = client.target(token_endpoint);

        MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", CALLBACK_URL);

        Response response = target.request()
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()))
                .post(Entity.form(formData));

        JSONObject json = new JSONObject(response.readEntity(String.class));

        String access_token = json.getString("access_token");
        String id_token = json.getString("id_token");

        return new User(access_token, JWTHandler.getPayload(access_token), id_token, JWTHandler.getPayload(id_token));
    }

    /**
     * Fetch the protected resource UserInfo by using the access_token.
     */
    public JSONObject getUserInfo(User user) {
        Client client = ClientBuilder.newClient();

        Feature feature = OAuth2ClientSupport.feature(user.getAccessToken());
        client.register(feature);

        Response response = client.target(userinfo_endpoint).request().get();
        if (response.getStatus()==401) {
            return null;
        } else
            return JWTHandler.getPayload(response.readEntity(String.class));
    }


    /**
     * Set utf-8-encoding for string.
     */
    private static String encoded(String s) {
        try {
            return encode(s, Charset.forName("UTF-8").name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }


}
