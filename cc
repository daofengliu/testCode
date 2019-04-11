<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
		 version="3.1">
	<display-name>Spring MVC</display-name>

	<servlet>
		<servlet-name>helloServlet</servlet-name>
		<servlet-class>idv.javaee.servlet.servlet.webxml.HelloServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>helloServlet</servlet-name>
		<url-pattern>/hello</url-pattern>
		<url-pattern>/hello.do</url-pattern>
	</servlet-mapping>

	<filter>
		<filter-name>UrlFilter</filter-name>
		<filter-class>idv.javaee.servlet.filter.OrangecFilter$UrlFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>UrlFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

    <filter>
        <filter-name>EncodingFilter</filter-name>
        <filter-class>idv.javaee.servlet.filter.OrangecFilter$EncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>EncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

	<context-param>
		<param-name>log4j-config-location</param-name>
		<param-value>/WEB-INF/classes/log4j.xml</param-value>
	</context-param>

	<listener>
		<listener-class>idv.javaee.servlet.listener.Log4jConfigListener</listener-class>
	</listener>
</web-app>




package idv.javaee.servlet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <filter>
 * 		<filter-name>UrlFilter</filter-name>
 * 		<filter-class>idv.javaee.servlet.filter.OrangecFilter$UrlFilter</filter-class>
 * 	</filter>
 * 	<filter-mapping>
 * 		<filter-name>UrlFilter</filter-name>
 * 		<url-pattern>/*</url-pattern>
 * 	</filter-mapping>
 *
 *  <filter>
 *      <filter-name>EncodingFilter</filter-name>
 *      <filter-class>idv.javaee.servlet.filter.OrangecFilter$EncodingFilter</filter-class>
 *      <init-param>
 *          <param-name>encoding</param-name>
 *          <param-value>UTF-8</param-value>
 *      </init-param>
 *  </filter>
 *  <filter-mapping>
 *      <filter-name>EncodingFilter</filter-name>
 *      <url-pattern>/*</url-pattern>
 *  </filter-mapping>
 *
 * [2019/04/11 16:04:06:948] INFO  idv.javaee.servlet.filter.OrangecFilter$UrlFilter.doFilter(OrangecFilter.java:72) - == UrlFilter doFilter ==
 * [2019/04/11 16:04:06:949] INFO  idv.javaee.servlet.filter.OrangecFilter$UrlFilter.doFilter(OrangecFilter.java:75) - UrlFilter - ServletPath = /hello, URL = http://localhost:8080/orangec/hello
 * [2019/04/11 16:04:06:949] INFO  idv.javaee.servlet.filter.OrangecFilter$EncodingFilter.doFilter(OrangecFilter.java:52) - == EncodingFilter doFilter ==
 * [2019/04/11 16:04:06:949] INFO  idv.javaee.servlet.servlet.webxml.HelloServlet.doGet(HelloServlet.java:76) - HelloServlet doGet
 */
public class OrangecFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        /*
         * Use chain.doFilter(request, response) to move the request to the next stage.
         * If the chain.doFilter(request, response) filter is not called, the user's request will not reach the target, it will be stopped at that filter.
         */
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    /** Encoding Filter */
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
        }

        @Override
        public void destroy() {
        }
    }

    public static class UrlFilter implements Filter {
        private static final Logger LOGGER = LoggerFactory.getLogger(UrlFilter.class);

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                FilterChain chain) throws IOException, ServletException {
            LOGGER.info("== UrlFilter doFilter ==");
            HttpServletRequest req = (HttpServletRequest) request;
            String servletPath = req.getServletPath();
            LOGGER.info("UrlFilter - ServletPath = " + servletPath + ", URL = " + req.getRequestURL());
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
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
