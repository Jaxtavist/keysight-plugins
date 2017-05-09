package com.keysight.database.macros; 

import java.util.Map;
import java.util.ArrayList;
import java.lang.reflect.*;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.v2.components.HtmlEscaper;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

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
import java.util.Enumeration;

import com.keysight.database.helpers.DatabaseDriverDelegate;
import com.keysight.database.helpers.SavedPluginSettings;

public class DatabaseQuery implements Macro
{
    private final static String CONNECTION_STRING = "connection-string";
    private final static String DATABASE_TYPE     = "database-type";
    private final static String USERNAME          = "username";
    private final static String PASSWORD          = "password";

    private final PluginSettingsFactory pluginSettingsFactory;
    private final SettingsManager settingsManager;
    private final TransactionTemplate transactionTemplate;
    private final VelocityHelperService velocityHelperService;

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String databaseType = null;

    public DatabaseQuery( PluginSettingsFactory pluginSettingsFactory,
                          SettingsManager settingsManager,
                          VelocityHelperService velocityHelperService,
                          TransactionTemplate transactionTemplate
    ){
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.settingsManager            = settingsManager;
        this.transactionTemplate = transactionTemplate;
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

       SavedPluginSettings pluginSettings = new SavedPluginSettings( pluginSettingsFactory, transactionTemplate );

       ArrayList<String> columnNames = new ArrayList<String>();
       ArrayList<ArrayList<String>> dataRows = new ArrayList<ArrayList<String>>();
       File mysqlJar = new File("C:\\jdbc-drivers\\mysql-connector-java-5.1.40-bin.jar");
       URLClassLoader urlClassLoader;
       Driver driver;
       String classname;

       if( parameters.containsKey( DATABASE_TYPE ) ){
          setDatabaseType( parameters.get( DATABASE_TYPE ) );
       }

       try {
           connect = DriverManager.getConnection( parameters.get(CONNECTION_STRING),
                                                  parameters.get(USERNAME),
                                                  parameters.get(PASSWORD) );
       } catch( SQLException exception ) {
          if( exception.getMessage().matches( ".*No suitable driver found.*" ) ){
             pluginSettings.loadJdbcDriver( getDatabaseType() );
             try {
                connect = DriverManager.getConnection( parameters.get(CONNECTION_STRING),
                                                       parameters.get(USERNAME),
                                                       parameters.get(PASSWORD) );
             } catch( Exception exception2 ){
                throw new MacroExecutionException( exception2 );
             }
          } else {
             throw new MacroExecutionException( exception );
          }
       } catch( Exception exception ){
          throw new MacroExecutionException( exception );
       }

       if( connect != null ){
           try {
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
