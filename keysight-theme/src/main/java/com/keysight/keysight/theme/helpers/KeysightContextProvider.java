package com.keysight.keysight.theme.helpers;

import java.util.List;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;

import com.keysight.keysight.theme.helpers.KeysightPageStateConstants;

import java.util.Map;
import java.util.HashMap;

public class KeysightContextProvider implements ContextProvider
{
   private final PageManager       pageManager;
   private final PermissionManager permissionManager;
   
   public static final String INITIAL_STATE_KEY   = "initialState";
   public static final String PAGE_STATES_KEY     = "pageStates";
   public static final String PAGE_STATE_KEYS_KEY = "pageStateKeys";

   public KeysightContextProvider( PageManager       pageManager,
		                   PermissionManager permissionManager )
   {
      this.pageManager       = pageManager;
      this.permissionManager = permissionManager;
   }

   @Override
   public void init(Map<String, String> params) throws PluginParseException
   {
   }

   @Override
   public Map<String, Object> getContextMap(Map<String, Object> context)
   {
      boolean isDocumentation = false;
      boolean canEdit         = false;
      boolean isLabeled       = false;

      context.put( INITIAL_STATE_KEY,   KeysightPageStateConstants.DRAFT_KEY );
      context.put( PAGE_STATES_KEY,     KeysightPageStateConstants.PAGE_STATES);
      context.put( PAGE_STATE_KEYS_KEY, KeysightPageStateConstants.PAGE_STATE_KEYS);
      
      // Wraping in a try catch as I don't know how to check
      // that the pageId can be converted to Long
      try{
	 String pageTitleInVelocity = context.get( "title" ).toString();
         String pageId = context.get( "pageId" ).toString();
         Page currentPage = pageManager.getPage( Long.valueOf( pageId ) );

	 if( currentPage instanceof Page
	    && !pageTitleInVelocity.matches( "Attachments - " + currentPage.getTitle() ) ){
            List<Label> spaceCategories = currentPage.getSpace().getDescription().getLabels();
            List<Label> pageLabels      = currentPage.getLabels();

	    for( Label label : spaceCategories ){
               if( label.getName().equals( KeysightPageStateConstants.PAGE_STATE_ACTIVATION_KEY ) ){
                  isDocumentation = true;
	          break;
	       }
	    }
	
            for( Label label : pageLabels ){
               if( label.getName().equals( KeysightPageStateConstants.PAGE_STATE_ACTIVATION_KEY ) ){
                  isDocumentation = true;
               } else if( KeysightPageStateConstants.isPageStateLabel( label.getName() ) ){
	          isLabeled = true;
                  context.put( INITIAL_STATE_KEY, label.getName() );
               }
	    }
	 
	    // see if the currently logged in user has EDIT permissions
            if( permissionManager.hasPermission( AuthenticatedUserThreadLocal.get(), 
				                 com.atlassian.confluence.security.Permission.EDIT,
					         currentPage ) ){
	       context.put("canEdit", "true" );
            }
	 
	    if( isDocumentation ){
	       context.put("isDocumentation", "true");
            }
	 }
      } catch( Exception e ){}

      return context;
   }

}
