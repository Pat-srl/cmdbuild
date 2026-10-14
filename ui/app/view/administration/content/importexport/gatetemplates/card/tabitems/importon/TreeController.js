Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.importon.TreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexport-gatetemplates-card-tabitems-importon-tree',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        }
    },

    onAfterRender: function (treepanel) {
        var vm = treepanel.lookupViewModel();
        var theGate = vm.get('theGate');
        CMDBuildUI.model.classes.Class.load(CMDBuildUI.util.helper.ModelHelper.systemClasses.class, {
            success: function (model) {
                var tree = [];
                tree = model.getChildrenAsTree(
                    true,
                    function (item) {
                        item.set('enabled', theGate.get('importOn').indexOf(item.get('name')) !== -1);
                        return item;
                    },
                    false
                );
                // generate the destination tree
                var destinationRoot = {
                    expanded: true,
                    text: model.get('description'),
                    name: model.get('name'),
                    leaf: tree.length > 1 ? false : true,
                    children: tree.length > 1 ? tree : false,
                    enabled: false
                };
                treepanel.getStore().setRoot(destinationRoot);
            }
        });
    }
});
