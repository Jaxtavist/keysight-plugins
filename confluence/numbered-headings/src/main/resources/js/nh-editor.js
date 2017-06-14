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

        currentParams["start-numbering-with"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "numberedheadings",
                params: currentParams,
                defaultParameterValue: "",
                body : $macroDiv.find(".wysiwyg-macro-body").html()
            }
        };
        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-0", function(e, macroNode) {
        updateMacro(macroNode, "");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-1", function(e, macroNode) {
        updateMacro(macroNode, "1");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-2", function(e, macroNode) {
        updateMacro(macroNode, "2");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-3", function(e, macroNode) {
        updateMacro(macroNode, "3");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-4", function(e, macroNode) {
        updateMacro(macroNode, "4");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-5", function(e, macroNode) {
        updateMacro(macroNode, "5");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-6", function(e, macroNode) {
        updateMacro(macroNode, "6");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-7", function(e, macroNode) {
        updateMacro(macroNode, "7");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-8", function(e, macroNode) {
        updateMacro(macroNode, "8");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-9", function(e, macroNode) {
        updateMacro(macroNode, "9");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nh-start-at-10", function(e, macroNode) {
        updateMacro(macroNode, "10");
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

        currentParams["start-numbering-with"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "numberedheadings",
                params: currentParams,
                defaultParameterValue: "",
                body : $macroDiv.find(".wysiwyg-macro-body").html()
            }
        };
        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-0", function(e, macroNode) {
        updateMacro(macroNode, "");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-1", function(e, macroNode) {
        updateMacro(macroNode, "1");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-2", function(e, macroNode) {
        updateMacro(macroNode, "2");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-3", function(e, macroNode) {
        updateMacro(macroNode, "3");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-4", function(e, macroNode) {
        updateMacro(macroNode, "4");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-5", function(e, macroNode) {
        updateMacro(macroNode, "5");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-6", function(e, macroNode) {
        updateMacro(macroNode, "6");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-7", function(e, macroNode) {
        updateMacro(macroNode, "7");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-8", function(e, macroNode) {
        updateMacro(macroNode, "8");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-9", function(e, macroNode) {
        updateMacro(macroNode, "9");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("nhl-start-at-10", function(e, macroNode) {
        updateMacro(macroNode, "10");
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

        currentParams["start-numbering-with"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "numbered-headings",
                params: currentParams,
                defaultParameterValue: "",
                body : $macroDiv.find(".wysiwyg-macro-body").html()
            }
        };
        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-0", function(e, macroNode) {
        updateMacro(macroNode, "");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-1", function(e, macroNode) {
        updateMacro(macroNode, "1");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-2", function(e, macroNode) {
        updateMacro(macroNode, "2");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-3", function(e, macroNode) {
        updateMacro(macroNode, "3");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-4", function(e, macroNode) {
        updateMacro(macroNode, "4");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-5", function(e, macroNode) {
        updateMacro(macroNode, "5");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-6", function(e, macroNode) {
        updateMacro(macroNode, "6");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-7", function(e, macroNode) {
        updateMacro(macroNode, "7");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-8", function(e, macroNode) {
        updateMacro(macroNode, "8");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-9", function(e, macroNode) {
        updateMacro(macroNode, "9");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-start-at-10", function(e, macroNode) {
        updateMacro(macroNode, "10");
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

        currentParams["start-numbering-with"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "numbered-headings-for-page",
                params: currentParams,
                defaultParameterValue: "",
                body : $macroDiv.find(".wysiwyg-macro-body").html()
            }
        };
        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);

    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-0", function(e, macroNode) {
        updateMacro(macroNode, "");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-1", function(e, macroNode) {
        updateMacro(macroNode, "1");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-2", function(e, macroNode) {
        updateMacro(macroNode, "2");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-3", function(e, macroNode) {
        updateMacro(macroNode, "3");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-4", function(e, macroNode) {
        updateMacro(macroNode, "4");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-5", function(e, macroNode) {
        updateMacro(macroNode, "5");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-6", function(e, macroNode) {
        updateMacro(macroNode, "6");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-7", function(e, macroNode) {
        updateMacro(macroNode, "7");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-8", function(e, macroNode) {
        updateMacro(macroNode, "8");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-9", function(e, macroNode) {
        updateMacro(macroNode, "9");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("n-h-f-p-start-at-10", function(e, macroNode) {
        updateMacro(macroNode, "10");
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

        currentParams["start-numbering-with"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "renumber-and-include-page",
                params: currentParams,
                defaultParameterValue: "",
                body : ""
            }
        };
        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-0", function(e, macroNode) {
        updateMacro(macroNode, "");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-1", function(e, macroNode) {
        updateMacro(macroNode, "1");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-2", function(e, macroNode) {
        updateMacro(macroNode, "2");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-3", function(e, macroNode) {
        updateMacro(macroNode, "3");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-4", function(e, macroNode) {
        updateMacro(macroNode, "4");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-5", function(e, macroNode) {
        updateMacro(macroNode, "5");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-6", function(e, macroNode) {
        updateMacro(macroNode, "6");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-7", function(e, macroNode) {
        updateMacro(macroNode, "7");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-8", function(e, macroNode) {
        updateMacro(macroNode, "8");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-9", function(e, macroNode) {
        updateMacro(macroNode, "9");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raip-start-at-10", function(e, macroNode) {
        updateMacro(macroNode, "10");
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

        currentParams["start-numbering-with"] = param;

        var macroRenderRequest = {
            contentId: Confluence.Editor.getContentId(),
            macro: {
                name: "renumber-and-include-children",
                params: currentParams,
                defaultParameterValue: "",
                body : ""
            }
        };
        tinymce.confluence.MacroUtils.insertMacro(macroRenderRequest);
    };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-0", function(e, macroNode) {
        updateMacro(macroNode, "");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-1", function(e, macroNode) {
        updateMacro(macroNode, "1");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-2", function(e, macroNode) {
        updateMacro(macroNode, "2");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-3", function(e, macroNode) {
        updateMacro(macroNode, "3");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-4", function(e, macroNode) {
        updateMacro(macroNode, "4");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-5", function(e, macroNode) {
        updateMacro(macroNode, "5");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-6", function(e, macroNode) {
        updateMacro(macroNode, "6");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-7", function(e, macroNode) {
        updateMacro(macroNode, "7");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-8", function(e, macroNode) {
        updateMacro(macroNode, "8");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-9", function(e, macroNode) {
        updateMacro(macroNode, "9");
    });

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("raic-start-at-10", function(e, macroNode) {
        updateMacro(macroNode, "10");
    });

})();
