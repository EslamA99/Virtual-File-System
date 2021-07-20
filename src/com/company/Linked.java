package com.company;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Scanner;

public class Linked implements FileCommands,Parser {
    Disk disk;

    public Linked(){
        disk=Disk.getInstance(2);
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
        try {
            int start=Integer.parseInt(parsedCommand[1].trim()),end=Integer.parseInt(parsedCommand[2].trim());
        }catch (Exception e){
            System.out.println("create file command  has mistake");
            return;
        }

        int x1=0,y1=0;
        Scanner sc=new Scanner(System.in);
        ArrayList<Integer>arr=new ArrayList<>();
        while(y1!=-1){
            x1=sc.nextInt();
            y1=sc.nextInt();
            arr.add(x1);
            arr.add(y1);
            //disk.diskBlocks[x1]=y1;
        }
        for(int i=0;i<arr.size();i+=2){
            if(arr.get(i)>disk.numOfBlocks-1||arr.get(i)<0){
                System.out.println("wrong block");
                return;
            }
            if(disk.diskBlocks[arr.get(i)]!=0){
                System.out.println(arr.get(i)+" has been allocated");
                return;
            }
        }
        int []allocated=new int[arr.size()/2];
        int cont=0;
        for(int i=0;i<arr.size();i+=2){
            disk.diskBlocks[arr.get(i)]=arr.get(i+1);
            allocated[cont]=arr.get(i);
            cont++;
        }
        FileD file=new FileD(path,allocated,false);
        dir.files.add(file);
        //System.out.println(dir.directoryPath);
        String temp1="<"+spitedPath[spitedPath.length-2]+">",temp2=spitedPath[spitedPath.length-1];
        disk.writeToFile(temp1,temp2);
        disk.removeFile(spitedPath[spitedPath.length-1]);
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
        FileD file=disk.getFileFromDir(dir,spitedPath[spitedPath.length-1]);
        if(file==null){
            System.out.println("file not found");
            return;
        }
        disk.deleteFile(file,dir);
    }

    @Override
    public String[]createFileParser(String command) {
        return command.split("\\s+");
    }
}
