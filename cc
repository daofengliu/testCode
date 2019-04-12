<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
		 version="3.1">
	<display-name>Spring MVC</display-name>

    <context-param>
        <param-name>log4j-config-location</param-name>
        <param-value>/WEB-INF/classes/log4j.xml</param-value>
    </context-param>

    <listener>
        <listener-class>idv.javaee.servlet.listener.Log4jConfigListener</listener-class>
    </listener>

	<servlet>
		<servlet-name>helloServlet</servlet-name>
		<servlet-class>idv.javaee.servlet.servlet.MyServlet$HelloServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>helloServlet</servlet-name>
		<url-pattern>/hello</url-pattern>
		<url-pattern>/hello.do</url-pattern>
	</servlet-mapping>

	<!-- file upload -->
	<servlet>
		<servlet-name>fileUploadServlet</servlet-name>
		<servlet-class>idv.javaee.servlet.servlet.MyServlet$FileUploadServlet</servlet-class>
		<load-on-startup>10</load-on-startup>
		<multipart-config>
			<location>/tmp</location>
			<max-file-size>20848820</max-file-size>
			<max-request-size>418018841</max-request-size>
			<file-size-threshold>1048576</file-size-threshold>
		</multipart-config>
	</servlet>
	<servlet-mapping>
		<servlet-name>fileUploadServlet</servlet-name>
		<url-pattern>/fileUpload.do</url-pattern>
	</servlet-mapping>

	<filter>
		<filter-name>UrlFilter</filter-name>
		<filter-class>idv.javaee.servlet.filter.MyFilter$UrlFilter</filter-class>
	</filter>
    <filter>
        <filter-name>EncodingFilter</filter-name>
        <filter-class>idv.javaee.servlet.filter.MyFilter$EncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>

    <filter-mapping>
        <filter-name>EncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
        <filter-name>PerformanceFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <filter-mapping>
        <filter-name>UrlFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
</web-app>

<!--
web.xml 在部署時，必須存放在 WEB-INF 的目錄

== <load-on-startup> ==
web container 啟動時會一併建立這個 servlet
沒設定的話則會等到第一次呼叫這個 servlet 時，才初始化並建立


== <url-pattern> ==
可以對同一個 servlet 設定多組 URL mapping

