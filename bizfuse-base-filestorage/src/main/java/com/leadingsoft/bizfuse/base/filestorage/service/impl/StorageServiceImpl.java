package com.leadingsoft.bizfuse.base.filestorage.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.leadingsoft.bizfuse.base.filestorage.enums.NormalizationType;
import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;
import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord.ObjectType;
import com.leadingsoft.bizfuse.base.filestorage.service.StorageRecordService;
import com.leadingsoft.bizfuse.base.filestorage.util.ImageUtils;
import com.leadingsoft.bizfuse.base.filestorage.util.Mp3DurationUtils;
import com.leadingsoft.bizfuse.base.filestorage.util.Mp3TranslateUtils;
import com.leadingsoft.bizfuse.base.filestorage.util.ZipUtils;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

@Service
@Transactional
public class StorageServiceImpl extends AbstractStorageService {

    @Value("${local.storage.root}")
    private String rootPath;
    @Autowired
    private StorageRecordService storageRecordService;
    @Value("${local.storage.normalization.tool.ffmpeg}")
    private String ffmpeg;
    @Value("${local.storage.normalization.tool.silkconverter:/}")
    private String silkConverterPath;

    private Container currentContainer;
    private final Object lock = new Object();
    private SimpleFilePathContainer simpleFilePathContainer;

    @PostConstruct
    public void init() {
        this.simpleFilePathContainer = new SimpleFilePathContainer(this.rootPath);
    }

    @Override
    public File getFile(final String filePath) {
        // C + yyyyMMdd + 自增数值
        File file = null;
        if (filePath.matches("C20\\d+_.*")) {
            final Container c = Container.getContainer(this.rootPath, filePath);
            if (c == null) {
                throw new CustomRuntimeException("404", "文件不存在.");
            }
            file = c.getFile(filePath);
        } else {
            file = this.simpleFilePathContainer.getFile(filePath);
        }
        if (file.exists()) {
            return file;
        } else {
            throw new CustomRuntimeException("404", "文件不存在.");
        }
    }

    @Override
    public StorageRecord getFileStorageRecord(final String no) {
        return this.storageRecordService.getStorageRecordByNo(no);
    }

    @Override
    public File getFileStorageRecordsTo7Zip(final List<String> nos, final String filePath) {
        final File targetZip = new File(filePath);
        final List<StorageRecord> records = this.storageRecordService.getStorageRecords(nos);
        final List<String> filePaths = records.stream().map(record -> {
            final File file = this.getFile(record.getOriginalFilePath());
            return file.getAbsolutePath();
        }).collect(Collectors.toList());
        ZipUtils.exec7zip(filePaths, targetZip);
        return targetZip;
    }

    @Override
    public StorageRecord save(final File file) {
        final String filePath = this.getActiveContainer().addFile(file);

        return this.registerStorageRecord(filePath, file.getName(), file.length());
    }

    @Override
    public StorageRecord save(final MultipartFile file) {
        if (file == null) {
            throw new CustomRuntimeException("文件上传失败");
        }
        final String filePath = this.getActiveContainer().addFile(file);
        return this.registerStorageRecord(filePath, file.getOriginalFilename(), file.getSize());
    }

    @Override
    public StorageRecord save(final File[] files, final String originalFilename, final long fileTotalSize) {
        final String filePath = this.getActiveContainer().addFileChunks(files, originalFilename);
        return this.registerStorageRecord(filePath, originalFilename, fileTotalSize);
    }

    @Override
    public void delete(final String no) {
        final StorageRecord record = this.storageRecordService.getStorageRecordByNo(no);
        if (record.getFilePath().matches("C20\\d+_.*")) {
            final Container c = Container.getContainer(this.rootPath, record.getFilePath());
            if (c != null) {
                c.deleteFile(record.getFilePath());
                c.deleteFile(record.getThumbnailFilePath());
                c.deleteFile(record.getOriginalFilePath());
            }
        } else {
            this.simpleFilePathContainer.deleteFile(record.getFilePath());
            this.simpleFilePathContainer.deleteFile(record.getThumbnailFilePath());
            this.simpleFilePathContainer.deleteFile(record.getOriginalFilePath());
        }
    }

    @Override
    public StorageRecord normalize(final String no, final boolean sync) {
        //根据ID从Core获取信息
        final StorageRecord storageRecord = this.storageRecordService.getStorageRecordByNo(no);

        //标准化处理：
        //标准图、缩略图的生成和存储
        //根据Type选取适当的数据存储位置
        final ObjectType storeType = storageRecord.getObjectType();
        switch (storeType) {
        case picture:
            this.normalizePicture(storageRecord);
            break;
        case video:
            this.normalizeVideo(storageRecord);
            break;
        case audio:
            this.normalizeAudio(storageRecord);
            break;
        default:
            break;
        }
        return storageRecord;
    }

