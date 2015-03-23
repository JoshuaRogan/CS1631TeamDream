/*   1:    */ import java.awt.Button;
/*   2:    */ import java.awt.Choice;
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.FileDialog;
/*   6:    */ import java.awt.FlowLayout;
/*   7:    */ import java.awt.Frame;
/*   8:    */ import java.awt.GridBagConstraints;
/*   9:    */ import java.awt.GridBagLayout;
/*  10:    */ import java.awt.GridLayout;
/*  11:    */ import java.awt.Label;
/*  12:    */ import java.awt.TextField;
/*  13:    */ import java.awt.event.ActionEvent;
/*  14:    */ import java.awt.event.ActionListener;
/*  15:    */ import java.awt.event.ItemEvent;
/*  16:    */ import java.awt.event.ItemListener;
/*  17:    */ import java.awt.event.MouseEvent;
/*  18:    */ import java.awt.event.MouseListener;
/*  19:    */ import java.awt.event.WindowAdapter;
/*  20:    */ import java.awt.event.WindowEvent;
/*  21:    */ import java.io.BufferedReader;
/*  22:    */ import java.io.File;
/*  23:    */ import java.io.FileReader;
/*  24:    */ import java.io.FilenameFilter;
/*  25:    */ import java.io.IOException;
/*  26:    */ import java.net.Socket;
/*  27:    */ import java.net.UnknownHostException;
/*  28:    */ import java.util.ArrayList;
/*  29:    */ import javax.swing.JOptionPane;
/*  30:    */ import javax.swing.JPanel;
/*  31:    */ import javax.swing.JScrollPane;
/*  32:    */ import javax.swing.JTable;
/*  33:    */ import javax.swing.border.TitledBorder;
/*  34:    */ import javax.swing.table.DefaultTableModel;
/*  35:    */ 
/*  36:    */ class prjGui
/*  37:    */   extends Frame
/*  38:    */ {
/*  39:    */   Button button1;
/*  40:    */   Button button2;
/*  41:    */   Button button3;
/*  42:    */   Button button4;
/*  43:    */   Button button5;
/*  44:    */   Button button6;
/*  45:    */   Button button7;
/*  46:    */   TextField text1;
/*  47:    */   TextField text2;
/*  48:    */   TextField text3;
/*  49:    */   TextField text4;
/*  50:    */   JPanel panel1;
/*  51:    */   JPanel panel2;
/*  52:    */   JPanel panel3;
/*  53:    */   Choice msglist;
/*  54:    */   JTable msgdetail1;
/*  55:    */   DefaultTableModel tablemodel1;
/*  56:    */   JTable msgdetail2;
/*  57:    */   DefaultTableModel tablemodel2;
/*  58:    */   Label label1;
/*  59:    */   Label label2;
/*  60:    */   Label label3;
/*  61:    */   
/*  62:    */   prjGui()
/*  63:    */   {
/*  64: 72 */     this.button1 = new Button("Connect");
/*  65: 73 */     this.button2 = new Button("Save");
/*  66: 74 */     this.button3 = new Button("Load");
/*  67: 75 */     this.button4 = new Button("Send");
/*  68: 76 */     this.button5 = new Button("Send All");
/*  69: 77 */     this.button6 = new Button("Save Message");
/*  70: 78 */     this.button7 = new Button("Apply Rate");
/*  71: 79 */     this.text1 = new TextField("127.0.0.1");
/*  72: 80 */     this.text2 = new TextField("7999");
/*  73: 81 */     this.label1 = new Label("Server's IP");
/*  74: 82 */     this.label2 = new Label("Port Number");
/*  75: 83 */     this.label3 = new Label("Refresh Rate (s/Msg)");
/*  76: 84 */     this.text4 = new TextField("2.0");
/*  77: 85 */     this.panel3 = new JPanel();
/*  78:    */     
/*  79:    */ 
/*  80: 88 */     GridBagConstraints c = new GridBagConstraints();
/*  81:    */     
/*  82: 90 */     this.panel1 = new JPanel();
/*  83:    */     
/*  84: 92 */     this.panel1.setBorder(new TitledBorder("Sending Message"));
/*  85:    */     
/*  86: 94 */     this.text3 = new TextField("");
/*  87: 95 */     this.text3.setEditable(false);
/*  88: 96 */     JPanel temppanel1 = new JPanel();
/*  89: 97 */     temppanel1.setLayout(new GridBagLayout());
/*  90: 98 */     c.fill = 2;
/*  91: 99 */     c.gridx = 0;
/*  92:100 */     c.gridy = 0;
/*  93:101 */     c.weighty = 1.0D;
/*  94:102 */     c.weightx = 2000.0D;
/*  95:103 */     temppanel1.add(this.text3, c);
/*  96:    */     
/*  97:    */ 
/*  98:106 */     c.gridx = 1;
/*  99:107 */     c.gridy = 0;
/* 100:108 */     c.weighty = 1.0D;
/* 101:109 */     c.weightx = 1.0D;
/* 102:110 */     temppanel1.add(this.button3, c);
/* 103:    */     
/* 104:112 */     JPanel temppanel2 = new JPanel();
/* 105:113 */     this.msglist = new Choice();
/* 106:114 */     this.msglist.addItem("TestMsg");
/* 107:115 */     this.msglist.select(0);
/* 108:116 */     temppanel2.add(this.msglist);
/* 109:117 */     temppanel2.add(this.button2);
/* 110:118 */     temppanel2.add(this.button4);
/* 111:119 */     temppanel2.add(this.button5);
/* 112:    */     
/* 113:121 */     this.tablemodel1 = new DefaultTableModel();
/* 114:122 */     this.tablemodel1.addColumn("Key");
/* 115:123 */     this.tablemodel1.addColumn("Value");
/* 116:124 */     this.msgdetail1 = new JTable(this.tablemodel1);
/* 117:125 */     this.tablemodel1.addRow(new Object[] { "MsgID", "1000" });
/* 118:126 */     this.tablemodel1.addRow(new Object[] { "Description", "Test Msg" });
/* 119:127 */     this.msgdetail1.setCellSelectionEnabled(true);
/* 120:    */     
/* 121:129 */     this.panel1.setLayout(new GridBagLayout());
/* 122:130 */     c.fill = 1;
/* 123:131 */     c.gridx = 0;
/* 124:132 */     c.gridy = 0;
/* 125:133 */     c.weighty = 1.0D;
/* 126:134 */     c.weightx = 1.0D;
/* 127:135 */     this.panel1.add(temppanel1, c);
/* 128:136 */     c.gridx = 0;
/* 129:137 */     c.gridy = 1;
/* 130:138 */     c.weighty = 2000.0D;
/* 131:139 */     c.weightx = 1.0D;
/* 132:    */     JScrollPane tempjspane;
/* 133:141 */     this.panel1.add(tempjspane = new JScrollPane(this.msgdetail1), c);
/* 134:142 */     c.gridx = 0;
/* 135:143 */     c.gridy = 2;
/* 136:144 */     c.weighty = 1.0D;
/* 137:145 */     c.weightx = 1.0D;
/* 138:146 */     this.panel1.add(temppanel2, c);
/* 139:    */     
/* 140:    */ 
/* 141:149 */     this.panel2 = new JPanel();
/* 142:    */     
/* 143:151 */     this.panel2.setBorder(new TitledBorder("Message Received"));
/* 144:    */     
/* 145:153 */     this.tablemodel2 = new DefaultTableModel()
/* 146:    */     {
/* 147:    */       public boolean isCellEditable(int rowIndex, int vColIndex)
/* 148:    */       {
/* 149:157 */         return false;
/* 150:    */       }
/* 151:159 */     };
/* 152:160 */     this.tablemodel2.addColumn("Key");
/* 153:161 */     this.tablemodel2.addColumn("Value");
/* 154:162 */     this.msgdetail2 = new JTable(this.tablemodel2);
/* 155:163 */     this.msgdetail2.setCellSelectionEnabled(true);
/* 156:    */     
/* 157:165 */     JPanel temppanel3 = new JPanel();
/* 158:166 */     temppanel3.setLayout(new FlowLayout(1, 10, 5));
/* 159:167 */     temppanel3.add(this.label3);
/* 160:168 */     temppanel3.add(this.text4);
/* 161:169 */     temppanel3.add(this.button7);
/* 162:    */     
/* 163:171 */     this.panel2.setLayout(new GridBagLayout());
/* 164:172 */     c.fill = 1;
/* 165:173 */     c.gridx = 0;
/* 166:174 */     c.gridy = 0;
/* 167:175 */     c.weighty = 2000.0D;
/* 168:176 */     c.weightx = 1.0D;
/* 169:177 */     this.panel2.add(new JScrollPane(this.msgdetail2), c);
/* 170:178 */     c.fill = 10;
/* 171:179 */     c.gridx = 0;
/* 172:180 */     c.gridy = 1;
/* 173:181 */     c.weighty = 1.0D;
/* 174:182 */     c.weightx = 1.0D;
/* 175:183 */     this.panel2.add(temppanel3, c);
/* 176:184 */     c.gridx = 0;
/* 177:185 */     c.gridy = 2;
/* 178:186 */     c.weighty = 1.0D;
/* 179:187 */     c.weightx = 1.0D;
/* 180:188 */     this.panel2.add(this.button6, c);
/* 181:    */     
/* 182:190 */     JPanel temppanel = new JPanel();
/* 183:191 */     temppanel.setLayout(new GridLayout());
/* 184:192 */     temppanel.add(this.panel1);
/* 185:193 */     temppanel.add(this.panel2);
/* 186:    */     
/* 187:    */ 
/* 188:196 */     this.panel3.setLayout(new FlowLayout(1, 10, 5));
/* 189:197 */     this.panel3.add(this.label1);
/* 190:198 */     this.panel3.add(this.text1);
/* 191:199 */     this.panel3.add(this.label2);
/* 192:200 */     this.panel3.add(this.text2);
/* 193:201 */     this.panel3.add(this.button1);
/* 194:    */     
/* 195:203 */     setLayout(new GridBagLayout());
/* 196:204 */     c.fill = 1;
/* 197:205 */     c.gridx = 0;
/* 198:206 */     c.gridy = 0;
/* 199:207 */     c.weighty = 1.0D;
/* 200:208 */     c.weightx = 1.0D;
/* 201:209 */     add(this.panel3, c);
/* 202:210 */     c.gridx = 0;
/* 203:211 */     c.gridy = 1;
/* 204:212 */     c.weighty = 2000.0D;
/* 205:213 */     c.weightx = 1.0D;
/* 206:214 */     add(temppanel, c);
/* 207:    */     
/* 208:216 */     this.button1.addActionListener(new ActionListener()
/* 209:    */     {
/* 210:    */       public void actionPerformed(ActionEvent e)
/* 211:    */       {
/* 212:    */         try
/* 213:    */         {
/* 214:219 */           prjRemote.serverconn = new Socket(prjGui.this.text1.getText(), Integer.parseInt(prjGui.this.text2.getText()));
/* 215:220 */           prjGui.this.button2.setEnabled(true);
/* 216:221 */           prjGui.this.button3.setEnabled(true);
/* 217:222 */           prjGui.this.button4.setEnabled(true);
/* 218:223 */           prjGui.this.button5.setEnabled(true);
/* 219:224 */           prjGui.this.button6.setEnabled(true);
/* 220:225 */           prjGui.this.button7.setEnabled(true);
/* 221:226 */           prjGui.this.msgdetail1.setEnabled(true);
/* 222:227 */           prjGui.this.msglist.setEnabled(true);
/* 223:228 */           prjGui.this.msgdetail2.setEnabled(true);
/* 224:229 */           prjGui.this.text3.setEnabled(true);
/* 225:230 */           prjGui.this.text4.setEnabled(true);
/* 226:231 */           prjGui.this.button1.setEnabled(false);
/* 227:232 */           prjGui.this.text1.setEnabled(false);
/* 228:233 */           prjGui.this.text2.setEnabled(false);
/* 229:234 */           prjRemote.init();
/* 230:    */         }
/* 231:    */         catch (NumberFormatException e1)
/* 232:    */         {
/* 233:236 */           JOptionPane.showMessageDialog(new Frame(), "Port number must be an integer.");
/* 234:    */         }
/* 235:    */         catch (UnknownHostException e1)
/* 236:    */         {
/* 237:238 */           JOptionPane.showMessageDialog(new Frame(), "Connection to server failed.");
/* 238:    */         }
/* 239:    */         catch (IOException e1)
/* 240:    */         {
/* 241:240 */           JOptionPane.showMessageDialog(new Frame(), "Connection to server failed.");
/* 242:    */         }
/* 243:    */       }
/* 244:244 */     });
/* 245:245 */     this.button7.addActionListener(new ActionListener()
/* 246:    */     {
/* 247:    */       public void actionPerformed(ActionEvent e)
/* 248:    */       {
/* 249:248 */         int rate = Math.round(Float.parseFloat(prjGui.this.text4.getText()) * 1000.0F);
/* 250:249 */         if (rate <= 10) {
/* 251:250 */           rate = 10;
/* 252:    */         }
/* 253:251 */         prjRemote.refreshrate = rate;
/* 254:    */       }
/* 255:254 */     });
/* 256:255 */     this.button6.addActionListener(new ActionListener()
/* 257:    */     {
/* 258:    */       public void actionPerformed(ActionEvent e)
/* 259:    */       {
/* 260:258 */         KeyValueList kvlist = prjGui.this.readKvList("r");
/* 261:259 */         if (kvlist != null)
/* 262:    */         {
/* 263:261 */           FileDialog fchooser = new FileDialog(new Frame(), "Save Message", 1);
/* 264:262 */           fchooser.setVisible(true);
/* 265:263 */           fchooser.setFilenameFilter(new FilenameFilter()
/* 266:    */           {
/* 267:    */             public boolean accept(File dir, String name)
/* 268:    */             {
/* 269:265 */               if ((name.endsWith(".xml")) || (name.endsWith(".XML"))) {
/* 270:266 */                 return true;
/* 271:    */               }
/* 272:267 */               return false;
/* 273:    */             }
/* 274:269 */           });
/* 275:270 */           String fpath = fchooser.getFile();
/* 276:271 */           if (fpath != null) {
/* 277:    */             try
/* 278:    */             {
/* 279:273 */               if ((!fpath.endsWith(".xml")) && (!fpath.endsWith(".XML"))) {
/* 280:274 */                 fpath = fpath + ".XML";
/* 281:    */               }
/* 282:275 */               XMLUtil.writeToXML(kvlist, new File(fchooser.getDirectory(), fpath));
/* 283:    */             }
/* 284:    */             catch (IOException localIOException) {}
/* 285:    */           }
/* 286:    */         }
/* 287:    */       }
/* 288:280 */     });
/* 289:281 */     this.button2.addActionListener(new ActionListener()
/* 290:    */     {
/* 291:    */       public void actionPerformed(ActionEvent e)
/* 292:    */       {
/* 293:284 */         KeyValueList kvlist = prjGui.this.readKvList("s");
/* 294:285 */         if (kvlist != null)
/* 295:    */         {
/* 296:287 */           FileDialog fchooser = new FileDialog(new Frame(), "Save Message", 1);
/* 297:    */           
/* 298:289 */           fchooser.setFilenameFilter(new FilenameFilter()
/* 299:    */           {
/* 300:    */             public boolean accept(File dir, String name)
/* 301:    */             {
/* 302:291 */               if ((name.endsWith(".xml")) || (name.endsWith(".XML"))) {
/* 303:292 */                 return true;
/* 304:    */               }
/* 305:293 */               return false;
/* 306:    */             }
/* 307:294 */           });
/* 308:295 */           String temp = prjGui.this.msglist.getSelectedItem();
/* 309:296 */           if ((!temp.endsWith(".xml")) && (!temp.endsWith(".XML"))) {
/* 310:297 */             temp = temp + ".XML";
/* 311:    */           }
/* 312:298 */           fchooser.setFile(temp);
/* 313:299 */           fchooser.setVisible(true);
/* 314:    */           
/* 315:301 */           String fpath = fchooser.getFile();
/* 316:302 */           if (fpath != null) {
/* 317:    */             try
/* 318:    */             {
/* 319:304 */               if ((!fpath.endsWith(".xml")) && (!fpath.endsWith(".XML"))) {
/* 320:305 */                 fpath = fpath + ".XML";
/* 321:    */               }
/* 322:306 */               XMLUtil.writeToXML(kvlist, new File(fchooser.getDirectory(), fpath));
/* 323:    */             }
/* 324:    */             catch (IOException localIOException) {}
/* 325:    */           }
/* 326:    */         }
/* 327:    */       }
/* 328:311 */     });
/* 329:312 */     this.button3.addActionListener(new ActionListener()
/* 330:    */     {
/* 331:    */       public void actionPerformed(ActionEvent e)
/* 332:    */       {
/* 333:315 */         FileDialog fchooser = new FileDialog(new Frame(), "Load Message or Message List", 0);
/* 334:    */         
/* 335:317 */         fchooser.setFilenameFilter(new FilenameFilter()
/* 336:    */         {
/* 337:    */           public boolean accept(File dir, String name)
/* 338:    */           {
/* 339:319 */             if ((name.endsWith(".xml")) || (name.endsWith(".XML")) || (name.endsWith(".txt")) || (name.endsWith(".TXT")) || (name.indexOf('.') == -1)) {
/* 340:320 */               return true;
/* 341:    */             }
/* 342:321 */             return false;
/* 343:    */           }
/* 344:323 */         });
/* 345:324 */         fchooser.setVisible(true);
/* 346:    */         
/* 347:    */ 
/* 348:    */ 
/* 349:328 */         String filename = fchooser.getFile();
/* 350:330 */         if (filename != null)
/* 351:    */         {
/* 352:332 */           File tempfile = new File(fchooser.getDirectory(), fchooser.getFile());
/* 353:333 */           prjGui.this.text3.setText(tempfile.toString());
/* 354:335 */           if ((filename.endsWith(".xml")) || (filename.endsWith(".XML")))
/* 355:    */           {
/* 356:337 */             prjGui.this.msglist.removeAll();
/* 357:338 */             prjRemote.loadedmsgs.clear();
/* 358:339 */             prjRemote.currentloadedmsg = null;
/* 359:340 */             KeyValueList tempkvlist = XMLUtil.readFromXML(tempfile);
/* 360:341 */             if (tempkvlist != null)
/* 361:    */             {
/* 362:343 */               prjGui.this.msglist.add(filename);
/* 363:344 */               prjGui.this.msglist.select(0);
/* 364:345 */               prjRemote.loadedmsgs.add(new LoadedMsg(0, tempkvlist));
/* 365:346 */               prjRemote.currentloadedmsg = (LoadedMsg)prjRemote.loadedmsgs.get(0);
/* 366:    */             }
/* 367:    */             else
/* 368:    */             {
/* 369:351 */               JOptionPane.showMessageDialog(new Frame(), "Error: File " + filename + " could not be loaded.");
/* 370:352 */               prjGui.this.text3.setText("");
/* 371:    */             }
/* 372:355 */             prjGui.this.displayKvList(tempkvlist, "s");
/* 373:    */           }
/* 374:    */           else
/* 375:    */           {
/* 376:359 */             prjGui.this.msglist.removeAll();
/* 377:360 */             prjRemote.loadedmsgs.clear();
/* 378:361 */             prjRemote.currentloadedmsg = null;
/* 379:    */             try
/* 380:    */             {
/* 381:363 */               BufferedReader br = new BufferedReader(new FileReader(tempfile));
/* 382:364 */               while (br.ready())
/* 383:    */               {
/* 384:366 */                 String tempstr = br.readLine().trim();
/* 385:367 */                 if (tempstr.length() != 0)
/* 386:    */                 {
/* 387:369 */                   File tempfile1 = new File(tempstr);
/* 388:370 */                   if (!tempfile1.exists())
/* 389:    */                   {
/* 390:372 */                     tempfile1 = new File(System.getProperty("user.dir"), tempstr);
/* 391:373 */                     if (!tempfile1.exists())
/* 392:    */                     {
/* 393:375 */                       JOptionPane.showMessageDialog(new Frame(), "Error: File " + tempfile1 + " does not exist.");
/* 394:376 */                       continue;
/* 395:    */                     }
/* 396:    */                   }
/* 397:379 */                   KeyValueList tempkvlist = XMLUtil.readFromXML(tempfile1);
/* 398:380 */                   if (tempkvlist != null)
/* 399:    */                   {
/* 400:382 */                     prjRemote.loadedmsgs.add(new LoadedMsg(prjRemote.loadedmsgs.size(), tempkvlist));
/* 401:383 */                     prjGui.this.msglist.add(tempfile1.getName());
/* 402:    */                   }
/* 403:    */                 }
/* 404:    */               }
/* 405:    */             }
/* 406:    */             catch (Exception localException) {}
/* 407:    */             KeyValueList tempkvlist;
/* 408:388 */             if (prjGui.this.msglist.getItemCount() != 0)
/* 409:    */             {
/* 410:390 */               prjGui.this.msglist.select(0);
/* 411:391 */               KeyValueList tempkvlist = ((LoadedMsg)prjRemote.loadedmsgs.get(0)).msg;
/* 412:392 */               prjRemote.currentloadedmsg = (LoadedMsg)prjRemote.loadedmsgs.get(0);
/* 413:    */             }
/* 414:    */             else
/* 415:    */             {
/* 416:396 */               tempkvlist = null;
/* 417:397 */               JOptionPane.showMessageDialog(new Frame(), "Error: No File loaded.");
/* 418:398 */               prjGui.this.text3.setText("");
/* 419:    */             }
/* 420:401 */             prjGui.this.displayKvList(tempkvlist, "s");
/* 421:    */           }
/* 422:    */         }
/* 423:    */       }
/* 424:406 */     });
/* 425:407 */     this.button4.addActionListener(new ActionListener()
/* 426:    */     {
/* 427:    */       public void actionPerformed(ActionEvent e)
/* 428:    */       {
/* 429:411 */         KeyValueList tkvlist = prjGui.this.readKvList("s");
/* 430:412 */         if (tkvlist == null) {
/* 431:413 */           return;
/* 432:    */         }
/* 433:416 */         prjRemote.currentloadedmsg.msg = tkvlist;
/* 434:    */         
/* 435:    */ 
/* 436:419 */         KeyValueList kvlist = prjRemote.currentloadedmsg.msg;
/* 437:420 */         kvlist.prepareForSend();
/* 438:    */         try
/* 439:    */         {
/* 440:423 */           prjRemote.mEncoder.sendMsg(kvlist, prjRemote.serverconn.getOutputStream());
/* 441:    */         }
/* 442:    */         catch (IOException localIOException) {}
/* 443:    */       }
/* 444:426 */     });
/* 445:427 */     this.button5.addActionListener(new ActionListener()
/* 446:    */     {
/* 447:    */       public void actionPerformed(ActionEvent e)
/* 448:    */       {
/* 449:431 */         KeyValueList tkvlist = prjGui.this.readKvList("s");
/* 450:432 */         if (tkvlist == null) {
/* 451:433 */           return;
/* 452:    */         }
/* 453:436 */         prjRemote.currentloadedmsg.msg = tkvlist;
/* 454:    */         try
/* 455:    */         {
/* 456:440 */           for (int i = 0; i < prjRemote.loadedmsgs.size(); i++)
/* 457:    */           {
/* 458:442 */             KeyValueList kvlist = ((LoadedMsg)prjRemote.loadedmsgs.get(i)).msg;
/* 459:443 */             kvlist.prepareForSend();
/* 460:444 */             prjRemote.mEncoder.sendMsg(kvlist, prjRemote.serverconn.getOutputStream());
/* 461:    */           }
/* 462:    */         }
/* 463:    */         catch (IOException localIOException) {}
/* 464:    */       }
/* 465:449 */     });
/* 466:450 */     this.msglist.addItemListener(new ItemListener()
/* 467:    */     {
/* 468:    */       public void itemStateChanged(ItemEvent e)
/* 469:    */       {
/* 470:454 */         KeyValueList kvlist = prjGui.this.readKvList("s");
/* 471:455 */         if (kvlist == null)
/* 472:    */         {
/* 473:457 */           prjGui.this.msglist.select(prjRemote.currentloadedmsg.index);
/* 474:458 */           return;
/* 475:    */         }
/* 476:462 */         prjRemote.currentloadedmsg.msg = kvlist;
/* 477:    */         
/* 478:464 */         int selected = prjGui.this.msglist.getSelectedIndex();
/* 479:465 */         if (selected == -1)
/* 480:    */         {
/* 481:467 */           prjRemote.currentloadedmsg = null;
/* 482:468 */           prjGui.this.displayKvList(null, "s");
/* 483:469 */           return;
/* 484:    */         }
/* 485:471 */         prjRemote.currentloadedmsg = (LoadedMsg)prjRemote.loadedmsgs.get(selected);
/* 486:472 */         prjGui.this.displayKvList(prjRemote.currentloadedmsg.msg, "s");
/* 487:    */       }
/* 488:475 */     });
/* 489:476 */     this.msgdetail1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
/* 490:    */     
/* 491:478 */     tempjspane.addMouseListener(new MouseListener()
/* 492:    */     {
/* 493:    */       public void mouseClicked(MouseEvent e)
/* 494:    */       {
/* 495:482 */         if ((e.getClickCount() >= 2) && (prjGui.this.button3.isEnabled())) {
/* 496:483 */           prjGui.this.tablemodel1.addRow(new String[] { "", "" });
/* 497:    */         }
/* 498:    */       }
/* 499:    */       
/* 500:    */       public void mouseEntered(MouseEvent e) {}
/* 501:    */       
/* 502:    */       public void mouseExited(MouseEvent e) {}
/* 503:    */       
/* 504:    */       public void mousePressed(MouseEvent e) {}
/* 505:    */       
/* 506:    */       public void mouseReleased(MouseEvent e) {}
/* 507:490 */     });
/* 508:491 */     this.msgdetail1.setGridColor(Color.black);
/* 509:492 */     this.msgdetail2.setGridColor(Color.black);
/* 510:    */     
/* 511:    */ 
/* 512:495 */     this.button2.setEnabled(false);
/* 513:496 */     this.button3.setEnabled(false);
/* 514:497 */     this.button4.setEnabled(false);
/* 515:498 */     this.button5.setEnabled(false);
/* 516:499 */     this.button6.setEnabled(false);
/* 517:500 */     this.button7.setEnabled(false);
/* 518:501 */     this.msgdetail1.setEnabled(false);
/* 519:502 */     this.msglist.setEnabled(false);
/* 520:503 */     this.msgdetail2.setEnabled(false);
/* 521:504 */     this.text3.setEnabled(false);
/* 522:505 */     this.text4.setEnabled(false);
/* 523:    */     
/* 524:507 */     setPreferredSize(new Dimension(750, 550));
/* 525:508 */     addWindowListener(new WindowAdapter()
/* 526:    */     {
/* 527:    */       public void windowClosing(WindowEvent e)
/* 528:    */       {
/* 529:508 */         System.exit(0);
/* 530:    */       }
/* 531:508 */     });
/* 532:509 */     pack();
/* 533:510 */     setVisible(true);
/* 534:    */   }
/* 535:    */   
/* 536:    */   synchronized void displayKvList(KeyValueList in, String flag)
/* 537:    */   {
/* 538:    */     DefaultTableModel tempmodel;
/* 539:516 */     if (flag.equalsIgnoreCase("r"))
/* 540:    */     {
/* 541:518 */       tempmodel = this.tablemodel2;
/* 542:    */     }
/* 543:    */     else
/* 544:    */     {
/* 545:    */       DefaultTableModel tempmodel;
/* 546:520 */       if (flag.equalsIgnoreCase("s")) {
/* 547:522 */         tempmodel = this.tablemodel1;
/* 548:    */       } else {
/* 549:    */         return;
/* 550:    */       }
/* 551:    */     }
/* 552:    */     DefaultTableModel tempmodel;
/* 553:526 */     int rowcount = tempmodel.getRowCount();
/* 554:527 */     for (int i = 0; i < rowcount; i++) {
/* 555:528 */       tempmodel.removeRow(0);
/* 556:    */     }
/* 557:530 */     if (in == null) {
/* 558:531 */       return;
/* 559:    */     }
/* 560:532 */     for (int i = 0; i < in.size(); i++) {
/* 561:533 */       tempmodel.addRow(new String[] { in.keyAt(i), in.valueAt(i) });
/* 562:    */     }
/* 563:    */   }
/* 564:    */   
/* 565:    */   synchronized KeyValueList readKvList(String flag)
/* 566:    */   {
/* 567:539 */     KeyValueList result = null;
/* 568:    */     DefaultTableModel tempmodel;
/* 569:541 */     if (flag.equalsIgnoreCase("r"))
/* 570:    */     {
/* 571:543 */       tempmodel = this.tablemodel2;
/* 572:    */     }
/* 573:    */     else
/* 574:    */     {
/* 575:    */       DefaultTableModel tempmodel;
/* 576:545 */       if (flag.equalsIgnoreCase("s")) {
/* 577:547 */         tempmodel = this.tablemodel1;
/* 578:    */       } else {
/* 579:550 */         return null;
/* 580:    */       }
/* 581:    */     }
/* 582:    */     DefaultTableModel tempmodel;
/* 583:551 */     result = new KeyValueList();
/* 584:552 */     int rowcount = tempmodel.getRowCount();
/* 585:553 */     for (int i = 0; i < rowcount; i++)
/* 586:    */     {
/* 587:555 */       String temp1 = (String)tempmodel.getValueAt(i, 0);
/* 588:556 */       String temp2 = (String)tempmodel.getValueAt(i, 1);
/* 589:557 */       temp1 = temp1.trim();
/* 590:558 */       temp2 = temp2.trim();
/* 591:559 */       if ((temp1.indexOf('$') != -1) || (temp2.indexOf('$') != -1))
/* 592:    */       {
/* 593:561 */         JOptionPane.showMessageDialog(new Frame(), "No '$' character allowed in any key or value.\nError: Line " + String.valueOf(i + 1));
/* 594:562 */         return null;
/* 595:    */       }
/* 596:564 */       if ((temp1.length() == 0) && (temp2.length() != 0))
/* 597:    */       {
/* 598:566 */         JOptionPane.showMessageDialog(new Frame(), "Value \"" + temp2 + "\" has no corresponding Key.\nError: Line " + String.valueOf(i + 1));
/* 599:567 */         return null;
/* 600:    */       }
/* 601:569 */       if ((temp2.length() == 0) && (temp1.length() != 0))
/* 602:    */       {
/* 603:571 */         JOptionPane.showMessageDialog(new Frame(), "Key \"" + temp2 + "\" has no corresponding Value.\nError: Line " + String.valueOf(i + 1));
/* 604:572 */         return null;
/* 605:    */       }
/* 606:574 */       if ((temp2.length() != 0) && (temp1.length() != 0)) {
/* 607:575 */         result.addPair(temp1, temp2);
/* 608:    */       }
/* 609:    */     }
/* 610:577 */     if (result.size() == 0)
/* 611:    */     {
/* 612:579 */       JOptionPane.showMessageDialog(new Frame(), "Empty Message.");
/* 613:580 */       return null;
/* 614:    */     }
/* 615:582 */     return result;
/* 616:    */   }
/* 617:    */ }


/* Location:           C:\Users\admin.sis\Desktop\Jerry's workspace\ConnectPaceTech\prjRemote\
 * Qualified Name:     prjGui
 * JD-Core Version:    0.7.0.1
 */