<url-pattern> 的命名策略有四種
1. 精確路徑對應 -> 以 "/" 起頭                            -> /abc/gg.do
2. 前置路徑對應 -> 以 "/" 起頭，以"/*" 結尾    -> /abc/*
3. 延伸路徑對應 -> 以"*." 起頭                           -> /*.do
4. 預設路徑對應 -> "/"                   -> /

精確路徑對應 > 前置路徑對應 > 延伸路徑對應 > 預設路徑對應 (/ 表示為 web application 的 context root)
-->




package idv.javaee.servlet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ==== web.xml ====
 * <filter>
 *     <filter-name>filter1</filter-name>
 *     <filter-class>idv.Filter1</filter-class>
 *     <async-supported>false</async-supported>
 * </filter>
 * <filter-mapping>
 *     <filter-name>filter1</filter-name>
 *     <url-pattern>/*</url-pattern>
 *     <servlet-name>servlet1</servlet-name>
 *     <dispatcher>REQUEST</dispatcher>
 *     <dispatcher>FORWARD</dispatcher>
 *     <dispatcher>INCLUDE</dispatcher>
 *     <dispatcher>ERROR</dispatcher>
 *     <dispatcher>ASYNC</dispatcher>
 * </filter-mapping>
 *
 * <dispatcher> 表示在什麼情況下的 Servlet 才需要執行這一個 filter，預設是 Request
 *   1. REQUEST - 符合 <url-pattern> 的任何 http request
 *   2. FORWARD - 透過 RequestDispatcher 的 forward() 的 request
 *   3. INCLUDE - 透過 RequestDispatcher 的 include() 而來的請求
 *   4. ERROR   - 由容器處理例外而轉發過來的請求
 *   5. ASYNC   - 非同步處理的請求
 *
 *
 * ==== @WebFilter ====
 * @WebFilter("/*")
 *   filter for all URLs
 * @WebFilter("/admin")
 *   filter for the URL pattern /admin
 *
 * @WebFilter(servletNames = {"SomeServlet"})
 *   filter for a specific servlet
 *
 * @WebFilter(
 *     urlPatterns = "/uploadFilter",
 *     initParams = {
 *         @WebInitParam(name = "fileTypes", value = "doc;xls;zip;txt;jpg;png;gif")
 *     }
 * )
 *   initialization parameters for the filter
 *
 * @WebFilter(
 *     urlPatterns = "/admin",
 *     dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD}
 * )
 *   dispatcher types
 *
 * ps1. Servlet 3.0 開始可以使用 @WebFilter 取代 web.xml 的 <filter>
 *
 * ps2. 在 web.xml 設定同樣名稱的 filter，會覆蓋 @WebFilter 的設定
 *
 * ps3. 同時具備 <url-pattern> 與 <servlet-name>，則先比對 <url-pattern>，再比對 <servlet-name>
 *
 * ps4. @WebFilter Does Not Define the Order of Filter
 *      @WebFilter has no element to define the order of filter of execution.
 *      We need to define order in web.xml.
 *      This may be because if we decide order of filter at class level using @WebFilter,
 *        then what if we include third party library which has filter with same order.
 *
 *      _@WebFilter 沒有指定執行順序的 attribute，必須在 web.xml 設定 <filter-mapping> 來決定執行順序
 *
 *          @WebFilter(filterName = "filter1", urlPatterns = "/url1/*")
 *          public class Filter1 implements Filter {}
 *          @WebFilter(filterName = "filter2", urlPatterns = "/url2/*")
 *          public class Filter2 implements Filter {}
 *          <filter-mapping>
 *              <filter-name>filter1</filter-name>
 *              <url-pattern />
 *          </filter-mapping>
 *          <filter-mapping>
 *              <filter-name>filter2</filter-name>
 *              <url-pattern />
 *          </filter-mapping>
 *
 * ==== example ====
 * web.xml 設定兩個 <filter>: EncodingFilter & UrlFilter
 * _使用 @WebFilter: PerformanceFilter
 *
 * 並在 web.xml 設定 <filter-mapping> 決定執行順序
 *   <filter-mapping>
 *       <filter-name>EncodingFilter</filter-name>
 *       <url-pattern>/*</url-pattern>
 *   </filter-mapping>
 *   <filter-mapping>
 *       <filter-name>PerformanceFilter</filter-name>
 *       <url-pattern>/*</url-pattern>
 *   </filter-mapping>
 *   <filter-mapping>
 *       <filter-name>UrlFilter</filter-name>
 *       <url-pattern>/*</url-pattern>
 *   </filter-mapping>
 *
 * [2019/04/12 11:10:31:058] INFO  idv.javaee.servlet.filter.MyFilter$EncodingFilter.doFilter(MyFilter.java:143) - == EncodingFilter doFilter ==
 * [2019/04/12 11:10:31:059] INFO  idv.javaee.servlet.filter.MyFilter$PerformanceFilter.doFilter(MyFilter.java:180) - == PerformanceFilter doFilter ==
 * [2019/04/12 11:10:31:060] INFO  idv.javaee.servlet.filter.MyFilter$UrlFilter.doFilter(MyFilter.java:165) - == UrlFilter doFilter ==
 * [2019/04/12 11:10:31:060] INFO  idv.javaee.servlet.filter.MyFilter$UrlFilter.doFilter(MyFilter.java:167) - UrlFilter - ServletPath = /hello.do, URL = http://localhost:8080/orangec/hello.do
 * [2019/04/12 11:10:31:061] INFO  idv.javaee.servlet.servlet.webxml.HelloServlet.doGet(HelloServlet.java:76) - HelloServlet doGet
 * [2019/04/12 11:10:31:061] INFO  idv.javaee.servlet.filter.MyFilter$PerformanceFilter.doFilter(MyFilter.java:186) - PerformanceFilter - Request process in 2 milliseconds
 */
