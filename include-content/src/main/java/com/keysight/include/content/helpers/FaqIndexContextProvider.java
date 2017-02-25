package com.keysight.include.content.helpers;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;

public class FaqIndexContextProvider extends AbstractBlueprintContextProvider
{
    public static final String BLUEPRINT_LABEL = "faq";

    public FaqIndexContextProvider()
    {
    }

    @Override
    protected BlueprintContext updateBlueprintContext(BlueprintContext context)
    {
        String spaceKey     = context.getSpaceKey();
        String blueprintId  = context.getBlueprintId().toString();
        String blueprintKey = context.getBlueprintModuleCompleteKey().getCompleteKey();
        
        String createFromTemplate = "<ac:structured-macro ac:name=\"create-from-template\">"
                                   +"   <ac:parameter ac:name=\"blueprintModuleCompleteKey\">"+blueprintKey+"</ac:parameter>"
                                   +"   <ac:parameter ac:name=\"contentBlueprintId\">"+blueprintId+"</ac:parameter>"
                                   +"   <ac:parameter ac:name=\"spaceKey\">"+spaceKey+"</ac:parameter>"
                                   +"   <ac:parameter ac:name=\"createButtonLabel\">Create a new FAQ page</ac:parameter>"
                                   +"</ac:structured-macro>";

        String contentReport = "<ac:structured-macro ac:name=\"content-report-table\">"
                              +"   <ac:parameter ac:name=\"blueprintModuleCompleteKey\">"+blueprintKey+"</ac:parameter>"
                              +"   <ac:parameter ac:name=\"spaces\">"+spaceKey+"</ac:parameter>"
                              +"   <ac:parameter ac:name=\"contentBlueprintId\">"+blueprintId+"</ac:parameter>"
                              +"   <ac:parameter ac:name=\"labels\">"+BLUEPRINT_LABEL+"</ac:parameter>"
                              +"   <ac:parameter ac:name=\"analyticsKey\">"+BLUEPRINT_LABEL+"</ac:parameter>"
                              +"   <ac:parameter ac:name=\"blankDescription\">No Content</ac:parameter>"
                              +"   <ac:parameter ac:name=\"blankTitle\">No Content</ac:parameter>"
                              +"</ac:structured-macro>";

        String recentActivity = "<ac:structured-macro ac:name=\"recently-updated\">"
                               +"   <ac:parameter ac:name=\"max\">5</ac:parameter>"
                               +"   <ac:parameter ac:name=\"hideHeading\">true</ac:parameter>"
                               +"   <ac:parameter ac:name=\"theme\">consise</ac:parameter>"
                               +"   <ac:parameter ac:name=\"labels\">"+BLUEPRINT_LABEL+"</ac:parameter>"
                               +"   <ac:parameter ac:name=\"types\">page, comment</ac:parameter>"
                               +"</ac:structured-macro>";
        
        String contributors = "<ac:structured-macro ac:name=\"contributors\">"
                             +"   <ac:parameter ac:name=\"limit\">5</ac:parameter>"
                             +"   <ac:parameter ac:name=\"labels\">"+BLUEPRINT_LABEL+"</ac:parameter>"
                             +"   <ac:parameter ac:name=\"contentType\">pages</ac:parameter>"
                             +"   <ac:parameter ac:name=\"showLastTime\">true</ac:parameter>"
                             +"   <ac:parameter ac:name=\"mode\">list</ac:parameter>"
                             +"</ac:structured-macro>";
        

        context.put("createFromTemplateMacro", createFromTemplate );
        context.put("contentReportTableMacro", contentReport );
        context.put("recentActivityMacro", recentActivity );
        context.put("contributorsMacro", recentActivity );

        return context;
    }
}
