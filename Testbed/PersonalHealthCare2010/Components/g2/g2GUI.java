import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.sql.Timestamp;
import javax.swing.*;

public class g2GUI extends javax.swing.JFrame implements Runnable {
    boolean didUserLogin;
    String address;
    int port;
    Socket s;
    MsgEncoder en;
    MsgDecoder de;
    int[] l1,l2,l3;
    ArrayList<Point> pointsl1,pointsl2,pointsl3;

    int min,max, numData;

    public g2GUI(String addr, int prt) {
        address = addr;
        port = prt;

        initComponents();
        getContentPane().setBackground(new Color(102,102,102));
        
        new Thread(this).start();
        didUserLogin=false;
        jPanel2.setVisible(false);
        loginMessage.setVisible(true);

    }



    public void run() {
          KeyValueList inkvl;
          int sys, dia,pul,spo2In,bloodSugar;
        try{
            s = new Socket(address,port);
            en = new MsgEncoder();
            de = new MsgDecoder(s.getInputStream());
            KeyValueList l = new KeyValueList();
            l.addPair("MsgID", "23");
            l.addPair("Name", "g2GUI");
            en.sendMsg(l, s.getOutputStream());
            int msgID;
            while(true){
                System.out.println("Waiting for server to send message...");
                inkvl = de.getMsg();
                msgID = Integer.parseInt(inkvl.getValue("MsgID"));
                // look for a message ID in the KeyValueList
                if((inkvl.lookupKey("MsgID")!=-1 && didUserLogin) || msgID==26 ){
                    // parse to an int, then do what is needed
                    

                    switch(msgID){
						case 22:
								System.exit(0);break;
								
                        case 26: System.out.println("Initialized");break;

                        case 32:
                            // take in Blood Pressure Alert
                            System.out.println("Got message 32 Blood Pressure Alert");
                            sys = Integer.parseInt(inkvl.getValue("Systolic"));
                            dia = Integer.parseInt(inkvl.getValue("Diastolic"));
                            pul = Integer.parseInt(inkvl.getValue("Pulse"));
                            this.systolic.setText(""+sys);
                            this.diastolic.setText(""+dia);
                            this.pulse.setText(""+pul);
							JOptionPane.showMessageDialog(this, inkvl.getValue("Alert Type"));
                            break;

                        case 34:
                            // SPO2 Alert
                            System.out.println("Got message 34 SPO2 Alert");
                            spo2In = Integer.parseInt(inkvl.getValue("SPO2"));
                            this.spo2.setText(""+spo2In);
							JOptionPane.showMessageDialog(this, inkvl.getValue("Alert Type"));
                            break;

                        case 36:
                            try {
								this.explode(inkvl.getValue("LeadI"),inkvl.getValue("LeadII"),inkvl.getValue("LeadIII"));
                            this.drawEkg();
                            }
                            catch(Exception e) {
                                System.out.println("Error graphing "+e);
                            }
							JOptionPane.showMessageDialog(this, inkvl.getValue("Alert Type"));
                            break;
                        case 38:
							
                            System.out.println("Got 38");
                            ///////////////
                            sys = Integer.parseInt(inkvl.getValue("Systolic"));
                            dia = Integer.parseInt(inkvl.getValue("Diastolic"));
                            pul = Integer.parseInt(inkvl.getValue("Pulse"));
                            spo2In = Integer.parseInt(inkvl.getValue("SPO2"));

                            this.spo2.setText(""+spo2In);
                            this.systolic.setText(""+sys);
                            this.diastolic.setText(""+dia);
                            this.pulse.setText(""+pul);

                            //comment this out if the graph doesnt work//////////////////
                            try {
								this.explode(inkvl.getValue("LeadI"),inkvl.getValue("LeadII"),inkvl.getValue("LeadIII"));
                            this.drawEkg();
                            }
                            catch(Exception e) {
                                System.out.println("Error graphing");
                            }
							
                            JOptionPane.showMessageDialog(this, inkvl.getValue("Alert Type"));
                            break;
                        case 42:
                        	System.out.println("Got 42");
                        	bloodSugar = Integer.parseInt(inkvl.getValue("Blood Sugar"));
                        	//update bloodSugar on gui here with the value bloodSugar
                        	this.bloodSugarLabel.setText(""+bloodSugar);
                        	JOptionPane.showMessageDialog(this, inkvl.getValue("Alert Type"));
                        	break;

                        default: System.out.println("GUI does not take MsgID: "+msgID);break;
                    }
                }
                else{
                    System.out.println("No MsgID or Not logged in");
                }
            }
         }
         catch (Exception e) {
            System.out.println(e);
        }
    }

