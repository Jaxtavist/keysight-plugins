package com.keysight.keysight.theme.helpers;

// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Implements Filter class
public class ImageRedirectFilter implements Filter  {
   public void  init(FilterConfig config) throws ServletException{
   }

   public void  doFilter(ServletRequest request, 
                         ServletResponse response,
                         FilterChain chain) 
                         throws java.io.IOException, ServletException {

      HttpServletRequest httpRequest = (HttpServletRequest) request;
      String servletPath = httpRequest.getServletPath();
      String contextPath = httpRequest.getContextPath();
      String image = servletPath.substring( servletPath.lastIndexOf('/')+1 );

      /*
      System.out.println( "\n\n" );
      System.out.println( "Context Path: " + httpRequest.getContextPath() );
      System.out.println( "Servlet Path: " + httpRequest.getServletPath() );
      System.out.println( "Path Info: " + httpRequest.getPathInfo() );
      System.out.println( "URL: " + httpRequest.getRequestURL() );
      System.out.println( "URI: " + httpRequest.getRequestURI() );
      System.out.println( "Image: " + image );
      System.out.println( "\n\n" );
      */

      // Redirect to the images provided by the plugin...
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.sendRedirect(contextPath+"/download/resources/com.keysight.keysight-theme:keysight-theme-resources/images/" + image);

      // Pass request back down the filter chain
      //chain.doFilter(request,response);
      return;
   }

   public void destroy( ){
   }
}
