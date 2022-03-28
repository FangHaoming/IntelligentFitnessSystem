package com.example.intelligentfitnesssystem.util;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author hzh
 */
public class FileUtils {
    public static byte[] fileToBytes(String filePath) throws IOException {
        byte[] buffer = null;
        File file = new File(filePath);
        buffer = getBytes(file);
        return buffer;
    }

    public static byte[] fileToBytes(File file) throws IOException {
        byte[] buffer = null;
        buffer = getBytes(file);
        return buffer;
    }

    private static byte[] getBytes(File file) throws IOException {
        byte[] buffer;
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        byte[] b = new byte[1000];
        int n;
        while ((n = fis.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        buffer = bos.toByteArray();
        return buffer;
    }

    public static File bytesToFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            file = new File(filePath + fileName);
            if (!file.getParentFile().exists()) {
                //文件夹不存在 生成
                file.getParentFile().mkdirs();
            }
            int i = 1;
            while (file.exists()) {
                String newName = fileName.split("\\.")[0] + "(" + i + ")." + fileName.split("\\.")[1];
                i++;
                file = new File(filePath, newName);
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public static String sha1String(String str) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(str.getBytes());
            return new BigInteger(1, sha1.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String sha1String(byte[] bytes) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(bytes);
            return new BigInteger(1, sha1.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
