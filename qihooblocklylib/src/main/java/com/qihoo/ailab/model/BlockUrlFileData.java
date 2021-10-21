package com.qihoo.ailab.model;

import android.support.annotation.NonNull;

import com.qihoo.ailab.repo.blockfile.BlockFilePathGenerator;

import java.io.File;

public class BlockUrlFileData {

    private String url;

    private String version;

    private BlockFilePathGenerator generator;

    public BlockUrlFileData(@NonNull String url, @NonNull String version, @NonNull BlockFilePathGenerator generator) {
        this.url = url;
        this.version = version;
        this.generator = generator;
    }



    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }


    public BlockFilePathGenerator getGenerator() {
        return generator;
    }

    /**
     * Get the abs path for the block file download.
     * @return
     */
    public String getDownloadFilePath(){
        return new File(generator.pathDir(),generator.fileName(url, String.valueOf(version))).getAbsolutePath();
    }
}
