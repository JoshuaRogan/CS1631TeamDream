/*   1:    */ import java.io.BufferedReader;
/*   2:    */ import java.io.IOException;
/*   3:    */ import java.io.InputStream;
/*   4:    */ import java.io.InputStreamReader;
/*   5:    */ import java.util.StringTokenizer;
/*   6:    */ 
/*   7:    */ class MsgDecoder
/*   8:    */ {
/*   9:    */   private BufferedReader bufferIn;
/*  10:158 */   private final String delimiter = "$$$";
/*  11:    */   
/*  12:    */   public MsgDecoder(InputStream in)
/*  13:    */   {
/*  14:162 */     this.bufferIn = new BufferedReader(new InputStreamReader(in));
/*  15:    */   }
/*  16:    */   
/*  17:    */   public KeyValueList getMsg()
/*  18:    */     throws IOException
/*  19:    */   {
/*  20:171 */     String strMsg = this.bufferIn.readLine();
/*  21:173 */     if (strMsg == null) {
/*  22:174 */       return null;
/*  23:    */     }
/*  24:176 */     KeyValueList kvList = new KeyValueList();
/*  25:177 */     StringTokenizer st = new StringTokenizer(strMsg, "$$$");
/*  26:178 */     while (st.hasMoreTokens()) {
/*  27:180 */       kvList.addPair(st.nextToken(), st.nextToken());
/*  28:    */     }
/*  29:182 */     return kvList;
/*  30:    */   }
/*  31:    */ }


/* Location:           C:\Users\admin.sis\Desktop\Jerry's workspace\ConnectPaceTech\prjRemote\
 * Qualified Name:     MsgDecoder
 * JD-Core Version:    0.7.0.1
 */