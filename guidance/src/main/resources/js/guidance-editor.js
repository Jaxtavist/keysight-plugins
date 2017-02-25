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

        currentParams["guidance"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "guideline-inline",
                params: currentParams,
                defaultParameterValue: "",
                body : ""
            }
        };

        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("InlineGuidelineDo", function(e, macroNode) {
        updateMacro(macroNode, "Do");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("InlineGuidelineConsider", function(e, macroNode) {
        updateMacro(macroNode, "Consider");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("InlineGuidelineAvoid", function(e, macroNode) {
        updateMacro(macroNode, "Avoid");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("InlineGuidelineDoNot", function(e, macroNode) {
        updateMacro(macroNode, "Do Not");
    });

})();

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

        currentParams["guidance"] = param;

        var t = tinymce.confluence.macrobrowser;
        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "guideline-block",
                params: currentParams,
                defaultParameterValue: "",
                body : $macroDiv.find(".wysiwyg-macro-body").html()
            }
        };

        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("BlockGuidelineAuto", function(e, macroNode) {
        updateMacro(macroNode, "Auto");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("BlockGuidelineDo", function(e, macroNode) {
        updateMacro(macroNode, "Do");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("BlockGuidelineConsider", function(e, macroNode) {
        updateMacro(macroNode, "Consider");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("BlockGuidelineAvoid", function(e, macroNode) {
        updateMacro(macroNode, "Avoid");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("BlockGuidelineDoNot", function(e, macroNode) {
        updateMacro(macroNode, "Do Not");
    });

})();

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

        currentParams["guidance"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "lp-guideline",
                params: currentParams,
                defaultParameterValue: "",
                body : $macroDiv.find(".wysiwyg-macro-body").html()
            }
        };

        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("LpGuidelineAuto", function(e, macroNode) {
        updateMacro(macroNode, "Auto");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("LpGuidelineDo", function(e, macroNode) {
        updateMacro(macroNode, "Do");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("LpGuidelineConsider", function(e, macroNode) {
        updateMacro(macroNode, "Consider");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("LpGuidelineAvoid", function(e, macroNode) {
        updateMacro(macroNode, "Avoid");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("LpGuidelineDoNot", function(e, macroNode) {
        updateMacro(macroNode, "Do Not");
    });

})();






