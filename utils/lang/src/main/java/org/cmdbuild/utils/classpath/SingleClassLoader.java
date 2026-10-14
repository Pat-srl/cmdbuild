package org.cmdbuild.utils.classpath;

import static com.google.common.base.Objects.equal;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.CmNullableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleClassLoader extends ClassLoader {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Class<?> classe;

    public SingleClassLoader(Class<?> classe, @Nullable ClassLoader parent) {
        super(CmNullableUtils.firstNotNull(parent, Thread.currentThread().getContextClassLoader()));
        this.classe = Preconditions.checkNotNull(classe);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (equal(name, classe.getName())) {
                logger.trace("returning this class = {}", classe);
                return classe;
            } else {
                return getParent().loadClass(name);
            }
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (equal(name, classe.getName())) {
                logger.trace("returning this class = {}", classe);
                return classe;
            } else {
                return super.loadClass(name, resolve);
            }
        }
    }

}