    @Override
    public StorageRecord createCombinationImage(final List<String> storageNos, final NormalizationType type) {
        final List<StorageRecord> records = this.storageRecordService.getStorageRecords(storageNos);
        final List<File> images = records.stream().map(record -> {
            if ((type == null) || (NormalizationType.standard == type)) {
                return this.getFile(record.getFilePath());
            } else if (NormalizationType.original == type) {
                return this.getFile(record.getOriginalFilePath());
            } else {
                return this.getFile(record.getThumbnailFilePath());
            }
        }).collect(Collectors.toList());
        // 执行标准化处理，合成图片
        final File tempImage = this.createLocalTempFile(ExtensionType.jpg.name());
        if (NormalizationType.thumbnail != type) {
            // 只支持缩略图
            throw new UnsupportedOperationException();
        }
        ImageUtils.createCombinationImage(images, tempImage, type);
        // 存储
        final String combinationImage = this.getActiveContainer().addFile(tempImage);

        // 更新到core
        final StorageRecord record = new StorageRecord();
        record.setObjectType(ObjectType.picture);
        record.setThumbnailFilePath(combinationImage);
        record.setFilePath(combinationImage);
        record.setOriginalFilePath(combinationImage);
        record.setFileName(tempImage.getName());
        record.setFileSize(tempImage.length());
        this.storageRecordService.createStorageRecord(record);
        return record;
    }

    /**
     * 图片的标准化处理
     * <p>
     * <li>生成标准图、缩略图
     * <li>标准图、缩略图保存到存储
     * <li>更新存储记录
     *
     * @param record
     */
    private StorageRecord normalizePicture(final StorageRecord record) {
        final String originalFilePath = record.getOriginalFilePath();
        final File originalImage = this.getFile(originalFilePath);
        final String extension = FilenameUtils.getExtension(record.getFileName());

        // 执行标准化处理
        final File tempStandardImage = this.createLocalTempFile(extension);
        ImageUtils.createStandardImage(originalImage, tempStandardImage);
        final File tempThumbnailImage = this.createLocalTempFile(extension);
        ImageUtils.createThumbnailImage(originalImage, tempThumbnailImage);

        // 存储
        final String standardImagePath = this.getActiveContainer().addFile(tempStandardImage);
        final String thumbnailImagePath = this.getActiveContainer().addFile(tempThumbnailImage);

        // 更新到core
        record.setFilePath(standardImagePath);
        record.setThumbnailFilePath(thumbnailImagePath);
        record.setFileSize(originalImage.length());
        this.storageRecordService.updateStorageRecord(record);
        return record;
    }

    /**
     * 视频文件的标准化处理
     * <p>
     * <li>生成视频封面
     * <li>视频封面保存到存储
     * <li>更新存储记录
     *
     * @param record
     */
    private StorageRecord normalizeVideo(final StorageRecord record) {
        final String originalFilePath = record.getOriginalFilePath();
        final File originalFile = this.getFile(originalFilePath);

        // 执行标准化处理，生成视频的封面图片
        final File tempCoverImage = this.createLocalTempFile(ExtensionType.jpg.name());
        ImageUtils.getVideoFirstFramePic(this.ffmpeg, originalFile.getAbsolutePath(),
                tempCoverImage.getAbsolutePath());

        // 存储
        final String thumbnailImagePath = this.getActiveContainer().addFile(tempCoverImage);

        // 更新到core
        record.setThumbnailFilePath(thumbnailImagePath);
        record.setFileSize(originalFile.length());
        return this.storageRecordService.updateStorageRecord(record);
    }

    /**
     * 音频文件的标准化处理
     * <p>
     * <li>如果原始文件是amr音频格式，转成标准的MP3格式音频
     * <li>转换后的MP3音频保存到存储
     * <li>计算音频时长
     * <li>更新存储记录
     *
     * @param record
     */
    private StorageRecord normalizeAudio(final StorageRecord record) {
        final String originalFilePath = record.getOriginalFilePath();
        final File originalFile = this.getFile(originalFilePath);
        final String extension = FilenameUtils.getExtension(record.getFileName());
        File standardMp3File = null;

        if (ExtensionType.mp3.name().equalsIgnoreCase(extension)) {
            standardMp3File = originalFile;
        } else if (ExtensionType.amr.name().equalsIgnoreCase(extension)) {
            // 执行标准化处理，转成标准的MP3格式音频
            standardMp3File = this.createLocalTempFile(ExtensionType.mp3.name());
            Mp3TranslateUtils.changeAmr2Mp3(this.ffmpeg,
                    originalFile.getAbsolutePath(),
                    standardMp3File.getAbsolutePath());
            // 存储
            final String standardFilePath = this.getActiveContainer().addFile(standardMp3File);
            record.setFilePath(standardFilePath);
        } else if (ExtensionType.silk.name().equalsIgnoreCase(extension)) {
            // 执行标准化处理，转成标准的MP3格式音频
            standardMp3File = this.createLocalTempFile(ExtensionType.mp3.name());
            Mp3TranslateUtils.changeSilk2Mp3(
                    originalFile.getAbsolutePath(),
                    standardMp3File.getAbsolutePath());
            // 存储
            final String standardFilePath = this.getActiveContainer().addFile(standardMp3File);
            record.setFilePath(standardFilePath);
        } else {
            // 不支持的音频格式，不做处理
        }

        // 计算MP3音频时长
        if (standardMp3File != null) {
            final int duration = Mp3DurationUtils.getMp3TrackLength(this.ffmpeg, standardMp3File.getAbsolutePath());
            record.setDuration(duration);
        }

        // 更新到core
        record.setFileSize(originalFile.length());
        return this.storageRecordService.updateStorageRecord(record);
    }

