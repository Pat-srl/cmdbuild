/**
 * @file CMDBuildUI.util.helper.MapHelper
 * @module CMDBuildUI.util.helper.MapHelper
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.util.helper.MapHelper', {
    singleton: true,

    /**
     * Pattern definitions
     */
    patternDefaultColor: '#00000099',
    patternRegistry: {
        diag45: {
            opts: {
                spacing: 8,
                lineWidth: 1,
                angle: 45
            },

            drawTile(ctx, opts) {
                const { color, spacing, lineWidth } = opts;

                ctx.clearRect(0, 0, spacing, spacing);
                ctx.save();
                ctx.strokeStyle = color;
                ctx.lineWidth = lineWidth;

                ctx.beginPath();

                ctx.moveTo(0, spacing);
                ctx.lineTo(spacing, 0);

                ctx.moveTo(-spacing, spacing);
                ctx.lineTo(0, 0);

                ctx.moveTo(spacing, spacing * 2);
                ctx.lineTo(spacing * 2, spacing);

                ctx.stroke();
                ctx.restore();
            }
        },

        crosshatch45: {
            opts: {
                spacing: 8,
                lineWidth: 1,
                angle: 45
            },

            drawTile(ctx, opts) {
                const { color, spacing, lineWidth } = opts;

                ctx.clearRect(0, 0, spacing, spacing);
                ctx.save();
                ctx.strokeStyle = color;
                ctx.lineWidth = lineWidth;

                ctx.beginPath();

                // +45°
                ctx.moveTo(0, spacing);
                ctx.lineTo(spacing, 0);

                ctx.moveTo(-spacing, spacing);
                ctx.lineTo(0, 0);

                ctx.moveTo(spacing, spacing * 2);
                ctx.lineTo(spacing * 2, spacing);

                // -45°
                ctx.moveTo(0, 0);
                ctx.lineTo(spacing, spacing);

                ctx.moveTo(-spacing, 0);
                ctx.lineTo(0, spacing);

                ctx.moveTo(spacing, spacing);
                ctx.lineTo(spacing * 2, spacing * 2);

                ctx.stroke();
                ctx.restore();
            }
        },

        horizontal: {
            opts: {
                spacing: 6,
                lineWidth: 1
            },

            drawTile(ctx, opts) {
                const { color, spacing, lineWidth } = opts;
                const y = lineWidth / 2;

                ctx.clearRect(0, 0, spacing, spacing);
                ctx.save();
                ctx.strokeStyle = color;
                ctx.lineWidth = lineWidth;

                ctx.beginPath();
                ctx.moveTo(0, y);
                ctx.lineTo(spacing, y);
                ctx.stroke();

                ctx.restore();
            }
        },

        vertical: {
            opts: {
                spacing: 6,
                lineWidth: 1
            },

            drawTile(ctx, opts) {
                const { color, spacing, lineWidth } = opts;
                const x = lineWidth / 2;

                ctx.clearRect(0, 0, spacing, spacing);
                ctx.save();
                ctx.strokeStyle = color;
                ctx.lineWidth = lineWidth;

                ctx.beginPath();
                ctx.moveTo(x, 0);
                ctx.lineTo(x, spacing);
                ctx.stroke();

                ctx.restore();
            }
        },

        grid: {
            opts: {
                spacing: 8,
                lineWidth: 1
            },

            drawTile(ctx, opts) {
                const { color, spacing, lineWidth } = opts;
                const o = lineWidth / 2;

                ctx.clearRect(0, 0, spacing, spacing);
                ctx.save();
                ctx.strokeStyle = color;
                ctx.lineWidth = lineWidth;

                ctx.beginPath();

                // top
                ctx.moveTo(0, o);
                ctx.lineTo(spacing, o);

                // bottom
                ctx.moveTo(0, spacing - o);
                ctx.lineTo(spacing, spacing - o);

                // left
                ctx.moveTo(o, 0);
                ctx.lineTo(o, spacing);

                // right
                ctx.moveTo(spacing - o, 0);
                ctx.lineTo(spacing - o, spacing);

                ctx.stroke();
                ctx.restore();
            }
        },

        dots: {
            opts: {
                spacing: 8,
                lineWidth: 2
            },

            drawTile(ctx, opts) {
                const { color, spacing, lineWidth } = opts;
                const r = lineWidth / 2;

                ctx.clearRect(0, 0, spacing, spacing);
                ctx.save();
                ctx.fillStyle = color;

                ctx.beginPath();
                ctx.arc(spacing / 2, spacing / 2, r, 0, Math.PI * 2);
                ctx.fill();

                ctx.restore();
            }
        },

        brick: {
            opts: {
                spacing: 10,
                lineWidth: 1,
                tileWidth: 10 * 2.5, // Spacing multiplied by costant
                tileHeight: 10 * 2
            },
            getTileSize(opts) {
                const brickH = opts.spacing;
                const brickW = opts.spacing * 2.5;
                return { width: brickW, height: brickH * 2 };
            },
            drawTile(ctx, opts) {
                const { color, spacing, lineWidth, tileWidth, tileHeight } = opts;
                const brickH = spacing;
                const brickW = tileWidth;
                const totalH = tileHeight;

                ctx.clearRect(0, 0, brickW, totalH);
                ctx.save();
                ctx.strokeStyle = color;
                ctx.lineWidth = lineWidth;
                ctx.lineCap = 'square'; // Crisp corners

                ctx.beginPath();

                // Horizontal Lines (Mortar joints)
                // Top
                ctx.moveTo(0, 0);
                ctx.lineTo(brickW, 0);
                // Middle
                ctx.moveTo(0, brickH);
                ctx.lineTo(brickW, brickH);
                // Bottom (optional if tile repeats, but good for edge)
                // patterns tile, so y=0 of next tile covers y=totalH of this one.
                // usually better to draw top-left lines.

                // Vertical Lines (Head joints)
                // Row 1: Line at x=0
                ctx.moveTo(0, 0);
                ctx.lineTo(0, brickH);

                // Row 2: Line at x=brickW/2 (staggered)
                ctx.moveTo(brickW / 2, brickH);
                ctx.lineTo(brickW / 2, totalH);

                ctx.stroke();
                ctx.restore();
            }
        },

        zigzag: {
            opts: {
                spacing: 12,
                lineWidth: 1
            },

            drawTile(ctx, opts) {
                const { color, spacing, lineWidth } = opts;

                ctx.clearRect(0, 0, spacing, spacing);
                ctx.save();
                ctx.strokeStyle = color;
                ctx.lineWidth = lineWidth;

                ctx.beginPath();
                ctx.moveTo(0, spacing / 2);
                ctx.lineTo(spacing / 2, 0);
                ctx.lineTo(spacing, spacing / 2);
                ctx.lineTo(spacing / 2, spacing);
                ctx.closePath();
                ctx.stroke();

                ctx.restore();
            }
        },

        triangles: {
            opts: {
                spacing: 12,
                lineWidth: 1
            },

            drawTile(ctx, opts) {
                const { color, spacing, lineWidth } = opts;

                ctx.clearRect(0, 0, spacing, spacing);
                ctx.save();
                ctx.strokeStyle = color;
                ctx.lineWidth = lineWidth;

                ctx.beginPath();
                ctx.moveTo(0, spacing);
                ctx.lineTo(spacing / 2, 0);
                ctx.lineTo(spacing, spacing);
                ctx.closePath();
                ctx.stroke();

                ctx.restore();
            }
        },

        hex: {
            opts: {
                spacing: 12,
                lineWidth: 1
            },

            drawTile(ctx, opts) {
                const { color, spacing, lineWidth } = opts;

                ctx.clearRect(0, 0, spacing, spacing);
                ctx.save();
                ctx.strokeStyle = color;
                ctx.lineWidth = lineWidth;

                const r = spacing / 2.5;
                const cx = spacing / 2;
                const cy = spacing / 2;

                ctx.beginPath();
                for (let i = 0; i < 6; i++) {
                    const a = (Math.PI / 3) * i - Math.PI / 6;
                    const x = cx + r * Math.cos(a);
                    const y = cy + r * Math.sin(a);
                    i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
                }
                ctx.closePath();
                ctx.stroke();

                ctx.restore();
            }
        }
    },

    /**
     * Returns the GIS layers menu configuration.
     *
     * @returns {Ext.promise.Promise<Ext.data.TreeStore>}
     *
     * @example
     * CMDBuildUI.util.helper.MapHelper.getLayersMenu().then(function (tree) {
     *      // Do something
     * });
     */
    getLayersMenu: function () {
        const deferred = new Ext.Deferred();
        const me = this;

        if (!me._layersmenu) {
            me._layersmenu = Ext.create('Ext.data.TreeStore', {
                model: 'CMDBuildUI.model.menu.MenuItem',
                proxy: {
                    type: 'baseproxy',
                    url: '/menu'
                },
                root: {
                    expanded: false // to make it working with autoLoad=false
                },
                defaultRootProperty: 'data',
                defaultRootId: 'gismenu',
                sorters: ['index'],
                autoLoad: false
            });
            me._layersmenu.load({
                callback: function (records, operation, success) {
                    deferred.resolve(me._layersmenu);
                }
            });
        } else {
            deferred.resolve(me._layersmenu);
        }

        return deferred.promise;
    },

    /**
     * Get pattern function to render on canvas
     * @param {String} pattern
     */
    getPatternRenderer: function (pattern) {
        const def = this.patternRegistry[pattern.id];
        if (!def) {
            CMDBuildUI.util.Logger.log(
                `Pattern ${pattern.id} not defined in patternRegistry`,
                CMDBuildUI.util.Logger.levels.warn
            );
            return null;
        }
        return {
            id: pattern.id,
            opts: Ext.clone(def.opts),
            drawTile: def.drawTile
        };
    }
});
