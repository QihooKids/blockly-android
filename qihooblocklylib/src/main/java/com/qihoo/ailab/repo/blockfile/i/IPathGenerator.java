package com.qihoo.ailab.repo.blockfile.i;

import java.io.File;

public interface IPathGenerator {

    String pathDir(String ... args);

    String fileName(String ... args);

    File generateFilePath(String name);
}
