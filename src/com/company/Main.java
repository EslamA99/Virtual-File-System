package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static ArrayList<String> commands=new ArrayList<>(Arrays.asList(
            "CreateFile","CreateFolder","DeleteFile","DeleteFolder","DisplayDiskStatus","DisplayDiskStructure"
            ,"FreeSpaceManager","TellUser","CreateUser","Grant","Login","DeleteUser","Exit"
    ));
    private static void  parse(FileCommands command,String commandLine,int x) throws IOException {
        Disk disk=Disk.getInstance(x);
        String []commandLineSlitted=commandLine.split("\\s+",4);
        switch (commands.indexOf(commandLineSlitted[0])){
            case 0:
                command.createFile(commandLineSlitted[1]);
                break;
            case 1:
                disk.createFolder(commandLineSlitted[1]);
                break;
            case 2:
                command.deleteFile(commandLineSlitted[1]);
                break;
            case 3:
                disk.deleteFolder(commandLineSlitted[1]);
                break;
            case 4:
                disk.displayDiskStatus();
                break;
            case 5:
                disk.displayDiskStructure();
                break;
            case 6:
                disk.freeSpaceManager();
                break;
            case 7:
                System.out.println(disk.tellUser());
                break;
            case 8:
                User user =new User(commandLineSlitted[1],commandLineSlitted[2]);
                disk.createUser(user);
                break;
            case 9:
                disk.grant(commandLineSlitted[1],commandLineSlitted[2],commandLineSlitted[3]);
                break;
            case 10:
                User user1 =new User(commandLineSlitted[1],commandLineSlitted[2]);
                disk.login(user1);
                break;
            case 11:
                User user2 =new User(commandLineSlitted[1],"");
                disk.deleteUser(user2);
                break;
            case 12:
                File file;
                switch (x){
                    case 1:
                        file=new File("Contiguous.vfs");
                        file.createNewFile();
                        break;
                    case 2:
                        file=new File("Linked.vfs");
                        file.createNewFile();
                        break;
                    case 3:
                        file=new File("Indexed.vfs");
                        file.createNewFile();
                        break;
                    default:
                        return;
                }
                FileOutputStream f = new FileOutputStream(file);
                ObjectOutputStream o = new ObjectOutputStream(f);
                // Write objects to file
                o.writeObject(disk);
                o.close();
                f.close();
                disk.writeUsersAndcab();
                System.exit(0);
                break;
            default:
                System.out.println("command not found");
                break;
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        Scanner sc2 = new Scanner(System.in);
        FileCommands command;
        System.out.print("which allocation type->\n" +
                "1-Contiguous 2-Linked 3-Indexed\n" +
                "Choice: ");
        int choice=sc.nextInt();
        if(choice==1)command=new Contiguous();
        else if(choice==2)command=new Linked();
        else command=new Indexed();

        String commandLine = "";
        while (true) {
            System.out.print("Command $: ");
            commandLine = sc2.nextLine();
            parse(command,commandLine,choice);
        }
    }
}