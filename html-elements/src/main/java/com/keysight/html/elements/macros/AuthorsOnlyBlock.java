package com.keysight.html.elements.macros;

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;

public class AuthorsOnlyBlock implements Macro
{
    private final PermissionManager permissionManager;

    public AuthorsOnlyBlock( PermissionManager permissionManager )
    {
      this.permissionManager = permissionManager;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
       // see if the currently logged in user has EDIT permissions
       if( permissionManager.hasPermission( AuthenticatedUserThreadLocal.get(), 
	                                    com.atlassian.confluence.security.Permission.EDIT,
			                    context.getEntity() ) ){
          if( parameters.containsKey( "no-border" ) ){
             return body;
          } else {
             return "<div class=\"panel private-content-container\">\n"
                   +"   <div class=\"private-content private-content-top private-content-header\">This content is only viewable to those who can edit the page.</div>\n"
                   +"   <div class=\"private-content private-content-body\">"+body+"</div>"
                   +"</div>\n";
          }
       } else {
          return "";
       }
    }

    @Override
    public BodyType getBodyType()
    {
        return BodyType.RICH_TEXT;
    }

    @Override
    public OutputType getOutputType()
    {
        return OutputType.BLOCK;
    }
}
