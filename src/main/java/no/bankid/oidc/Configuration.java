package no.bankid.oidc;

class Configuration {

    /**
     * Environment to be used.
     * We currently have three environments:
     *  - Current: Production like stable test-environment with data from BankID preprod
     *  - Preprod: Might be unstable due to testing of forthcoming release.
     *  - Production
     */
    public static final String CONFIG_URL = "https://oidc-current.bankidapis.no/auth/realms/current/.well-known/openid-configuration";
    //public static final String CONFIG_URL = "https://oidc-preprod.bankidapis.no/auth/realms/preprod/.well-known/openid-configuration";
    // public static final String CONFIG_URL = "https://oidc.bankidapis.no/auth/realms/prod/.well-known/openid-configuration";

    public static final String CALLBACK_URL = "https://localhost:8443/callback";

    /**
     * LOGIN_HINT can be one of the following:
     *      - ""
     *        Blank means you get a chooser with all IDPs on or above the acr_level selected (default acrlevel is 4)
     *      - "BID"
     *        BankID
     *      - "BID:<nnin>"
     *        BankID with pre-selected user 11 digit nnin (Norwegian national Identity Number)
     *      - "BIM"
     *        BankID on Mobile
     *      - BIM:<phone_no>:<ddmmyy>
     *        BankID on Mobile with pre-selected phone and birthday
     *      - XID
     *      - VIPPS
     *
     */
    public static final String LOGIN_HINT = "";

    /**
     * Default acr-value is 4
     * BankID (BID): 4
     * BankID on Mobile (BIM): 4
     * Vipps: 3
     * XID: 2
     */
    public static final String ACR_VALUES = "";

    /**
     * Scopes to be requested.
     * "openid" is mandatory. Others are optional.
     */
    public static final String SCOPE = "openid profile";

    /**
     * Client_id and secret must be inserted here for the application to work.
     * <p>
     * See https://confluence.bankidnorge.no/confluence/pdoidcl/developer-resources/provisioning for details on how to receive this.
     */
    public static final String CLIENT_ID = "<INSERT YOUR CLIENT-ID HERE>";
    public static final String CLIENT_SECRET = "<INSERT YOUR CLIENT-SECRET HERE";

}
