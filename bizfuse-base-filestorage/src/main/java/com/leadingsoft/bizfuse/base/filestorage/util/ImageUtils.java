package com.leadingsoft.bizfuse.base.filestorage.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.leadingsoft.bizfuse.base.filestorage.enums.NormalizationType;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
public class ImageUtils {

    public static BufferedImage getSourceImg(final String picUrl) throws IOException, FileNotFoundException {
        final File file = new File(picUrl);
        return ImageIO.read(new FileInputStream(file));
    }

    public static void createStandardImage(final File originalImage, final File standardImage) {
        // TODO：计算获得标准图片的长宽
        final int width = 900;
        final int hight = 500;
        ImageUtils.convert(originalImage, standardImage, width, hight, false);
    }

    public static void createThumbnailImage(final File originalImage, final File thumnailImage) {
        // TODO：计算获得缩略图的长宽
        final int width = 180;
        final int hight = 100;
        ImageUtils.convert(originalImage, thumnailImage, width, hight, false);
    }

    public static void createCombinationImage(final List<File> originalImages, final File image,
            final NormalizationType type) {
        // TODO：计算获得缩略图的长宽
        final int width = 132;
        final int hight = 132;
        ImageUtils.createCombinationOfHeadImage(originalImages, image, width, hight);
    }

    /**
     * <p>
     * Title: thumbnailImage
     * </p>
     * <p>
     * Description: 根据图片路径生成缩略图
     * </p>
     *
     * @param imagePath 原图片路径
     * @param w 缩略图宽
     * @param h 缩略图高
     * @param thumnailImageFile 生成的缩略图路径
     * @param force 是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     */
    public static void convert(final File imgFile, final File thumnailImageFile, final int w, final int h,
            final boolean force) {
        if (!imgFile.exists()) {
            ImageUtils.log.error(String.format("The image [%s] is not exist.", imgFile.getAbsolutePath()));
            throw new CustomRuntimeException("标准化处理的文件不存在.");
        }
        try {
            Thumbnails.of(imgFile)
                    .size(w, h)
                    .toFile(thumnailImageFile);
        } catch (final IOException e) {
            ImageUtils.log.error("generate thumbnail image failed.", e);
            throw new CustomRuntimeException("标准化处理失败.", e);
        }
    }

    /**
     * 取得上传的视频文件的第一帧图片
     *
     * @param videoPath 视频路径
     * @param imagePath 生成的图片的路径
     * @return
     */
    public static String getVideoFirstFramePic(final String ffmpegPath, final String videoPath,
            final String imagePath) {

        final File file = new File(videoPath);

        if (!file.exists()) {

            ImageUtils.log.error("路径[" + videoPath + "]对应的视频文件不存在!");

            return null;

        }

        final List<String> commands = new ArrayList<String>();

        commands.add(ffmpegPath);

        commands.add("-i");

        commands.add(videoPath);

        commands.add("-y");

        commands.add("-f");

        commands.add("image2");

        commands.add("-t");

        commands.add("0.001");

        commands.add("-s");

        commands.add("320x240");

        commands.add(imagePath);

        try {

            final ProcessBuilder builder = new ProcessBuilder();

            builder.command(commands);

            final Process process = builder.start();

            process.waitFor();

            return imagePath;

        } catch (final Exception e) {

            ImageUtils.log.error("生成视频文件[" + videoPath + "]对应的第一帧图片失败!");
            ImageUtils.log.error("错误信息：" + e.getMessage());

            return null;
        }

    }

    /**
     * @param files
     * @param outPath
     * @throws Exception
     */
    public static boolean createCombinationOfHeadImage(final List<File> files, final File outFile, final int imageWidth,
            final int imageHeight) {
        try {
            final String[] imageSize = ImageUtils.getXY(files.size());
            final int width = ImageUtils.getWidth(files.size());
            final BufferedImage ImageNew = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

            // 生成画布
            final Graphics g = ImageNew.getGraphics();

            final Graphics2D g2d = (Graphics2D) g;

            // 设置背景色
            g2d.setBackground(new Color(231, 231, 231));

            // 通过使用当前绘图表面的背景色进行填充来清除指定的矩形。
            g2d.clearRect(0, 0, imageWidth, imageHeight);

            for (int i = 0; i < imageSize.length; i++) {
                final String size = imageSize[i];
                final String[] sizeArr = size.split(",");
                final int x = Integer.valueOf(sizeArr[0]);
                final int y = Integer.valueOf(sizeArr[1]);
                BufferedImage ImageOne;
                ImageOne = ImageUtils.zoom(files.get(i).getPath(), width, width, true);
                g2d.drawImage(ImageOne, x, y, null);
            }
            ImageIO.write(ImageNew, "jpg", outFile);// 写图片
            return true;
        } catch (final IOException e) {
            return false;
        }
    }

