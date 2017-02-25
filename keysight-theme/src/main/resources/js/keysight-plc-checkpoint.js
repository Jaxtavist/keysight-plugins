(function() {
    var updateMacro = function(macroNode, param) {
        var $macroDiv = AJS.$(macroNode);
        AJS.Rte.getEditor().selection.select($macroDiv[0]);
        AJS.Rte.BookmarkManager.storeBookmark();

        var currentParams = {};
        if ($macroDiv.attr("data-macro-parameters")) {
            AJS.$.each($macroDiv.attr("data-macro-parameters").split("|"), function(idx, item) {
                var param = item.split("=");
                currentParams[param[0]] = param[1];
            });
        }

        currentParams["checkpoint"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "keysight-plc-checkpoint",
                params: currentParams,
                defaultParameterValue: "",
                body : ""
            }
        };

        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("PRECON", function(e, macroNode) {
        updateMacro(macroNode, "PRECON");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("CON", function(e, macroNode) {
        updateMacro(macroNode, "CON");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("INV", function(e, macroNode) {
        updateMacro(macroNode, "INV");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("DEF", function(e, macroNode) {
        updateMacro(macroNode, "DEF");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("DEV", function(e, macroNode) {
        updateMacro(macroNode, "DEV");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("HQ", function(e, macroNode) {
        updateMacro(macroNode, "HQ");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("SQ", function(e, macroNode) {
        updateMacro(macroNode, "SQ");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("FPR", function(e, macroNode) {
        updateMacro(macroNode, "FPR");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("MS", function(e, macroNode) {
        updateMacro(macroNode, "MS");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("SHP", function(e, macroNode) {
        updateMacro(macroNode, "SHP");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("DIS", function(e, macroNode) {
        updateMacro(macroNode, "DIS");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("SUP", function(e, macroNode) {
        updateMacro(macroNode, "SUP");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("CLO", function(e, macroNode) {
        updateMacro(macroNode, "CLO");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("PRC", function(e, macroNode) {
        updateMacro(macroNode, "PRC");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("PC", function(e, macroNode) {
        updateMacro(macroNode, "PC");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("LSR", function(e, macroNode) {
        updateMacro(macroNode, "LSR");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("RTR", function(e, macroNode) {
        updateMacro(macroNode, "RTR");
    });
    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("DD", function(e, macroNode) {
        updateMacro(macroNode, "DD");
    });
})();
