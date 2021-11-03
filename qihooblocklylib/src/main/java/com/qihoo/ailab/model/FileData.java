package com.qihoo.ailab.model;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.qihoo.ailab.repo.blockfile.BlockFilePathGenerator;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FileData {

    public static final int DATA_JSON = 1;
    public static final int DATA_XML = 2;
    public static final int DATA_URL = 3;

    @Retention(RetentionPolicy.CLASS)
    @IntDef({DATA_JSON, DATA_URL, DATA_XML})
    public @interface DataType {
    }

    private String data;

    private String version;

    private int type;

    private String savePath;


    /**
     * The rule file data, can be string content or file url.
     * @param data
     * @param dataType
     * @param version
     * @param savePath
     */
    public FileData(@NonNull String data, @NonNull @DataType int dataType, @NonNull String version, @NonNull String savePath) {
        this.data = data;
        this.version = version;
        this.savePath = savePath;
        this.type = dataType;
    }

    public String getData() {
        return data;
    }

    public int getType(){
        return type;
    }

    public String getVersion() {
        return version;
    }


}
