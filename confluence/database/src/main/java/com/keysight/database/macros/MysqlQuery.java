package com.keysight.database.macros; 

import java.util.Map;
import java.util.ArrayList;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.v2.components.HtmlEscaper;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;


public class MysqlQuery implements Macro
{
    private final static String CONNECTION_STRING = "connection-string";

    private final SettingsManager settingsManager;
    private final VelocityHelperService velocityHelperService;

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public MysqlQuery( SettingsManager settingsManager,
                        VelocityHelperService velocityHelperService
    ){
        this.settingsManager            = settingsManager;
        this.velocityHelperService      = velocityHelperService;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
       String template = "/com/keysight/database/templates/mysql-query.vm";
       Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

       ArrayList<String> columnNames = new ArrayList<String>();
       ArrayList<ArrayList<String>> dataRows = new ArrayList<ArrayList<String>>();

       try {

           System.out.println( "\n\n\nPATH: " + new File( "." ).getCanonicalPath() + "\n\n\n" );
           File mysqlJar = new File("mysql-connector-java-5.1.40-bin.jar");

           if( mysqlJar.exists() ){
              System.out.println( "\n\n\nMYSQL JAR EXISTS\n\n\n" );
           } else {
              System.out.println( "\n\n\nMYSQL JAR DOES NOT EXISTS\n\n\n" );
           }

           ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();

           URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{mysqlJar.toURL()}, currentThreadClassLoader);

           // Replace the thread classloader - assumes // you have permissions to do so
           Thread.currentThread().setContextClassLoader(urlClassLoader);

           // This will load the MySQL driver, each DB has its own driver
           Class.forName("com.mysql.jdbc.Driver");

           // Setup the connection with the DB
           //jdbc:mysql://localhost/feedback?user=sqluser&password=sqluserpw
           connect = DriverManager.getConnection( parameters.get(CONNECTION_STRING) );

           // Statements allow to issue SQL queries to the database
           statement = connect.createStatement();

           // Result set get the result of the SQL query
           resultSet = statement.executeQuery( parameters.get(body) );

           // Get the Column Names
           for( int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++ ){
              columnNames.add( resultSet.getMetaData().getColumnName(i) );
           }
           velocityContext.put( "columnNames", columnNames );

           // Get the Column Names
           while( resultSet.next() ){
              ArrayList<String> row = new ArrayList<String>();
              for( int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++ ){
                 row.add( resultSet.getString(i) );
              }
              dataRows.add( row );
           }
           velocityContext.put( "dataRows", dataRows );
       } catch( Exception e ) {
          throw new MacroExecutionException( e );
       } finally {
          close();
       }

       velocityContext.put( "bodyAsHtml", body );
       return velocityHelperService.getRenderedTemplate( template, velocityContext );
    }

    private void close(){
        try{
            if( resultSet != null ){ resultSet.close(); }
            if( statement != null ){ statement.close(); }
            if( connect   != null ){ connect.close();   }
        } catch( Exception e ){

        }
        return;
    }

    @Override
    public BodyType getBodyType()
    {
        return BodyType.PLAIN_TEXT;
    }

    @Override
    public OutputType getOutputType()
    {
        return OutputType.BLOCK;
    }
}
