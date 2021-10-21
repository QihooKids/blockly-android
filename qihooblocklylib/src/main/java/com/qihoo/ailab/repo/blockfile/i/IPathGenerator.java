package com.qihoo.ailab.repo.blockfile.i;

public interface IPathGenerator {

    String pathDir(String ... args);

    String fileName(String ... args);
}
