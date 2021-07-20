package com.company;

import javafx.util.Pair;

import java.util.Scanner;

public class Contiguous implements FileCommands,Parser{
    Disk disk;

    public Contiguous(){
        disk=Disk.getInstance(1);
    }
    @Override
    public void createFile(String command) {

        String[]parsedCommand=createFileParser(command);
        String path=parsedCommand[0];
        Directory dir=disk.getLastDirFromPath(path);

        if(dir==null)
            return;
        Pair<Boolean,Boolean>cab=disk.getUserCapability(path);
        if(cab==null){
            System.out.println("u don't have grant to this directory");
            return;
        }
        if(!cab.getKey()){
            System.out.println("u don't have grant create to this directory");
        }
        String []spitedPath=path.split("/");
        if(disk.getFileFromDir(dir,spitedPath[spitedPath.length-1])!=null){
            System.out.println("file created before");
            return;
        }
        int start,size;
        try {
            start=Integer.parseInt(parsedCommand[1]);
            size=Integer.parseInt(parsedCommand[2]);
        }catch (Exception e){
            System.out.println("command has mistake");
            return;
        }

        for(int i =start;i<size;i++) {
            if (disk.diskBlocks[i] != 0) {
                System.out.println("No enough space in this allocation");
                return;
            }
        }
        int []allocated=new int[size];
        for(int i =0;i<size;i++) {
            disk.diskBlocks[start+i]=1;
            allocated[i]=start+i;
        }
        FileD file=new FileD(path,allocated,false);
        dir.files.add(file);
        String temp1="<"+spitedPath[spitedPath.length-2]+">",temp2=spitedPath[spitedPath.length-1];
        disk.writeToFile(temp1,temp2);
    }

    @Override
    public void deleteFile(String command) {
        String[]parsedCommand=createFileParser(command);
        String path=parsedCommand[0];
        Directory dir=disk.getLastDirFromPath(path);
        if(dir==null)
            return;
        Pair<Boolean,Boolean>cab=disk.getUserCapability(path);
        if(cab==null){
            System.out.println("u don't have grant to this directory");
            return;
        }
        if(!cab.getValue()){
            System.out.println("u don't have grant to delete from this directory");
        }
        String []spitedPath=path.split("/");
        if(disk.getFileFromDir(dir,spitedPath[spitedPath.length-1])==null){
            System.out.println("file not found");
            return;
        }
        FileD file=disk.getFileFromDir(dir,spitedPath[spitedPath.length-1]);
        disk.deleteFile(file,dir);
        disk.removeFile(spitedPath[spitedPath.length-1]);
    }

    @Override
    public String[]createFileParser(String command) {
        return command.split("\\s+");
    }
}