    /**
     * 图片缩放
     *
     * @param filePath 图片路径
     * @param height 高度
     * @param width 宽度
     * @param isPadding 比例不对时是否需要补白
     * @throws IOException
     */
    public static BufferedImage zoom(final String filePath, final int height, final int width, final boolean isPadding)
            throws IOException {

        double ratio = 0; // 缩放比例
        final File f = new File(filePath);
        final BufferedImage bi = ImageIO.read(f);
        Image itemp = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        // 计算比例
        if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
            if (bi.getHeight() > bi.getWidth()) {
                ratio = (new Integer(height)).doubleValue() / bi.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue() / bi.getWidth();
            }
            final AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            itemp = op.filter(bi, null);
        }
        if (isPadding) {
            final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            final Graphics2D g = image.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, width, height);
            if (width == itemp.getWidth(null)) {
                g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null),
                        Color.white, null);
            } else {
                g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null),
                        Color.white, null);
            }
            g.dispose();
            itemp = image;
        }
        return (BufferedImage) itemp;
    }

    private static String[] getXY(final int size) {
        final String[] s = new String[size];
        int _x = 0;
        int _y = 0;
        if (size == 1) {
            _x = _y = 6;
            s[0] = "6,6";
        }
        if (size == 2) {
            _x = _y = 4;
            s[0] = "4," + ((132 / 2) - (60 / 2));
            s[1] = 60 + (2 * _x) + "," + ((132 / 2) - (60 / 2));
        }
        if (size == 3) {
            _x = _y = 4;
            s[0] = ((132 / 2) - (60 / 2)) + "," + _y;
            s[1] = _x + "," + (60 + (2 * _y));
            s[2] = (60 + (2 * _y)) + "," + (60 + (2 * _y));
        }
        if (size == 4) {
            _x = _y = 4;
            s[0] = _x + "," + _y;
            s[1] = ((_x * 2) + 60) + "," + _y;
            s[2] = _x + "," + (60 + (2 * _y));
            s[3] = (60 + (2 * _y)) + "," + (60 + (2 * _y));
        }
        if (size == 5) {
            _x = _y = 3;
            s[0] = ((132 - (40 * 2) - _x) / 2) + "," + ((132 - (40 * 2) - _y) / 2);
            s[1] = (((132 - (40 * 2) - _x) / 2) + 40 + _x) + "," + ((132 - (40 * 2) - _y) / 2);
            s[2] = _x + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
            s[3] = ((_x * 2) + 40) + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
            s[4] = ((_x * 3) + (40 * 2)) + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
        }
        if (size == 6) {
            _x = _y = 3;
            s[0] = _x + "," + ((132 - (40 * 2) - _x) / 2);
            s[1] = ((_x * 2) + 40) + "," + ((132 - (40 * 2) - _x) / 2);
            s[2] = ((_x * 3) + (40 * 2)) + "," + ((132 - (40 * 2) - _x) / 2);
            s[3] = _x + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
            s[4] = ((_x * 2) + 40) + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
            s[5] = ((_x * 3) + (40 * 2)) + "," + (((132 - (40 * 2) - _x) / 2) + 40 + _y);
        }
        if (size == 7) {
            _x = _y = 3;
            s[0] = ((132 - 40) / 2) + "," + _y;
            s[1] = _x + "," + ((_y * 2) + 40);
            s[2] = ((_x * 2) + 40) + "," + ((_y * 2) + 40);
            s[3] = ((_x * 3) + (40 * 2)) + "," + ((_y * 2) + 40);
            s[4] = _x + "," + ((_y * 3) + (40 * 2));
            s[5] = ((_x * 2) + 40) + "," + ((_y * 3) + (40 * 2));
            s[6] = ((_x * 3) + (40 * 2)) + "," + ((_y * 3) + (40 * 2));
        }
        if (size == 8) {
            _x = _y = 3;
            s[0] = ((132 - 80 - _x) / 2) + "," + _y;
            s[1] = (((132 - 80 - _x) / 2) + _x + 40) + "," + _y;
            s[2] = _x + "," + ((_y * 2) + 40);
            s[3] = ((_x * 2) + 40) + "," + ((_y * 2) + 40);
            s[4] = ((_x * 3) + (40 * 2)) + "," + ((_y * 2) + 40);
            s[5] = _x + "," + ((_y * 3) + (40 * 2));
            s[6] = ((_x * 2) + 40) + "," + ((_y * 3) + (40 * 2));
            s[7] = ((_x * 3) + (40 * 2)) + "," + ((_y * 3) + (40 * 2));
        }
        if (size == 9) {
            _x = _y = 3;
            s[0] = _x + "," + _y;
            s[1] = (_x * 2) + 40 + "," + _y;
            s[2] = (_x * 3) + (40 * 2) + "," + _y;
            s[3] = _x + "," + ((_y * 2) + 40);
            s[4] = ((_x * 2) + 40) + "," + ((_y * 2) + 40);
            s[5] = ((_x * 3) + (40 * 2)) + "," + ((_y * 2) + 40);
            s[6] = _x + "," + ((_y * 3) + (40 * 2));
            s[7] = ((_x * 2) + 40) + "," + ((_y * 3) + (40 * 2));
            s[8] = ((_x * 3) + (40 * 2)) + "," + ((_y * 3) + (40 * 2));
        }
        return s;
    }

    private static int getWidth(final int size) {
        int width = 0;
        if (size == 1) {
            width = 120;
        }
        if ((size > 1) && (size <= 4)) {
            width = 60;
        }
        if (size >= 5) {
            width = 40;
        }
        return width;
    }
}
