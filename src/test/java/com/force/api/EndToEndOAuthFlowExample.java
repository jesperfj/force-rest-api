package com.force.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class EndToEndOAuthFlowExample {

    // These variables are accessed by multiple threads as part of oauth test
    String code;
    String state;

    @Test
    public void endToEndOAuthFlowTest() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

        // This test goes through the complete oauth flow.
        // It uses HtmlUnit as web client and Jetty as embedded server.

        // start the server.
        Server jetty = server();

        // --------------------------------------------------------------------
        // This part is what you'd normally do somewhere in your web app when
        // you want to authenticate an unauthenticated user.

        ApiConfig config = new ApiConfig().setClientId(Fixture.get("clientId"))
                .setClientSecret(Fixture.get("clientSecret")).setRedirectURI(Fixture.get("redirectURI"));

        String url = Auth.startOAuthWebServerFlow(new AuthorizationRequest().apiConfig(config).state("test_state"));


        // The HtmlUnit stuff below broke when SFDC introduced the S1 mobile app and added a
        // mobile app detection. I am giving up on maintaining it. Instead this test
        // will need manual intervention for now. See below the comments.

        // --------------------------------------------------------------------
        // In a real web app, you just pass the url back as a redirect to the
        // client Here we emulate the client browser using HtmlUnit to perform
        // a scripted interactive login.

//        final WebClient webClient = new WebClient();
//        final HtmlPage page = (HtmlPage) webClient.getPage(url);
//        System.out.println(page.toString());
//        HtmlForm form = page.getFormByName("login");
//        final HtmlButton submit = (HtmlButton) form.getButtonByName("Login");
//        final HtmlTextInput username = (HtmlTextInput) form.getInputByName("username");
//        final HtmlPasswordInput password = (HtmlPasswordInput) form.getInputByName("pw");
//
//        username.setValueAttribute(Fixture.get("username"));
//        password.setValueAttribute(Fixture.get("password"));
//
//        Page result = submit.click();
//        System.out.println("Result of login: " + result.getWebResponse().getStatusCode());

        // --------------------------------------------------------------------
        // HtmlUnit will follow all redirects, so the page returned should be
        // the page served on the redirectURI, i.e. in this case served by 
        // Jetty. Once we get this page, the thread running Jetty will have
        // updated the code and state variables in this class and we can
        // continue the flow.

        // There really is no way that concurrent access should occur. The web
        // client should not continue until it has received a response from
        // Jetty and by then the Jetty thread has already called
        // setOauthResponse. But for good measure we do synchronized access
        // combined with a check-and-wait loop.

        // Switching to manual intervention:

        System.out.println("Paste the following URL into your browser:");
        System.out.println();
        System.out.println(url);
        System.out.println();

        System.out.println("Authenticate with test credentials, then authorize oauth client");


        synchronized (this) {
            while (code == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        
        //---------------------------------------------------------------------
        // Your web app should run this code when it gets called on the Oauth
        // redirect URI. This completes the oauth flow.
        
        assertEquals("test_state", state);

        ApiSession s = Auth.completeOAuthWebServerFlow(new AuthorizationResponse().apiConfig(config).code(code));

        assertNotNull(s);

        // --------------------------------------------------------------------
        // Now you're done. You have a valid access and refresh token.
        
        ForceApi api = new ForceApi(config, s);

        assertNotNull(api.getIdentity());

        try {
            jetty.stop();
            jetty.join();
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    private synchronized void setOauthResponse(String code, String state) {
        this.code = code;
        this.state = state;
        notifyAll();
    }

    @SuppressWarnings("serial")
    private Server server() {
        int port = -1;
        String path = null;
        try {
            URI redir = new URI(Fixture.get("redirectURI"));
            port = redir.getPort();
            path = redir.getPath();
            assertEquals("'redirectURI' test parameter must point to localhost", "localhost", redir.getHost());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new HttpServlet() {
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                System.out.println("Jetty received OAuth callback");
                setOauthResponse(req.getParameter("code"), req.getParameter("state"));
            }

        }), path);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // server.join();
        return server;
    }


}
