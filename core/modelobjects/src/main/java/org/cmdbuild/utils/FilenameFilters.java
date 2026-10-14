package org.cmdbuild.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FilenameFilters {

	private static class All implements FilenameFilter {

		@Override
		public boolean accept(final File dir, final String name) {
			return true;
		}

	}

	private static final All ALL = new All();

	private static class PatternFilenameFilters implements FilenameFilter {

		final String pattern;

		public PatternFilenameFilters(final String pattern) {
			this.pattern = pattern;
		}

		@Override
		public boolean accept(final File dir, final String name) {
			return name.matches(pattern);
		}

	}

	public static final FilenameFilter all() {
		return ALL;
	}

	public static final FilenameFilter pattern(final String value) {
		return (value == null) ? all() : new PatternFilenameFilters(value);
	}

	private FilenameFilters() {
		// prevents instantiation
	}

}
