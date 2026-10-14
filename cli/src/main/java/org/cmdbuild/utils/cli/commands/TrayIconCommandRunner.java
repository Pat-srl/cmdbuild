/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkNotNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.utils.cli.TrayIconApp;

public class TrayIconCommandRunner extends AbstractCommandRunner {

	public TrayIconCommandRunner() {
		super("trayicon", "start a tray icon to monitor status of an existing cmdbuild instance");
	}

	@Override
	protected Options buildOptions() {
		Options options = super.buildOptions();
		options.addOption("url", true, "set cmdbuild root url (es: http://localhost/cmdbuild/)");
		return options;
	}

	@Override
	protected void exec(CommandLine cmd) throws Exception {
		String cmdbuildUrl = checkNotNull(trimToNull(cmd.getOptionValue("url")), "must set url param");
		new TrayIconApp(cmdbuildUrl).runAndJoin();
	}

}
