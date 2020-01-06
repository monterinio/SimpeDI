package org.example.ioc.scanner;

class Directory {
    private final String dir;
    private final DirectoryType dirType;

    Directory(String dir, DirectoryType dirType) {
        this.dir = dir;
        this.dirType = dirType;
    }

    String getDir() {
        return dir;
    }

    DirectoryType getDirType() {
        return dirType;
    }
}
