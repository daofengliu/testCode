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




<%-- disable right-click of mouse --%>
window.onload = function() {
    document.addEventListener("contextmenu", function(e){
        e.preventDefault();
    }, false);
};



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
