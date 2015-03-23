/*   1:    */ import java.io.IOException;
/*   2:    */ import java.io.PrintStream;
/*   3:    */ import java.net.Socket;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.LinkedList;
/*   6:    */ 
/*   7:    */ class prjRemote
/*   8:    */ {
/*   9:    */   static prjGui gui;
/*  10:    */   static Socket serverconn;
/*  11:    */   static prjRemote.ReceiveBuff receivedmsgs;
/*  12:    */   static KeyValueList lastreceivedmsg;
/*  13:    */   static ArrayList<LoadedMsg> loadedmsgs;
/*  14:    */   static LoadedMsg currentloadedmsg;
/*  15:    */   static MsgDecoder mDecoder;
/*  16:    */   static MsgEncoder mEncoder;
/*  17:    */   static int refreshrate;
/*  18:    */   static Thread receivethread;
/*  19:    */   static Thread displaythread;
/*  20:    */   
/*  21:    */   public static void main(String[] args)
/*  22:    */   {
/*  23: 27 */     gui = new prjGui();
/*  24:    */   }
/*  25:    */   
/*  26:    */   public static void init()
/*  27:    */     throws IOException
/*  28:    */   {
/*  29: 33 */     receivedmsgs = new prjRemote.ReceiveBuff();
/*  30: 34 */     lastreceivedmsg = null;
/*  31: 35 */     loadedmsgs = new ArrayList();
/*  32: 36 */     KeyValueList kvlist = new KeyValueList();
/*  33: 37 */     kvlist.addPair("MsgID", "1000");
/*  34: 38 */     kvlist.addPair("Description", "Test Msg");
/*  35: 39 */     currentloadedmsg = new LoadedMsg(0, kvlist);
/*  36: 40 */     loadedmsgs.add(currentloadedmsg);
/*  37:    */     
/*  38: 42 */     mDecoder = new MsgDecoder(serverconn.getInputStream());
/*  39: 43 */     mEncoder = new MsgEncoder();
/*  40: 44 */     refreshrate = 2000;
/*  41:    */     
/*  42:    */ 
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46: 50 */     receivethread = new Thread(new Runnable()
/*  47:    */     {
/*  48:    */       public void run()
/*  49:    */       {
/*  50:    */         try
/*  51:    */         {
/*  52:    */           for (;;)
/*  53:    */           {
/*  54: 57 */             KeyValueList kvInput = prjRemote.mDecoder.getMsg();
/*  55: 58 */             if (kvInput != null)
/*  56:    */             {
/*  57: 60 */               prjRemote.receivedmsgs.add(kvInput);
/*  58: 61 */               prjRemote.lastreceivedmsg = kvInput;
/*  59: 62 */               System.out.println("received\n" + kvInput);
/*  60:    */             }
/*  61:    */           }
/*  62:    */         }
/*  63:    */         catch (Exception localException) {}
/*  64:    */       }
/*  65: 73 */     });
/*  66: 74 */     receivethread.start();
/*  67:    */     
/*  68: 76 */     displaythread = new Thread(new Runnable()
/*  69:    */     {
/*  70:    */       public void run()
/*  71:    */       {
/*  72:    */         try
/*  73:    */         {
/*  74:    */           for (;;)
/*  75:    */           {
/*  76: 83 */             KeyValueList kvInput = prjRemote.receivedmsgs.remove();
/*  77: 84 */             prjRemote.gui.displayKvList(kvInput, "r");
/*  78: 85 */             System.out.println("disp\n" + kvInput);
/*  79:    */             
/*  80:    */ 
/*  81: 88 */             Thread.currentThread();Thread.sleep(prjRemote.refreshrate);
/*  82:    */           }
/*  83:    */         }
/*  84:    */         catch (Exception localException) {}
/*  85:    */       }
/*  86: 96 */     });
/*  87: 97 */     displaythread.start();
/*  88:    */   }
/*  89:    */   
/*  90:    */   static class ReceiveBuff
/*  91:    */   {
/*  92:    */     private LinkedList<KeyValueList> buffs;
/*  93:    */     
/*  94:    */     ReceiveBuff()
/*  95:    */     {
/*  96:106 */       this.buffs = new LinkedList();
/*  97:    */     }
/*  98:    */     
/*  99:    */     synchronized void add(KeyValueList in)
/* 100:    */     {
/* 101:111 */       this.buffs.add(in);
/* 102:112 */       notify();
/* 103:    */     }
/* 104:    */     
/* 105:    */     synchronized KeyValueList remove()
/* 106:    */     {
/* 107:117 */       if (this.buffs.isEmpty()) {
/* 108:    */         try
/* 109:    */         {
/* 110:119 */           wait();
/* 111:    */         }
/* 112:    */         catch (InterruptedException localInterruptedException) {}
/* 113:    */       }
/* 114:121 */       return (KeyValueList)this.buffs.poll();
/* 115:    */     }
/* 116:    */   }
/* 117:    */ }


/* Location:           C:\Users\admin.sis\Desktop\Jerry's workspace\ConnectPaceTech\prjRemote\
 * Qualified Name:     prjRemote
 * JD-Core Version:    0.7.0.1
 */