    public void drawEkg() {
        max = min = l1[0];
        
        numData = Math.max(l3.length,Math.max(l1.length,l2.length));
        for (int i = 1; i<l1.length;i++) {
            if (l1[i] > max)
                max = l1[i];
            if (l1[i] < min)
                min = l1[i];
        }
        for (int i = 0; i<l2.length;i++) {
            if (l2[i] > max)
                max = l1[i];
            if (l2[i] < min)
                min = l2[i];
        }
        for (int i = 0; i<l3.length;i++) {
            if (l3[i] > max)
                max = l3[i];
            if (l3[i] < min)
                min = l3[i];
        }
        //graph.setSize(new Dimension(numData,max-min));
        
        int squeeze = (int)Math.ceil(numData/graph.getWidth());
        int shrink = (int)(Math.ceil((Math.abs(min)+max)/graph.getHeight()));
        int zoom = 1;
        pointsl1 = new ArrayList<Point>();
        pointsl2 = new ArrayList<Point>();
        pointsl3 = new ArrayList<Point>();
        for (int i = 0; i<l1.length-squeeze;i+=squeeze) {
            int av=0;
            for (int j = 0;j<squeeze;j++) {
                av+= l1[i+j];
            }
            av=(av/squeeze)/(shrink/zoom);
            pointsl1.add(new Point(i,-av+(max/shrink)));
        }
        for (int i = 0; i<l2.length-squeeze;i+=squeeze) {
            int av=0;
            for (int j = 0;j<squeeze;j++) {
                av+= l2[i+j];
            }
            av=(av/squeeze)/(shrink/zoom);
            pointsl2.add(new Point(i,-av+(max/shrink)));
        }
        for (int i = 0; i<l3.length-squeeze;i+=squeeze) {
            int av=0;
            for (int j = 0;j<squeeze;j++) {
                av += l3[i+j];
            }
            av=(av/squeeze)/(shrink/zoom);
            pointsl3.add(new Point(i,-av+(max/shrink)));
        }
        ((Graph)graph).paintIt(pointsl1, pointsl2, pointsl3,(max/shrink));


    }

    
    public void explode (String l1, String l2, String l3) {
        String[] one = l1.split(" ");
        String[] two = l2.split(" ");
        String[] three = l3.split(" ");
        this.l1 = new int[one.length];
        this.l2 = new int[two.length];
        this.l3 = new int[three.length];

        for(int i =0; i<one.length;i++) {
            
            this.l1[i] = (int)Integer.parseInt(one[i]);
            this.l2[i] = (int)Integer.parseInt(two[i]);
            this.l3[i] = (int)Integer.parseInt(three[i]);
        }
    }
       
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Meal = new javax.swing.ButtonGroup();
        Sex = new javax.swing.ButtonGroup();
        diabetesGroup = new javax.swing.ButtonGroup();
        graph = new Graph();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        pulse = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        spo2 = new javax.swing.JLabel();
        systolic = new javax.swing.JLabel();
        diastolic = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        bloodSugarLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        label3 = new java.awt.Label();
        label4 = new java.awt.Label();
        label5 = new java.awt.Label();
        HeartDisease = new javax.swing.JCheckBox();
        MealFast = new javax.swing.JRadioButton();
        MealBefore = new javax.swing.JRadioButton();
        MealAfter = new javax.swing.JRadioButton();
        UserName = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        SexMale = new javax.swing.JRadioButton();
        SexFemale = new javax.swing.JRadioButton();
        Weight = new javax.swing.JTextField();
        Age = new javax.swing.JTextField();
        Height = new javax.swing.JTextField();
        submitB = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        Dnormal = new javax.swing.JRadioButton();
        Dprediabetic = new javax.swing.JRadioButton();
        Ddiabetic = new javax.swing.JRadioButton();
        loginMessage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 102));
        setResizable(false);

        graph.setBackground(new java.awt.Color(204, 204, 255));
        graph.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("EKG Graph");

        javax.swing.GroupLayout graphLayout = new javax.swing.GroupLayout(graph);
        graph.setLayout(graphLayout);
        graphLayout.setHorizontalGroup(
            graphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, graphLayout.createSequentialGroup()
                .addContainerGap(866, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );
        graphLayout.setVerticalGroup(
            graphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(graphLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        jLabel1.setBackground(new java.awt.Color(228, 224, 224));
        jLabel1.setFont(new java.awt.Font("Monospaced", 1, 18));
        jLabel1.setForeground(new java.awt.Color(255, 255, 102));
        jLabel1.setText("Health Care Monitor");

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel2.setForeground(new java.awt.Color(28, 28, 28));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        pulse.setFont(new java.awt.Font("Lucida Grande", 0, 36));
        pulse.setText("-");

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 0, 15));
        jLabel6.setForeground(new java.awt.Color(255, 255, 102));
        jLabel6.setText("Pulse");
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        spo2.setFont(new java.awt.Font("Lucida Grande", 0, 36));
        spo2.setText("-");

        systolic.setText("-");

        diastolic.setText("-");

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        jLabel4.setText("Systolic:");

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        jLabel5.setText("Diastolic:");

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 0, 15));
        jLabel7.setForeground(new java.awt.Color(255, 255, 102));
        jLabel7.setText("SPO2");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 15));
        jLabel3.setForeground(new java.awt.Color(255, 255, 102));
        jLabel3.setText("Blood Pressure");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 0, 15));
        jLabel10.setForeground(new java.awt.Color(255, 255, 102));
        jLabel10.setText("Blood Sugar");
        jLabel10.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        bloodSugarLabel.setFont(new java.awt.Font("Lucida Grande", 0, 36)); // NOI18N
        bloodSugarLabel.setText("-");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(systolic)
                            .addComponent(diastolic))
                        .addGap(27, 27, 27))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(bloodSugarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pulse, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spo2)
                    .addComponent(jLabel7))
                .addGap(54, 54, 54))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bloodSugarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pulse, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spo2))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(systolic))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(diastolic)))
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(44, 44, 44))
        );

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel3.setForeground(new java.awt.Color(28, 28, 28));

        label1.setBackground(new java.awt.Color(153, 153, 153));
        label1.setText("User Name");

        label2.setBackground(new java.awt.Color(153, 153, 153));
        label2.setText("Sex");

        label3.setText("Age");

        label4.setBackground(new java.awt.Color(153, 153, 153));
        label4.setText("Weight(lb)");

        label5.setBackground(new java.awt.Color(153, 153, 153));
        label5.setText("Height(in)");

        HeartDisease.setBackground(new java.awt.Color(153, 153, 153));
        HeartDisease.setText("Heart Disease?");

        MealFast.setBackground(new java.awt.Color(153, 153, 153));
        Meal.add(MealFast);
        MealFast.setText("Fasting");
        MealFast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MealFastActionPerformed(evt);
            }
        });

        MealBefore.setBackground(new java.awt.Color(153, 153, 153));
        Meal.add(MealBefore);
        MealBefore.setText("Before");

        MealAfter.setBackground(new java.awt.Color(153, 153, 153));
        Meal.add(MealAfter);
        MealAfter.setText("After");

        jLabel8.setText("Meal:");

        SexMale.setBackground(new java.awt.Color(153, 153, 153));
        Sex.add(SexMale);
        SexMale.setText("Male");

        SexFemale.setBackground(new java.awt.Color(153, 153, 153));
        Sex.add(SexFemale);
        SexFemale.setText("Female");

        submitB.setText("Submit");
        submitB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitBActionPerformed(evt);
            }
        });

        jLabel9.setText("Diabetes:");

        Dnormal.setBackground(new java.awt.Color(153, 153, 153));
        diabetesGroup.add(Dnormal);
        Dnormal.setText("Normal");

        Dprediabetic.setBackground(new java.awt.Color(153, 153, 153));
        diabetesGroup.add(Dprediabetic);
        Dprediabetic.setText("Prediabetic");

        Ddiabetic.setBackground(new java.awt.Color(153, 153, 153));
        diabetesGroup.add(Ddiabetic);
        Ddiabetic.setText("diabetic");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(UserName, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(HeartDisease))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap(8, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGap(40, 40, 40)
                                                .addComponent(MealFast))
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(MealBefore)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(MealAfter))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(Age, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Height, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(Weight, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(SexMale)
                                .addGap(23, 23, 23)
                                .addComponent(SexFemale))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Dnormal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Dprediabetic)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Ddiabetic)))
                .addContainerGap(64, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(303, Short.MAX_VALUE)
                .addComponent(submitB)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(UserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(HeartDisease))
                    .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(SexMale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SexFemale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Age)
                    .addComponent(Weight)
                    .addComponent(label4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(label5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Height)
                    .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(MealFast)
                                .addComponent(MealBefore)
                                .addComponent(MealAfter))
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Dnormal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Dprediabetic, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Ddiabetic, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(submitB)))
                .addGap(4, 4, 4))
        );

        loginMessage.setBackground(new java.awt.Color(153, 153, 153));
        loginMessage.setFont(new java.awt.Font("Tahoma", 0, 18));
        loginMessage.setForeground(new java.awt.Color(255, 255, 51));
        loginMessage.setText("Please Login");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(loginMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(graph, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loginMessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void MealFastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MealFastActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MealFastActionPerformed

    private void submitBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitBActionPerformed
        // this button is pressed when the user wants to send message 45
        KeyValueList l = new KeyValueList();
        String errorStr ="Please provide the following information:";
		boolean validMSG = true;
        l.addPair("MsgID", "45");
		
        // Username
        if(UserName.getText().equals("")){
			errorStr+="\nusername";
            validMSG = false;
        }else{
            l.addPair("UserName",UserName.getText());
        }

        // Age
        if(Age.getText().equals("")){
            errorStr+="\nage";
            validMSG = false;
        }else{
            l.addPair("Age",Age.getText());
        }

        // Height
        if(Height.getText().equals("")){
            errorStr+="\nheight";
            validMSG = false;
        }else{
            l.addPair("Height",Height.getText());
        }

        // Weight
        if(Weight.getText().equals("")){
			errorStr+="\nweight";
            validMSG = false;
        }else{
            l.addPair("Weight",Weight.getText());
        }

        // check sex:
        if(!SexMale.isSelected() && !SexFemale.isSelected()){
			errorStr+="\ngender";
            validMSG = false;
        } else {
            // determine m / f
            if(SexMale.isSelected()){
                l.addPair("Sex","male");
            }else{
                l.addPair("Sex","female");
            }
        }

        // check meal
        if(!MealFast.isSelected() && !MealBefore.isSelected() && !MealAfter.isSelected()){
            errorStr+="\ndiet";
            validMSG = false;
        } else {
            // fast / before / afer
            if(MealFast.isSelected()){
                l.addPair("Meal","fasting");
            }else if(MealBefore.isSelected()) {
                l.addPair("Meal","before");
            }else{
                l.addPair("Meal","after");
            }
        }

         // check diabetes
        if(!Dnormal.isSelected() && !Dprediabetic.isSelected() && !Ddiabetic.isSelected()){
			errorStr+="\ndiabetes category";
            validMSG = false;
        } else {
            // fast / before / afer
            if(Dnormal.isSelected()){
                l.addPair("Diabetes","normal");
            }else if(Dprediabetic.isSelected()) {
                l.addPair("Diabetes","prediabetic");
            }else{
                l.addPair("Diabetes","diabetic");
            }
        }

        // heart disease - No check either yes or no
        if(HeartDisease.isSelected()){
            l.addPair("HeartDisease", "yes");
        } else {
            l.addPair("HeartDisease", "no");
        }

        if(validMSG){
            // timestamp it and send
            Date d = new Date();
            d.getTime();
            Timestamp ts = new Timestamp(d.getTime());
            l.addPair("DateTime",ts.toString());
            
            try{
                // send it *as of 4/13/10 this will not show up in remote
            
                System.out.println(l);

                en.sendMsg(l, s.getOutputStream());
                didUserLogin=true;
                jPanel2.setVisible(true);
                loginMessage.setVisible(false);
            }
            catch(Exception e){
                System.out.println("Cannot send message 45");
            }
        }
		else{
			JOptionPane.showMessageDialog(this, errorStr);
		}
    }//GEN-LAST:event_submitBActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        final String address1;
        final int port1 = 7999;
        // get params (address / port)
        if(args.length>=1){
            address1 = args[0];
        }
        else{
            System.out.println("No address given, assuming 127.0.0.1");
            address1 = "127.0.0.1";
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new g2GUI(address1,port1).setVisible(true);
            }
        });
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Age;
    private javax.swing.JRadioButton Ddiabetic;
    private javax.swing.JRadioButton Dnormal;
    private javax.swing.JRadioButton Dprediabetic;
    private javax.swing.JCheckBox HeartDisease;
    private javax.swing.JTextField Height;
    private javax.swing.ButtonGroup Meal;
    private javax.swing.JRadioButton MealAfter;
    private javax.swing.JRadioButton MealBefore;
    private javax.swing.JRadioButton MealFast;
    private javax.swing.ButtonGroup Sex;
    private javax.swing.JRadioButton SexFemale;
    private javax.swing.JRadioButton SexMale;
    private javax.swing.JTextField UserName;
    private javax.swing.JTextField Weight;
    private javax.swing.JLabel bloodSugarLabel;
    private javax.swing.ButtonGroup diabetesGroup;
    private javax.swing.JLabel diastolic;
    public javax.swing.JPanel graph;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.Label label5;
    private javax.swing.JLabel loginMessage;
    private javax.swing.JLabel pulse;
    private javax.swing.JLabel spo2;
    private javax.swing.JButton submitB;
    private javax.swing.JLabel systolic;
    // End of variables declaration//GEN-END:variables

