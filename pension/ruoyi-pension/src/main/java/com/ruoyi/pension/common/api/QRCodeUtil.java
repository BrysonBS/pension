package com.ruoyi.pension.common.api;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ruoyi.common.utils.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Dimension;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class QRCodeUtil {
    public static void createCodeToFile(String content, File toFile,Dimension dimension,Color front,Color background){
        assert !StringUtils.isBlank(content) : "二维码内容不能为空!";
        assert toFile != null : "存储File对象不能为空";
        BufferedImage bufferedImage = getBufferedImage(content,dimension,front,background);
        /*
         * javax.imageio.ImageIO：java扩展的图像IO
         * write(RenderedImage im, String formatName, File output)
         *       im：待写入的图像， formatName：图像写入的格式，output：写入的图像文件，文件不存在时会自动创建
         */
        try {
            ImageIO.write(bufferedImage, "png", toFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 生成二维码并输出到输出流: 默认400X400,黑白二维码
     * @param content 二维码内容
     * @param outputStream 输出流
     */
    public static void createCodeToOutputStream(String content,OutputStream outputStream){
        createCodeToOutputStream(content,outputStream,new Dimension(200,200),Color.BLACK,Color.WHITE);
    }
    /**
     * 生成二维码并输出到输出流: 默认黑白二维码
     * @param content 二维码内容
     * @param outputStream 输出流
     */
    public static void createCodeToOutputStream(String content,OutputStream outputStream,Dimension dimension){
        createCodeToOutputStream(content,outputStream,dimension,Color.BLACK,Color.WHITE);
    }
    /**
     * 生成二维码并输出到输出流, 通常用于输出到网页上进行显示
     * 输出到网页与输出到磁盘上的文件中，区别在于最后一句 ImageIO.write
     * write(RenderedImage im,String formatName,File output)：写到文件中
     * write(RenderedImage im,String formatName,OutputStream output)：输出到输出流中
     *
     * @param content  ：二维码内容
     * @param outputStream ：输出流，比如 HttpServletResponse 的 getOutputStream
     */
    public static void createCodeToOutputStream(String content, OutputStream outputStream,Dimension dimension,Color front,Color background){
        assert !StringUtils.isBlank(content) : "二维码内容不能为空!";
        BufferedImage bufferedImage = getBufferedImage(content.trim(),dimension,front,background);
        try {
            ImageIO.write(bufferedImage, "png", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createCodeAndLogoToOutputStream(String content,String logoPath,OutputStream outputStream){
        createCodeAndLogoToOutputStream(content,logoPath,outputStream,new Dimension(200,200),Color.BLACK,Color.WHITE);
    }
    public static void createCodeAndLogoToOutputStream(String content,String logoPath, OutputStream outputStream,Dimension dimension,Color front,Color background){
        assert !StringUtils.isBlank(content) : "二维码内容不能为空!";
        BufferedImage bufferedImage = getBufferedImage(content.trim(),dimension,front,background);
        //插入logo
        if(logoPath != null) insertImage(bufferedImage,logoPath);
        try {
            ImageIO.write(bufferedImage, "png", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 根据本地二维码图片解析二维码内容 注：图片必须是二维码图片，但也可以是微信用户二维码名片，上面有名称、头像也是可以的）
     *
     * @param file 本地二维码图片文件,如 E:\\logs\\2.jpg
     */
    public static String parseQRCodeByFile(File file) {
        if (file == null || file.isDirectory() || !file.exists()) return null;
        /*
         * ImageIO的BufferedImage read(URL input)方法用于读取网络图片文件转为内存缓冲图像
         * 同理还有：read(File input)、read(InputStream input)、、read(ImageInputStream stream)
         */
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
         * com.google.zxing.client.j2se.BufferedImageLuminanceSource：缓冲图像亮度源
         * 将 java.awt.image.BufferedImage 转为 zxing 的 缓冲图像亮度源
         * 关键就是下面这几句：HybridBinarizer 用于读取二维码图像数据，BinaryBitmap 二进制位图
         */
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Hashtable hints = new Hashtable();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        /*
         * 如果图片不是二维码图片，则 decode 抛异常：com.google.zxing.NotFoundException
         * MultiFormatWriter 的 encode 用于对内容进行编码成 2D 矩阵
         * MultiFormatReader 的 decode 用于读取二进制位图数据
         */
        Result result = null;
        try {
            result = new MultiFormatReader().decode(bitmap, hints);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        return result.getText();
    }
    /**
     * 根据网络二维码图片解析二维码内容, 区别仅仅在于 ImageIO.read(url); 这一个重载的方法）
     *
     * @param url 二维码图片网络地址，如 https://res.wx.qq.com/mpres/htmledition/images/mp_qrcode3a7b38.gif
     */
    public static String parseQRCodeByUrl(URL url) {
        String resultStr = null;
        if (url == null) return null;
        try {
            /*
             * ImageIO 的 BufferedImage read(URL input) 方法用于读取网络图片文件转为内存缓冲图像
             * 同理还有：read(File input)、read(InputStream input)、、read(ImageInputStream stream)
             * 如果图片网络地址错误，比如不能访问，则 read 抛异常：javax.imageio.IIOException: Can't get input stream from URL!
             */
            BufferedImage bufferedImage = ImageIO.read(url);
            /*
             * com.google.zxing.client.j2se.BufferedImageLuminanceSource：缓冲图像亮度源
             * 将 java.awt.image.BufferedImage 转为 zxing 的 缓冲图像亮度源
             * 关键就是下面这几句：HybridBinarizer 用于读取二维码图像数据，BinaryBitmap 二进制位图
             */
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Hashtable hints = new Hashtable();
            /*
             * 如果内容包含中文，则解码的字符集格式应该和编码时一致
             */
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            /*
             * 如果图片不是二维码图片，则 decode 抛异常：com.google.zxing.NotFoundException
             * MultiFormatWriter 的 encode 用于对内容进行编码成 2D 矩阵
             * MultiFormatReader 的 decode 用于读取二进制位图数据
             */
            Result result = new MultiFormatReader().decode(bitmap, hints);
            resultStr = result.getText();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return resultStr;
    }
    public static BufferedImage getBufferedImage(String content, Dimension dimension,Color front,Color background){
        /*
         * com.google.zxing.EncodeHintType：编码提示类型,枚举类型
         * EncodeHintType.CHARACTER_SET：设置字符编码类型
         * EncodeHintType.ERROR_CORRECTION：设置误差校正
         * ErrorCorrectionLevel：误差校正等级，L = ~7% correction、M = ~15% correction、Q = ~25% correction、H = ~30% correction
         *   不设置时，默认为 L 等级，等级不一样，生成的图案不同，但扫描的结果是一样的
         * EncodeHintType.MARGIN：设置二维码边距，单位像素，值越小，二维码距离四周越近
         */
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        /*
         * MultiFormatWriter:多格式写入，这是一个工厂类，里面重载了两个 encode 方法，用于写入条形码或二维码
         *      encode(String contents,BarcodeFormat format,int width, int height,Map<EncodeHintType,?> hints)
         *      contents:条形码/二维码内容
         *      format：编码类型，如 条形码，二维码 等
         *      width：码的宽度
         *      height：码的高度
         *      hints：码内容的编码类型
         * BarcodeFormat：枚举该程序包已知的条形码格式，即创建何种码，如 1 维的条形码，2 维的二维码 等
         * BitMatrix：位(比特)矩阵或叫2D矩阵，也就是需要的二维码
         */
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, dimension.width, dimension.height, hints);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
        /*
         * java.awt.image.BufferedImage：具有图像数据的可访问缓冲图像，实现了 RenderedImage 接口
         * BitMatrix 的 get(int x, int y) 获取比特矩阵内容，指定位置有值，则返回true，将其设置为前景色，否则设置为背景色
         * BufferedImage 的 setRGB(int x, int y, int rgb) 方法设置图像像素
         *      x：像素位置的横坐标，即列
         *      y：像素位置的纵坐标，即行
         *      rgb：像素的值，采用 16 进制,如 0xFFFFFF 白色
         */
        BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_BGR);
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? front.getRGB() : background.getRGB());
            }
        }
        return bufferedImage;
    }
    private static void insertImage(BufferedImage source,String logoPath){
        File file = new File(logoPath);
        assert !file.exists() : "logo文件不存在";
        Image logo = null;
        try {
            logo = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //超过最大比例则压缩
        int maxLength = (int)Math.sqrt(source.getWidth() * source.getHeight() * 0.05D);
        int logoWidth = logo.getWidth(null);
        int logoHeight = logo.getHeight(null);
        int width = logoWidth;
        int height = logoHeight;
        if(logoHeight > logoWidth && logoHeight > maxLength){
            height = maxLength;
            width = (int)(logoWidth * maxLength/(double)logoHeight);
        }
        else if(logoHeight <= logoWidth && logoWidth > maxLength){
            height = (int)(logoHeight * maxLength/(double)logoWidth);
            width = maxLength;
        }
        logo = logo.getScaledInstance(width,height,Image.SCALE_SMOOTH);
        // 插入logo
        Graphics2D graph = source.createGraphics();
        int x = (source.getWidth() - width) / 2;
        int y = (source.getHeight() - height) / 2;
        graph.drawImage(logo, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, height, width * 0.3f, height * 0.3f);
        graph.setStroke(new BasicStroke(4f));
        graph.draw(shape);
        graph.dispose();
    }
}
