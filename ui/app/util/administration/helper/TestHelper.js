/*jshint multistr: true */
Ext.define('CMDBuildUI.model.TestType', {
    extend: 'CMDBuildUI.model.base.Base',
    requires: [],
    fields: [{
        name: 'classname',
        type: 'string'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'type',
        type: 'string'
    }, {
        name: 'declaration',
        type: 'string'
    }, {
        name: 'result',
        calculate: function (data) {
            var declaration = data.declaration ? data.declaration.replace('By element ', Ext.String.format('By {0} ', data.name)) : '';
            var result = [];
            var expectation;
            result.push("// declaration");
            result.push('');
            result.push(declaration.trim());

            switch (data.type) {
                case 'textfield':
                    
                    expectation = "\n\
                    public String get{1}Value() {\n\
                            WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated({2}));\n\
                            return field.findElement(By.tagName(\"input\")).getAttribute(\"value\");\n\
                        }\n\
                        public {0} clear{1}() {\n\
                            WebElement field = wait.until(ExpectedConditions.elementToBeClickable({2}));\n\
                            WebElement input = field.findElement(By.tagName(\"input\"));\n\
                            input.clear();\n\
                            return this;\n\
                        }\n\
                        \n\
                        public {0} typeInto{1}(String text) {\n\
                            WebElement field = wait.until(ExpectedConditions.elementToBeClickable({2}));\n\
                            WebElement input = field.findElement(By.tagName(\"input\"));\n\
                            input.sendKeys(text);\n\
                            \n\
                            return this;\n\
                        }\n\
                        public {0} clearAndTypeInto{1}(String text) {\n\
                            WebElement field = wait.until(ExpectedConditions.elementToBeClickable({2}));\n\
                            WebElement input = field.findElement(By.tagName(\"input\"));\n\
                            input.clear();\n\
                            input.sendKeys(text);\n\
                            return this;\n\
                        }\n\
                        \n\
                        public Boolean is{1}Visible() {\n\
                            Boolean isVisible = wait.until(ExpectedConditions.visibilityOfElementLocated({2}))\n\
                                .isDisplayed();\n\
                        \n\
                            return isVisible;\n\
                        }\n\
                        \n\
                        public Boolean is{1}Invisible() {\n\
                        \n\
                            return wait.until(ExpectedConditions.invisibilityOfElementLocated({2}));\n\
                        }";

                    result.push('');
                    break;
                case 'checkbox':
                    result.push('// TODO: fix view in row selector');
                    result.push(data.declaration ? data.declaration.replace('By element ', Ext.String.format('By {0}ViewInRow ', data.name)).trim() : '');                    
                    expectation = "\n\
                    public Boolean get{1}Value(Boolean inViewInRow) {\n\
                        WebElement field = wait.until(\n\
                                ExpectedConditions.visibilityOfElementLocated(inViewInRow ? {2}ViewInRow : {2}));\n\
                    \n\
                        return field.findElement(By.tagName(\"input\")).isSelected();\n\
                    }\n\
                    \n\
                    public {0} click{1}Value() {\n\
                        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated({2}));\n\
                        field.findElement(By.tagName(\"input\")).click();\n\
                        \n\
                        return this;\n\
                    }\n\
                    \n\
                    public Boolean is{1}Visible() {\n\
                        Boolean isVisible = wait.until(ExpectedConditions.visibilityOfElementLocated({2}))\n\
                            .isDisplayed();\n\
                    \n\
                        return isVisible;\n\
                    }\n\
                    \n\
                    public Boolean is{1}Invisible() {\n\
                    \n\
                        return wait.until(ExpectedConditions.invisibilityOfElementLocated({2}));\n\
                    }";


                    break;
                case 'displayfield':
                    result.push('// TODO: fix view in row selector');
                    result.push(data.declaration ? data.declaration.replace('By element ', Ext.String.format('By {0}ViewInRow ', data.name)).trim() : '');
                    expectation = "\n\
                    public String get{1}DisplayValue(Boolean inViewInRow) {\n\
                        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(\n\
                                inViewInRow ? {2}ViewInRow : {2}));\n\
                        return field.findElement(By.cssSelector(\"[data-ref=\"inputEl\"]\")).getText();\n\
                    }\n\
                    \n\
                    public Boolean is{1}Visible(Boolean inViewInRow) {\n\
                        Boolean isVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(inViewInRow ? {2}ViewInRow : {2})).isDisplayed();\n\
                    \n\
                        return isVisible;\n\
                    }\n\
                    \n\
                    public Boolean is{1}Invisible(Boolean inViewInRow) {\n\
                    \n\
                        return wait.until(ExpectedConditions.invisibilityOfElementLocated(inViewInRow ? {2}ViewInRow : {2}));\n\
                    }";
                    break;
                case 'combobox':
                    expectation = "\n\
                    public {0} select{1}Value(String value) {\n\
                        ExtUtils.selectComboItemByOptionLabel(wait, {2}, value);\n\
                        \n\
                        return this;\n\
                    }\n\
                    \n\
                    public String get{1}SelectedLabel() {\n\
                        WebElement fieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated({2}));\n\
                        WebElement input = fieldElement.findElement(By.tagName(\"input\"));\n\
                        return input.getAttribute(\"value\");\n\
                    }\n\
                    \n\
                    public Boolean is{1}Editable() {\n\
                        WebElement fieldElement = wait.until(ExpectedConditions.visibilityOfElementLocated({2}));\n\
                        WebElement inputElement = fieldElement.findElement(By.tagName(\"input\"));\n\
                        return inputElement.getAttribute(\"aria-disabled\") == null;\n\
                    }\n\
                    \n\
                    public Boolean is{1}Visible() {\n\
                        Boolean isVisible = wait.until(ExpectedConditions.visibilityOfElementLocated({2}))\n\
                            .isDisplayed();\n\
                        \n\
                        return isVisible;\n\
                    }\n\
                    \n\
                    public Boolean is{1}Invisible() {\n\
                    \n\
                        return wait.until(ExpectedConditions.invisibilityOfElementLocated({2}));\n\
                    }";

                    break;
                case 'textarea':
                    result.push('// TODO: fix view in row selector');
                    result.push(data.declaration ? data.declaration.replace('By element ', Ext.String.format('By {0}ViewInRow ', data.name)).trim() : '');
                    expectation = "\n\
                    public String get{1}Value(Boolean inViewInRow) {\n\
                            WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(inViewInRow ? {2}ViewInRow : {2}));\n\
                            return field.findElement(By.tagName(\"textarea\")).getAttribute(\"value\");\n\
                        }\n\
                        public {0} clear{1}() {\n\
                            WebElement field = wait.until(ExpectedConditions.elementToBeClickable({2}));\n\
                            WebElement input = field.findElement(By.tagName(\"textarea\"));\n\
                            input.clear();\n\
                            return this;\n\
                        }\n\
                        \n\
                        public {0} typeInto{1}(String text) {\n\
                            WebElement field = wait.until(ExpectedConditions.elementToBeClickable({2}));\n\
                            WebElement input = field.findElement(By.tagName(\"textarea\"));\n\
                            input.sendKeys(text);\n\
                            \n\
                            return this;\n\
                        }\n\
                        public {0} clearAndTypeInto{1}(String text) {\n\
                            WebElement field = wait.until(ExpectedConditions.elementToBeClickable({2}));\n\
                            WebElement input = field.findElement(By.tagName(\"textarea\"));\n\
                            input.clear();\n\
                            input.sendKeys(text);\n\
                            \n\
                            return this;\n\
                        }\n\
                        \n\
                        public Boolean is{1}Visible(Boolean inViewInRow) {\n\
                            Boolean isVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(inViewInRow ? {2}ViewInRow : {2}))\n\
                                .isDisplayed();\n\
                            \n\
                            return isVisible;\n\
                        }\n\
                        \n\
                        public Boolean is{1}Invisible(Boolean inViewInRow) {\n\
                        \n\
                            return wait.until(ExpectedConditions.invisibilityOfElementLocated(inViewInRow ? {2}ViewInRow : {2}));\n\
                        }";

                    break;
                case 'tab':
                    expectation = "\n\
                    public {0} click{1}() {\n\
                        wait.until(ExpectedConditions.visibilityOfElementLocated({2})).click();\n\
                        \n\
                        return this;\n\
                    }";
                    break;
                case 'tool':
                    result.push('// TODO: fix view in row selector');
                    result.push(data.declaration ? data.declaration.replace('By element ', Ext.String.format('By {0}ViewInRow ', data.name)).trim() : '');
                    expectation = "\n\
                    public {0} click{1}(Boolean inViewInRow) {\n\
                        wait.until(ExpectedConditions.visibilityOfElementLocated(inViewInRow ? {2}ViewInRow : {2})).click();\n\
                        \n\
                        return this;\n\
                    }\n\
                    public Boolean is{1}Visible(Boolean inViewInRow) {\n\
                        return wait.until(ExpectedConditions.visibilityOfElementLocated(\n\
                            inViewInRow ? {2}ViewInRow : {2})).isDisplayed();\n\
                    }\n\
                    \n\
                    public Boolean is{1}Invisible(Boolean inViewInRow) {\n\
                        return !wait.until(ExpectedConditions.presenceOfElementLocated(\n\
                            inViewInRow ? {2}ViewInRow : {2})).isDisplayed();\n\
                    }";
                    break;
                default:
                    break;
            }
            result.push("");
            result.push("");
            result.push("// expectation\n");
            result.push(Ext.String.format(expectation, data.classname, Ext.String.capitalize(data.name), data.name));
            result.push('');
            return result.join('\n');
        }
    }],

    proxy: {
        type: 'memory'
    }
});