class Graph extends JPanel {
    private ArrayList<Point> l1,l2,l3;
    private int max;
    public void paintIt(ArrayList<Point> l1, ArrayList<Point> l2, ArrayList<Point> l3, int max) {
     this.l1 = l1;
     this.l2 = l2;
     this.l3 = l3;
     this.max = max;
     //System.out.println("got to printIt");
     this.repaint();
    }
    public void paintComponent(Graphics g2) {
        Graphics2D g = (Graphics2D)g2.create();
        super.paintComponent(g);
        //System.out.println("got to paint c");
        if(l1 == null)
            return;
        //System.out.println("got to paint c");
        g.setColor(Color.DARK_GRAY);
        //g.drawString("Hello", 50, 50);
        g.drawLine(0,max,this.getWidth(),max);
        try {
        for(int i = 0;i<l1.size()-1;i++) {
            //System.out.println(this.l1.get(i).x + "," +this.l1.get(i).y+ "," + this.l1.get(i+1).x+ "," + this.l1.get(i+1).y);
            g.drawLine(i, this.l1.get(i).y, i+1, this.l1.get(i+1).y);
            g.drawLine(i, this.l2.get(i).y, i+1, this.l2.get(i+1).y);
            g.drawLine(i, this.l3.get(i).y, i+1, this.l3.get(i+1).y);
        }
        }
        catch (Exception e) {System.out.println(e);}
        System.out.println("finished");

    }
}
class Point  {
    public int x,y;
    public Point(int x, int y) {
        this.x=x;
        this.y=y;
    }    
}
class KeyValueList
{
 private Vector keys;
 private Vector values;
   
