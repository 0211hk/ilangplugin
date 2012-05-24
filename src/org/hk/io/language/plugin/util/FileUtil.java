package org.hk.io.language.plugin.util;

import java.io.File;

public final class FileUtil {

    public static final boolean fileExists(final String path) {
        File f = new File(path);
        if (f.exists() && f.isFile()) {
            return true;
        }
        return false;
    }
}
