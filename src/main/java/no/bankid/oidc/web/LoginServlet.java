package no.bankid.oidc.web;

import no.bankid.oidc.BankIdOIDCClient;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/login"}, loadOnStartup = 1)
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String redirectUrl = BankIdOIDCClient.getInstance().createAuthenticationUrl();
        LOGGER.info("Redirecting to OIDC");
        response.sendRedirect(redirectUrl);
    }
}