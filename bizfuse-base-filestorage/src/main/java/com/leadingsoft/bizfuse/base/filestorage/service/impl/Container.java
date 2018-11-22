package com.leadingsoft.bizfuse.base.filestorage.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Container {
    String path;
    String name;
    int number;
    int fileCounts = 0;

    public static Container getContainer(final String rootPath, final String filePath) {
        final int containerIndex = filePath.indexOf("_");
        if (containerIndex != -1) {
            final Container c = new Container(filePath.substring(0, containerIndex), rootPath);
            return c;
        }
        return null;
    }

    public Container(final String name, final String rootPath) {
        this.path = rootPath + File.separator + name;
        this.name = name;
    }

    public String addFileChunks(final File[] files, final String fileName) {
        InputStream iStream = null;
        OutputStream oStream = null;
        final String extension = FilenameUtils.getExtension(fileName);
        final String filePath = this.createFilePath(extension);
        try {
            final File saveFile = this.getFile(filePath);
            oStream = new FileOutputStream(saveFile, true);
            for (final File file : files) {
                iStream = new FileInputStream(file);
                IOUtils.copy(iStream, oStream);
                IOUtils.closeQuietly(iStream);
            }
            return filePath;
        } catch (final IOException e) {
            Container.log.error("文件存储失败.", e);
            throw new CustomRuntimeException("文件处理失败");
        } finally {
            IOUtils.closeQuietly(oStream);
        }
    }

    public String addFile(final File file) {
        InputStream iStream = null;
        OutputStream oStream = null;
        final String extension = FilenameUtils.getExtension(file.getName());
        try {
            iStream = new FileInputStream(file);
            final String filePath = this.createFilePath(extension);
            final File saveFile = this.getFile(filePath);
            oStream = new FileOutputStream(saveFile, false);
            IOUtils.copy(iStream, oStream);
            return filePath;
        } catch (final IOException e) {
            Container.log.error("文件存储失败.", e);
            throw new CustomRuntimeException("文件处理失败");
        } finally {
            IOUtils.closeQuietly(iStream);
            IOUtils.closeQuietly(oStream);
        }
    }

    public String addFile(final MultipartFile file) {
        InputStream iStream = null;
        OutputStream oStream = null;
        final String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        try {
            iStream = file.getInputStream();
            final String filePath = this.createFilePath(extension);
            final File saveFile = this.getFile(filePath);
            oStream = new FileOutputStream(saveFile, false);
            IOUtils.copy(iStream, oStream);
            return filePath;
        } catch (final IOException e) {
            throw new CustomRuntimeException("文件上传失败");
        } finally {
            IOUtils.closeQuietly(iStream);
            IOUtils.closeQuietly(oStream);
        }
    }

    public void deleteFile(final String fileId) {
        final File file = this.getFile(fileId);
        if (file.exists()) {
            file.delete();
        }
    }

    public File getFile(final String filePath) {
        final File path = new File(this.path);
        if (!path.exists()) {
            path.mkdirs();
        }
        final File file = new File(this.path, filePath);
        return file;
    }

    private String createFilePath(final String extension) {
        final StringBuilder id = new StringBuilder();
        id.append(this.name)
                .append("_")
                .append(String.valueOf(System.currentTimeMillis()))
                .append(String.valueOf((int) (Math.random() * 10000)))
                .append(".")
                .append(extension);
        return id.toString();
    }
}
