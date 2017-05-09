package com.keysight.database.helpers;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.net.URLDecoder;
import java.io.StringReader;

import org.apache.commons.lang.StringUtils;

import com.keysight.database.helpers.PluginConfig;
import com.keysight.database.helpers.DatabaseDriverDelegate;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Enumeration;
import java.net.URL;

import java.sql.DriverManager;
import java.sql.Driver;
import java.net.URLClassLoader;

public class SavedPluginSettings {
   private String jdbcDriverDirectory;
   private Element root;

   private final PluginSettingsFactory pluginSettingsFactory;
   private final TransactionTemplate transactionTemplate;

   public SavedPluginSettings( final PluginSettingsFactory pluginSettingsFactory,
                               final TransactionTemplate transactionTemplate )
   {
      this.pluginSettingsFactory = pluginSettingsFactory;
      this.transactionTemplate = transactionTemplate;

      try{
         PluginConfig pluginConfig = (PluginConfig) transactionTemplate.execute(new TransactionCallback(){
            public Object doInTransaction(){
               PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
               PluginConfig pluginConfig = new PluginConfig();
               pluginConfig.setXml( (String) settings.get(PluginConfig.class.getName() + ".xml"));
               return pluginConfig;
            };
         });

         String pluginConfigXml = URLDecoder.decode( pluginConfig.getXml() );

         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document document = builder.parse( new InputSource( new StringReader(pluginConfigXml) ) );
         root = document.getDocumentElement();

         String jdbcDriverDirectory = getValueOfElement( "jdbc-driver-directory" );

         if( !StringUtils.isEmpty( jdbcDriverDirectory ) ){
            this.setJdbcDriverDirectory( jdbcDriverDirectory );
         }

      } catch( Exception e ){
      }
   }

   private String getValueOfElement( String tagName ){
      NodeList allNodes = root.getElementsByTagName( tagName );
      String text = null;
      for( int i = 0; i < allNodes.getLength(); i++ ){
         if( allNodes.item(i).getNodeType() == Node.ELEMENT_NODE){ 
            text = allNodes.item(i).getTextContent();
            break;
         }
      }
      return text;
   }

   public void printLoadedDrivers()
   {
      Enumeration<Driver> drivers = DriverManager.getDrivers();
      while( drivers.hasMoreElements()){
         DatabaseDriverDelegate driver = (DatabaseDriverDelegate) drivers.nextElement();
         System.out.println("Driver: " + driver.getRealDriver().getClass().getName() );
      }
   }

   public void loadJdbcDriver( String databaseType )
   {
      String classname = null;
      URLClassLoader urlClassLoader = null;
      Driver driver = null;

      if( !StringUtils.isEmpty( getJdbcDriverDirectory() ) ){
         Path jdbcDriverDirectoryPath = Paths.get( this.getJdbcDriverDirectory() );
         if( Files.isDirectory( jdbcDriverDirectoryPath ) ){
            List<String> fileNames = new ArrayList<String>();
            try{ 

               DirectoryStream<Path> directoryStream = Files.newDirectoryStream( jdbcDriverDirectoryPath );
               for( Path path : directoryStream ){
                  classname = null;

                  if( databaseType.equals( "MySQL" ) && path.toString().matches(".*mysql.*") ){
		             classname = "com.mysql.jdbc.Driver";
                  } else if( databaseType.equals( "Microsoft SQL Server" ) && path.toString().matches(".*sqljdbc.*") ){
		             classname = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                  } else if( databaseType.equals( "Oracle" ) && path.toString().matches(".*ojdbc.*") ){
		             classname = "oracle.jdbc.driver.OracleDriver";
                  } else if( databaseType.equals( "PostgreSQL" ) && path.toString().matches(".*postgre.*") ){
		             classname = "org.postgresql.Driver";
                  }

                  if( !StringUtils.isEmpty( classname ) ){
                     urlClassLoader = new URLClassLoader(new URL[]{path.toUri().toURL()}, System.class.getClassLoader());
		             driver = (Driver)Class.forName(classname, true, urlClassLoader).newInstance();
		             DriverManager.registerDriver(new DatabaseDriverDelegate(driver));
                  }
                  
               }

               printLoadedDrivers();
            } catch( Exception exception ){
               System.out.println( exception );
            }
         }
      }
   }

   public String getJdbcDriverDirectory()                           { return this.jdbcDriverDirectory;                }
   public void   setJdbcDriverDirectory(String jdbcDriverDirectory) { this.jdbcDriverDirectory = jdbcDriverDirectory; }
}


