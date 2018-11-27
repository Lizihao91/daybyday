package com.liyyang.yunnote.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * Created by leeyang on 16/7/30.
 */
public class Zip4jUtil {

    /**
     * 解压
     * @param zipFile
     * @param dest
     * @param passwd
     */
    public static int unzip(File zipFile, String dest, String passwd) {
        try {
            ZipFile zFile = new ZipFile(zipFile);//待解压文件
            zFile.setFileNameCharset("GBK");
            if (!zFile.isValidZipFile()) {
                throw new ZipException("文件不合法");
            }
            File destDir = new File(dest);//解压目录
            if (destDir.isDirectory() && !destDir.exists()) {
                destDir.mkdir();
            }
            if (zFile.isEncrypted()) {
                zFile.setPassword(passwd.toCharArray());//设置密码
            }
            zFile.extractAll(dest);
            return 1;
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 压缩
     */

    public static String zip(String src, String dest, boolean isCreateDir, String passwd) {
        File srcFile = new File(src);
        dest = buildDestinationZipPath(srcFile,dest);
        ZipParameters parameters = new ZipParameters();

        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);//压缩模式
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        if (passwd != null) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            parameters.setPassword(passwd.toCharArray());
        }
        try {
            ZipFile zipFile = new ZipFile(dest);
            if (srcFile.isDirectory()) {
                if (!isCreateDir) {
                    File[] subFiles = srcFile.listFiles();
                    ArrayList<File> temp = new ArrayList<File>();
                    Collections.addAll(temp, subFiles);
                    zipFile.addFiles(temp, parameters);
                    return dest;
                }
                zipFile.addFolder(srcFile, parameters);
            } else {
                zipFile.addFile(srcFile, parameters);
            }
            return dest;
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String buildDestinationZipPath(File srcFile, String destParam) {
        if (destParam == null) {
            if (srcFile.isDirectory()) {
                destParam = srcFile.getParent() + File.separator + srcFile.getName() + ".zip";
            } else {
                String fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
                destParam = srcFile.getParent() + File.separator + fileName + ".zip";
            }
        } else {
            creatDestDirectoryIfNeed(destParam);
            if (destParam.endsWith(File.separator)){
                String fileName = "";
                if (srcFile.isDirectory()){
                    fileName = srcFile.getName();
                }else {
                    fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf("."));
                }
                destParam += fileName + ".zip";
            }
        }
        return destParam;
    }

    private static void creatDestDirectoryIfNeed(String destParam){
        File destDir = null;
        if (destParam.endsWith(File.separator)){
            destDir = new File(destParam);
        }else {
            destDir = new File(destParam.substring(0, destParam.lastIndexOf(File.separator)));
        }
        if (!destDir.exists()){
            destDir.mkdir();
        }
    }
}
