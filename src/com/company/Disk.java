package com.company;

import javafx.util.Pair;

import java.io.*;
import java.util.*;


public class Disk implements PublicCommands, Parser, Serializable {
    private Directory root;
    public int numOfBlocks;
    public int[] diskBlocks;
    private static Disk disk = null;
    private ArrayList<User> users;
    private User onlineUser;
    private Map<User, Map<String, Pair<Boolean,Boolean>>>folderCapabilities;
    private Disk() {

    }
    public Directory getDirectory(Directory currDirectory, String aimedDirectory) {
        for (Directory dir : currDirectory.subDirectories) {
            String[] x = dir.directoryPath.split("/");
            if (x[x.length - 1].equals(aimedDirectory))
                return dir;
        }
        return null;
    }

    public FileD getFileFromDir(Directory currDirectory, String aimedFile) {
        for (FileD file : currDirectory.files) {
            String[] x = file.filePath.split("/");
            if (x[x.length - 1].equals(aimedFile))
                return file;
        }
        return null;
    }

    public Directory getLastDirFromPath(String path) {
        String[] spitedPath = path.split("/");
        Directory currDir;
        if (!spitedPath[0].equals("root")) {
            return null;
        } else {
            currDir = disk.getRoot();
        }
        for (int i = 1; i < spitedPath.length - 1; i++) {
            currDir = disk.getDirectory(currDir, spitedPath[i]);
            if (currDir == null) {
                System.out.println("wrong Path");
                return null;
            }
        }
        /*if(disk.getFileFromDir(currDir,spitedPath[spitedPath.length-1])==null){
            System.out.println("file Created Before");
            return null;
        }*/
        return currDir;
    }
    public Directory checkPath(String path) {
        String[] spitedPath = path.split("/");
        Directory currDir;
        if (!spitedPath[0].equals("root")) {
            return null;
        } else {
            currDir = disk.getRoot();
        }
        for (int i = 1; i < spitedPath.length ; i++) {
            currDir = disk.getDirectory(currDir, spitedPath[i]);
            if (currDir == null) {
                System.out.println("wrong Path");
                return null;
            }
        }
        /*if(disk.getFileFromDir(currDir,spitedPath[spitedPath.length-1])==null){
            System.out.println("file Created Before");
            return null;
        }*/
        return currDir;
    }
    public void grant(String name,String path,String cab){
        if(cab.length()!=2) {
            System.out.println("wrong command");
            return;
        }

        if(!onlineUser.getUsername().equals("admin")){
            System.out.println("u cannot grant user");
            return;
        }

        Directory dir = disk.checkPath(path);
         if(dir == null)
            return;
         User tmpUser=null;
         for(User user:users){
             if(user.getUsername().equals(name)){
                tmpUser=user;
             }
         }
         if(tmpUser==null){
             System.out.println("userNotFound");
             return;
         }
         Map<String,Pair<Boolean,Boolean>>map=disk.folderCapabilities.get(tmpUser);

         map.put(path,new Pair<>(cab.charAt(0)=='1',cab.charAt(1)=='1'));
        /*Directory tmp = disk.getDirectory(dir, spitedPath[spitedPath.length - 1]);
        if (tmp != null) {
            System.out.println("directory found");
            return;
        }*/
    }
    public Pair<Boolean,Boolean> getUserCapability(String path){
        Map<String,Pair<Boolean,Boolean>>folderC;
        if(!disk.folderCapabilities.containsKey(onlineUser)){
            return null;
        }
        folderC=disk.folderCapabilities.get(onlineUser);
        String tmp="";
        for(Character x:path.toCharArray()){
            if(x=='/'){
                if(folderC.containsKey(tmp)){
                    return folderC.get(tmp);
                }
            }
            tmp+=x;
        }
        return folderC.getOrDefault(tmp,null);
    }


    public void deleteDirectory(Directory currDir) {
        for (int i = 0; i < currDir.files.size(); i++) {
            FileD file = currDir.files.get(i--);
            deleteFile(file, currDir);
        }
        for (int i = 0; i < currDir.subDirectories.size(); i++) {
            deleteDirectory(currDir.subDirectories.get(i));
            for (User user:users){
                Map<String,Pair<Boolean,Boolean>>foldC=disk.folderCapabilities.get(user);
                foldC.remove(currDir.directoryPath);
            }
            currDir.subDirectories.remove(i--);
        }
    }

    public void deleteFile(FileD file, Directory dir) {
        //file=disk.getFileFromDir(dir,spitedPath[spitedPath.length-1]);
        for (int i = 0; i < file.allocatedBlocks.length; i++) {
            disk.diskBlocks[file.allocatedBlocks[i]] = 0;
            file.allocatedBlocks[i] = 0;
        }
        dir.files.remove(file);
        file.deleted = true;
    }



