package com.leadingsoft.bizfuse.base.filestorage.service.impl;

import java.io.File;

/**
 * 简单路径的文件存储容器
 * 
 * @author liuyg
 */
public class SimpleFilePathContainer {
    private final File parent;

    public SimpleFilePathContainer(final String rootPath) {
        this.parent = new File(rootPath);
        if (!this.parent.exists()) {
            this.parent.mkdirs();
        }
    }

    public File getFile(final String filePath) {
        return new File(this.parent, filePath);
    }

    public void deleteFile(final String filePath) {
        final File file = this.getFile(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
