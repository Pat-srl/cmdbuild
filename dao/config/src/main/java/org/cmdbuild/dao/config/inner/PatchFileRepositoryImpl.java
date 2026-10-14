package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.io.File;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.config.api.DirectoryService;
import static org.cmdbuild.dao.config.utils.PatchManagerUtils.buildPatchFileDirs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class PatchFileRepositoryImpl implements PatchFileRepository {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<Pair<File, String>> dirs;

	@Autowired
	public PatchFileRepositoryImpl(DirectoryService directoryService) {
		this(buildDirs(directoryService));
	}

	public PatchFileRepositoryImpl(File... dirs) {
		this(list(dirs).stream().map(f -> Pair.of(f, (String) null)).collect(toList()));
	}

	public PatchFileRepositoryImpl(List<Pair<File, String>> dirs) {
		this.dirs = ImmutableList.copyOf(dirs);
	}

	public PatchFileRepositoryImpl(File dir, @Nullable String category) {
		this(singletonList(Pair.of(checkNotNull(dir), category)));
	}

	@Override
	public Collection<PatchFile> getPatchFiles() {
		return dirs.stream().flatMap(p -> getPatchFiles(p.getLeft(), p.getRight())).collect(toImmutableList());
	}

	private Stream<PatchFile> getPatchFiles(File dir, @Nullable String category) {
		if (!(dir.exists() && dir.isDirectory() && dir.canRead())) {
			logger.warn(marker(), "unable to access patch dir = {}", dir.getAbsolutePath());
			return Stream.empty();
		} else {
			return FileUtils.listFiles(dir, new String[]{"sql"}, true).stream().map(f -> new PatchFileImpl(f, category));
		}
	}

	private static List<Pair<File, String>> buildDirs(DirectoryService directoryService) {
		if (directoryService.hasWebappDirectory()) {
			return buildPatchFileDirs(new File(directoryService.getWebappDirectory(), "WEB-INF/sql"));
		} else {
			return emptyList();
		}
	}

	private final static class PatchFileImpl implements PatchFile {

		private final File file;
		private final String category;

		public PatchFileImpl(File file, String category) {
			this.file = checkNotNull(file);
			this.category = category;
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		@Nullable
		public String getCategory() {
			return category;
		}

	}

}
