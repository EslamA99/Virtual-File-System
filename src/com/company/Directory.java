package com.company;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Directory implements Serializable {
    String directoryPath;
    ArrayList<FileD> files;
    ArrayList<Directory> subDirectories;
    boolean hasUser;
    public Directory(String directoryPath) {
        this.directoryPath=directoryPath;
        files=new ArrayList<>();
        subDirectories=new ArrayList<>();
    }

    public void printDirectoryStructure(int level) {
    /*this method prints the directory name and its files
    then makes recursion to loop on the subDirectories to print their structure too.

    The level parameter can be used to print spaces before the directory name is printed to show its level in the structure */

    }

}
