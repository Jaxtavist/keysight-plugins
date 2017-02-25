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

        currentParams["color"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "status-flag",
                params: currentParams,
                defaultParameterValue: "",
                body : ""
            }
        };

        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("StatusFlagGrey", function(e, macroNode) {
        updateMacro(macroNode, "Grey");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("StatusFlagGreen", function(e, macroNode) {
        updateMacro(macroNode, "Green");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("StatusFlagYellow", function(e, macroNode) {
        updateMacro(macroNode, "Yellow");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("StatusFlagRed", function(e, macroNode) {
        updateMacro(macroNode, "Red");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("StatusFlagBlue", function(e, macroNode) {
        updateMacro(macroNode, "Blue");
    });
})();
