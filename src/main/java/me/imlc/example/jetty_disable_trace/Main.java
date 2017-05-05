package me.imlc.example.jetty_disable_trace;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.security.Constraint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by lawrence on 2017/5/5.
 */
public class Main {
    private static final Logger logger = Logger.getLogger("Main");
    private Server server;

    public void run() throws Exception {
        server = new Server(8080);

        ContextHandler rootContextHandler = new ContextHandler();
        rootContextHandler.setContextPath("/");
        rootContextHandler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getWriter().println("Hello, Jetty!");
                baseRequest.setHandled(true);
            }
        });

        ContextHandler aboutContextHandler = new ContextHandler();
        aboutContextHandler.setContextPath("/about");
        aboutContextHandler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getWriter().println("About");
                baseRequest.setHandled(true);
            }
        });

        ContextHandlerCollection collection = new ContextHandlerCollection();
        collection.setHandlers(new Handler[]{
                rootContextHandler,
                aboutContextHandler
        });

        server.setHandler(wrapWithSecurityHandler(collection));

        server.start();
        logger.info("Server started at http://127.0.0.1:8080/");
    }

    public Server getServer() {
        return server;
    }

    private Handler wrapWithSecurityHandler(Handler h){
        Constraint disableTraceConstraint = new Constraint();
        disableTraceConstraint.setName("Disable TRACE");
        disableTraceConstraint.setAuthenticate(true);

        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setConstraint(disableTraceConstraint);
        mapping.setMethod("TRACE");
        mapping.setPathSpec("/");

        // omissionConstraint is to fix the warning log ""null has uncovered http methods for path: /
        // No impact to disable TRACE if you do not add this constraint
        // But if you're using the monitoring tool like Geneos, and your component requires keep production monitoring all green,
        // You can try to add this omissionConstraint to fix the warning Jetty prints.
        Constraint omissionConstraint = new Constraint();
        ConstraintMapping omissionMapping = new ConstraintMapping();
        omissionMapping.setConstraint(omissionConstraint);
        omissionMapping.setMethod("*");
        omissionMapping.setPathSpec("/");


        ConstraintSecurityHandler handler = new ConstraintSecurityHandler();
        handler.addConstraintMapping(mapping);
        handler.addConstraintMapping(omissionMapping);
        handler.setHandler(h);
        return handler;
    }

    public static void main(String[] args) throws Exception {
        Main m = new Main();
        m.run();
        m.getServer().join();
    }
}
