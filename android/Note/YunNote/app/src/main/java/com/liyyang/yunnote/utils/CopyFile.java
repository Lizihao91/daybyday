package com.liyyang.yunnote.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 复制文件
 * Created by leeyang on 16/7/30.
 */
public class CopyFile {

    File fromFile, toFile;

    public void CopyFile(String path, String newPath){
        this.fromFile = new File(path);
        copyFile(newPath);
    }

    public void copyFile(String newPath){
        if (!fromFile.exists()){return;}

        if (!fromFile.isFile()){return;}

        if (!fromFile.canRead()){return;}

        try {
            String fileName = fromFile.getName();
            toFile = new File(newPath+fileName);
            if (!toFile.getParentFile().exists()){
                toFile.getParentFile().mkdir();
            }

            if (toFile.exists()){
                toFile.delete();
            }
            FileInputStream fis = new FileInputStream(fromFile);
            FileOutputStream fos = new FileOutputStream(toFile);
            byte b[] = new byte[1024];
            int c;
            while ((c = fis.read(b)) > 0){
                fos.write(b,0,c);
            }
            fis.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
