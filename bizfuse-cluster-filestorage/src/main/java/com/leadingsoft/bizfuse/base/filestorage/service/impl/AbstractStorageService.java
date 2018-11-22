package com.leadingsoft.bizfuse.base.filestorage.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.leadingsoft.bizfuse.base.filestorage.enums.NormalizationType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.leadingsoft.bizfuse.base.filestorage.service.StorageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractStorageService implements StorageService, InitializingBean {

    @Value("${local.storage.root}")
    protected String rootPath;
    @Value("${local.storage.tmp}")
    protected String tmpFilePath;// 临时文件目录
    @Value("${local.storage.tmp.clean.periodOfDays}")
    protected int tmpFileCleanPeriodOfDays;

    @Override
    public File createLocalTempFile(final String extension) {
        final String subPath = DateFormatUtils.format(new Date(), "yyyyMMdd");
        final File filePath = new File(this.tmpFilePath, subPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String fileName = String.valueOf(System.currentTimeMillis());
        if (extension != null) {
            fileName = fileName + "." + extension;
        }
        File target = new File(filePath, fileName);
        while (target.exists()) {
            fileName = String.valueOf(System.currentTimeMillis());
            if (extension != null) {
                fileName = fileName + "." + extension;
            }
            target = new File(filePath, fileName);
        }
        return target;
    }

    public File createLocalTempFile(String relativePath, String filename) {
        final File filePath = new File(this.tmpFilePath, relativePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        File target = new File(filePath, filename);
        if (target.exists())
            target.delete();

        return target;
    }

    public File getLocalTempFile(String relativePath, String filename) {
        return new File(this.tmpFilePath + File.separatorChar + relativePath + File.separatorChar + filename);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final File filePath = new File(this.tmpFilePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        // 启动定时清理临时文件的操作
        this.startCleanTask();
    }

    private void startCleanTask() {
        final Timer cleanScheduler = new Timer();
        final TimerTask cleanTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    cleanExpireFiles(AbstractStorageService.this.tmpFilePath);
                    cleanExpireFiles(AbstractStorageService.this.rootPath + File.separator + NormalizationType.standard);
                    cleanExpireFiles(AbstractStorageService.this.rootPath + File.separator + NormalizationType.thumbnail);

                } catch (Exception e) {
                    AbstractStorageService.log.warn(e.getMessage(), e);
                }
            }
        };
        final long delay = 10000l;
        final long period = 24L * 3600L * 1000L;
        cleanScheduler.scheduleAtFixedRate(cleanTask, delay, period);
    }

    private void cleanExpireFiles(String path) {
        final File filePath = new File(path);
        // 清理10天前的临时文件
        final Date cleanEndDate = DateUtils.addDays(new Date(), -this.tmpFileCleanPeriodOfDays);
        final String cleanFilename = DateFormatUtils.format(cleanEndDate, "yyyyMMdd");
        final File[] cleanFiles = filePath.listFiles((final File dir) -> {
            try {
                FileTime t = Files.readAttributes(dir.toPath(), BasicFileAttributes.class).creationTime();
                return cleanEndDate.getTime() > (t.toMillis());
            } catch (IOException e) {
                log.error("获取文件 [{}]创建时间失败", dir.getName());
                return false;
            }
        });
        if (cleanFiles == null) {
            return;
        }
        for (final File cleanFile : cleanFiles) {
            boolean status = FileUtils.deleteQuietly(cleanFile);
            if (!status)
                AbstractStorageService.log.error("文件{}删除失败.", cleanFile.getName());

        }
    }

}