 /* Constructor */
 public KeyValueList()
 {
  keys = new Vector();
  values = new Vector();
 }
   
 /* Look up the value given key, used in getValue() */
   
 public int lookupKey(String strKey)
 {
  for(int i=0; i < keys.size(); i++)
  {
   String k = (String) keys.elementAt(i);
   if (strKey.equals(k)) 
    return i;
  } 
  return -1;
 }
   
 /* add new (key,value) pair to list */
   
 public boolean addPair(String strKey,String strValue)
 {
  return (keys.add(strKey) && values.add(strValue));
 }
   
 /* get the value given key */
   
 public String getValue(String strKey)
 {
  int index=lookupKey(strKey);
  if (index==-1) 
   return null;
  return (String) values.elementAt(index);
 } 
 
 public void setValue(int index, String val)
 {
  if(index >= 0 && index < size())
   values.set(index, val);
 }

 /* Show whole list */
 public String toString()
 {
  String result = new String();
  for(int i=0; i<keys.size(); i++)
  {
         result+=(String) keys.elementAt(i)+":"+(String) values.elementAt(i)+"\n";
  } 
  return result;
 }
   
 public int size()
 { 
  return keys.size(); 
 }
   
 /* get Key or Value by index */
 public String keyAt(int index){ return (String) keys.elementAt(index);}
 public String valueAt(int index){ return (String) values.elementAt(index);}
 
