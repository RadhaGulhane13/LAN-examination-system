/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 *
 * @author RADHA
 */
public class Client extends JFrame implements ActionListener {

    JLabel lbl1, qField, lbl2;
    JRadioButton rd1, rd2, rd3, rd4;
    ButtonGroup bg;
    JButton next;

    String username;
    String password;
    JTextField txtuser;
    JPasswordField pass;
    JLabel luser;
    JLabel lpass;
    JButton blogin;
    JFrame fm;

    Socket s;
    DataInputStream in;
    DataOutputStream out;

    String marks;

    Client() throws UnknownHostException, IOException {
        /* Each client will be redirect to the Login Page*/
        LoginPage();
    }

    public void LoginPage() {
        fm = new JFrame();
        fm.setVisible(true);
        fm.setLocation(700, 350);
        fm.setLayout(null);
        fm.setSize(550, 350);
        fm.setTitle("LAN Examination System - Login");
        fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title
        JLabel titleLabel = new JLabel("LAN Examination System");
        titleLabel.setBounds(175, 20, 200, 30);
        titleLabel.setFont(titleLabel.getFont().deriveFont(16.0f));
        fm.add(titleLabel);
        
        luser = new JLabel("Username:");
        fm.add(luser);
        luser.setBounds(130, 80, 100, 30);

        txtuser = new JTextField();
        fm.add(txtuser);
        txtuser.setBounds(240, 80, 180, 30);
        txtuser.setToolTipText("Enter your username");

        lpass = new JLabel("Password:");
        fm.add(lpass);
        lpass.setBounds(130, 120, 100, 30);

        pass = new JPasswordField();
        fm.add(pass);
        pass.setBounds(240, 120, 180, 30);
        pass.setToolTipText("Enter your password");

        blogin = new JButton("Login");
        fm.add(blogin);
        blogin.setBounds(225, 180, 100, 35);
        
        // Instructions
        JLabel instructionsLabel = new JLabel("<html><center>Enter your credentials to start the examination</center></html>");
        instructionsLabel.setBounds(100, 230, 350, 40);
        instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
        fm.add(instructionsLabel);

        blogin.addActionListener(this);
        
        // Allow Enter key to submit
        fm.getRootPane().setDefaultButton(blogin);
    }

    public void ErrorPage() {
        ErrorPage("Authentication Failed - Invalid Username or Password");
    }
    
    public void ErrorPage(String errorMessage) {
        fm = new JFrame();
        fm.setVisible(true);
        fm.setLocation(700, 350);
        fm.setLayout(null);
        fm.setSize(500, 200);
        fm.setTitle("Error");
        fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        luser = new JLabel("<html><center>" + errorMessage + "</center></html>");
        luser.setHorizontalAlignment(JLabel.CENTER);
        fm.add(luser);
        luser.setBounds(50, 50, 400, 50);
        
        JButton okButton = new JButton("OK");
        okButton.setBounds(200, 120, 100, 30);
        okButton.addActionListener(e -> {
            fm.dispose();
            LoginPage(); // Return to login page
        });
        fm.add(okButton);
    }
    
