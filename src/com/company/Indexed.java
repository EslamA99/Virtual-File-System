package com.company;

import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Indexed implements FileCommands,Parser {
    Disk disk;

    public Indexed(){
        disk=Disk.getInstance(3);
    }
    @Override
    public void createFile(String command) {
        String[]parsedCommand=createFileParser(command);
        String path=parsedCommand[0];

        Directory dir=disk.getLastDirFromPath(path);
        if(dir==null)
            return;
        Pair<Boolean,Boolean> cab=disk.getUserCapability(path);
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
        int x;
        try {
            x=Integer.parseInt(parsedCommand[1].trim());
        }catch (Exception e){
            System.out.println("command create has mistake");
            return;
        }
        Scanner sc=new Scanner(System.in);
        ArrayList<Integer>arr=new ArrayList<>();
        if(disk.diskBlocks[x]!=0){
            System.out.println("the block is not empty");
            return;
        }
        arr.add(x);
        while (true){
            x=sc.nextInt();
            if(x==-1)
                break;
            if(disk.diskBlocks[x]!=0){
                System.out.println("the block is not empty");
                return;
            }
            arr.add(x);
        }
        int []allocated=new int[arr.size()];
        for(int i=0;i<arr.size();i++){
            allocated[i]=arr.get(i);
            disk.diskBlocks[arr.get(i)]=1;
        }
        FileD fileD=new FileD(path,allocated,false);
        dir.files.add(fileD);
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
            System.out.println("u don't have grant to delete this directory");
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
