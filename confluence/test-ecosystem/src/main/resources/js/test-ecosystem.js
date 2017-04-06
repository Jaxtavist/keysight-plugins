// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var testEcosystemHelp = (function( $ ){
	
   // module variables
   var methods     = new Object();
   var pluginId    = "test-ecosystem";
   var restVersion = "1.0";

   // module methods
   methods[ 'showNavBarHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "test-ecosystem-navbar" );
   }
   methods[ 'showEtmsHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "etms-block" );
   }
   methods[ 'showShareDocLinkHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "sharedoc-link" );
   }
   methods[ 'showShareDocLinkBankHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "sharedoc-link-bank" );
   }
   methods[ 'showSpecialHandlingPropertiesHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "special-handling-properties" );
   }
   methods[ 'showSpecialHandlingDocumentsHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "special-handling-documents" );
   }

   // return the object with the methods
   return methods;

// end closure
})( AJS.$ || jQuery );

AJS.bind("blueprint.wizard-register.ready", function () {

    // Basic Space Methods
    function submitTestEcosystemSpace(e, state) {
        state.pageData.ContentPageTitle = state.pageData.name + " " + AJS.I18n.getText("test-ecosystem.basic-space.home.title.suffix");
        return Confluence.SpaceBlueprint.CommonWizardBindings.submit(e, state);
    }
    function preRenderTestEcosystemSpace(e, state) {
        state.soyRenderContext['atlToken'] = AJS.Meta.get('atl-token');
        state.soyRenderContext['showSpacePermission'] = false;
    }
    
    // FAQ Methods
    function labelPickerPostRender(e, state) {
       var wizardForm = state.$container;
       var labelsField = $("#test-ecosystem-blueprint-labels", wizardForm);
       labelsField.auiSelect2(Confluence.UI.Components.LabelPicker.build({
               separator: " ",
               queryOpts: {
                       spaceKey: state.wizardData.spaceKey
               }
    }));
    }
    
    function validate($container, spaceKey) {
       var wizardForm = $container,
           $titleField = wizardForm.find('#test-ecosystem-create-page-dialog-title'),
           pageTitle = $.trim($titleField.val()),
           error;

        wizardForm.find('.error').html(''); // clear all existing errors

        if (!pageTitle) {
            error = "Title is required.";
        }
        else if (!Confluence.Blueprint.canCreatePage(spaceKey, pageTitle)) {
            error = "A page with this name already exists.";
        }
        if (error) {
            $titleField.focus().siblings(".error").html(error);
            return false;
        }

        return true;
    }

    function submit(e, state) {
        return validate(state.$container, state.wizardData.spaceKey);
    }
    
    // Register basic space blueprint hooks
    Confluence.Blueprint.setWizard('com.keysight.test-ecosystem:test-ecosystem-basic-space-web-item', function(wizard) {
        wizard.on("submit.test-ecosystem-basic-space-create-dialog-page-1", submitTestEcosystemSpace);
        wizard.on("pre-render.test-ecosystem-basic-space-create-dialog-page-1", preRenderTestEcosystemSpace);
        wizard.on("post-render.test-ecosystem-basic-space-create-dialog-page-1", Confluence.SpaceBlueprint.CommonWizardBindings.postRender);
    });

    // Register tutorial blueprint hooks
    Confluence.Blueprint.setWizard('com.keysight.test-ecosystem:test-ecosystem-tutorial-web-item', function(wizard) {
        wizard.on("post-render.test-ecosystem-tutorial-create-dialog-page-1", labelPickerPostRender);
        wizard.on("submit.test-ecosystem-tutorial-create-dialog-page-1", submit);
    });
});
