Ext.define('CMDBuildUI.view.fields.schedulerdate.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-schedulerdate-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#openBtnEvent': {
            click: 'onOpenBtnClick'
        },
        '#editBtnEvent': {
            click: 'onEditButton'
        },
        '#deleteBtnEvent': {
            click: 'onDeleteBtn'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        vm.bind('{record}',
            function (record) {
                vm.set("theEvent", record);
                view.generateForm();
            }
        );
    },

    /**
    * Triggered on open tool click.
    * 
    * @param {Ext.panel.Tool} tool
    * @param {Ext.Event} event
    * @param {Object} eOpts
    */
    onOpenBtnClick: function (tool, event, eOpts) {
        const me = this;
        const popup = CMDBuildUI.util.Utilities.openPopup(null, CMDBuildUI.locales.Locales.calendar.viewevent, {
            xtype: 'events-event-view',
            shownInPopup: true,
            viewModel: {
                data: {
                    record: this.getViewModel().get("theEvent")
                }
            },
            controller: 'fields-schedulerdate-view',
            listeners: {
                // the scope is the events-event-view view opened in the popup
                deletebutton: function (tool, event, eOpts) {
                    me.onDeleteBtn(tool, event, eOpts);
                    popup.close();
                }
            }
        });
    },

    /**
     * Triggered on edit tool click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onEditButton: function (tool, event, eOpts) {
        const me = this,
            theEvent = this.getViewModel().get("theEvent"),
            theEventClone = theEvent.copy(null);

        const popup = CMDBuildUI.util.Utilities.openPopup('editEventPopup', CMDBuildUI.locales.Locales.calendar.editevent, {
            xtype: 'events-event-edit',
            hideTools: true,
            controller: 'fields-schedulerdate-edit',
            viewModel: {
                data: {
                    theEvent: theEventClone
                }
            },
            listeners: {
                popupsave: function (view, theEventClone) {
                    this.getController().savePopup(theEvent);
                    me.onOpenBtnClick();
                    popup.close();
                },
                popupsaveandclose: function (view, theEventClone) {
                    this.getController().savePopup(theEvent);
                    popup.close();
                },
                popupclose: function (view, theEventClone) {
                    popup.close();
                }
            }
        });
    },

    /**
    * Triggered on open tool click.
    * 
    * @param {Ext.panel.Tool} tool
    * @param {Ext.Event} event
    * @param {Object} eOpts
    */
    onDeleteBtn: function (tool, event, eOpts) {
        const theEvent = this.getViewModel().get("theEvent"),
            theSequence = theEvent.store.associatedEntity;

        //field like dirty. This information tells that there are removed records.
        theSequence._dirty_delete = true;

        theEvent.drop()
    }
});
