var glossaryBlueprintHelper = (function(){
   // module variables
   var methods = new Object();

   // module methods
   // First to execute: preRender.  This is before the dialog box is created
   methods[ 'preRender' ] = function(e, state){
      state.soyRenderContext['atlToken'] = AJS.Meta.get('atl-token');
      state.soyRenderContext['showSpacePermission'] = false;
      return Confluence.SpaceBlueprint.CommonWizardBindings.preRender( e, state );
   }

   // Second to execute: postRender.  This is before the dialog box shows
   methods[ 'postRender' ] = function(e, state){
      $("#keysight-glossary-space-categories").auiSelect2(Confluence.UI.Components.LabelPicker.build({
             separator: ",",
      }));
      return Confluence.SpaceBlueprint.CommonWizardBindings.postRender( e, state );
   }

   // Last to execute: submit.  This is when the submit button is pressed.
   methods[ 'submit' ] = function(e, state){
      state.pageData.ContentPageTitle = state.pageData.name + " " + AJS.I18n.getText("glossary-space.home.title.suffix");
      return Confluence.SpaceBlueprint.CommonWizardBindings.submit( e, state );
   }
    
   // return the object with the methods
   return methods;
})();

// Register space blueprint hooks
AJS.bind("blueprint.wizard-register.ready", function () {
    Confluence.Blueprint.setWizard('com.keysight.glossary:glossary-space-web-item', function(wizard) {
        wizard.on("submit.glossary-space-create-dialog-page-1", glossaryBlueprintHelper.submit);
        wizard.on("pre-render.glossary-space-create-dialog-page-1", glossaryBlueprintHelper.preRender);
        wizard.on("post-render.glossary-space-create-dialog-page-1", glossaryBlueprintHelper.postRender);
    });
});

