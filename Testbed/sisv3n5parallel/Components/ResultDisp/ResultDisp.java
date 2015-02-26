import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ResultDisp extends JFrame implements ActionListener
{
	static OutputStream outstream;
	static MsgEncoder mEncoder;
	static Socket universal;
	static MsgDecoder mDecoder;
	
	static int nMsg603=0;
	static String rootdir = "../../Data/";
	
	public static void ConnectServer() throws Exception
	{

	}	
	
	public static void main(String[] args) throws Exception
	{
		new ResultDisp();
		
		universal = new Socket("127.0.0.1", 7999);
		mEncoder = new MsgEncoder();
		mDecoder = new MsgDecoder(universal.getInputStream());
		
		KeyValueList msg23 = new KeyValueList();
		msg23.addPair("MsgID","23");
		msg23.addPair("Description", "Connect to SISServer");
		msg23.addPair("Name","Eliminator");
		mEncoder.sendMsg(msg23, universal.getOutputStream());
		//Thread.currentThread().sleep(1000);
		KeyValueList kvList;
		outstream = universal.getOutputStream();
		
		/******************************
		* Reminder: Don't forget to save algorithm arguments in args[]  
		* before going into the following main loop.
		*******************************/
		
		while((kvList = mDecoder.getMsg()) !=null)
		{	
			ProcessMsg(kvList);
		}
	}
	
	static void ProcessMsg(KeyValueList kvList) throws Exception
	{	
		int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
		switch(MsgID)
		{
		case 26:
			System.out.println("Connect to SISServer successful.");
			break;
		case 603:
			
			jlabStat.setText("The result of "+kvList.getValue("CompName")+" is received. Waiting for next result ...");
			
			mod.setValueAt(kvList.getValue("CompName"), nMsg603, 0);
			mod.setValueAt(kvList.getValue("Cycle"), nMsg603, 1);
			mod.setValueAt(kvList.getValue("Algorithm"), nMsg603, 2);
			mod.setValueAt(kvList.getValue("Data"), nMsg603, 3);
			mod.setValueAt(kvList.getValue("Evaluation"), nMsg603, 4);
			
			nMsg603++;
			
			break;
		case 22:
			jlabStat.setText("All results have been received. Look result file Data/Result/EliminatorLog.txt .");
			//System.exit(0);
			break;
		case 24:
			System.out.println("Algorithm Activated");
			break;
		case 25:
			System.out.println("Algorithm Deactivated");
			break;
		default:
			break;
		}
	}	
	
	
	
	
	static JPanel content;
	JLabel jlabel1 = new JLabel("Input Data: Select one face database to display");
	JLabel jlabel2 = new JLabel("Output Result:");
	JLabel jlabel3 = new JLabel("Note: The face databases are under folder Data/Face.");
	static JLabel jlabStat = new JLabel("Wait for receiving result ...");
	static JComboBox jcmbData = new JComboBox();
	ImageCanvas canvas=new ImageCanvas();
	JScrollPane jscroll1 = new JScrollPane(canvas);
	JScrollPane jscroll3 = new JScrollPane();
	JTable table = new JTable();
	static DefaultTableModel mod;
	String[] columns;
	Object[][] p2;

    private class ImageCanvas extends JComponent{
        private BufferedImage[] img;
        private AffineTransform af = AffineTransform.getScaleInstance(1,1);
 
       private void setImage(BufferedImage[] m){
            img = m;
            paintImmediately(getBounds());
        }

        private void concateZoom(double scale){
            if (img==null)return;
            af.preConcatenate(AffineTransform.getScaleInstance(scale,scale));
            paintImmediately(getBounds());
        }

        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img!=null) {
                //Graphics2D g2d = (Graphics2D)g;
                //g2d.setTransform(af);
            	int x=0,y=0;
                for (int i=0;i<img.length;i++)
                {
                //g2d.drawImage(img[i],i*399,0,this);
                	if (x+img[i].getWidth()+10>750)
                	{
                		x=0;
                		y =y+img[i].getHeight()+10;
                	}
                	
                	g.drawImage(img[i],x,y,this);
                	x+=img[i].getWidth()+10;
                
                }
                
        	    setPreferredSize(new Dimension(750,y+img[0].getHeight()));
        	    jscroll1.setViewportView(this);
            }
        }
    };	
	
    public ResultDisp()//constructor
    {		
    	setTitle("Result Displayer");
		setSize(new Dimension(800, 600));
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		content = (JPanel) getContentPane();// ?
		content.setLayout(null);
		this.setResizable(false);
		//jlabel1.setFont(new Font("Dialog", 15, 15));
		jlabel1.setBounds(20, 0, 320, 55);
		jcmbData.setBounds(290, 15, 100, 20);
		jlabel3.setBounds(410, 0, 320, 55);
		jlabel2.setBounds(20, 290, 80, 55);
		jlabStat.setBounds(110, 290, 600, 55);
		jscroll1.setBounds(20, 40, 760, 250);
		jscroll3.setBounds(20, 330, 760, 210);
		content.add(jlabel1);
		content.add(jlabel2);
		content.add(jlabel3);
		content.add(jlabStat);
		content.add(jscroll1);
		content.add(jscroll3);
	
		content.add(jcmbData);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);

		columns = new String[] { "Component Name", "Cycle","Algorithm","Face Database", "Recogintion Rate" };
	    p2 = new Object[30][5];
	    mod=new DefaultTableModel(p2, columns);
		JTable table = new JTable(mod);
		table.setRowHeight(19);
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(true);
		jscroll3.setViewportView(table);
     	jscroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jscroll1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		attachListeners();
		

		File directory = new File(rootdir+"Face/");
		String[] filelist = directory.list();
		for(int i=0;i<filelist.length;i++)
			jcmbData.insertItemAt(filelist[i], 0);

		if (filelist.length>0)	jcmbData.setSelectedIndex(0);


  }

	
	public void ChangeImagePath(String strData)
	{
		int personCount;
		BufferedImage[] imgFaces;
	    try{
	    }catch(Exception e){}
	    try {
	    	personCount=GetPersonCount(rootdir+"Face/"+strData);
	    	imgFaces=new BufferedImage[personCount];
	    	for (int i=1;i<=personCount;i++)
	    	{
	    		String strI="00"+i;
	    		if (i<10)
	    			strI="00"+i;
	    		else if(i<100)
	    			strI="0"+i;
	    		else	    			
	    			strI=Integer.toString(i);
	    			
	    		String strFile=String.format("%s%s/%s-01.bmp", rootdir+"Face/",strData,strI);
	    		imgFaces[i-1]=ImageIO.read(new File(strFile));
	    		
	    	}

	        canvas.setImage(imgFaces);
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    }
	
	}
	
	public int GetPersonCount(String facePath) throws Exception
	{
		File directory = new File(facePath);
		String[] filelist = directory.list(new FilenameFilter(){
			public boolean accept(File arg0, String fileName) {
				return (fileName.endsWith("-01.bmp"));
			}
		});

		return filelist.length;
	}
	


	public void actionPerformed(ActionEvent e){
		if(e.getSource()==jcmbData)
		{
			System.out.println("jcmbData");
		}
	}
	private void attachListeners() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jcmbData.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==ItemEvent.SELECTED) 
                {
               		ChangeImagePath(e.getItem().toString());
 
                }
                    
            }
        });        
    }



}