public class MyFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * ... (target Servlet service() pre-processing，目標 servlet 執行之前的前處理)
     *
     * chain.doFilter(request, response);
     *
     * ... (target Servlet service() post-processing，目標 servlet 執行之後的後處理)
     *
     *          | Servlet
     * client   | Container    Filter1             Filter2             Servlet
     * -------------------------------------------------------------------------
     * request  | request   ->
     *          |              pre-processing
     *          |              chain.doFilter() ->
     *          |                                  pre-processing
     *          |                                  chain.doFilter() ->
     *          |                                                      service()
     *          |                                                   <-
     *          |                                  post-processing
     *          |                               <-
     *          |              post-processing
     * response | response  <-
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        // pre-processing
        chain.doFilter(request, response);
        // post-processing
    }

    @Override
    public void destroy() {
    }

    /**************************************************************************/
    /** Encoding filter */
    public static class EncodingFilter implements Filter {
        private static final Logger LOGGER = LoggerFactory.getLogger(EncodingFilter.class);

        private String encoding;

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            encoding = filterConfig.getInitParameter("encoding");
            if (encoding == null || encoding.isEmpty()) {
                encoding = "UTF-8";
            }
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                FilterChain chain) throws IOException, ServletException {
            LOGGER.info("== EncodingFilter doFilter ==");
            request.setCharacterEncoding(encoding);
            chain.doFilter(request, response);
            response.setCharacterEncoding(encoding);
        }

        @Override
        public void destroy() {
        }
    }

    /**
     * URL filter
     *
     * ps1. Servlet 4.0 可以改為 extends HttpFilter，不用再 implements Filter
     */
//    public static class UrlFilter implements Filter {
    public static class UrlFilter extends HttpFilter {
        private static final Logger LOGGER = LoggerFactory.getLogger(UrlFilter.class);

        @Override
        public void doFilter(HttpServletRequest req, HttpServletResponse res,
                FilterChain chain) throws IOException, ServletException {
            LOGGER.info("== UrlFilter doFilter ==");
            String servletPath = req.getServletPath();
            LOGGER.info("UrlFilter - ServletPath = " + servletPath + ", URL = " + req.getRequestURL());
            super.doFilter(req, res, chain);
        }
    }

    /** PerformanceFilter */
    @WebFilter(filterName = "PerformanceFilter", urlPatterns = {"/*"})
    public static class PerformanceFilter extends HttpFilter {
        private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceFilter.class);

        @Override
        protected void doFilter(HttpServletRequest req, HttpServletResponse res,
                FilterChain chain) throws IOException, ServletException {
            LOGGER.info("== PerformanceFilter doFilter ==");
            long begin = System.currentTimeMillis();
            super.doFilter(req, res, chain);
//            getServletContext().log("Request process in " + (System.currentTimeMillis() - begin) + " milliseconds");
            LOGGER.info("PerformanceFilter - " + "Request process in " +
                    (System.currentTimeMillis() - begin) + " milliseconds");
        }
    }
}



			       
package idv.javaee.servlet.servlet;

import idv.javaee.servlet.servlet.webxml.HelloServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;