Ext.define('CMDBuildUI.util.administration.helper.TestHelper', {
    singleton: true,
    isInspectOpen: function () {

    },
    testId: function (testid) {
        var setText = function (element, text) {
            return Ext.String.format('"{0}"', findUpTestId(element, text));

        };
        var findUpTestId = function (el, testidSelector) {
            while (el.parentNode) {
                el = el.parentNode;
                if (el.dataset && el.dataset.testid)
                    return findUpTestId(el, Ext.String.format('{0} {1}', formatTestid(el.dataset.testid), testidSelector));
            }
            return testidSelector === '""' ? '' : testidSelector;
        };
        var formatTestid = function (testId) {
            return Ext.String.format('[data-testid=\\"{0}\\"]', testId);
        };

        var me = CMDBuildUI.util.administration.helper.TestHelper;
        var dom = document.querySelectorAll('[data-testid]');
        if (!document.hasCopyTestIdEvent) {
            document.addEventListener('keydown', function (event) {

                if (event.ctrlKey && event.keyCode == 67) {
                    event.preventDefault();
                    event.stopPropagation();
                    me.textEl.select();
                    me.textEl.setSelectionRange(0, 99999);
                    document.execCommand("copy");
                }

            });
            document.hasCopyTestIdEvent = true;
        }

        me.testIdContainer = Ext.dom.Query.select('[data-testid="header-instancename"]')[0].nextElementSibling;

        if (!me.textEl) {
            me.textEl = document.createElement('input');
            me.textEl.style.width = "100%";
            me.textEl.style.fontSize = "20px";
            me.textEl.placeholder = 'testid selector, press CTRL+c to copy to clipboard';
            me.testIdContainer.style.top = "0px";

            me.testIdContainer.append(me.textEl);
        }

        dom.forEach(function (element) {
            if (!element.hasMouseoverListener) {
                element.style.border = "2px dotted #000";
                element.style.boxSizing = "border-box";

                var testId = element.dataset.testid;
                var onmouseenter = function (e) {

                        var testidSelector = formatTestid(testId);
                        var textElValue = setText(element, testidSelector);
                        me.textEl.value = Ext.String.format('   private By element = By.cssSelector({0});', textElValue);
                        CMDBuildUI.util.Logger.log(me.textEl.value, CMDBuildUI.util.Logger.levels.debug);
                        if (me.textEl.value.length) {
                            me.textEl.scrollLeft = me.textEl.scrollWidth;
                        }
                        e.preventDefault();
                        e.stopPropagation();
                    },
                    onmouseclick = function (e) {
                        if (e.ctrlKey) {
                            CMDBuildUI.util.Utilities.openPopup(
                                'test_pop' + new Date().getMilliseconds(),
                                CMDBuildUI.locales.Locales.bulkactions.edit, {
                                    xtype: 'form',
                                    layout: 'column',
                                    viewModel: {
                                        links: {
                                            element: {
                                                type: 'CMDBuildUI.model.TestType',
                                                create: {
                                                    classname: localStorage.getItem('test-classname'),
                                                    declaration: me.textEl.value
                                                }
                                            }
                                        },

                                        formulas: {
                                            poClassNameManager: {
                                                bind: '{element.classname}',
                                                get: function (classname) {
                                                    localStorage.setItem('test-classname', classname);
                                                    return classname;
                                                }
                                            }
                                        }
                                    },
                                    items: [{
                                        columnWidth: 0.5,
                                        layout: 'fit',
                                        height: "100%",
                                        xtype: 'container',
                                        items: [{
                                            xtype: 'textfield',
                                            fieldLabel: 'Page object name',
                                            bind: {
                                                value: '{element.classname}'
                                            }
                                        }, {
                                            xtype: 'textfield',

                                            fieldLabel: 'Element name',
                                            bind: {
                                                value: '{element.name}'
                                            }
                                        }, {
                                            xtype: 'combobox',
                                            store: {
                                                fields: ["value", "label"],
                                                data: [{
                                                    label: 'Checkbox',
                                                    value: 'checkbox',
                                                    type: 'formfield'
                                                }, {
                                                    label: 'Combobox',
                                                    value: 'combobox',
                                                    type: 'formfield'
                                                }, {
                                                    label: 'Displayfield',
                                                    value: 'displayfield',
                                                    type: 'formfield'
                                                }, {
                                                    label: 'Textfield',
                                                    value: 'textfield',
                                                    type: 'formfield'
                                                }, {
                                                    label: 'Textarea',
                                                    value: 'textarea',
                                                    type: 'formfield'
                                                }, {
                                                    label: 'Tab',
                                                    value: 'tab',
                                                    type: 'panel'
                                                }, {
                                                    label: 'Tool',
                                                    value: 'tool',
                                                    type: 'panel'
                                                }]
                                            },
                                            fieldLabel: 'Element type',
                                            displayField: 'label',
                                            valueField: 'value',
                                            bind: {
                                                value: '{element.type}'
                                            }
                                        }]

                                    }, {
                                        columnWidth: 0.5,
                                        layout: 'fit',
                                        height: "100%",
                                        xtype: 'container',
                                        items: [{
                                            xtype: 'textarea',
                                            width: '100%',
                                            height: '100%',
                                            bind: {
                                                value: '{element.result}'
                                            }
                                        }]
                                    }],

                                    closePopup: function () {
                                        popup.destroy();
                                    }
                                }
                            );
                            e.preventDefault();
                            e.stopPropagation();
                        }
                    };
                element.addEventListener('mouseover', onmouseenter);
                element.addEventListener('click', onmouseclick);
                element.hasMouseoverListener = true;
            }
        });

        setTimeout(me.testId, 1000, [testid]);
    }
});
if(Ext.isDebugEnabled()){
    document.addEventListener('keydown', function (event) {
        if (event.ctrlKey && event.shiftKey && event.altKey && event.keyCode == 67) {
            CMDBuildUI.util.administration.helper.TestHelper.testId();
        }
    });
}
