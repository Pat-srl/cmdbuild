/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload.ftp;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Math.toIntExact;
import static java.util.Collections.synchronizedSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.cmdbuild.auth.login.LoginUserIdentity;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ALL;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.config.FtpServiceConfiguration;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadItemInfo;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.easyupload.EasyuploadUtils;
import static org.cmdbuild.easyupload.EasyuploadUtils.normalizePath;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.cmdbuild.auth.user.PasswordSupplier;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;

@Component
public class EasyuploadFtpService implements MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EasyuploadService easyuploadService;
    private final UserRoleService userRepository;
    private final PasswordSupplier passwordSupplier;
    private final FtpServiceConfiguration config;

    private final MinionHandler minionHandler;

    private final Set<String> fakeDirs = synchronizedSet(set());//TODO improve this, handle cluster

    private FtpServer server;

    public EasyuploadFtpService(EasyuploadService easyuploadService, UserRoleService userRepository, PasswordSupplier passwordSupplier, FtpServiceConfiguration configuration) {
        this.easyuploadService = checkNotNull(easyuploadService);
        this.userRepository = checkNotNull(userRepository);
        this.passwordSupplier = checkNotNull(passwordSupplier);
        this.config = checkNotNull(configuration);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("FTP Service")
                .withEnabledChecker(config::isEnabled)
                .withStatusChecker(() -> server != null ? MRS_READY : MRS_NOTRUNNING)
                .reloadOnConfigs(FtpServiceConfiguration.class)
                .withHidden(true)
                .build();
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void start() {
        try {
            logger.info("start ftp service");
            FtpServerFactory serverFactory = new FtpServerFactory();
            ListenerFactory listener = new ListenerFactory();

            listener.setPort(config.getPort());
            serverFactory.addListener("default", listener.createListener());

            PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
            UserManager userManager = userManagerFactory.createUserManager();

            userRepository.getAllGroups().stream().filter(g -> g.hasPrivileges(RP_ADMIN_ALL)).flatMap(g -> userRepository.getAllWithRole(g.getId()).stream().filter(UserData::isActive).map(UserData::getUsername)).distinct().forEach(rethrowConsumer(username -> {//TODO improve this, do not load all users at startup

                String password = passwordSupplier.getUnencryptedPasswordOrNull(LoginUserIdentity.build(username));
                if (isNotBlank(password)) {//TODO improve this
                    BaseUser user = new BaseUser();
                    user.setName(username);
                    user.setPassword(password);
                    user.setHomeDirectory("/");

//        List<Authority> authorities = new ArrayList<Authority>();
//        authorities.add(new WritePermission());
//        user.setAuthorities(authorities);
                    userManager.save(user);
                }
            }));

            serverFactory.setUserManager(userManager);

            serverFactory.setFileSystem(new EasyuploadFileSystemFactory());

            server = serverFactory.createServer();
            server.start();
        } catch (FtpException ex) {
            stop();
            throw runtime(ex);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            try {
                logger.info("stop ftp service");
                server.stop();
            } catch (Exception ex) {
                logger.warn("error stopping ftp server", ex);
            }
            server = null;
        }
    }

    private class EasyuploadFileSystemFactory implements FileSystemFactory {

        @Override
        public FileSystemView createFileSystemView(User user) throws FtpException {
            return new EasyuploadFileSystemView();
        }
    }

    private class EasyuploadFileSystemView implements FileSystemView {

        private String currDir = "/";

        @Override
        public FtpFile getHomeDirectory() {
            return new EasyuploadFtpDirectory("/");
        }

        @Override
        public FtpFile getWorkingDirectory() {
            return new EasyuploadFtpDirectory(currDir);
        }

        @Override
        public FtpFile getFile(String file) {
            logger.debug("get file =< {} >", file);
            checkNotBlank(file);
            if (file.startsWith("/")) {
                file = normalizePath(file);
            } else {
                file = normalizePath(currDir, file);
            }
            EasyuploadItem item = easyuploadService.getByPathOrNull(file);
            if (item != null) {
                return new EasyuploadFtpFile(item);
            } else if (isBlank(FilenameUtils.getExtension(file))) {//TODO improve this 
                return new EasyuploadFtpDirectory(file);
            } else {
                return new EasyuploadFtpFile(file);
            }
        }

        @Override
        public boolean changeWorkingDirectory(String dir) {
            dir = normalizePath(dir);
            logger.debug("change dir from < {} > to < {} >", currDir, dir);
            currDir = dir;
            return true;
        }

        @Override
        public boolean isRandomAccessible() {
            return true;
        }

        @Override
        public void dispose() {
            //nothing to do
        }

    }

    private class EasyuploadFtpDirectory implements FtpFile {

        private final String path;

        public EasyuploadFtpDirectory(String directory) {
            this.path = normalizePath(directory);
            logger.debug("create handle for dir =< {} >", path);
        }

        @Override
        public String getAbsolutePath() {
            return ("/" + path).replaceFirst("^/+", "/");
        }

        @Override
        public String getName() {
            return FilenameUtils.getName(path);
        }

        @Override
        public boolean isHidden() {
            return false;
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

        @Override
        public boolean isFile() {
            return false;
        }

        @Override
        public boolean doesExist() {
            return isRoot()
                    || fakeDirs.contains(path)//TODO improve this
                    || hasContent();
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isWritable() {
            return true;
        }

        @Override
        public boolean isRemovable() {
            return !(isRoot() || hasContent());
        }

        @Override
        public String getOwnerName() {
            return "system";
        }

        @Override
        public String getGroupName() {
            return "system";
        }

        @Override
        public int getLinkCount() {
            return 3;//?
        }

        @Override
        public long getLastModified() {
            return 0;
        }

        @Override
        public boolean setLastModified(long time) {
            logger.error("setLastModified operation not supported for path =< {} >", path);
            return false;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public Object getPhysicalFile() {
            return path;
        }

        @Override
        public boolean mkdir() {
            if (doesExist()) {
                return false;
            } else {
                fakeDirs.add(path);
                return true;
            }
        }

        @Override
        public boolean delete() {
            fakeDirs.remove(path);
            if (!doesExist()) {
                return true;
            } else {
                logger.warn("cannot delete path =< {} >", path);
                return false;
            }
        }

        @Override
        public boolean move(FtpFile destination) {
            logger.warn("move operation not supported for path =< {} >", path);
            return false;
        }

        @Override
        public List<? extends FtpFile> listFiles() {
            logger.debug("list files for dir = {}", path);
            return listOf(FtpFile.class).accept(l -> {
                List<String> dirs = easyuploadService.getSubdirsForDir(path);
                logger.debug("list of sub dirs = {}", dirs);
                dirs.stream().map(d -> new EasyuploadFtpDirectory(normalizePath(path, d))).forEach(l::add);

                List<EasyuploadItemInfo> files = easyuploadService.getByDir(path);
                logger.debug("list files = {}", files);
                files.stream().map(f -> new EasyuploadFtpFile(f)).forEach(l::add);
            });
        }

        @Override
        public OutputStream createOutputStream(long offset) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream createInputStream(long offset) throws IOException {
            throw new UnsupportedOperationException();
        }

        private boolean isRoot() {
            return EasyuploadUtils.isRoot(path);
        }

        private boolean hasContent() {
            return !easyuploadService.getByDir(path).isEmpty();
        }

    }

    private class EasyuploadFtpFile implements FtpFile {

        private final String path;
        private EasyuploadItemInfo item;

        public EasyuploadFtpFile(EasyuploadItemInfo item) {
            this.item = checkNotNull(item);
            this.path = item.getPath();
            logger.debug("create handle for file =< {} > with item =< {} >", path, item);
        }

        public EasyuploadFtpFile(String path) {
            this.path = normalizePath(path);
            logger.debug("create handle for new file =< {} >", path);
        }

        @Override
        public String getAbsolutePath() {
            return path;
        }

        @Override
        public String getName() {
            return FilenameUtils.getName(path);
        }

        @Override
        public boolean isHidden() {
            return false;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public boolean isFile() {
            return true;
        }

        @Override
        public boolean doesExist() {
            return item != null;
        }

        @Override
        public long getSize() {
            return item.getSize();
        }

        @Override
        public String getOwnerName() {
            return "system";
        }

        @Override
        public String getGroupName() {
            return "system";
        }

        @Override
        public int getLinkCount() {
            return 1;
        }

        @Override
        public long getLastModified() {
            return 0;//TODO
        }

        @Override
        public boolean setLastModified(long time) {
            return false;//TODO
        }

        @Override
        public boolean isReadable() {
            return true;
        }

        @Override
        public boolean isWritable() {
            return true;
        }

        @Override
        public boolean isRemovable() {
            return true;
        }

        @Override
        public boolean delete() {
            try {
                logger.debug("delete file = {}", item);
                easyuploadService.delete(item.getId());
                return true;
            } catch (Exception ex) {
                logger.error("error deleting file = {} path =< {} >", item, path, ex);
                return false;
            }
        }

        @Override
        public boolean move(final FtpFile dest) {
            logger.error("move operation not supported yet");
            return false;
        }

        @Override
        public boolean mkdir() {
            logger.error("mkdir operation not supported for path =< {} >", path);
            return false;
        }

        @Override
        public String getPhysicalFile() {
            return path;
        }

        @Override
        public List<FtpFile> listFiles() {
            return null;
        }

        @Override
        public OutputStream createOutputStream(long offset) throws IOException {
            logger.debug("read file = {}", item);
            return new ByteArrayOutputStream() {
                @Override
                public void close() throws IOException {
                    byte[] data = toByteArray();

                    if (offset > 0) {
                        byte[] currentData = item == null ? new byte[]{} : easyuploadService.getById(item.getId()).getContent(),
                                fullData = new byte[toIntExact(offset) + data.length];
                        for (int i = 0; i < offset; i++) {
                            if (i < currentData.length) {
                                fullData[i] = currentData[i];
                            } else {
                                fullData[i] = 0;
                            }
                        }
                        for (int i = 0; i < data.length; i++) {
                            fullData[i + toIntExact(offset)] = data[i];
                        }
                        data = fullData;
                    }

                    if (item != null) {
                        item = easyuploadService.update(item.getId(), data, item.getDescription());
                    } else {
                        item = easyuploadService.create(newDataHandler(data), path, null);
                    }
                }

            };
        }

        @Override
        public InputStream createInputStream(long offset) throws IOException {
            logger.debug("write file = {}", item);
            return new ByteArrayInputStream(easyuploadService.getById(item.getId()).getContent(), toIntExact(offset), Integer.MAX_VALUE);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FtpFile) {
                return equal(((FtpFile) obj).getAbsolutePath(), getAbsolutePath());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }
    }

}
