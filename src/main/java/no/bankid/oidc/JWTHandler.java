package no.bankid.oidc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.logging.Logger;

class JWTHandler {

    private static final Logger LOGGER = Logger.getLogger(JWTHandler.class.getName());
    private final JWKSet publicKeys;

    public JWTHandler(String jwsKeysUri) {
        try {
            publicKeys = JWKSet.load(new URL(jwsKeysUri));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts information from token and validates signature
     */
    public JSONObject getPayload(String jws) {
        try {
            JWSObject jwsObject = JWSObject.parse(jws);
            String kid = jwsObject.getHeader().getKeyID();
            KeyType keyType = publicKeys.getKeyByKeyId(kid).getKeyType();
            boolean signatureIsOK = false;

            if (KeyType.RSA.equals(keyType)) {
                signatureIsOK = jwsObject.verify(new RSASSAVerifier((RSAKey)
                        publicKeys.getKeyByKeyId(kid)));
            } else if (KeyType.EC.equals(keyType)) {
                signatureIsOK = jwsObject.verify(new ECDSAVerifier((ECKey) publicKeys.getKeyByKeyId(kid)));
            }

            if (!signatureIsOK) {
                LOGGER.severe("Signature in jwk could not be verified.");
                // Should be handled.
            } else LOGGER.info("Signature in JWK is valid");
            return new JSONObject(jwsObject.getPayload().toString());
        } catch (ParseException | JOSEException e) {
            LOGGER.severe(e.toString());
            throw new RuntimeException(e);
        }
    }
}