/**
 * ==== Servlet Container ====
 * Container 的功能
 *   1. 溝通支援: Container 會和 Web Server 溝通，Container 會知道它和 Web Server 的通訊協定，建立 ServerSocket，偵聽通訊埠，建立串流...
 *   2. 生命週期管理: Container 控制 Servlet 的生與死，負責載入類別，實例化 Servlet 物件，初始化 Servlet，呼叫 Servlet 的方法，並且適時的讓 Servlet 物件能夠被 garbage collection
 *   3. 多執行緒支援: Container 接收到一個請求，會自動建立一個 Java 執行緒來處理它，當 Servlet 執行完 service() 方法，該執行緒就會結束 (不過 Servlet 的程式還是要注意執行緒安全)
 *   4. 宣告式的權限管理: 可以透過 XML 格式的部署描述檔(DD，即 web.xml.txt)來組態或修改權限管理機制，而不用寫在 Servlet 中 (Servlet 3.0 開始可以透過 annotation)
 *   5. 支援 JSP: Container 會負責把 JSP 轉譯為 Java 程式(Servlet)
 *
 * Container & Servlet
 *   1. Servlet 本身並沒有 main() method, 它們是由另個稱為 container 的 Java 應用程式所控制
 *   2. 當 web server 接收到一個針對 Servlet 的 request 時，會把這個請求交給該 Servlet 所屬的 Container, Container 建立 HttpServletRequest 以及 HttpServletResponse 物件後,
 *      再交由 Servlet 處理這個請求  (Container 會呼叫 Servlet 的 service()，service() 會判斷要呼叫 doGet() 或  doPost()...)
 *
 * Servlet
 *   1. Servlet<interface> ← GenericServlet<interface> ← HttpServlet<abstract class>
 *   2. web application 99% 都是繼承 HttpServlet，然後覆寫 doGet() 或 doPost()
 *   3. lifecycle
 *     3.1. init()
 *       Container 會在 Servlet 物件被建立後，呼叫 init()，將這個物件初始化為可提供服務的 Servlet
 *
 *     3.2. service()
 *       當第一個 client request 被 container 接受到後，Container 啟動一個新的執行緒，再透過它呼叫 Servlet 的 service() ( service() 會再根據 HTTP method，呼叫對應的 doGet(), doPost()... )
 *
 *     3.3. goGet() & doPost()
 *       應用程式的起點，至少要覆寫一個，如果沒覆寫 doGet() 表示不支援 HTTP GET；如果沒覆寫 doPost() 表示不支援 HTTP POST
 *
 * ServletConfig
 *   1. 每個 Servlet 都有一個 ServletConfig 物件
 *   2. 可以透過 ServletConfig 存取 ServletContext
 *   3. 可以在 DD(web.xml.txt) 設定 ServletConfig parameter
 *
 * ServletContext
 *   1. 每個 web application 只有一個 ServletContext(application context)
 *   2. 可以取得伺服器的資訊，例如 Container 的資料
 *
 *
 * The getContentType() method returns the MIME content type of the request, such as
 *   application/x-www-form-urlencoded, application/json, text/plain, or application/zip, to name a few.
 *
 * The getContentLength() and getContentLengthLong() methods both return the number of bytes in
 *   the request body (the content length), with the latter method being useful for requests whose
 *   content might exceed 2 gigabytes (unusual, but not impossible).
 *
 * The getCharacterEncoding() method returns the character encoding (such as UTF-8 or ISO-8859-1)
 *   of the request contents whenever the request contains character-type content.
 *   (text/plain, application/json, and application/ x-www-form-urlencoded are some examples of character-type MIME content types.)
 * _可以從 HttpServletRequest 取得 MIME(Multipurpose Internet Mail Extensions) type
 * _MIME 就是 Internet media type(text/html...)
 *
 *
 * Reading the Contents of a Request
 *   The methods getInputStream(), which returns a javax.servlet.ServletInputStream, and getReader(),
 *     which returns a java.io.BufferedReader, can both be used to read the contents of the request.
 *
 *   You should never use them both on the same request.
 *   After a call to either method, a call to the other will fail with an IllegalStateException.
 *   _getInputStream() 可以讀取 binary data
 *   _getReader() 則是方便處理  character-encoded data
 *   _不可以同時使用這兩個方法，否則會發生 IllegalStateException
 *
 *
 * Getting Request Characteristics Such as URL, URI, and Headers
 *   == getRequestURL() ==
 *   Returns the entire URL that the client used to make the request,
 *     including protocol (http or https), server name, port number, and server path
 *     but not including the query string.
 *
 *   ex: http://www.example.org/application/index.jsp.
 *
 *   == getRequestURI() ==
 *   This is slightly different from getRequestURL() in that it returns only the server path part of the URL;
 *
 *   ex: /application/index.jsp
 *
 *   == getServletPath() ==
 *   Similar to getRequestURI, this returns even less of the URL.
 *   If the request is /hello-world/greeting?foo=world, the application is deployed as /hello-world on Tomcat,
 *   and the servlet-mappings are /greeting, /salutation, and /wazzup, getServletPath returns only the part of the URL used to match the servlet mapping: /greeting.
 *
 *   == getHeader() ==
 *   Returns the value of a header with the given name.
 *   The case of the header does not have to match the case of the string passed into the method, so getHeader("content-type") can match the Content-Type header.
 *   If there are multiple headers with the same name, this returns only the first value.
 *   In such cases, you would want to use the getHeaders method to return an enumeration of all the values.
 *
 * Sessions and Cookies
 *   == getSession() ==
 *   == getCookies() ==
 *
 * Using HttpServletResponse
 *   Writing to the Response Body
 *   The most common thing you’ll do with a response object, and something you have already done with a response object, is write content to the response body.
 *   This might be HTML to display in a browser, an image that the browser is retrieving, or the contents of a file that the client is downloading.
 *   It could be plain text or binary data.
 *   It might be just a few bytes long or it could be gigabytes long.
 *
 *   The getOutputStream() method, which returns a javax.servlet.ServletOutputStream, and the getWriter()
 *     method, which returns a java.io.PrintWriter, both enable you to write data to the response.
 *   Also, you should never use both getOutputStream() and getWriter() in the same response.
 *   After a call to one, a call to the other will fail with an IllegalStateException.
 *   _同樣不能在同一個 response 呼叫 getOutputStream() 和 getWriter()，否則會發生 IllegalStateException
 *
 * Setting Headers and Other Response Properties
 *   == setStatus() ==
 *   To set the HTTP response status code
 *
 *   == getStatus() ==
 *   To determine what the current status of the response is
 *
 *   == sendError() ==
 *   To set the status code, indicate an optional error message to write to the response data,
 *     direct the web container to provide an error page to the client, and clear the buffer
 *
 *   == sendRedirect() ==
 *   To redirect the client to a different URL
 */
