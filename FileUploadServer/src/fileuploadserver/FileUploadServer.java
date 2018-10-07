/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fileuploadserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Sabbir
 */
public class FileUploadServer extends javax.swing.JFrame {

    /**
     * Creates new form FileUploadServer
     */
    
    
    public static HashMap<String,Integer> IP_ID;
    public static HashMap<Integer,Integer> ID_FileNo;
    public static Vector ID;
    
    
    private Thread connectThread;  //thread to wait for connection
    private int portServerInt=6789;  //port converted to integer
    private String portServer="";  //get server port as String from portTextField
    //public static String fileType="";  //accepted file types
    public static String fileName="";  //file name pattern is saved here
    public static String folderName="";  //folder name pattern is saved here
    public static String fileLocation="";  //destination of file saved
    public static boolean fileUpload=false;  //true if file can be uploaded
    public static boolean folderUpload=false;  //true if folder can be uploaded
    public static boolean regularExpression=false;  //true if folder can be uploaded
    private boolean loop=true;  //check condition to wait for connection
    
    private ServerSocket server; //server socket
    private Socket connection; //connection to client
    
    
    private String studentId="";
    private String fileSize="";
    private String fileNumber="";
    
    public static long fileSizeLong=0;
    public static int fileNumberInt=0;
    
    public FileUploadServer() {
        initComponents();
        ID=new Vector<Integer>();
        
        IP_ID=new HashMap();
        ID_FileNo=new HashMap();
        //this.setVisible(true);
        //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        displayIP();//displaying server ip address
        setStatusLabel.setText("Ready to start");
        
        //setup exitDialog
        exitDialog.setLocationByPlatform(true);
        exitDialog.setAlwaysOnTop(true);
        exitDialog.setTitle("Exit");
        exitDialog.setSize(220, 110);
        
        
        //setup aboutDialog
        aboutDialog.setLocationByPlatform(true);
        aboutDialog.setAlwaysOnTop(true);
        aboutDialog.setTitle("About");
        aboutDialog.setSize(250, 220);
        
        stopServerFileMenuItem.setEnabled(false);   //disabled until server starts
    }
    
    private void displayIP()
    {
        try
        {
            java.net.InetAddress i=java.net.InetAddress.getLocalHost();//get ip address
            ipTextField.setText(i.getHostAddress());//show ip address is jtextfield
        }//end try
        catch(Exception exception)
        {
            ipTextField.setText("IP not found");//showing if ip is not found
        }//end catch
    }//end method displayIP
    
    private void runServer() throws IOException{
        server=new ServerSocket(portServerInt); //create new ServerSocket
        setStatusLabel.setText("Server Running");
        loop=true;  
        connectThread=new Thread(){ //new thread to continue the loop to connect clients
            @Override
            public void run() {
                while(loop==true){    
                    try {
                        waitForConnection();//method to connect to a client
                    } //end try
                    catch (IOException ex) {
                        Logger.getLogger(FileUploadServer.class.getName()).log(Level.SEVERE, null, ex);
                    } //end catch
                }//end while
            }
        };//end connectThread
        connectThread.start(); //start connectThread
        
        startServerButton.setEnabled(false); //start server button is disabled after server starts
        stopServerFileMenuItem.setEnabled(true);  //stop server enabled after server starts
        
    }//end method runServer
    
    
    
    private void createStudentList(){
        studentId=idTextField.getText();
        if(studentId.equals("")){
            return;
        }
        ID.clear();
        ID_FileNo.clear();
        int i,j,id1,id2;
        String[] commaList,dashList;
        commaList=studentId.split(",");
        for(i=0;i<commaList.length;i++){
            id1=id2=-1;
            //dashList=null;
            dashList=commaList[i].split("-");
            id1=Integer.parseInt(dashList[0]);
            //System.out.println(id1++);
            ID_FileNo.put(id1,0);
            ID.add(id1++);
            if(dashList.length>1){
                id2=Integer.parseInt(dashList[1]);
                j=1;
                while(id1<id2){
                    //System.out.println(id1++);
                    ID_FileNo.put(id1,0);
                    ID.add(id1++);
                }
                //System.out.println(id2);
                ID_FileNo.put(id2,0);
                ID.add(id2);
                
            }
        }
        /*
        for(i=0;i<ID.size();i++){
            System.out.print(ID.get(i) +" ");
        }
        * 
        */
    }
    