 public ArrayList<String> getValueLike(String key)
 {
  String temp;
  ArrayList<String> results = new ArrayList<String>();
  for(int i=0; i < keys.size(); i++)
  {
   temp = (String) keys.elementAt(i);
   if (temp.contains(key)) 
    results.add((String) values.elementAt(i));
  }
  if(results.size() == 0)
   return null;
  return results;
 }
}

/*
  Class MsgEncoder:
      Serialize the KeyValue List and Send it out to a Stream.
*/
class MsgEncoder
{
 private PrintStream printOut;
 /*If you would like to write msg interpreter your self, read below*/
 /* Default of delimiter in system is $$$ */
 private final String delimiter = "$$$";
   
 public MsgEncoder(){}
   
 /* Encode the Key Value List into a string and Send it out */
   
 public void sendMsg(KeyValueList kvList, OutputStream out) throws IOException
 {
  PrintStream printOut= new PrintStream(out);
  if (kvList == null) 
   return;
  String outMsg= new String();
  for(int i=0; i<kvList.size(); i++)
  {
       if (outMsg.equals(""))
        outMsg = kvList.keyAt(i) + delimiter + kvList.valueAt(i);
       else
        outMsg += delimiter + kvList.keyAt(i) + delimiter + kvList.valueAt(i);
  }
  //System.out.println(outMsg);
  printOut.println(outMsg);
 }
}

/*
  Class MsgDecoder:
     Get String from input Stream and reconstruct it to 
     a Key Value List.
*/

class MsgDecoder 
{
 private BufferedReader bufferIn;
 private final String delimiter = "$$$";
   
 public MsgDecoder(InputStream in)
 {
  bufferIn  = new BufferedReader(new InputStreamReader(in)); 
 }
   
 /*
     get String and output KeyValueList
 */
   
 public KeyValueList getMsg() throws IOException
 {
  String strMsg= bufferIn.readLine();
       
  if (strMsg==null) 
   return null;
       
  KeyValueList kvList = new KeyValueList(); 
  StringTokenizer st = new StringTokenizer(strMsg, delimiter);
  while (st.hasMoreTokens()) 
  {
   kvList.addPair(st.nextToken(), st.nextToken());
  }
  return kvList;
 }
   
}

}