public class MyServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloServlet.class);

    @Override
    public void init() throws ServletException {
        LOGGER.info(this.getServletName() + " has started.");
    }
    @Override
    public void destroy() {
        LOGGER.info(this.getServletName() + " has stopped.");
    }

    /**
     * 如果沒 override doGet()，則 request 是 HTTP GET 時，會發生 HTTP Error 405 Method Not Allowed
     *
     * == request header ==
     * GET /hello?user=daniel HTTP/1.1
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }
    /**
     * 如果沒 override doPost()，則 request 是 HTTP POST 時，會發生 HTTP Error 405 Method Not Allowed
     *
     * == request header ==
     * POST /hello HTTP/1.1
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    /**************************************************************************/

    public static class HelloServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            LOGGER.info("HelloServlet doGet");
            String user = req.getParameter("user");
            if (user == null) {
                res.getWriter().println("Hello Servlet!");
            } else {
                res.getWriter().println("Hello " + user);
            }
        }
    }

    /**
     * <servlet>
     *     <servlet-name>fileUploadServlet</servlet-name>
     *     <servlet-class>idv.javaee.servlet.servlet.FileUploadServlet</servlet-class>
     *     <multipart-config>
     *         <location>/tmp</location>
     *         <max-file-size>20848820</max-file-size>
     *         <max-request-size>418018841</max-request-size>
     *         <file-size-threshold>1048576</file-size-threshold>
     *     </multipart-config>
     *     <load-on-startup>10</load-on-startup>
     * </servlet>
     *
     * client JSP -> pages/javaee/servlet/servlet/webxml/fileUpload.jsp
     */
    public static class FileUploadServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                res.getWriter().print("No file is uploaded");
                res.flushBuffer();
                return;
            }
            InputStream inputStream = filePart.getInputStream();
            String fileName = filePart.getSubmittedFileName();

            System.out.println(System.getProperty("user.dir"));
            File targetPath = new File("download");
            if (!targetPath.exists()) {
                targetPath.mkdirs();
            }