    public enum ExtensionType {
        mp3, wav, wma, amr, mp4, png, jpg, jpeg, silk, other;

        public static ExtensionType value(final String name) {
            for (final ExtensionType type : ExtensionType.values()) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return other;
        }
    }

    private StorageRecord registerStorageRecord(final String filePath, final String fileName, final long size) {

        //Register File Info to Core
        final String extension = FilenameUtils.getExtension(fileName);
        final StorageRecord record = new StorageRecord();
        record.setNo(UUID.randomUUID().toString());
        record.setThumbnailFilePath("");
        switch (extension) {
        case "jpg":
        case "jpeg":
        case "png":
            record.setObjectType(ObjectType.picture);
            record.setThumbnailFilePath(filePath);
            break;
        case "mp3":
        case "amr":
        case "silk":
            record.setObjectType(ObjectType.audio);
            break;
        case "mp4":
        case "wav":
        case "wma":
        case "3gp":
            record.setObjectType(ObjectType.video);
            break;
        default:
            record.setObjectType(ObjectType.file);
            break;
        }
        int pathMarkIndex = fileName.lastIndexOf("/");
        if (pathMarkIndex == -1) {
            pathMarkIndex = fileName.lastIndexOf("\\");
        }
        if (pathMarkIndex >= 0) {
            record.setFileName(fileName.substring(pathMarkIndex + 1));
        } else {
            record.setFileName(fileName);
        }
        record.setOriginalFilePath(filePath);
        record.setFilePath(filePath);
        record.setFileSize(size);

        return this.storageRecordService.createStorageRecord(record);
    }

    /**
     * 容器生成规则：
     * <li>文件数量超过10000生成新的容器
     * <li>新容器命名规则：C + yyyyMMdd + 自增数值
     *
     * @return
     */
    private Container getActiveContainer() {
        if ((this.currentContainer != null) && (this.currentContainer.fileCounts < 10000)) {
            return this.currentContainer;
        }
        synchronized (this.lock) {
            if ((this.currentContainer != null) && (this.currentContainer.fileCounts < 10000)) {
                return this.currentContainer;
            }
            if (this.currentContainer == null) {
                this.currentContainer = this.loadContainer();
            }
            if (this.currentContainer.fileCounts >= 10000) {

                String name = "C" + DateFormatUtils.format(new Date(), "yyyyMMdd");
                if (name.substring(0, 9).equals(this.currentContainer.name.substring(0, 9))) {
                    name += (this.currentContainer.number + 1);
                } else {
                    name += "0";
                }
                final Container container = new Container(name, this.rootPath);
                container.fileCounts = 0;
                this.currentContainer = container;
            }
            return this.currentContainer;
        }
    }

    private Container loadContainer() {
        final File root = new File(this.rootPath);
        if (!root.exists()) {
            root.mkdirs();
        }
        final File[] containers = root.listFiles((final File dir, final String name) -> {
            return dir.isDirectory() && dir.getName().startsWith("C");
        });
        if (containers.length == 0) {
            final String name = "C" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "0";
            final Container container = new Container(name, this.rootPath);
            container.number = 0;
            return container;
        } else {
            String latestContainer = null;
            for (final File c : containers) {
                final String tempContainer = c.getName();
                if (latestContainer == null) {
                    latestContainer = tempContainer;
                } else {
                    if (Integer.parseInt(tempContainer.substring(1)) > Integer.parseInt(latestContainer.substring(1))) {
                        latestContainer = tempContainer;
                    }
                }
            }
            final Container container = new Container(latestContainer, this.rootPath);
            final File containerFile = new File(this.rootPath, container.name);
            container.number = containerFile.list().length;
            return container;
        }
    }
}
