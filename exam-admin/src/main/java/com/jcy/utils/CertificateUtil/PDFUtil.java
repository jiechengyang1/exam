package com.jcy.utils.CertificateUtil;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;

public class PDFUtil {

    private Document document;
    private PdfWriter writer;

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setWriter(PdfWriter writer) {
        this.writer = writer;
    }

    /**
     * 开启创建PDF对象
     *
     * @param pafPath ： 生成pdf的磁盘路径
     */
    public PDFUtil openDocument(String pafPath) throws FileNotFoundException, DocumentException, URISyntaxException, MalformedURLException {
        Document document = new Document();
        writer = PdfWriter.getInstance(document, new FileOutputStream(pafPath));
        document.open();
        this.document = document;
        return this;
    }

    /**
     * 添加图片背景
     *
     * @param absoluteX ：左边距
     * @param absoluteY ：底边距
     */
    public PDFUtil addImage(String imagePath, float absoluteX, float absoluteY) throws MalformedURLException, IOException, DocumentException {
        Image tImgCover = Image.getInstance(imagePath);
        tImgCover.setAbsolutePosition(absoluteX, absoluteY);
        float heigth = tImgCover.getHeight();
        float width = tImgCover.getWidth();
        // int percent=getPercent(heigth, width);
        int percent = getPercent2(heigth, width);
        // 设置图片居中显示
        // tImgCover.setAlignment(Image.MIDDLE);
        tImgCover.scalePercent(percent);// 表示是原来图像的比例;
        document.add(tImgCover);
        return this;
    }

    public PDFUtil addLogo(String imagePath, float absoluteX, float absoluteY) throws MalformedURLException, IOException, DocumentException {
        Image tImgCover = Image.getInstance(imagePath);
        tImgCover.setAbsolutePosition(absoluteX, absoluteY);
        tImgCover.scalePercent(20);// 表示是原来图像的比例;
        document.add(tImgCover);
        return this;
    }

    /**
     * @param certificateContent :pdf证书的中文内容
     * @param x                  ：左边距
     * @param y                  ：底边距
     * @param contentStyle       ：中文内容的样式
     */
    public PDFUtil addContent(String certificateContent, float x, float y, ContentStyle contentStyle) throws DocumentException, IOException {

        if (contentStyle == null) {
            contentStyle = new ContentStyle();
        }

        PdfContentByte canvas = writer.getDirectContent();
        // windows下用下面的
//        BaseFont bf = BaseFont.createFont(contentStyle.getTTFPath(),BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        // linux用这个
        BaseFont bf = BaseFont.createFont("STSong-Light",
                "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font secFont = new Font(bf, contentStyle.getFontSize(), contentStyle.getStyle(), contentStyle.getBaseColor());
        Phrase certificateContentPhrase = new Phrase(certificateContent, secFont);
        ColumnText.showTextAligned(canvas, contentStyle.getAlignment(), certificateContentPhrase, x, y, 0);
        return this;
    }

    /**
     * 添加日期内容
     *
     * @param x 插入pdf左边距
     * @param y 插入pdf底边距
     */
    public PDFUtil addDateContent(float x, float y, ContentStyle contentStyle) throws DocumentException, IOException {

        if (contentStyle == null) {
            contentStyle = new ContentStyle();
        }

        Date currentDate = DateTimeUtil.getCurrentDate();
        String currentDateString = DateTimeUtil.DateToString(currentDate);

        PdfContentByte canvas = writer.getDirectContent();
        BaseFont bf = BaseFont.createFont(contentStyle.getTTFPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font secFont = new Font(bf, contentStyle.getFontSize(), contentStyle.getStyle(), contentStyle.getBaseColor());
        Phrase certificateDatephrase = new Phrase(currentDateString, secFont);
        ColumnText.showTextAligned(canvas, contentStyle.getAlignment(), certificateDatephrase, x, y, 0);
        return this;
    }

    /**
     * 释放资源
     */
    public void close() {
        document.close();
    }

    /**
     * 第二种解决方案，统一按照宽度压缩
     * 这样来的效果是，所有图片的宽度是相等的，自我认为给客户的效果是最好的
     */
    public int getPercent2(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        p2 = 595 / w * 100;
        System.out.println("--" + p2);
        p = Math.round(p2);
        return p;
    }

    /**
     * 第一种解决方案
     * 在不改变图片形状的同时，判断，如果h>w，则按h压缩，否则在w>h或w=h的情况下，按宽度压缩
     */

    public int getPercent(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        if (h > w) {
            p2 = 297 / h * 100;
        } else {
            p2 = 210 / w * 100;
        }
        p = Math.round(p2);
        return p;
    }
}
