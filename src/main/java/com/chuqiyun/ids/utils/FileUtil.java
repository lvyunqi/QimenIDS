package com.chuqiyun.ids.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author mryunqi
 * @date 2023/3/7
 */
public class FileUtil {
    private static final String ROOT_PATH = System.getProperty("user.dir");

    public static String isDirectory(String path) {
        String Path = ROOT_PATH.concat(path);
        File directory = new File(Path);
        if (!directory.exists() && !directory.isDirectory()) {
            String filePath = ROOT_PATH.concat(path).concat("/config.ini");
            boolean result = directory.mkdirs();
            File file = new File(filePath);
            if (result) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    return "配置文件创建失败";
                }
                return "配置文件已创建";
            }
            return "配置文件创建失败";
        } else {
            return "配置文件已存在";
        }
    }


    public static Boolean isFile(String filePath) {
        String Path = ROOT_PATH.concat(filePath);
        File file = new File(Path);
        return file.exists();
    }

    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

}
