/*   1:    */ import java.io.IOException;
/*   2:    */ import java.io.OutputStream;
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ class MsgEncoder
/*   6:    */ {
/*   7:    */   private PrintStream printOut;
/*   8:125 */   private final String delimiter = "$$$";
/*   9:    */   
/*  10:    */   public void sendMsg(KeyValueList kvList, OutputStream out)
/*  11:    */     throws IOException
/*  12:    */   {
/*  13:133 */     PrintStream printOut = new PrintStream(out);
/*  14:134 */     if (kvList == null) {
/*  15:135 */       return;
/*  16:    */     }
/*  17:136 */     String outMsg = new String();
/*  18:137 */     for (int i = 0; i < kvList.size(); i++) {
/*  19:139 */       if (outMsg.equals("")) {
/*  20:140 */         outMsg = kvList.keyAt(i) + "$$$" + kvList.valueAt(i);
/*  21:    */       } else {
/*  22:142 */         outMsg = outMsg + "$$$" + kvList.keyAt(i) + "$$$" + kvList.valueAt(i);
/*  23:    */       }
/*  24:    */     }
/*  25:145 */     printOut.println(outMsg);
/*  26:    */   }
/*  27:    */ }


/* Location:           C:\Users\admin.sis\Desktop\Jerry's workspace\ConnectPaceTech\prjRemote\
 * Qualified Name:     MsgEncoder
 * JD-Core Version:    0.7.0.1
 */