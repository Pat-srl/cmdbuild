Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.CardMixin'],
    requires: ['CMDBuildUI.util.administration.helper.ConfigHelper'],
    alias: 'controller.administration-content-importexport-gatetemplates-tabitems-templates-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {             
        var vm = this.getViewModel();
        var selected = view._rowContext.record;        
        Ext.asap(function(){            
            try {
                view.mask(CMDBuildUI.locales.Locales.administration.common.messages.loading);
            } catch (error) {
                CMDBuildUI.util.Logger.log("unable to mask lookup value forminrow", CMDBuildUI.util.Logger.levels.debug);
            }
        });

        vm.linkTo('theGateTemplate', {
            type: 'CMDBuildUI.model.importexports.GateTemplate',
            id: selected.get('_id')
        });
        vm.bind({
            bindTo: {
                theGateTemplate: '{theGateTemplate}'
            }
        }, function (data) {
           
        });
        
         
    },

    onAfterRender: function(view){
        var vm = this.getViewModel();
        vm.bind({
            bindTo: {
                theGateTemplate: '{theGateTemplate}'
            }
        }, function (data) {
            if (data.theGateTemplate) {  
                       
                Ext.asap(function(){
                    try {
                        view.unmask();
                    } catch (error) {
                        CMDBuildUI.util.Logger.log("unable to unmask view in row", CMDBuildUI.util.Logger.levels.debug);
                    }
                });
                view.setActiveTab(0);
            }
        });
    }
});