package com.leadingsoft.bizfuse.base.filestorage.service.impl;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.leadingsoft.bizfuse.base.filestorage.service.StorageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractStorageService implements StorageService, InitializingBean {

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

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动定时清理临时文件的操作
        this.startCleanTmpFilesTask();
    }

    private void startCleanTmpFilesTask() {
        final Timer cleanScheduler = new Timer();
        final TimerTask cleanTask = new TimerTask() {
            @Override
            public void run() {
                final File filePath = new File(AbstractStorageService.this.tmpFilePath);
                // 清理10天前的临时文件
                final Date cleanEndDate = DateUtils.addDays(new Date(), -10);
                final String cleanFilename = DateFormatUtils.format(cleanEndDate, "yyyyMMdd");
                final File[] cleanFiles = filePath.listFiles((final File dir, final String name) -> {
                    return name.compareTo(cleanFilename) < 0;
                });
                if (cleanFiles == null) {
                    return;
                }
                for (final File cleanFile : cleanFiles) {
                    try {
                        if (cleanFile.isDirectory()) {
                            for (final File file : cleanFile.listFiles()) {
                                file.delete();
                            }
                        }
                        cleanFile.delete();
                    } catch (final Exception e) {
                        AbstractStorageService.log.error("文件删除失败.", e);
                    }
                }
            }
        };
        final long delay = 10000l;
        final long period = this.tmpFileCleanPeriodOfDays * 24L * 3600L * 1000L;
        cleanScheduler.scheduleAtFixedRate(cleanTask, delay, period);
    }
}
