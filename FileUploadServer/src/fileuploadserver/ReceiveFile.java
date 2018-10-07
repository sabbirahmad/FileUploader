/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fileuploadserver;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Sabbir
 */
public class ReceiveFile implements Runnable{
    
    private static final int WRONG_ID=0;
    private static final int DUPLICATE_IP=1;//two rolls uses same IP
    private static final int DUPLICATE_ID=2;//two IP uses same roll
    private static final int VERIFIED_IP_ID=3;
    
    private Thread receiveThread; //thread for this class
    private Socket connection; 
    private DataOutputStream output;
    private DataInputStream input;
    private InputStream is; 
    private FileOutputStream fos;   //to write received file to destination
    //private BufferedOutputStream bos;
    
    byte[] readBuffer = new byte[1024 * 10000];//to receive data
    //private int length=0;
    
    private String receivedFileName="";
    private String[] fileName; //array of strings for splitted file name of received file
    
    private boolean fileExists=false;//true if file already exists
    //private boolean overwrite=false;//true is file need to be overwritten
    private int overwrite=0;//0 means no overwrite, 1-overwrite,2-keep both
    private boolean loop=false;//
    private boolean isFile=true;
    
    private int verified=3;
    
    private boolean matchPattern=false;
    
    private String destination="";//destination of file
    
    private String[] fileNamePattern;
    private String[] folderNamePattern;
    
    //private int test=0;
    
    //private String regExpr="";
    private Pattern pattern;//pattern to create from regular expression
    private Matcher matcher;//to match with reg exp
    private int i;
    private int studentIdClient=-1;
    
    private String id="";
    private String idPath="";
    
    private byte[] getArray;
    
    //public static JOptionPane duplicateIPJOption;
    
    public ReceiveFile(Socket connection){
        //duplicateIPJOption = new JOptionPane();
        //duplicateIPJOption.setVisible(false);
        //duplicateIPJOption.setMessageType(JOptionPane.YES_NO_OPTION);
        
        
        
        
        this.connection=connection;
        receiveThread=new Thread(this);
        receiveThread.start(); //starts this thread
        
        
        fileNamePattern=FileUploadServer.fileName.split(";");
        folderNamePattern=FileUploadServer.folderName.split(";");
        //int i;
        for(i=0;i<fileNamePattern.length;i++){
            System.out.println("fileName["+i+"]: "+fileNamePattern[i]);
        }
        for(i=0;i<folderNamePattern.length;i++){
            System.out.println("folderName["+i+"]: "+folderNamePattern[i]);
        }
        
    }//end method ReceiveFile
    
    @Override
    public void run(){
        try {
            getStreams();//gets input output stream from client
            sendRules();//send file/folder rules to client
            if(verified!=VERIFIED_IP_ID){
                return;
            }
            getNames();//get the name of file from client checks for existence and name pattern
            getFile();//gets the desired file from client
            
        }//end try 
        catch (IOException ex) {
            Logger.getLogger(ReceiveFile.class.getName()).log(Level.SEVERE, null, ex);
            //try {
            //    fos.close();
            //} catch (IOException ex1) {
            //    Logger.getLogger(ReceiveFile.class.getName()).log(Level.SEVERE, null, ex1);
            //}
        }//end catch
    }//end method run
    
    private void getStreams() throws IOException{
        output=new DataOutputStream(connection.getOutputStream());
        input=new DataInputStream(connection.getInputStream());
        is = connection.getInputStream();
    }//end method getStream
    
    private void sendRules() throws IOException{
        
        studentIdClient=input.readInt();
        System.out.println("StudentID: "+studentIdClient);
        
        String newIP=connection.getInetAddress().toString();
        //String newIP2=connection.getLocalAddress().toString();
        System.out.println("Client IP: "+newIP);
        //System.out.println("IP2: "+newIP2);
        
        verified=verifyID(studentIdClient);
        if(verified==VERIFIED_IP_ID){
            verified=verifyDuplicateIP(newIP, studentIdClient);
            System.out.println("DuplicateIP checked");
        }
        
        if(verified==VERIFIED_IP_ID){
            verified=verifyDuplicateID(newIP, studentIdClient);
            System.out.println("DuplicateID checked");
        }
        
        System.out.println("verified: "+verified);
        
        System.out.println("MAP: "+FileUploadServer.IP_ID);
        
        output.writeInt(verified);
        
        if(verified!=VERIFIED_IP_ID){
            return;
        }
        
        output.writeBoolean(FileUploadServer.fileUpload);//sends if file can be uploaded
        output.writeBoolean(FileUploadServer.folderUpload);//sends if folder can be uploaded
        output.writeLong(FileUploadServer.fileSizeLong);
        output.writeInt(FileUploadServer.fileNumberInt);
        
        int fileNumberCount=0;
        fileNumberCount=FileUploadServer.ID_FileNo.get(studentIdClient);
        output.writeInt(fileNumberCount);
        id=String.valueOf(studentIdClient);
    }//end method sendRules
    
