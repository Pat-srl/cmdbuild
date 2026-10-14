Ext.define('CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-widgets-systemstatus-nodestats',

    formulas: {
        currentNode: {
            bind: {
                node: '{nodeId}',
                clusterNodes: '{clusterNodes}',
                store: '{systemStatusGridStore}'
            },
            get: function (data) {
                return data.store ? data.store.query('hostname', data.node) : null;
            }
        },
        dynHtml: {
            bind: {
                records: '{currentNode}'
            },
            get: function (data) {
                var rows = [];
                if (data.records) {
                    var keys = CMDBuildUI.view.administration.home.widgets.systemstatus.SystemStatuses.statskeys;

                    // title
                    rows.push('<h3>' + CMDBuildUI.locales.Locales.administration.home.nodestats.title + '</h3>');

                    // version
                    var version = data.records.find('key', keys.version);
                    var build = data.records.find('key', keys.build);
                    if (version && build) {
                        var version_value = version.get('value');
                        var build_value = build.get('value');
                        rows.push(Ext.String.format(
                            '<div class="admin-home-stats-row"><small>{0} [{1}]</small></div>',
                            version_value,
                            build_value
                        ));
                    }

                    // server time
                    var stime = data.records.find('key', keys.stime);
                    var stimezone = data.records.find('key', keys.stimezone);
                    if (stime && stimezone) {
                        var stime_value = new Date(stime.get('value'));
                        var stimezone_value = stimezone.get('value');
                        rows.push(Ext.String.format(
                            '<div class="admin-home-stats-row"><strong>{0}:</strong> {1} ({2})</div>',
                            CMDBuildUI.locales.Locales.administration.home.nodestats.servertime,
                            stime_value.toLocaleString({ timeZone: stimezone_value }),
                            stimezone_value
                        ));
                    }

                    // uptime
                    var uptime = data.records.find('key', keys.uptime);
                    if (uptime) {
                        rows.push(Ext.String.format(
                            '<div class="admin-home-stats-row"><strong>{0}:</strong> {1}</div>',
                            CMDBuildUI.locales.Locales.administration.home.nodestats.uptime,
                            CMDBuildUI.util.Utilities.intervalToHumanReadable(uptime.get('value'))
                        ));
                    }

                    // disk
                    var t_disk = data.records.find('key', keys.disktotal);
                    var u_disk = data.records.find('key', keys.diskused);
                    if (t_disk && u_disk) {
                        var t_disk_value = parseInt(t_disk.get('value'));
                        var u_disk_value = parseInt(u_disk.get('value'));
                        var p_disk_value = u_disk_value / t_disk_value * 100;
                        var slider_cls = p_disk_value > 90 ? 'danger' : p_disk_value > 75 ? 'warning' : '';
                        rows.push(Ext.String.format(
                            '<div class="admin-home-stats-row"><div><strong>' +
                            CMDBuildUI.locales.Locales.administration.home.nodestats.hd +
                            ':</strong> ' + CMDBuildUI.locales.Locales.administration.home.nodestats.usage +
                            '</div><div class="slider {3}"><div style="width: {4}%;"></div></div></div>',
                            Ext.Number.toFixed(p_disk_value, 2),
                            Ext.util.Format.fileSize(u_disk_value * 1024 * 1024),
                            Ext.util.Format.fileSize(t_disk_value * 1024 * 1024),
                            slider_cls,
                            p_disk_value
                        ));
                    }

                    // java memory
                    var t_jmem = data.records.find('key', keys.jmemtotal);
                    var u_jmem = data.records.find('key', keys.jmemused);
                    if (t_jmem && u_jmem) {
                        var t_jmem_value = parseInt(t_jmem.get('value'));
                        var u_jmem_value = parseInt(u_jmem.get('value'));
                        var p_jmem_value = u_jmem_value / t_jmem_value * 100;
                        rows.push(Ext.String.format(
                            '<div class="admin-home-stats-row"><div><strong>' +
                            CMDBuildUI.locales.Locales.administration.home.nodestats.jmem +
                            ':</strong> ' + CMDBuildUI.locales.Locales.administration.home.nodestats.usage +
                            '</div><div class="slider"><div style="width: {3}%;"></div></div></div>',
                            Ext.Number.toFixed(p_jmem_value, 2),
                            Ext.util.Format.fileSize(u_jmem_value * 1024 * 1024),
                            Ext.util.Format.fileSize(t_jmem_value * 1024 * 1024),
                            p_jmem_value
                        ));
                    }

                    // system memory
                    var t_smem = data.records.find('key', keys.smemtotal);
                    var u_smem = data.records.find('key', keys.smemused);
                    if (t_smem && u_smem) {
                        var t_smem_value = parseInt(t_smem.get('value'));
                        var u_smem_value = parseInt(u_smem.get('value'));
                        var p_smem_value = u_smem_value / t_smem_value * 100;
                        rows.push(Ext.String.format(
                            '<div class="admin-home-stats-row"><div><strong>' +
                            CMDBuildUI.locales.Locales.administration.home.nodestats.smem +
                            ':</strong> ' + CMDBuildUI.locales.Locales.administration.home.nodestats.usage +
                            '</div><div class="slider"><div style="width: {3}%;"></div></div></div>',
                            Ext.Number.toFixed(p_smem_value, 2),
                            Ext.util.Format.fileSize(u_smem_value * 1024 * 1024),
                            Ext.util.Format.fileSize(t_smem_value * 1024 * 1024),
                            p_smem_value
                        ));
                    }

                    // system load
                    var sload = data.records.find('key', keys.sload);
                    if (sload) {
                        var sload_value = parseFloat(sload.get('value'));
                        rows.push(Ext.String.format(
                            '<div class="admin-home-stats-row"><div><strong>' +
                            CMDBuildUI.locales.Locales.administration.home.nodestats.systemload +
                            ':</strong> {0}</div></div>',
                            Ext.Number.toFixed(sload_value, 2)
                        ));
                    }

                    // db connections
                    var a_conn = data.records.find('key', keys.dbconnactive);
                    var m_conn = data.records.find('key', keys.dbconnmax);
                    if (a_conn && m_conn) {
                        var m_conn_value = parseInt(m_conn.get('value'));
                        var a_conn_value = parseInt(a_conn.get('value'));
                        var p_conn_value = a_conn_value / m_conn_value * 100;
                        rows.push(Ext.String.format(
                            '<div class="admin-home-stats-row"><div><strong>' +
                            CMDBuildUI.locales.Locales.administration.home.nodestats.dbconnections +
                            ':</strong> {0}/{1}' +
                            '</div><div class="slider"><div style="width: {2}%;"></div></div></div>',
                            a_conn_value,
                            m_conn_value,
                            p_conn_value
                        ));
                    }

                    // db idle connections
                    var i_conn = data.records.find('key', keys.dbconnidle);
                    var mi_conn = data.records.find('key', keys.dbconnidlemax);
                    if (i_conn && mi_conn) {
                        var mi_conn_value = parseInt(mi_conn.get('value'));
                        var i_conn_value = parseInt(i_conn.get('value'));
                        var p_conn_value = i_conn_value / mi_conn_value * 100;
                        rows.push(Ext.String.format(
                            '<div class="admin-home-stats-row"><div><strong>' +
                            CMDBuildUI.locales.Locales.administration.home.nodestats.dbidleconnections +
                            ':</strong> {0}/{1}' +
                            '</div><div class="slider"><div style="width: {2}%;"></div></div></div>',
                            i_conn_value,
                            mi_conn_value,
                            p_conn_value
                        ));
                    }
                }
                return rows.join('');
            }
        }
    }

});
