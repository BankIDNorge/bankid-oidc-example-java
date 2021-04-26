package no.bankid.oidc.web;

import no.bankid.oidc.BankIdOIDCClient;
import no.bankid.oidc.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/callback"}, loadOnStartup = 1)
public class CallBackServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CallBackServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        LOGGER.info("Got callback from OIDC");

        String code = request.getParameter("code");

        User user = BankIdOIDCClient.getInstance().endAuthentication(code);

        request.getSession().setAttribute("user", user);

        response.sendRedirect("/");
    }
}