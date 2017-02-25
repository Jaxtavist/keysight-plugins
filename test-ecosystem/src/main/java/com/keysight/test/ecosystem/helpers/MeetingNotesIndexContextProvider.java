package com.keysight.test.ecosystem.helpers;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;

public class MeetingNotesIndexContextProvider extends AbstractBlueprintContextProvider
{
    public static final String BLUEPRINT_LABEL = "test-ecosystem-meeting-note";

    public MeetingNotesIndexContextProvider()
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
                                   +"   <ac:parameter ac:name=\"createButtonLabel\">New Meeting</ac:parameter>"
                                   +"</ac:structured-macro>";

        String taskReport = "<ac:structured-macro ac:name=\"tasks-report-macro\">"
                           +"   <ac:parameter ac:name=\"spaces\">"+spaceKey+"</ac:parameter>"
                           +"   <ac:parameter ac:name=\"status\">incomplete</ac:parameter>"
                           +"   <ac:parameter ac:name=\"spaceAndPage\">space:"+spaceKey+"</ac:parameter>"
                           +"   <ac:parameter ac:name=\"labels\">"+BLUEPRINT_LABEL+"</ac:parameter>"
                           +"   <ac:parameter ac:name=\"pageSize\">"+10+"</ac:parameter>"
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

        context.put("createFromTemplateMacro", createFromTemplate );
        context.put("taskReportMacro", taskReport );
        context.put("contentReportTableMacro", contentReport );

        return context;
    }
}
