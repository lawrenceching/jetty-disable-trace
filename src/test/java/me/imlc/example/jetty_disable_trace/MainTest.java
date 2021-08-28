package me.imlc.example.jetty_disable_trace;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.junit.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Created by lawrence on 2017/5/5.
 */
public class MainTest {
    public static final String URL = "http://127.0.0.1:8080/";
    private static Main main;
    private static final Logger logger = Logger.getLogger("MainTest");

    @BeforeClass
    public static void beforeClass() throws Exception {
        main = new Main();
        main.run();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if(main!=null && main.getServer().isRunning()){
            main.getServer().stop();
        }
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void canAccessRootPath() throws IOException {
        URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        int code = conn.getResponseCode();
        assertEquals(HttpServletResponse.SC_OK, code);

        String content = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8.name()).trim();
        assertEquals("Hello, Jetty!", content);
    }

    @Test
    public void canAccessAboutPath() throws Exception {
        URL url = new URL(URL+"about");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        int code = conn.getResponseCode();
        assertEquals(HttpServletResponse.SC_OK, code);

        String content = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8.name()).trim();
        assertEquals("About", content);
    }

    @Test
    public void cannotAccessRootPathByTrace() throws IOException {
        URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("TRACE");
        int code = conn.getResponseCode();
        assertEquals(HttpServletResponse.SC_FORBIDDEN, code);
    }

    @Test
    public void cannotAccessAboutPathByTrace() throws IOException {
        URL url = new URL(URL+"about");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("TRACE");
        int code = conn.getResponseCode();
        assertEquals(HttpServletResponse.SC_FORBIDDEN, code);
    }
}