    public synchronized int verifyID(int id){
        if(FileUploadServer.ID.isEmpty() || FileUploadServer.ID.contains(id)){
            return VERIFIED_IP_ID;
        }
        return WRONG_ID;
    }
    
    public synchronized int verifyDuplicateIP(String ip,int id){
        int curID;
        if(FileUploadServer.IP_ID.containsKey(ip)){
            curID = FileUploadServer.IP_ID.get(ip);
            if(curID==id){
                return VERIFIED_IP_ID;
            }
            else{
                String message;
                message="IP: "+ip+" is requested by ID: "+id+"\nThis IP is used by ID: "+curID+"\nDo you want to accept?";
                int action=JOptionPane.showConfirmDialog(null, message, "Same IP Detected!", JOptionPane.YES_NO_OPTION , JOptionPane.QUESTION_MESSAGE);
                //System.out.println("JOption");
                //System.out.println("action="+action);
                if(action==0){
                    return VERIFIED_IP_ID;
                }
                else{
                    return DUPLICATE_IP;
                }
            }
        }
        //FileUploadServer.IP_ID.put(ip, id);
        return VERIFIED_IP_ID;
    }
    
    public synchronized int verifyDuplicateID(String ip,int id){
        String curIP;
        if(FileUploadServer.IP_ID.containsValue(id)){
            //System.out.println("Key found");
            curIP=FileUploadServer.getKeyByValue(FileUploadServer.IP_ID, id);
            //System.out.println("curIP: "+curIP);
            if(curIP.equals(ip)){
                //System.out.println("if verified ip id");
                return VERIFIED_IP_ID;
            }
            else{
                String message;
                message="ID: "+id+" is requesting from IP: "+ip+"\nThis ID was connected from IP: "+curIP+"\nDo you want to accept?";
                int action=JOptionPane.showConfirmDialog(null, message, "Same ID Detected", JOptionPane.YES_NO_OPTION , JOptionPane.QUESTION_MESSAGE);
                if(action==0){
                    return VERIFIED_IP_ID;
                }
                else{
                    return DUPLICATE_ID;
                }
            }
        }
        FileUploadServer.IP_ID.put(ip, id);
        //System.out.println("direct return");
        return VERIFIED_IP_ID;
    }
    
    private boolean checkPattern(String patternString, String matchString){
        pattern=Pattern.compile(patternString);//makes a pattern from string of fileName
        System.out.println("Pattern String: "+patternString);
        System.out.println("Match String: "+matchString);
        matcher=pattern.matcher(matchString);//matcher for desired string with compiled pattern name
        if(matcher.matches()){//checks if string is matched with pattern
            System.out.println(" matches ");
            return true;//returns true if matches
        }
        else
            //System.out.println(" doesn't match ");
            return false;//returns false if not matched
    }//end method checkPattern
    
    private boolean checkName(String baseString, String matchString){
        if(baseString.equals(matchString)){
            return true;
        }
        return false;
    }
    