    private void showErrorMessage(String message) {
        JLabel errorLabel = new JLabel("<html><font color='red'>" + message + "</font></html>");
        errorLabel.setBounds(130, 150, 300, 30);
        fm.add(errorLabel);
        fm.repaint();
        
        // Remove error message after 3 seconds
        new Timer(3000, e -> {
            fm.remove(errorLabel);
            fm.repaint();
        }).start();
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void actionPerformed(ActionEvent e) {
        // Input validation
        username = txtuser.getText();
        password = new String(pass.getPassword());

        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            showErrorMessage("Please enter a username");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            showErrorMessage("Please enter a password");
            return;
        }

        try {
            int port = 5555;
            /*
            An IP address is a unique address that identifies a device on the 
            internet or a local network.
            get desktops ipaddress in network...ethernet
            get desktops ipaddress in network...if connnetcted to router 
            https://www.youtube.com/watch?v=4r4qm_Zxnik
            */
            s = new Socket(InetAddress.getLocalHost(), port);
            System.out.println("Connected to server successfully");
            
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
            
            /* Send username and password to the server */
            out.writeUTF(username.trim());
            out.flush();
            out.writeUTF(password);
            out.flush();
            System.out.println("Calling give test");
            fm.setVisible(false);
            if (in.readUTF().equals("ABORT")) {
                ErrorPage();
                disconnect();

            } else {
                /* 
                Once the username and passward is validated by server,
                student will be redirected to the test screen.
                */
                
                testScreen();
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
    Once the student done with answering all quetions, this function call gets
    called which will display marks scored by the student.
    */
    /*
    Once the student done with answering all questions, this function call gets
    called which will display marks scored by the student.
    */
    void displayMarks() {
        fm = new JFrame();
        fm.setVisible(true);
        fm.setLocation(700, 350);
        fm.setLayout(null);
        fm.setSize(500, 250);
        fm.setTitle("Examination Results");
        fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title
        JLabel titleLabel = new JLabel("Examination Completed!");
        titleLabel.setBounds(150, 20, 200, 30);
        titleLabel.setFont(titleLabel.getFont().deriveFont(16.0f));
        fm.add(titleLabel);
        
        // Username
        luser = new JLabel("Student: " + username);
        luser.setBounds(150, 60, 200, 30);
        fm.add(luser);
        
        // Marks with better formatting
        int totalMarks = 10;
        int studentMarks = Integer.parseInt(marks);
        double percentage = (studentMarks * 100.0) / totalMarks;
        
        JLabel lmarks = new JLabel("Score: " + marks + " out of " + totalMarks + 
                                 " (" + String.format("%.1f", percentage) + "%)");
        lmarks.setBounds(150, 90, 200, 30);
        fm.add(lmarks);
        
        // Performance message
        String performanceMessage;
        if (percentage >= 80) {
            performanceMessage = "Excellent Performance!";
        } else if (percentage >= 60) {
            performanceMessage = "Good Performance!";
        } else if (percentage >= 40) {
            performanceMessage = "Fair Performance";
        } else {
            performanceMessage = "Needs Improvement";
        }
        
        JLabel performanceLabel = new JLabel(performanceMessage);
        performanceLabel.setBounds(150, 120, 200, 30);
        fm.add(performanceLabel);
        
        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setBounds(200, 160, 100, 30);
        closeButton.addActionListener(e -> System.exit(0));
        fm.add(closeButton);
    }
    
    /*
    Read the questions and options sent by the server and and send the answer
    selected by student to the server
    */
    void iterator(ActionEvent e) throws IOException {
        if (rd1.isSelected()) {
            out.writeUTF("a");
        } else if (rd2.isSelected()) {
            out.writeUTF("b");
        } else if (rd3.isSelected()) {
            out.writeUTF("c");
        } else if (rd4.isSelected()) {
            out.writeUTF("d");
        } else {
            out.writeUTF("e");
        }

        bg.clearSelection();

        String data = "";
        data = in.readUTF();

        if (data != null) {
            if (data.equals("over")) {
                /* Read the marks sent by the server. */
                marks = in.readUTF();
                fm.setVisible(false);
                displayMarks();
                //System.out.println("Marks: "+marks);
                disconnect();
                return;
            }
            
            /* Read the quetion and option provided by server and display it on 
            testscreeen */
            String[] qform;
            qform = data.split("\\|");
            System.out.println(qform[0] + qform[1] + qform[2] + qform[3] + qform[4]);
            qField.setText(qform[0]);
            rd1.setText(qform[1]);
            rd2.setText(qform[2]);
            rd3.setText(qform[3]);
            rd4.setText(qform[4]);

        }
    }

    void testScreen() throws IOException {
        fm = new JFrame();
        fm.setVisible(true);
        fm.setLocation(650, 250);
        fm.setLayout(null);
        fm.setSize(800, 800);
        fm.setTitle("Test");

        lbl1 = new JLabel("Question :");
        lbl1.setBounds(100, 70, 100, 30);
        fm.add(lbl1);

        qField = new JLabel();
        qField.setBounds(200, 70, 400, 30);
        fm.add(qField);

        lbl2 = new JLabel("Options :");
        lbl2.setBounds(100, 160, 100, 30);
        fm.add(lbl2);

        rd1 = new JRadioButton("a");
        rd1.setBounds(200, 200, 350, 50);
        fm.add(rd1);

        rd2 = new JRadioButton("b");
        rd2.setBounds(200, 250, 350, 50);
        fm.add(rd2);

        rd3 = new JRadioButton("c");
        rd3.setBounds(200, 300, 350, 50);
        fm.add(rd3);

        rd4 = new JRadioButton("d");
        rd4.setBounds(200, 350, 350, 50);
        fm.add(rd4);

        bg = new ButtonGroup();
        bg.add(rd1);
        bg.add(rd2);
        bg.add(rd3);
        bg.add(rd4);

        next = new JButton("NEXT");
        next.setBounds(600, 600, 100, 50);
        fm.add(next);

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    iterator(e);
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //Scanner sin = new Scanner(System.in);
        String data = "";
        data = in.readUTF();

        if (data != null) {
            if (data.equals("over")) {
                //make fm invisible n marks show
                marks = in.readUTF();

                System.out.println("Marks: " + marks);
                fm.setVisible(false);
                disconnect();
                return;
            }
            String[] qform;
            qform = data.split("\\|");
        
            qField.setText(qform[0]);
            rd1.setText(qform[1]);
            rd2.setText(qform[2]);
            rd3.setText(qform[3]);
            rd4.setText(qform[4]);

        }

    }

    void disconnect() throws IOException {
        in.close();
        out.close();
        s.close();
        System.out.println("Client diconnected successfully...");
    }

    public static void main(String[] args) throws IOException {
        /* Create new client  */
        Client c = new Client();
    }

}
