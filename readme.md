# Simple HTTP Server 
> Simple implamentation of com.sun.net.httpserver

Supports GET and POST method, use macros to generate dynamic page to show the result of form
(See Contact page in the demo website)

## Functions
- Implaments most import functions in com.sun.net.httpserver
- Uses multi-threads to response multi-request at the same time
- Uses request in url to dispatch different requests for different handler. (/version is an example)
- Supports GET request for basic HTTP server
- Supports POST request to process form
- Use macro function to create dynamic page for showing the ressult of form

## Files
- Main.java: Start http server, Creates two http handlers based on MyhttpSever framework, one handler is used to process normal http request, anther handler is for checing version
- MyHttpServer: Implement simple http server, provide start/stop function.
- MyHttpHeader: Implement http header parser function
- MyhttpExchange: Provides data struction which is used to transfer http server class to http handler
- ServerConfig: Read/Write Server configs, such as http root dir, http server listenning port
- yoga: this is sample web site

## Run
- edit http.conf to set your http root dir and http server port
- "java -jar MyHttpServer.jar" to start server
- "Ctrl-C" to send signal to stop server

## Contribution
- All source code are my original idea and contribution, except part of ServerConfig.java
- Class and Function definiton are borrowed from com.sun.net.httpserver, but implementation are all my original work.

## Reference
- [Java Doc/HttpServer](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpContext.html)
- [Java Doc/HttpContext](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpContext.html)
- [Java Doc/httpserver.Headers](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/Headers.html)
- [Java Doc/URLDecoder](https://docs.oracle.com/javase/7/docs/api/java/net/URLDecoder.html)
- [HTTP - Message](https://www.tutorialspoint.com/http/http_messages.htm)
- [HTTP Header Fields](https://www.tutorialspoint.com/http/http_header_fields.htm)
- [List of HTTP status codes]("https://en.wikipedia.org/wiki/List_of_HTTP_status_codes")
- [HTTP Request Message and Response Message](http://www.cnblogs.com/biyeymyhjob/archive/2012/07/28/2612910.html)
- [Java Properties file examples]("http://www.mkyong.com/java/java-properties-file-examples")
## License

[![CC0](https://licensebuttons.net/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

To the extent possible under law, [Jie Yu](yujie.yjp@gmail.com) has waived all copyright and related or neighboring rights to this work.
