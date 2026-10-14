
function convertValue(value) {
    if (value && typeof value === 'object') {
        if (Array.isArray(value)) {
            value = value.join(",");
        } else {
            value = value["Id"];
        }
    }
    return value;
}

function evalJsSubcontext(context, subcontextExpr, expr) {
    context = subcontextExpr ? (context[subcontextExpr] || {}) : context;
    while (true) {
        if (context[expr]) {
            return convertValue(context[expr]);
        } else if (expr === 'Id' && typeof context !== 'object' && !isNaN(context)) {
            return context;
        } else if (expr && expr.match(/^[^.]+[.][^.]+.*$/)) {
            var part = expr.replace(/^([^.]+)[.](.+)$/, '$1');
            expr = expr.replace(/^([^.]+)[.](.+)$/, '$2');
            context = context[part] || {};
        } else {
            return null;
        }
    }
}