//            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(File.separator + "download", fileName)));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(targetPath, fileName)));

            int read = 0;
            final byte[] buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.close();
            res.getWriter().print("file had been uploaded");
        }
    }


    /**
     * ==== @WebServlet ====
     * 在 Servlet 3.0(JavaEE 6) 開始，可以使用 @WebServlet 來向 Web Container 註冊 Servlet
     *
     * @WebServlet(
     *   name = "",
     *   urlPatterns = {""},
     *   initParams = {},
     *   loadOnStartup = 10,
     *   value = {""},
     *   description = "",
     *   displayName = "",
     *   asyncupported = false,
     *   largeIcon = "",
     *   smallIcon = ""
     * )
     */
    @WebServlet(
        value = {"/annoInitParamServlet", "/annoInitParam.do"},
        initParams = {
            @WebInitParam(name = "user.email", value="user@abc.com"),
            @WebInitParam(name = "user.phone", value="3345678")
        }
    )
    public static class AnnoInitParamServlet extends HttpServlet {
        /**
         * ==== Using Context init Parameters ====
         * <context-param>
         *     <param-name>admin.email</param-name>
         *     <param-value>admin@abc.com</param-value>
         *     <param-name>admin.phone</param-name>
         *     <param-value>7788999</param-value>
         * </context-param>
         * _使用 javax.servlet.ServletContext.getInitParameter()
         *
         * ==== Using Servlet init Parameters ====
         * <servlet>
         *     <servlet-name>helloServlet</servlet-name>
         *     <servlet-class>idv.javaee.servlet.a.HelloServlet</servlet-class>
         *     <init-param>
         *         <param-name>user</param-name>
         *         <param-value>tony</param-value>
         *     </init-param>
         * </servlet>
         * _使用 javax.servlet.ServletConfig.getInitParameter()
         *
         * _整個 Web application 只會有一個 ServletContext
         * _每個 Servlet 都會有一個 ServletConfig
         *
         * ps. 在分散式系統，每個 JVM 會有一個 ServletContext!
         */
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            ServletContext context = getServletContext();
            String adminEmail = context.getInitParameter("admin.email");
            String adminPhone = context.getInitParameter("admin.phone");

            ServletConfig config = getServletConfig();
            String userMail = config.getInitParameter("user.email");
            String userPhone = config.getInitParameter("user.phone");
            response.getWriter().append("Admin email: " + adminEmail + ", phone = " + adminPhone)
                    .append("\nUser email: " + userMail + ", phone = " + userPhone);
        }
    }

    /**
     * ==== Dispatch ====
     * 1. 如果打算由其它元件處理請求，可以將請求重導(redirect) 或請求委派(dispatch)
     * 2. 使用 HttpServletRequest 的 getRequestDispatcher(String url) 取得 RequestDispatcher 物件
     * 3. client 會接收到 HTTP Status = 200
     * 4. browser 的網址列不會變
     */
    @WebServlet(name = "dispatchServlet", urlPatterns = {"/dispatch.do", "/dispatch.action"})
    public static class DispatchServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
//            RequestDispatcher rd = req.getRequestDispatcher("/orangec/hello.do");
            RequestDispatcher rd = req.getRequestDispatcher("/hello.do");
            rd.forward(req, res);
        }
    }

    /**
     * ==== Redirect ====
     * 1. 如果打算由其它元件處理請求，可以將請求重導(redirect) 或請求委派(dispatch)
     * 2. 使用 HttpServletRequest 的 sendRedirect(String url) 將 request 重新導到其它 URL
     * 3. client 會接收到 HTTP Status = 301 (用 chrome 測是 302)，然後 browser 會去找 location 的標頭，然後再重新發一個 request 至新的 URL
     * 4. browser 的網址列會變為被重導的 URL
     */
    @WebServlet(name = "redirectServlet", urlPatterns = {"/redirect.do", "/redirect.action"})
    public static class RedirectServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            /* 絕對路徑 */
//            res.sendRedirect("http://www.google.com");

            /*
             * 相對路徑 (使用 / 開頭，即相對於 web context root)
             *   http://localhost:8080/orangec/redirect.do -> http://localhost:8080/orangec/hello.do
             */
//            res.sendRedirect("/hello.do");
            res.sendRedirect("/orangec/hello.do");

            /*
             * 相對路徑(不使用 / 開頭，即相對於原始請求的 URL)
             * http://localhost:8080/orangec/aa/bb/redirect.action -> http://localhost:8080/orangec/aa/bb/hello.do
             */
