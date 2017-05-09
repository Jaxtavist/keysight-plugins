package com.keysight.database.macros; 

import java.util.Map;
import java.util.ArrayList;
import java.lang.reflect.*;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.v2.components.HtmlEscaper;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;

import com.keysight.database.helpers.DatabaseDriverDelegate;


public class DatabaseQuery implements Macro
{
    private final static String CONNECTION_STRING = "connection-string";
    private final static String DATABASE_TYPE     = "database-type";
    private final static String USERNAME          = "username";
    private final static String PASSWORD          = "password";

    private final SettingsManager settingsManager;
    private final VelocityHelperService velocityHelperService;

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String databaseType = null;

    public DatabaseQuery( SettingsManager settingsManager,
                          VelocityHelperService velocityHelperService
    ){
        this.settingsManager            = settingsManager;
        this.velocityHelperService      = velocityHelperService;
    }

    protected void setDatabaseType( String databaseType ){
       this.databaseType = databaseType;
    }
    protected String getDatabaseType( ){
       return this.databaseType;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
       String template = "/com/keysight/database/templates/database-query.vm";
       Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

       ArrayList<String> columnNames = new ArrayList<String>();
       ArrayList<ArrayList<String>> dataRows = new ArrayList<ArrayList<String>>();
       File mysqlJar = new File("C:\\mysql\\mysql-connector-java-5.1.40-bin.jar");
       URLClassLoader urlClassLoader;
       Driver driver;

       if( parameters.containsKey( DATABASE_TYPE ) ){
          setDatabaseType( parameters.get( DATABASE_TYPE ) );
       }

       try {
           URL url = mysqlJar.toURI().toURL();
           String classname = "com.mysql.jdbc.Driver";

           //System.out.println( "\n\n\nPATH: " + new File( "." ).getCanonicalPath() + "\n\n\n" );
           if( mysqlJar.exists() ){
              System.out.println( "\n\n\nMYSQL JAR EXISTS\n\n\n" );
           } else {
              System.out.println( "\n\n\nMYSQL JAR DOES NOT EXISTS\n\n\n" );
           }

           // This will load the MySQL driver, each DB has its own driver
           if( getDatabaseType().equals( "Mysql" ) ){
              System.out.println( "\n\n\nLoad mysql driver\n\n\n" );
		      url = mysqlJar.toURI().toURL();
		      classname = "com.mysql.jdbc.Driver";
           } else if( getDatabaseType().equals( "Microsoft Sql Server" ) ){
              System.out.println( "\n\n\nLoad sqlserver driver\n\n\n" );
              classname = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
           }

           urlClassLoader = new URLClassLoader(new URL[]{url}, System.class.getClassLoader());
		   driver = (Driver)Class.forName(classname, true, urlClassLoader).newInstance();
		   DriverManager.registerDriver(new DatabaseDriverDelegate(driver));

           System.out.println( "\n\n\nTry to connect\n\n\n" );

           // Setup the connection with the DB
           //jdbc:mysql://localhost/feedback?user=sqluser&password=sqluserpw
           connect = DriverManager.getConnection( parameters.get(CONNECTION_STRING),
                                                  parameters.get(USERNAME),
                                                  parameters.get(PASSWORD) );

           // Statements allow to issue SQL queries to the database
           statement = connect.createStatement();

           // Result set get the result of the SQL query
           resultSet = statement.executeQuery( body );

           // Get the Column Names
           for( int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++ ){
              columnNames.add( resultSet.getMetaData().getColumnName(i) );
           }
           velocityContext.put( "columnNames", columnNames );

           // Get the Column Names
           while( resultSet.next() ){
              ArrayList<String> row = new ArrayList<String>();
              for( int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++ ){
                 System.out.println( resultSet.getString(i) );
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
