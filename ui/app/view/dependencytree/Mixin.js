Ext.define('CMDBuildUI.view.dependencytree.Mixin', {
    mixinId: 'dependencytree-mixin',

    statics: {
        default: 'default',
        static: 'static',
        textColor: 'text_color',
        iconColor: 'icon_color',
        iconFont: 'icon_font'
    },

    /**
     *
     * @param {String} text
     * @param {Number} maxWidth
     */
    wrapCentered: function (text, maxWidth) {
        text.each(function () {
            const text = d3.select(this);
            const words = text.text().split(/\s+/).reverse();

            const x = +text.attr('x');
            const y = +text.attr('y');

            const lineHeight = 1.1; // em

            let line = [];
            const lines = [];
            let word;

            text.text(null);
            const temp = text.append('tspan').attr('x', x).attr('y', y);

            const splitLongWord = function (word, temp, maxWidth) {
                const chunks = [];
                let current = '';

                for (const ch of word) {
                    temp.text(current + ch);

                    if (temp.node().getComputedTextLength() > maxWidth) {
                        chunks.push(current);
                        current = ch;
                    } else {
                        current += ch;
                    }
                }

                if (current) {
                    chunks.push(current);
                }

                return chunks;
            };

            while ((word = words.pop())) {
                temp.text(word);

                if (temp.node().getComputedTextLength() > maxWidth) {
                    const chunks = splitLongWord(word, temp, maxWidth);

                    chunks.forEach(chunk => {
                        lines.push(chunk);
                    });

                    line = [];
                    continue;
                }

                line.push(word);
                temp.text(line.join(' '));

                if (temp.node().getComputedTextLength() > maxWidth) {
                    line.pop();
                    lines.push(line.join(' '));
                    line = [word];
                }
            }

            lines.push(line.join(' '));
            temp.remove();

            const offset = -((lines.length - 1) * lineHeight) / 2;
            lines.forEach((lineText, i) => {
                text.append('tspan')
                    .attr('x', x)
                    .attr('dy', i === 0 ? `${offset}em` : `${lineHeight}em`)
                    .text(lineText);
            });
        });
    },

    /**
     *
     * @returns {String}
     */
    getBaseColor: function () {
        return '#bbb';
    }
});
