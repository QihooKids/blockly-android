package com.qihoo.ailab.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    private static final String TAG = "QihooLogger-FileUtils";

    /*
     * zip given file
     */
    public static File zipFile(File f) {
        if (f == null) {
            return null;
        }

        String zipFile = f.getPath() + ".zip";
        File zip = new File(zipFile);
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zip));
            ZipEntry entry = new ZipEntry(f.getName());
            zos.putNextEntry(entry);
            FileInputStream fis = new FileInputStream(f);
            byte[] buf = new byte[1024];
            int read = -1;
            while ((read = fis.read(buf)) != -1) {
                zos.write(buf, 0, read);
            }
            zos.flush();
            Log.d(TAG, "generate zip file : " + zipFile);
        } catch (Exception e) {
            Log.d(TAG, "zipFile exception1:" + e);
        } finally {
            try {
                zos.closeEntry();
                zos.flush();
                zos.close();
            } catch (Exception e) {
                Log.d(TAG, "zipFile exception2:" + e);
            }
        }

        return zip;
    }

    public static boolean isCompressed(File file) {
        if (file == null) {
            return false;
        }

        final String name = file.getName();
        final String ext = name.substring(name.lastIndexOf('.') + 1, name.length());
        final String[] zipExts = {"gz", "zip", "rar", "7z", "tgz", "png"};
        for (String zipExt : zipExts) {
            if (zipExt.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    public synchronized static int append(File f, String output) {
        try {
            if (f == null) {
                return -1;
            }
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(f, true));
            Log.d(TAG, "write " + output);

            if (TextUtils.isEmpty(output)) {
                return -1;
            }

            outputWriter.write(output);
            outputWriter.newLine();
            outputWriter.flush();
            outputWriter.close();
            outputWriter = null;
            return 1;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized static int write(File f, String output) {
        try {
            if (f == null) {
                return -1;
            }
            BufferedWriter outputBehaviorWriter = new BufferedWriter(new FileWriter(f, false));
            Log.d(TAG, "File:" + f.getName() + ", writeBehavior " + output);

            if (TextUtils.isEmpty(output)) {
                return -1;
            }

            outputBehaviorWriter.write(output);
            outputBehaviorWriter.flush();
            outputBehaviorWriter.close();
            return 1;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public synchronized static String readFromFile(File file) {
        String ret = "";
        try {
            InputStream inputStream = new FileInputStream(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                inputStream = null;
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        return ret;
    }


    public static void copyFileOrThrow(File srcFile, File destFile) throws IOException {
        InputStream in = new FileInputStream(srcFile);
        Throwable var3 = null;

        try {
            copyToFileOrThrow(in, destFile);
        } catch (Throwable var12) {
            var3 = var12;
            throw var12;
        } finally {
            if (in != null) {
                if (var3 != null) {
                    try {
                        in.close();
                    } catch (Throwable var11) {
                        var3.addSuppressed(var11);
                    }
                } else {
                    in.close();
                }
            }

        }

    }

    public static void copyToFileOrThrow(InputStream inputStream, File destFile) throws IOException {
        if (destFile.exists()) {
            destFile.delete();
        }
        if(destFile.getParentFile() != null){
            destFile.getParentFile().mkdirs();
        }

        FileOutputStream out = new FileOutputStream(destFile);
        try {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while((bytesRead = inputStream.read(buffer)) >= 0) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            out.flush();
            try {
                out.getFD().sync();
            } catch (IOException var10) {
            }
            out.close();
        }
    }

    public static boolean deleteContentsAndDir(File dir) {
        return deleteContents(dir) ? dir.delete() : false;
    }

    public static boolean deleteContents(File dir) {
        File[] files = dir.listFiles();
        boolean success = true;
        if (files != null) {
            File[] var3 = files;
            int var4 = files.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File file = var3[var5];
                if (file.isDirectory()) {
                    success &= deleteContents(file);
                }
                if (!file.delete()) {
                    Log.w("FileUtils", "Failed to delete " + file);
                    success = false;
                }
            }
        }
        return success;
    }

    public static File zipDir(File dir){
        if(!dir.isDirectory()){
            return zipFile(dir);
        }
        byte[] buffer = new byte[1024];
        String source = dir.getName();
        String zipFile = dir.getPath() + ".zip";
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
            System.out.println("Output to Zip : " + zipFile);
            FileInputStream in = null;
            List<String> fileList = generateFileList(dir);
            for (String file: fileList) {
                System.out.println("File Added : " + file);
                ZipEntry ze = new ZipEntry(source + File.separator + file);
                zos.putNextEntry(ze);
                try {
                    in = new FileInputStream(dir + File.separator + file);
                    int len;
                    while ((len = in .read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                } finally {
                    in.close();
                }
            }
            zos.closeEntry();
            System.out.println("Folder successfully compressed");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File(zipFile);
    }

    private static List<String> generateFileList(File rootDir){

        List files = new ArrayList();
        if(rootDir.isDirectory()) {
            generateFileList(rootDir, rootDir, files);
        }
        return files;
    }

    private static void generateFileList(File rootDir,File node , List fileList) {
        // add file only
        if(fileList == null) {
            return;
        }
        if (node.isFile()) {
            fileList.add(node.getAbsolutePath().substring(rootDir.getAbsolutePath().length()+1));
        } else if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename: subNote) {
                generateFileList(rootDir,new File(node, filename), fileList);
            }
        }
    }

}