//            res.sendRedirect("hello.do");
        }
    }

    /**
     * <servlet>
     *     <servlet-name>fileUploadServlet</servlet-name>
     *     <servlet-class>idv.javaee.servlet.servlet.FileUploadServlet</servlet-class>
     *     <multipart-config>
     *         <location>/tmp</location>
     *         <max-file-size>20848820</max-file-size>
     *         <max-request-size>418018841</max-request-size>
     *         <file-size-threshold>1048576</file-size-threshold>
     *     </multipart-config>
     *     <load-on-startup>10</load-on-startup>
     * </servlet>
     *
     * ps. 如果有使用 Spring MVC，若有設定 CommonsMultipartResolver
     *     _則 file upload 的 Servlet API 會沒作用 (request.getPart(...) 會為 null)
     *
     * <beans>
     *     <!--
     *     <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
     *         <property name="maxUploadSize" value="100000"/>
     *     </bean>
     *     <bean id="multipartResolver" class="org.springframework.web.multipart.cos.CosMultipartResolver">
     *         <property name="maxUploadSize" value="100000"/>
     *     </bean>
     *     -->
     *
     *     <bean id="multipartResolver" class="org.springframework.web.multipart.support.StandardServletMultipartResolver" />
     * </beans>
     */
    @WebServlet(
            name = "/annoFileUploadServlet",
            urlPatterns = {"/annoFileUploadServlet", "/annoFileUpload.do"},
            loadOnStartup = 10
    )
    @MultipartConfig(
            fileSizeThreshold = 5_242_880, // 5MB
            maxFileSize = 20_971_520L,     // 20MB
            maxRequestSize = 40_943_040,   // 50
            location = "tmp"
    )
    public static class AnnoFileUploadServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                response.getWriter().print("No file is uploaded");
                response.flushBuffer();
                return;
            }

            InputStream inputStream = filePart.getInputStream();
            String fileName = filePart.getSubmittedFileName();

            File targetPath = new File("download");
            if (!targetPath.exists()) {
                targetPath.mkdirs();
            }
//            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(File.separator + "download", fileName)));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(targetPath, fileName)));

            int read = 0;
            final byte[] buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.close();
            response.getWriter().print("file had been uploaded");
        }
    }

}




package idv.javaee.servlet.listener;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

public class Log4jConfigListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        /* do nothing */
    }

    /**
     * _除非 log4j.xml 或 log4j.properties 不在 classpath 的路徑
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        /* log4j.xml 使用 ${log_dir} */
        System.setProperty("log_dir", "/logs");

        /* initialize log4j here */
        ServletContext context = event.getServletContext();
        String log4jConfigFile = context.getInitParameter("log4j-config-location");
        String fullPath = context.getRealPath("") + File.separator + log4jConfigFile;
        PropertyConfigurator.configure(fullPath);
    }

}




== struts2 ==
<package name="main" namespace="/main" extends="project-default">
    <action name="docDbApplyForm" class="idv.struts2.action.IndexAction" method="load">
        <result name="cancel" type="redirectAction">
            <param name="actionName">docDbApplyForm!loadMainInfo</param>
            <param name="namespace">/main</param>
            <param name="parse">true</param>
            <param name="formNo">${master.formNo}</param>
        </result>
    </action>
</package>


http://30thh.loc:8480/app/test%3F/a%3F+b;jsessionid=S%3F+ID?p+1=c+d&p+2=e+f#a
Method              URL-Decoded Result           
----------------------------------------------------
getContextPath()        no      /app
getLocalAddr()                  127.0.0.1
getLocalName()                  30thh.loc
getLocalPort()                  8480
getMethod()                     GET
getPathInfo()           yes     /a?+b
getProtocol()                   HTTP/1.1
getQueryString()        no      p+1=c+d&p+2=e+f
getRequestedSessionId() no      S%3F+ID
getRequestURI()         no      /app/test%3F/a%3F+b;jsessionid=S+ID
getRequestURL()         no      http://30thh.loc:8480/app/test%3F/a%3F+b;jsessionid=S+ID
getScheme()                     http
getServerName()                 30thh.loc
getServerPort()                 8480
getServletPath()        yes     /test?
getParameterNames()     yes     [p 2, p 1]
getParameter("p 1")     yes     c d