    public void printFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("DiskStructure.vfs"));
            while (reader.readLine() != null) System.out.println(reader.toString());
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void loadDateFile(int x) throws IOException, ClassNotFoundException {
        File f;
        switch (x) {
            case 1:
                f = new File("Contiguous.vfs");
                break;
            case 2:
                f = new File("Linked.vfs");
                break;
            case 3:
                f = new File("Indexed.vfs");
                break;
            default:
                return;
        }
        FileInputStream fi = new FileInputStream(f);
        ObjectInputStream oi = new ObjectInputStream(fi);
        disk = (Disk) oi.readObject();
        for(User user:disk.users){
            if(user.getUsername().equals("admin")){
                disk.onlineUser=user;
                break;
            }

        }
        oi.close();
        fi.close();

    }
    public static Disk getInstance(int x) {
        if (disk == null) {
            try {
                disk = new Disk();
                File f = new File("DiskStructure.vfs");

                if (!f.exists()) {
                    Scanner sc = new Scanner(System.in);
                    System.out.print("enter num of Blocks: ");
                    disk.numOfBlocks = sc.nextInt();
                    disk.diskBlocks = new int[disk.numOfBlocks];
                    FileWriter fw = new FileWriter("DiskStructure.vfs");
                    fw.write("<root>\n");
                    fw.close();
                    disk.root = new Directory("root");
                    disk.users=new ArrayList<>();
                    User admin = new User("admin", "admin");
                    disk.users.add(admin);
                    disk.onlineUser=admin;
                    disk.folderCapabilities=new HashMap<>();
                    Map <String,Pair<Boolean,Boolean>>map=new HashMap<>();
                    disk.folderCapabilities.put(admin,map);
                    map.put("root",new Pair<>(true,true));

                } else {
                    try {
                        loadDateFile(x);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return disk;
    }

    public Directory getRoot() {
        return root;
    }

    public void removeFolder(String file) {
        FileWriter fw = null;
        try {
            fw = new FileWriter("DiskStructure.vfs", true);
            BufferedReader reader = new BufferedReader(new FileReader("DiskStructure.vfs"));
            String str, temp = "";
            int size = 0, size2 = 0;
            boolean foundFolder = false;
            file = "<" + file + ">";
            while ((str = reader.readLine()) != null) {
                if (str.trim().equals(file)) {
                    size = str.length() - str.trim().length();
                    foundFolder = true;
                } else if (foundFolder) {
                    size2 = str.length() - str.trim().length();
                    if (size >= size2) {
                        temp += str + "\n";
                        foundFolder = false;
                    }
                } else
                    temp += str + "\n";
            }
            fw.close();
            reader.close();
            fw = new FileWriter("DiskStructure.vfs", false);
            fw.write(temp);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeFile(String file) {
        FileWriter fw = null;
        try {
            fw = new FileWriter("DiskStructure.vfs", true);
            BufferedReader reader = new BufferedReader(new FileReader("DiskStructure.vfs"));
            String str, temp = "";
            while ((str = reader.readLine()) != null) {
                if (str.trim().equals(file))
                    continue;
                temp += str + "\n";
            }
            fw.close();
            reader.close();
            fw = new FileWriter("DiskStructure.vfs", false);
            fw.write(temp);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writeToFile(String fold, String file) {
        try {
            FileWriter fw = new FileWriter("DiskStructure.vfs", true);
            BufferedReader reader = new BufferedReader(new FileReader("DiskStructure.vfs"));
            String str, temp = "";
            int size = 0;
            while ((str = reader.readLine()) != null) {
                temp += str + "\n";
                if (fold.equals(str.trim())) {
                    size = (str.length() - str.trim().length());
                    for (int i = 0; i <= size; i++) temp += ' ';
                    temp += file + "\n";
                }
            }
            fw.close();
            reader.close();
            fw = new FileWriter("DiskStructure.vfs", false);
            fw.write(temp);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromFile() throws IOException {
        FileWriter fw = null;
        try {
            fw = new FileWriter("DiskStructure.vfs", true);
            BufferedReader reader = new BufferedReader(new FileReader("DiskStructure.vfs"));
            String str, temp = "";
            while ((str = reader.readLine()) != null)
                temp += str + "\n";

            System.out.println(temp);
            fw.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getAllocatedBlocks() {
        ArrayList<Integer> allocatedBlocks = new ArrayList<>();
        for (int i = 0; i < diskBlocks.length; i++) {
            if (diskBlocks[i] != 0) allocatedBlocks.add(i);
        }
        return allocatedBlocks;
    }

    public ArrayList<Integer> getEmptyBlocks() {
        ArrayList<Integer> emptyBlocks = new ArrayList<>();
        for (int i = 0; i < diskBlocks.length; i++) {
            if (diskBlocks[i] == 0) emptyBlocks.add(i);
        }
        return emptyBlocks;
    }

    private ArrayList<Integer> getDiskBlocks() {
        ArrayList<Integer> tmp = new ArrayList<>();
        for (int i = 0; i < disk.diskBlocks.length; i++) {
            if (disk.diskBlocks[i] == 0) tmp.add(0);
            else tmp.add(1);
        }
        return tmp;
    }

    @Override
    public void deleteFolder(String command) {
        String[] parsedCommand = createFileParser(command);
        String path = parsedCommand[0];
        Directory dir = disk.getLastDirFromPath(path);
        if (dir == null)
            return;
        Pair<Boolean,Boolean>cab=disk.getUserCapability(path);
        if(cab==null){
            System.out.println("u don't have grant to this directory");
            return;
        }
        if(!cab.getValue()){
            System.out.println("u don't have grant to delete this directory");
        }
        String[] spitedPath = path.split("/");
        Directory tmp = disk.getDirectory(dir, spitedPath[spitedPath.length - 1]);
        if (tmp == null) {
            System.out.println("directory not found");
            return;
        }
        disk.deleteDirectory(tmp);
        dir.subDirectories.remove(tmp);
        disk.removeFolder(spitedPath[spitedPath.length - 1]);
    }

    @Override
    public void displayDiskStatus() {
        ArrayList<Integer> emptyBlocks = disk.getEmptyBlocks();
        ArrayList<Integer> allocatedBlocks = disk.getAllocatedBlocks();
        System.out.println("empty Space= " + emptyBlocks.size() + " KB");
        System.out.println("allocated Space= " + allocatedBlocks.size() + " KB");
        System.out.println("emptyBlocks is ");
        System.out.println(emptyBlocks);
        System.out.println("allocatedBlocks is ");
        System.out.println(allocatedBlocks);
    }

    @Override
    public void displayDiskStructure() {
        try {
            disk.readFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createFolder(String command) {
        String[] parsedCommand = createFileParser(command);
        String path = parsedCommand[0];
        String[] spitedPath = path.split("/");
        Directory dir = disk.getLastDirFromPath(path);
        if (dir == null)
            return;
        Pair<Boolean,Boolean>cab=disk.getUserCapability(path);
        if(cab==null){
            System.out.println("u don't have grant to this directory");
            return;
        }
        if(!cab.getKey()){
            System.out.println("u don't have grant create to this directory");
        }
        Directory tmp = disk.getDirectory(dir, spitedPath[spitedPath.length - 1]);
        if (tmp != null) {
            System.out.println("directory found");
            return;
        }
        tmp = new Directory(path);
        dir.subDirectories.add(tmp);
        String temp1 = "<" + spitedPath[spitedPath.length - 2] + ">", temp2 = "<" + spitedPath[spitedPath.length - 1] + ">";
        disk.writeToFile(temp1, temp2);
    }

    @Override
    public void freeSpaceManager() {
        System.out.println(disk.getDiskBlocks());
    }

    @Override
    public String[] createFileParser(String command) {
        return command.split("\\s+");
    }

    public String tellUser() {
        return onlineUser.getUsername();
    }

    public void login(User user) {

        int i;
        for (i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(user.getUsername())) {
                if(!users.get(i).getPassword().equals(user.getPassword())){
                    System.out.println("wrong pw");
                    return;
                }

                System.out.println("user logged in successfully.");
                onlineUser = disk.users.get(i);
                break;
            }
        }
        if (i == users.size()) {
            System.out.println("user not found");
        }
    }

    public void createUser(User user) {
        int c = 0;
        if (onlineUser.getUsername().equals("admin")) {
            for (User user1 : users) {
                if (user1.getUsername().equals(user.getUsername())) {
                    System.out.println("UserName found before");
                    break;
                }
                c++;
            }
        } else {
            System.out.println("This command runs by Admins Only !");
        }
        if (c == users.size()) {
            System.out.println("User Created Successfully");
            users.add(user);
            disk.folderCapabilities.put(user,new HashMap<>());
        }


    }

    public void deleteUser(User user) {
        int size=users.size();
        if (!onlineUser.getUsername().equals("admin")) {
            System.out.println("This command runs by Admins Only !");
        } else {
            if (user.getUsername().equals("admin")) {
                System.out.println("You can't delete Admin !");
                return;
            }
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUsername().equals(user.getUsername())) {
                    System.out.println("user deleted successfully.");
                    users.remove(i);
                    disk.folderCapabilities.remove(user);
                    break;
                }
            }
            if (size == users.size()) {
                System.out.println("user not found");
            }
        }
    }

    public void writeUsersAndcab() throws FileNotFoundException {
        String tmpUsers="";
        for(User user:disk.users){
            tmpUsers+=user.getUsername()+","+user.getPassword()+"\n";
        }
        PrintWriter out = new PrintWriter("user.txt");
        out.print(tmpUsers);
        out.close();
        String tmpcap="";
        for(Map.Entry<User, Map<String,Pair<Boolean,Boolean>>> entry : folderCapabilities.entrySet()){
            tmpcap+=entry.getKey().getUsername()+"--> ";
            Map<String,Pair<Boolean,Boolean>>map=entry.getValue();
            for(Map.Entry<String, Pair<Boolean,Boolean>> entry2 : map.entrySet()){
                String x=entry2.getValue().getKey()?"1":"0";
                String x2=entry2.getValue().getValue()?"1":"0";
                tmpcap+=entry2.getKey()+" "+x+x2+" , ";
            }
            tmpcap+="\n";
        }
        PrintWriter out2 = new PrintWriter("capabilities.txt");
        out2.print(tmpcap);
        out2.close();
    }
}