    private String createDirectory(final String dirName) throws IOException {
        String path="";
        final File dir = new File(dirName);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Unable to create " + dir.getAbsolutePath());
        }
        path=dir.getAbsolutePath();
        return path;
    }
    
    private void getNames() throws IOException{
        int length=input.readInt();//length of file name
        int index=0;
        char[] temp=new char[length];//creates a characted array of desired length
        while(index<length){//reads character one by one 
            temp[index++]=input.readChar();
        }//end while
        receivedFileName=new String(temp);//creates string from character array
        //System.out.println("file: "+receivedFileName);
        
        fileName=receivedFileName.split("\\.");//file name and extention is splitted
        //System.out.println("fileName[0]: "+fileName[0]);//file name in fileName[0]
        //System.out.println("fileName[1]: "+fileName[1]);//file type in fileName[1]
        
        if(FileUploadServer.fileUpload==true && FileUploadServer.folderUpload==true){
            isFile=input.readBoolean();
            if(isFile==true){
                if(FileUploadServer.fileName.equals("")){//if no pattern is given, accepts any name
                matchPattern=true;
                }
                else{
                    if(FileUploadServer.regularExpression==true){
                        for(i=0;i<fileNamePattern.length;i++){
                            matchPattern=checkPattern(fileNamePattern[i],receivedFileName);
                            if(matchPattern==true)
                                break;
                            //System.out.println("fileName["+i+"]: "+fileNamePattern[i]);
                        }
                    }
                    else{
                        for(i=0;i<fileNamePattern.length;i++){
                            matchPattern=checkName(fileNamePattern[i],receivedFileName);
                            if(matchPattern==true)
                                break;
                        }
                    }
        
                }
            }
            else{
                if(FileUploadServer.folderName.equals("")){//if no pattern is given, accepts any name
                matchPattern=true;
                }
                else{
                    if(FileUploadServer.regularExpression==true){
                        for(i=0;i<folderNamePattern.length;i++){
                            matchPattern=checkPattern(folderNamePattern[i],fileName[0]);
                            if(matchPattern==true)
                                break;
                            //System.out.println("folderName["+i+"]: "+folderNamePattern[i]);
                        }
                    }
                    else{
                        for(i=0;i<folderNamePattern.length;i++){
                            matchPattern=checkName(folderNamePattern[i],fileName[0]);
                            if(matchPattern==true)
                                break;
                            //System.out.println("folderName["+i+"]: "+folderNamePattern[i]);
                        }
                    }
                }
            }
        }
        
        else if(FileUploadServer.fileUpload==true){
            if(FileUploadServer.fileName.equals("")){//if no pattern is given, accepts any name
                matchPattern=true;
            }
            else{
                if(FileUploadServer.regularExpression==true){
                    for(i=0;i<fileNamePattern.length;i++){
                        matchPattern=checkPattern(fileNamePattern[i],receivedFileName);
                        if(matchPattern==true)
                            break;
                        //System.out.println("fileName["+i+"]: "+fileNamePattern[i]);
                    }
                }
                else{
                    for(i=0;i<fileNamePattern.length;i++){
                        matchPattern=checkName(fileNamePattern[i],receivedFileName);
                        if(matchPattern==true)
                            break;
                    }
                }
            }
        }
        //if folder can be uploaded then, folder is saved in zip format. so actualFileName is name without .zip ext
        else if(FileUploadServer.folderUpload==true){
            if(FileUploadServer.folderName.equals("")){//if no pattern is given, accepts any name
                matchPattern=true;
            }
            else{
                if(FileUploadServer.regularExpression==true){
                    for(i=0;i<folderNamePattern.length;i++){
                        matchPattern=checkPattern(folderNamePattern[i],fileName[0]);
                        if(matchPattern==true)
                            break;
                        //System.out.println("folderName["+i+"]: "+folderNamePattern[i]);
                    }
                }
                else{
                    for(i=0;i<folderNamePattern.length;i++){
                        matchPattern=checkName(folderNamePattern[i],fileName[0]);
                        if(matchPattern==true)
                            break;
                        //System.out.println("folderName["+i+"]: "+folderNamePattern[i]);
                    }
                }
            }
        }

        output.writeBoolean(matchPattern); //sends client if file name is matched
        if(matchPattern==false){ //if file name is not matched again calls getNames() method
            getNames();
        }
        
        //if file location is not selected then destination is only receivedFileName in default directory
        if(FileUploadServer.fileLocation.equals("")){
            destination=receivedFileName;
        }
        else{//else destination is set to desired location 
            idPath=createDirectory(FileUploadServer.fileLocation+"\\"+id);
            System.out.println("idPath: "+idPath);
            //destination=FileUploadServer.fileLocation+"\\"+receivedFileName;
            destination=idPath+"\\"+receivedFileName;
        }
        //System.out.println("destination: "+destination);
        
        //check thisssssssssssssssssssssssss
        
        String actualfileName="";//string to check for existing file
        
        
        if(FileUploadServer.fileUpload==true && FileUploadServer.folderUpload==true){
            if(isFile==true){
                actualfileName=destination;
            }
            else{
                //actualfileName=FileUploadServer.fileLocation+"\\"+fileName[0];
                actualfileName=idPath+"\\"+fileName[0];
            }
        }
        //if only file can be uploaded, then file is saved to destination, so actualFileName is same as destination
        else if(FileUploadServer.fileUpload==true){
            //actualfileName=FileUploadServer.fileLocation+"\\"+fileName[0];
            actualfileName=destination;
            //System.out.println("actual file name: "+actualfileName);
            
        }
        //if folder can be uploaded then, folder is saved in zip format. so actualFileName is name without .zip ext
        else if(FileUploadServer.folderUpload==true){
            //actualfileName=FileUploadServer.fileLocation+"\\"+fileName[0];
            actualfileName=idPath+"\\"+fileName[0];
            //System.out.println("actual file name: "+actualfileName);
        }
        File exfile=new File(actualfileName);//new file with the actialFileName
        if(exfile.exists()){//if file exists sets fileExists to true
            //System.out.println("file exists");
            fileExists=true;
        }
        else{
            fileExists=false;
        }
        //System.out.println("output file exists");
        output.writeBoolean(fileExists);//sends fileExists to client
        //System.out.println("done file exists");
        if(fileExists==true)//if file exists then overwrite is read from client
        {
            //System.out.println("overwrite read boolean");
            overwrite=input.readInt();//get from client if overwrite or not
            if(overwrite==1){//if overwrite is true deletes existing file
                //boolean existingfile=exfile.delete();
                deleteDirectory(exfile);//deletes existing file
                ////System.out.println("existing file delete: "+existingfile);
            }
            else if(overwrite==2){//keep both files
                String[] tempDest=destination.split("\\.");
                long milliseconds=System.currentTimeMillis();
                int seconds = (int) (milliseconds / 1000) % 60 ;
                int minutes = (int) ((milliseconds / (1000*60)) % 60);
                int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
                String extraName="at"+hours+""+minutes+""+seconds;
                tempDest[0]=tempDest[0]+extraName;
                fileName[0]=tempDest[0];
                destination=tempDest[0]+"."+tempDest[1];
            }
        }
        //System.out.println("getNames ends");
    }//end method getNames
    
    private void getFile() throws IOException{
        
        File newFile = new File(destination);
        fos = new FileOutputStream(newFile);//outputs the file to destination
        
        loop=true;
        int i=0;
        long startingByte=0;
        int segmentSize=0;
        String fileNameStr="";
        
        
        int size=0;
        int length;
        //try{
            while(size!=-1){
                //length=input.readInt();
                //getArray=new byte[length];
                
                fileNameStr=input.readUTF();
                startingByte=input.readLong();
                segmentSize=input.readInt();
                
                getArray=new byte[segmentSize];
                
                size=is.read(getArray);
                
                
                output.writeBoolean(true);
                
                
                
                
                
                //System.out.println(getArray);

                /*
                String byteStr=bytesToString(getArray);

                String[] segments=byteStr.split("::");


                byte[] lByte=stringToBytes(segments[1]);
                byte[] iByte=stringToBytes(segments[2]);

                long lval=bytesToLong(lByte);
                int ival=bytesToInt(iByte);

                System.out.println("lval: "+lval+"\tival: "+ival);

                output.writeBoolean(true);


                if(ival==-1 || size==-1){
                    break;
                }
                
                byte [] fileByte=stringToBytes(segments[3]);
                fos.write(fileByte);
                * 
                */
                
                if(size==-1){
                    break;
                }
                
                fos.write(getArray);
                
                /*
                if(ival<512){
                    break;
                }
                
                
                if(length<512){
                    break;
                }
                * 
                */
                i++;
                if(segmentSize<512){
                    break;
                }
            }
            
            
            fos.close();


            int fileUpNo=FileUploadServer.ID_FileNo.get(studentIdClient);
            fileUpNo++;
            FileUploadServer.ID_FileNo.put(studentIdClient,fileUpNo);
            System.out.println(FileUploadServer.ID_FileNo);
        //}catch(EOFException e){
        //    fos.close();
        //}
        
        
        if(isFile==false && fileName[1].equals("zip")){//if folder is zipped then unzip the folder
            //System.out.println("des: "+destination+"\nfile: "+fileName[0]);
            
            if(overwrite!=2){
                new Unziper(destination,fileName[0]);//calls Unziper class to unzip the file
                File file=new File(destination);//deletes the zipped file
                boolean del=file.delete();
                //System.out.println("delete: "+del);
                
            }
            else{
                File temp=new File(destination);
                createDirectory(fileName[0]);
                temp.renameTo(new File(fileName[0]+"\\"+temp.getName()));
                new Unziper(fileName[0]+"\\"+temp.getName(),null);//calls Unziper class to unzip the file
                
                File file=new File(fileName[0]+"\\"+temp.getName());//deletes the zipped file
                boolean del=file.delete();
                //System.out.println("delete: "+del);
            }
            
            
            
            
        }
        
        
        //System.out.println("file received");
        //System.out.println("getFile ends");
        //test=input.readInt();
        //JOptionPane.showMessageDialog(null, "Test :"+test);
    }//end method getFile
    
    /*
    private void processBytes(byte[] buff) throws IOException {
        //fos.write(buff);
    }
    *
    */
    
    
    public static byte[] stringToBytes(String str) {
        char[] buffer = str.toCharArray();
        byte[] b = new byte[buffer.length << 1];
        CharBuffer cBuffer = ByteBuffer.wrap(b).asCharBuffer();
        for(int i = 0; i < buffer.length; i++)
        cBuffer.put(buffer[i]);
        return b;
    }

    public static String bytesToString(byte[] bytes) {
        CharBuffer cBuffer = ByteBuffer.wrap(bytes).asCharBuffer();
        return cBuffer.toString();
    }
    
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE/8);
        buffer.putLong(x);
        return buffer.array();
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE/8);
        buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
    
    public static int bytesToInt(byte[] b) 
    {
        return   b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] intToBytes(int a)
    {
        return new byte[] {
            (byte) ((a >> 24) & 0xFF),
            (byte) ((a >> 16) & 0xFF),   
            (byte) ((a >> 8) & 0xFF),   
            (byte) (a & 0xFF)
        };
    }
    
    
    private void deleteDirectory(File file) throws IOException{
 
        if(file.isDirectory()){
            if(file.list().length==0){//if directory is empty, then delete it
                file.delete();//deletes file
                ////System.out.println("Directory is deleted : " + file.getAbsolutePath());
            }
            else{
                String files[] = file.list();   //list all the directory contents
                for (String temp : files) {
                    File fileDelete = new File(file, temp);//construct the file structure

                    deleteDirectory(fileDelete);//recursive delete
                }
                if(file.list().length==0){//check the directory again, if empty then delete it
                    file.delete();
                    ////System.out.println("Directory is deleted : " + file.getAbsolutePath());
                }
            }

        }
        else{
            file.delete();//if file, then delete it
            ////System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }//end method deleteDirectory
    
}//end class ReceiveFile


class Unziper {
    
    public Unziper(String source, String fileName) {

        try {
                File file = new File(source);
                ZipFile zipFile = new ZipFile(file);

                // create a directory named the same as the zip file in the 
                // same directory as the zip file.
                //File zipDir = new File(file.getParentFile(), fileName);
                //zipDir.mkdir();

                Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
                while(entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();

                        String nme = entry.getName();
                        // File for current file or directory
                        //File entryDestination = new File(zipDir, nme);
                        File entryDestination = new File(file.getParentFile(), nme);

                        // This file may be in a subfolder in the Zip bundle
                        // This line ensures the parent folders are all
                        // created.
                        entryDestination.getParentFile().mkdirs();

                        // Directories are included as seperate entries 
                        // in the zip file.
                        if(!entry.isDirectory()) {
                                generateFile(entryDestination, entry, zipFile);
                        }
                }
                zipFile.close();
        }
        catch(IOException e) {
                e.printStackTrace();
        }
    
    }
 
    private static void generateFile(File destination, ZipEntry entry, ZipFile owner) throws IOException {
            InputStream in = null;
            OutputStream out = null;
            try {
                    InputStream rawIn = owner.getInputStream(entry);
                    in = new BufferedInputStream(rawIn);

                    FileOutputStream rawOut = new FileOutputStream(destination);
                    out = new BufferedOutputStream(rawOut);

                    // pump data from zip file into new files
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                    }
            }
            finally {
                    if(in != null) {
                            in.close();
                    }
                    if(out != null) {
                            out.close();
                    }
            }
    }
}
