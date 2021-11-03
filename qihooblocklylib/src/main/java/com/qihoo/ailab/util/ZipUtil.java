/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Apache Harmony HEADER because the code in this class comes mostly from ZipFile, ZipEntry and
 * ZipConstants from android libcore.
 */

package com.qihoo.ailab.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipException;

/**
 * Tools to build a quick partial crc of zip files.
 */
public final class ZipUtil {
    private static final String TAG = "SystemUpdate/ZipUtil";

    public static class CentralDirectory {
        public long offset;
        public long size;
    }

    public static class FileHeader {
        public int number;
        public long crc;
        public long compressedSize;
        public long uncompressedSize;
        public long offset;
        public String fileName;
    }

    /* redefine those constant here because of bug 13721174 preventing to compile using the
     * constants defined in ZipFile */
    public static final int ENDHDR = 22;
    public static final int ENDSIG = 0x6054b50;
    public static final int FILEHEADERSIG = 0x02014b50;

    /**
     * Size of reading buffers.
     */
    private static final int BUFFER_SIZE = 0x4000;

    /**
     * Compute crc32 of the central directory of an apk. The central directory contains
     * the crc32 of each entries in the zip so the computed result is considered valid for the whole
     * zip file. Does not support zip64 nor multidisk but it should be OK for now since ZipFile does
     * not either.
     */
    public static long getZipCrc(File apk) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(apk, "r");
        try {
            CentralDirectory dir = findCentralDirectory(raf);

            return computeCrcOfCentralDir(raf, dir);
        } finally {
            raf.close();
        }
    }

    /* Package visible for testing */
    public static CentralDirectory findCentralDirectory(RandomAccessFile raf) throws IOException,
            ZipException {
        long scanOffset = raf.length() - ENDHDR;
        if (scanOffset < 0) {
            throw new ZipException("File too short to be a zip file: " + raf.length());
        }

        long stopOffset = scanOffset - 0x10000 /* ".ZIP file comment"'s max length */;
        if (stopOffset < 0) {
            stopOffset = 0;
        }

        int endSig = Integer.reverseBytes(ENDSIG);
        while (true) {
            raf.seek(scanOffset);
            if (raf.readInt() == endSig) {
                break;
            }

            scanOffset--;
            if (scanOffset < stopOffset) {
                throw new ZipException("End Of Central Directory signature not found");
            }
        }
        // Read the End Of Central Directory. ENDHDR includes the signature
        // bytes,
        // which we've already read.

        // Pull out the information we need.
        raf.skipBytes(2); // diskNumber
        raf.skipBytes(2); // diskWithCentralDir
        raf.skipBytes(2); // numEntries
        raf.skipBytes(2); // totalNumEntries
        CentralDirectory dir = new CentralDirectory();
        dir.size = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
        dir.offset = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
        return dir;
    }

    /* Package visible for testing */
    public static long computeCrcOfCentralDir(RandomAccessFile raf, CentralDirectory dir)
            throws IOException {
        CRC32 crc = new CRC32();
        long stillToRead = dir.size;
        raf.seek(dir.offset);
        int length = (int) Math.min(BUFFER_SIZE, stillToRead);
        byte[] buffer = new byte[BUFFER_SIZE];
        length = raf.read(buffer, 0, length);
        while (length != -1) {
            crc.update(buffer, 0, length);
            stillToRead -= length;
            if (stillToRead == 0) {
                break;
            }
            length = (int) Math.min(BUFFER_SIZE, stillToRead);
            length = raf.read(buffer, 0, length);
        }
        return crc.getValue();
    }

    public static List<FileHeader> getZipHeader(File apk) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(apk, "r");
        try {
            CentralDirectory dir = findCentralDirectory(raf);

            return findHeaderFromCentralDir(raf, dir);
        } finally {
            raf.close();
        }
    }

    public static List<FileHeader> findHeaderFromCentralDir(RandomAccessFile raf, CentralDirectory dir)
            throws IOException {
        int number = 0;
        long scanOffset = dir.offset;
        List<FileHeader> fileHeaderList = new ArrayList<FileHeader>();

        int endSig = Integer.reverseBytes(ENDSIG);
        int fileHeaderSig = Integer.reverseBytes(FILEHEADERSIG);
        while (true) {
            raf.seek(scanOffset);
            int findEndOrFileHeader = raf.readInt();
            if (findEndOrFileHeader == endSig) {
                //Log.i(TAG, "findHeaderFromCentralDir finish");
                break;
            }
            if (findEndOrFileHeader == fileHeaderSig) {
                //Log.i(TAG, "findHeaderFromCentralDir header");
                FileHeader fileHeader = new FileHeader();
                fileHeader.number = number++;
                // Pull out the information we need.
                raf.skipBytes(2); // Version made by
                raf.skipBytes(2); // Version needed to extract (minimum)
                raf.skipBytes(2); // General purpose bit flag
                raf.skipBytes(2); // Compression method
                raf.skipBytes(2); // File last modification time
                raf.skipBytes(2); // File last modification date
                fileHeader.crc = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
                fileHeader.compressedSize = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
                fileHeader.uncompressedSize = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
                raf.skipBytes(2); // File name length (n)
                raf.skipBytes(2); // Extra field length (m)
                raf.skipBytes(2); // File comment length (k)
                raf.skipBytes(2); // Disk number where file starts\
                raf.skipBytes(2); // Internal file attributes
                raf.skipBytes(4); // External file attributes
                fileHeader.offset = Integer.reverseBytes(raf.readInt()) & 0xFFFFFFFFL;
                fileHeader.fileName = raf.readLine();
                fileHeaderList.add(fileHeader);
                //Log.i(TAG, "findHeaderFromCentralDir fileHeader.number:" + fileHeader.number);
                //Log.i(TAG, "findHeaderFromCentralDir fileHeader.crc:" + fileHeader.crc);
                //Log.i(TAG, "findHeaderFromCentralDir fileHeader.compressedSize:" + fileHeader.compressedSize);
                //Log.i(TAG, "findHeaderFromCentralDir fileHeader.uncompressedSize:" + fileHeader.uncompressedSize);
                //Log.i(TAG, "findHeaderFromCentralDir fileHeader.offset:" + fileHeader.offset);
            }

            scanOffset++;

            if (scanOffset > raf.length()) {
                throw new ZipException("End Of Central Directory signature not found");
            }
        }

        return fileHeaderList;
    }
}