<%-- disable right-click of mouse --%>
window.onload = function() {
    document.addEventListener("contextmenu", function(e){
        e.preventDefault();
    }, false);
};


<rollingPolicy class="org.apache.log4j.rolling.TimeBasedRollingPolicy">
	<!--
	log4j does let you use system properties.
	system property on command-line looks like this
	
	java standalone 執行 java 使用 -D 設定 system properties
	  java -Dlog_dir=/mydir/otherdir/logs %{package}.${className}
	
	Tomcat
	  Log4jConfigListener 使用 System.setProperty("log_dir", "/logs");
	-->
	<param name="FileNamePattern" value="${log_dir}/%d{yyyyMMdd}/log_%d{yyyyMMddHH}.log" />
</rollingPolicy>


== difference between / and /* in servlet mapping url pattern ==
<url-pattern>/*</url-pattern>
The /* on a servlet overrides all other servlets, including all servlets provided by the servletcontainer such as the default servlet and the JSP servlet. Whatever request you fire, it will end up in that servlet. This is thus a bad URL pattern for servlets. Usually, you'd like to use /* on a Filter only. It is able to let the request continue to any of the servlets listening on a more specific URL pattern by calling FilterChain#doFilter().

<url-pattern>/</url-pattern>
The / doesn't override any other servlet. It only replaces the servletcontainer's builtin default servlet for all requests which doesn't match any other registered servlet. This is normally only invoked on static resources (CSS/JS/image/etc) and directory listings. The servletcontainer's builtin default servlet is also capable of dealing with HTTP cache requests, media (audio/video) streaming and file download resumes. Usually, you don't want to override the default servlet as you would otherwise have to take care of all its tasks, which is not exactly trivial (JSF utility library OmniFaces has an open source example). This is thus also a bad URL pattern for servlets. As to why JSP pages doesn't hit this servlet, it's because the servletcontainer's builtin JSP servlet will be invoked, which is already by default mapped on the more specific URL pattern *.jsp.

<url-pattern></url-pattern>
Then there's also the empty string URL pattern . This will be invoked when the context root is requested. This is different from the <welcome-file> approach that it isn't invoked when any subfolder is requested. This is most likely the URL pattern you're actually looking for in case you want a "home page servlet". I only have to admit that I'd intuitively expect the empty string URL pattern  and the slash URL pattern / be defined exactly the other way round, so I can understand that a lot of starters got confused on this. But it is what it is.

1. 以"/"开头和以"/*"结尾的是用来做路径映射的。
2. 以前缀"*."开头的是用来做扩展映射的。
3. "/" 是用来定义default servlet映射的。
4. 剩下的都是用来定义详细映射的。比如： /aa/bb/cc.action
5. /direcotry/*.jsp不支持，容器无法判别是路径映射还是扩展映射
<url-pattern>/</url-pattern>:将servlet定义为容器默认servlet，当没有其他servlet能够处理当前请求时，由该servlet进行处理。
<url-pattern>/*</url-pattern>:会匹配所有url - 路径型的和后缀型的url(包括/login , *.jsp , *.js 和 *.html 等)

There are 3 ways you can configure url-pattern for Filter:
URL Pattern        Example
/*	               http://example.com/contextPath
                   http://example.com/contextPath/status/abc
				   
/status/abc/*	   http://example.com/contextPath/status/abc
                   http://example.com/contextPath/status/abc/mnp
                   http://example.com/contextPath/status/abc/mnp?date=today
               (X) http://example.com/contextPath/test/abc/mnp
				   
*.map	           http://example.com/contextPath/status/abc.map
                   http://example.com/contextPath/status.map?date=today
               (X) http://example.com/contextPath/status/abc.MAP




https://howtodoinjava.com/
https://o7planning.org/
