Ext.define('Override.data.schema.Namer', {
    override: 'Ext.data.schema.Namer',

    manyToOne: function (leftType, leftRole, rightType, rightRole) {
        // ex: OrderItem -> Order  ==> OrderOrderItems
        //  Ticket (creator) -> User ==> UserCreatorTickets
        return this.apply('capitalize', rightType) + this.apply('capitalize', leftRole);
    },
});
