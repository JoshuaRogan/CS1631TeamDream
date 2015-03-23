/*   1:    */ import java.util.ArrayList;
/*   2:    */ import java.util.Vector;
/*   3:    */ 
/*   4:    */ class KeyValueList
/*   5:    */ {
/*   6:    */   private Vector keys;
/*   7:    */   private Vector values;
/*   8:    */   
/*   9:    */   public KeyValueList()
/*  10:    */   {
/*  11: 19 */     this.keys = new Vector();
/*  12: 20 */     this.values = new Vector();
/*  13:    */   }
/*  14:    */   
/*  15:    */   public int lookupKey(String strKey)
/*  16:    */   {
/*  17: 27 */     for (int i = 0; i < this.keys.size(); i++)
/*  18:    */     {
/*  19: 29 */       String k = (String)this.keys.elementAt(i);
/*  20: 30 */       if (strKey.equals(k)) {
/*  21: 31 */         return i;
/*  22:    */       }
/*  23:    */     }
/*  24: 33 */     return -1;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public boolean addPair(String strKey, String strValue)
/*  28:    */   {
/*  29: 40 */     return (this.keys.add(strKey)) && (this.values.add(strValue));
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String getValue(String strKey)
/*  33:    */   {
/*  34: 47 */     int index = lookupKey(strKey);
/*  35: 48 */     if (index == -1) {
/*  36: 49 */       return null;
/*  37:    */     }
/*  38: 50 */     return (String)this.values.elementAt(index);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setValue(int index, String val)
/*  42:    */   {
/*  43: 55 */     if ((index >= 0) && (index < size())) {
/*  44: 56 */       this.values.set(index, val);
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String toString()
/*  49:    */   {
/*  50: 62 */     String result = new String();
/*  51: 63 */     for (int i = 0; i < this.keys.size(); i++) {
/*  52: 65 */       result = result + (String)this.keys.elementAt(i) + ":" + (String)this.values.elementAt(i) + "\n";
/*  53:    */     }
/*  54: 67 */     return result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public int size()
/*  58:    */   {
/*  59: 72 */     return this.keys.size();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public String keyAt(int index)
/*  63:    */   {
/*  64: 76 */     return (String)this.keys.elementAt(index);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String valueAt(int index)
/*  68:    */   {
/*  69: 77 */     return (String)this.values.elementAt(index);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public ArrayList<String> getValueLike(String key)
/*  73:    */   {
/*  74: 82 */     ArrayList<String> results = new ArrayList();
/*  75: 83 */     for (int i = 0; i < this.keys.size(); i++)
/*  76:    */     {
/*  77: 85 */       String temp = (String)this.keys.elementAt(i);
/*  78: 86 */       if (temp.contains(key)) {
/*  79: 87 */         results.add((String)this.values.elementAt(i));
/*  80:    */       }
/*  81:    */     }
/*  82: 89 */     if (results.size() == 0) {
/*  83: 90 */       return null;
/*  84:    */     }
/*  85: 91 */     return results;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void prepareForSend()
/*  89:    */   {
/*  90: 97 */     for (int i = 0; i < this.keys.size(); i++)
/*  91:    */     {
/*  92: 99 */       String tempstr = (String)this.keys.get(i);
/*  93:100 */       tempstr = tempstr.replace('$', ' ').trim();
/*  94:101 */       if (tempstr.length() == 0) {
/*  95:102 */         tempstr = " ";
/*  96:    */       }
/*  97:103 */       this.keys.set(i, tempstr);
/*  98:    */     }
/*  99:105 */     for (int i = 0; i < this.values.size(); i++)
/* 100:    */     {
/* 101:107 */       String tempstr = (String)this.values.get(i);
/* 102:108 */       tempstr = tempstr.replace('$', ' ').trim();
/* 103:109 */       if (tempstr.length() == 0) {
/* 104:110 */         tempstr = " ";
/* 105:    */       }
/* 106:111 */       this.values.set(i, tempstr);
/* 107:    */     }
/* 108:    */   }
/* 109:    */ }


/* Location:           C:\Users\admin.sis\Desktop\Jerry's workspace\ConnectPaceTech\prjRemote\
 * Qualified Name:     KeyValueList
 * JD-Core Version:    0.7.0.1
 */