    private void closeConnection() throws IOException{
        loop=false; //stops to accept client connections 
        server.close();
        portTextField.setText("");
        setStatusLabel.setText("Ready to start");
        startServerButton.setEnabled(true);
        stopServerFileMenuItem.setEnabled(false);
    }//end method colseConnection
    
    private void waitForConnection() throws IOException{
        //System.out.println("waitForConnection");
        //System.out.println("port: "+portServerInt);
        
        connection=server.accept();//accept client connection
        
        //JOptionPane.showMessageDialog(null, "Connected");
        ReceiveFile receiveFile = new ReceiveFile(connection);//create new thread for connected client
    }//end method waitForConnection
    
    
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    
    
    private void chooseDestination(){
        JFileChooser fileChooser=new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//only directory can be selected
        
        int returnValue=fileChooser.showOpenDialog(null);
        if(returnValue==JFileChooser.APPROVE_OPTION){
            fileLocation=fileChooser.getSelectedFile().getAbsolutePath();//get path of selected directory
            destinationTextField.setText(fileLocation);//show the path to textbox
            //System.out.println("Location: "+fileLocation);
        }
    }//end method chooseDestination

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exitDialog = new javax.swing.JDialog();
        exitLabel = new javax.swing.JLabel();
        exitButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        aboutDialog = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        serverPanel = new javax.swing.JPanel();
        startServerButton = new javax.swing.JButton();
        fileInfoPanel = new javax.swing.JPanel();
        folderNameLabel = new javax.swing.JLabel();
        fileNameLabel = new javax.swing.JLabel();
        fileNameTextField = new javax.swing.JTextField();
        folderNameTextField = new javax.swing.JTextField();
        fileNameCheckBox = new javax.swing.JCheckBox();
        folderNameCheckBox = new javax.swing.JCheckBox();
        regularExpressionCheckBox = new javax.swing.JCheckBox();
        fileDestinationPanel = new javax.swing.JPanel();
        destinationButton = new javax.swing.JButton();
        destinationLabel = new javax.swing.JLabel();
        destinationTextField = new javax.swing.JTextField();
        ipPortPanel = new javax.swing.JPanel();
        ipLabel = new javax.swing.JLabel();
        portTextField = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        ipTextField = new javax.swing.JTextField();
        fileSizePanel = new javax.swing.JPanel();
        sizeLabel = new javax.swing.JLabel();
        sizeTextField = new javax.swing.JTextField();
        numberLabel = new javax.swing.JLabel();
        numberTextField = new javax.swing.JTextField();
        studentIdPanel = new javax.swing.JPanel();
        idLabel = new javax.swing.JLabel();
        idTextField = new javax.swing.JTextField();
        statusLabel = new javax.swing.JLabel();
        setStatusLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        stopServerFileMenuItem = new javax.swing.JMenuItem();
        exitFileMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutHelpMenuItem = new javax.swing.JMenuItem();

        exitDialog.setResizable(false);

        exitLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        exitLabel.setText("Are you sure to exit?");

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout exitDialogLayout = new javax.swing.GroupLayout(exitDialog.getContentPane());
        exitDialog.getContentPane().setLayout(exitDialogLayout);
        exitDialogLayout.setHorizontalGroup(
            exitDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exitDialogLayout.createSequentialGroup()
                .addGroup(exitDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(exitDialogLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(exitButton)
                        .addGap(18, 18, 18)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(exitDialogLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(exitLabel)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        exitDialogLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, exitButton});

        exitDialogLayout.setVerticalGroup(
            exitDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(exitDialogLayout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addComponent(exitLabel)
                .addGap(18, 18, 18)
                .addGroup(exitDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exitButton)
                    .addComponent(cancelButton))
                .addGap(21, 21, 21))
        );

        exitDialogLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cancelButton, exitButton});

        aboutDialog.setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setText("File Upload Server");

        jLabel2.setText("Version 1.0");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Sabbir Ahmad");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("#1005010");

        jLabel5.setText("CSE 322");

        javax.swing.GroupLayout aboutDialogLayout = new javax.swing.GroupLayout(aboutDialog.getContentPane());
        aboutDialog.getContentPane().setLayout(aboutDialogLayout);
        aboutDialogLayout.setHorizontalGroup(
            aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutDialogLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addContainerGap(159, Short.MAX_VALUE))
        );
        aboutDialogLayout.setVerticalGroup(
            aboutDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aboutDialogLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(64, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        serverPanel.setBackground(new java.awt.Color(56, 189, 246));

        startServerButton.setText("Start Server");
        startServerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startServerButtonActionPerformed(evt);
            }
        });

        folderNameLabel.setText("Folder Name:");

        fileNameLabel.setText("File Name:");

        fileNameTextField.setEditable(false);
        fileNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                fileNameTextFieldFocusLost(evt);
            }
        });

        folderNameTextField.setEditable(false);
        folderNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                folderNameTextFieldFocusLost(evt);
            }
        });

        fileNameCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNameCheckBoxActionPerformed(evt);
            }
        });

        folderNameCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                folderNameCheckBoxActionPerformed(evt);
            }
        });

        regularExpressionCheckBox.setText("Check with Regular Expression");
        regularExpressionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regularExpressionCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout fileInfoPanelLayout = new javax.swing.GroupLayout(fileInfoPanel);
        fileInfoPanel.setLayout(fileInfoPanelLayout);
        fileInfoPanelLayout.setHorizontalGroup(
            fileInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileInfoPanelLayout.createSequentialGroup()
                .addGroup(fileInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fileInfoPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(fileInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(fileInfoPanelLayout.createSequentialGroup()
                                .addComponent(folderNameCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(folderNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(fileInfoPanelLayout.createSequentialGroup()
                                .addComponent(fileNameCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(fileInfoPanelLayout.createSequentialGroup()
                        .addGroup(fileInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(fileInfoPanelLayout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addGroup(fileInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fileNameLabel)
                                    .addComponent(folderNameLabel)))
                            .addGroup(fileInfoPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(regularExpressionCheckBox)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        fileInfoPanelLayout.setVerticalGroup(
            fileInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileInfoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(fileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileNameCheckBox))
                .addGap(4, 4, 4)
                .addComponent(folderNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(folderNameCheckBox)
                    .addComponent(folderNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(regularExpressionCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        destinationButton.setText("Choose");
        destinationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                destinationButtonActionPerformed(evt);
            }
        });

        destinationLabel.setText("Destination:");

        destinationTextField.setEditable(false);
        destinationTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                destinationTextFieldMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout fileDestinationPanelLayout = new javax.swing.GroupLayout(fileDestinationPanel);
        fileDestinationPanel.setLayout(fileDestinationPanelLayout);
        fileDestinationPanelLayout.setHorizontalGroup(
            fileDestinationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileDestinationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fileDestinationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fileDestinationPanelLayout.createSequentialGroup()
                        .addComponent(destinationLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(fileDestinationPanelLayout.createSequentialGroup()
                        .addComponent(destinationButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(destinationTextField)))
                .addContainerGap())
        );
        fileDestinationPanelLayout.setVerticalGroup(
            fileDestinationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileDestinationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(destinationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileDestinationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(destinationButton)
                    .addComponent(destinationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        ipLabel.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        ipLabel.setText("Server IP");

        portTextField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        portLabel.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        portLabel.setText("Port");

        ipTextField.setEditable(false);
        ipTextField.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N

        javax.swing.GroupLayout ipPortPanelLayout = new javax.swing.GroupLayout(ipPortPanel);
        ipPortPanel.setLayout(ipPortPanelLayout);
        ipPortPanelLayout.setHorizontalGroup(
            ipPortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ipPortPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ipPortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ipPortPanelLayout.createSequentialGroup()
                        .addComponent(ipLabel)
                        .addGap(169, 169, 169))
                    .addGroup(ipPortPanelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(portLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(ipPortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(portTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
                            .addComponent(ipTextField))))
                .addContainerGap())
        );
        ipPortPanelLayout.setVerticalGroup(
            ipPortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ipPortPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(ipPortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ipLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ipPortPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        sizeLabel.setText("Size (MB):");

        numberLabel.setText("Number:");

        javax.swing.GroupLayout fileSizePanelLayout = new javax.swing.GroupLayout(fileSizePanel);
        fileSizePanel.setLayout(fileSizePanelLayout);
        fileSizePanelLayout.setHorizontalGroup(
            fileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileSizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numberLabel)
                    .addComponent(sizeLabel))
                .addGap(18, 18, 18)
                .addGroup(fileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numberTextField)
                    .addComponent(sizeTextField))
                .addContainerGap())
        );
        fileSizePanelLayout.setVerticalGroup(
            fileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileSizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeLabel)
                    .addComponent(sizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fileSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberLabel)
                    .addComponent(numberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        idLabel.setText("Student ID:");

        javax.swing.GroupLayout studentIdPanelLayout = new javax.swing.GroupLayout(studentIdPanel);
        studentIdPanel.setLayout(studentIdPanelLayout);
        studentIdPanelLayout.setHorizontalGroup(
            studentIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentIdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(studentIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(studentIdPanelLayout.createSequentialGroup()
                        .addComponent(idLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(idTextField))
                .addContainerGap())
        );
        studentIdPanelLayout.setVerticalGroup(
            studentIdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentIdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(idLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout serverPanelLayout = new javax.swing.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fileDestinationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, serverPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(startServerButton))
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ipPortPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(studentIdPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fileSizePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fileInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fileInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ipPortPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(studentIdPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fileSizePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileDestinationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(startServerButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        statusLabel.setText("Status:");

        fileMenu.setText("File");
        fileMenu.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        stopServerFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        stopServerFileMenuItem.setText("Stop Server");
        stopServerFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopServerFileMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(stopServerFileMenuItem);

        exitFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitFileMenuItem.setText("Exit");
        exitFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitFileMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitFileMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText("Help");
        helpMenu.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        aboutHelpMenuItem.setText("About");
        aboutHelpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutHelpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutHelpMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(serverPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setStatusLabel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(serverPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(setStatusLabel)
                    .addComponent(statusLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startServerButtonActionPerformed
        // TODO add your handling code here:
        createStudentList();
        portServer=portTextField.getText();
        fileSize=sizeTextField.getText();
        fileNumber=numberTextField.getText();
        studentId=idTextField.getText();
        
        
        try{
            portServerInt=Integer.parseInt(portServer);//converting String to integer
            if(portServerInt<1025 || portServerInt>65536)//checks if port is valid; throws exception for invalid one
                throw new Exception();
            
        }//end try
        catch(Exception e)
        {
            portServer="";
            portTextField.setText("");//if port is invalid then sets the field to blank
            //showing error message for wrong port
            JOptionPane.showMessageDialog(serverPanel, "Please enter valid port number.\nRange: 1025-65536","Error!",JOptionPane.ERROR_MESSAGE);
            return;
        }//end catch
        
        try{
            if(fileSize.equals("")){
                fileSizeLong=0;
            }
            else{
                fileSizeLong=Long.parseLong(fileSize);
            }
        }
        catch(Exception e){
            fileSize="";
            sizeTextField.setText("");//if port is invalid then sets the field to blank
            //showing error message for wrong port
            JOptionPane.showMessageDialog(serverPanel, "File size must be an integer!","Error!",JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try{
            if(fileNumber.equals("")){
                fileNumberInt=0;
            }
            else{
                fileNumberInt=Integer.parseInt(fileNumber);
            }
        }
        catch(Exception e){
            fileNumber="";
            numberTextField.setText("");//if port is invalid then sets the field to blank
            //showing error message for wrong port
            JOptionPane.showMessageDialog(serverPanel, "File number must be an integer!","Error!",JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            if(fileNameCheckBox.isSelected() || folderNameCheckBox.isSelected()){//checks file/folder name is selected
                fileName=fileNameTextField.getText();
                folderName=folderNameTextField.getText();
                runServer();//if everything is set, starts the server
            }
            else{
                throw new Exception();
            }
        }//end try 
        catch (IOException ex) {
            Logger.getLogger(FileUploadServer.class.getName()).log(Level.SEVERE, null, ex);
        }//end catch
        catch(Exception ex){
            //file/folder name is not selected and thrown exception is caught
            JOptionPane.showMessageDialog(serverPanel, "File name/Folder name is not selected","Error!",JOptionPane.ERROR_MESSAGE);
        }//end catch
        
    }//GEN-LAST:event_startServerButtonActionPerformed

    private void fileNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fileNameTextFieldFocusLost
        // TODO add your handling code here:
        fileName+=fileNameTextField.getText();
        //System.out.println(fileName);
    }//GEN-LAST:event_fileNameTextFieldFocusLost

    private void folderNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_folderNameTextFieldFocusLost
        // TODO add your handling code here:
        folderName+=folderNameTextField.getText();
        //System.out.println(folderName);
    }//GEN-LAST:event_folderNameTextFieldFocusLost

    private void destinationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_destinationButtonActionPerformed
        // TODO add your handling code here:
        chooseDestination();
    }//GEN-LAST:event_destinationButtonActionPerformed

    private void destinationTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_destinationTextFieldMouseClicked
        // TODO add your handling code here:
        chooseDestination();
    }//GEN-LAST:event_destinationTextFieldMouseClicked

    private void fileNameCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNameCheckBoxActionPerformed
        // TODO add your handling code here:
        if(fileNameCheckBox.isSelected()){
            fileNameTextField.setEditable(true);
            fileUpload=true;
        }
        else{
            fileNameTextField.setEditable(false);
            fileNameTextField.setText("");
            fileUpload=false;
        }
    }//GEN-LAST:event_fileNameCheckBoxActionPerformed

    private void folderNameCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_folderNameCheckBoxActionPerformed
        // TODO add your handling code here:
        if(folderNameCheckBox.isSelected()){
            folderNameTextField.setEditable(true);
            folderUpload=true;
        }
        else{
            folderNameTextField.setEditable(false);
            folderNameTextField.setText("");
            folderUpload=false;
        }
    }//GEN-LAST:event_folderNameCheckBoxActionPerformed

    private void exitFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitFileMenuItemActionPerformed
        // TODO add your handling code here:
        exitDialog.setVisible(true);
    }//GEN-LAST:event_exitFileMenuItemActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        exitDialog.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void stopServerFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopServerFileMenuItemActionPerformed
        try {
            // TODO add your handling code here:
            loop=false;
            closeConnection();
        } catch (IOException ex) {
            Logger.getLogger(FileUploadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_stopServerFileMenuItemActionPerformed

    private void aboutHelpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutHelpMenuItemActionPerformed
        // TODO add your handling code here:
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutHelpMenuItemActionPerformed

    private void regularExpressionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regularExpressionCheckBoxActionPerformed
        // TODO add your handling code here:
        if(regularExpressionCheckBox.isSelected()){
            regularExpression=true;
        }
        else{
            regularExpression=false;
        }
    }//GEN-LAST:event_regularExpressionCheckBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FileUploadServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FileUploadServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FileUploadServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FileUploadServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                FileUploadServer fileUploadServer=new FileUploadServer();
                fileUploadServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                fileUploadServer.setTitle("File Uploader - Server");
                fileUploadServer.setLocationByPlatform(true);
                fileUploadServer.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog aboutDialog;
    private javax.swing.JMenuItem aboutHelpMenuItem;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton destinationButton;
    private javax.swing.JLabel destinationLabel;
    private javax.swing.JTextField destinationTextField;
    private javax.swing.JButton exitButton;
    private javax.swing.JDialog exitDialog;
    private javax.swing.JMenuItem exitFileMenuItem;
    private javax.swing.JLabel exitLabel;
    private javax.swing.JPanel fileDestinationPanel;
    private javax.swing.JPanel fileInfoPanel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JCheckBox fileNameCheckBox;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JPanel fileSizePanel;
    private javax.swing.JCheckBox folderNameCheckBox;
    private javax.swing.JLabel folderNameLabel;
    private javax.swing.JTextField folderNameTextField;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField idTextField;
    private javax.swing.JLabel ipLabel;
    private javax.swing.JPanel ipPortPanel;
    private javax.swing.JTextField ipTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel numberLabel;
    private javax.swing.JTextField numberTextField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JTextField portTextField;
    private javax.swing.JCheckBox regularExpressionCheckBox;
    private javax.swing.JPanel serverPanel;
    private static javax.swing.JLabel setStatusLabel;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JTextField sizeTextField;
    private javax.swing.JButton startServerButton;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JMenuItem stopServerFileMenuItem;
    private javax.swing.JPanel studentIdPanel;
    // End of variables declaration//GEN-END:variables
}
