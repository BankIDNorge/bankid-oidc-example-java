package no.bankid.oidc.web;

import no.bankid.oidc.BankIdOIDCClient;
import no.bankid.oidc.User;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/"}, loadOnStartup = 1)
public class WelcomeServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        /*
         * Tries to find the user on the session. If no user can be found, a login link is presented.
         *
         * After login the user will be redirected back to this servlet with the user on the session.
         *
         */
        User user = (User) request.getSession().getAttribute("user");

        response.getWriter().append("<html>")
                .append("<head></head><body>")
                .append("<h1>Welcome to BankID OpenID Connect test app in Java</h1>");

        if (user == null) {
            response.getWriter()
                    .append("<p>You are not logged in</p>")
                    .append("<a href=\"/login\">Login</a>");
        } else {

            JSONObject userInfo = BankIdOIDCClient.getInstance().getUserInfo(user);

                response.getWriter()
                        .append(String.format("<p>You are logged in as:</p><p>%s</p>", user.getName()))
                        .append("<h2>Access token</h2>")
                        .append(String.format("<p>%s</p>", user.getAccessToken()))
                        .append(prettifyJSON(user.getAccessTokenPayload()))
                        .append("<h2>Id token</h2>")
                        .append(String.format("<p>%s</p>", user.getIdToken()))
                        .append(prettifyJSON(user.getIdTokenPayload()))
                        .append("<h2>User info</h2>")
                        .append((userInfo!=null ? prettifyJSON(userInfo) : "Userinfo unauthorized (missing scope-access?)"))
                        .append("<p><a href=\"/logout\">Logout</a></p>");
        }

        response.getWriter()
                .append("</body></html");
    }

    private String prettifyJSON(JSONObject json) {
        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<p>{</p>");
        for (String key : json.keySet()) {
            htmlString.append(String.format("<p>&nbsp;&nbsp;&nbsp;&nbsp;<b>%s:</b> %s</p>", key, json.get(key)));
        }
        htmlString.append("<p>}</p>");
        return htmlString.toString();
    }
}