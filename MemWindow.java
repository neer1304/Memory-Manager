/* 
 * MemWindow.java (Implements Window for memory manager.).
 *
 * Written by : Nitin  Motgi (nmotgi@cs.ucf.edu)
 * 
 * Portions copyright(c) 2001 to School of Electrical Engineering and 
 * Computer Science, UCF, Orlando.                   
 *                                    
 * Use and distribution of this source code are strictly governed by 
 * terms and conditions set by the authors.
 * 
 * $Id : MemWindow.java, v1.0.0 02/24/2001. $
 *            
 * Revision History:
 *
 * 1. Created basic structure           Nitin,        v1.0.0  02/26/2001.
 * 2. Added Documentation.              Nitin,        v1.0.1  02/26/2001.
 * 3. Final Documentation Check.        Nitin,        v1.4.3  02/27/2001.  
 * 4. Final Variable Name Check.        Nitin,        v1.4.4  03/07/2001.          
 * 5. Final Functionality Check.         Nitin,        v1.4.5  03/07/2001.          
*/

/* Import some of the libraries.*/
import java.lang.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MemWindow extends JComponent implements ActionListener{

  /* Some of Update functionalities.*/
  private JTextField lineProcessed;
  private JTextField FreeProcessed;
  private JTextField SystemProcessed;
  private int        nIndex;
  private int        nRequest;
  private String     szRequest[][];
  private Heap       MyHeap;
  private int        nHeapSize;
  JFrame  frame;
  
  /* Default constructor does nothing.*/
  public MemWindow(){
   /* TODO : Insert any code if you required.*/
  }/* End of default constructor.*/

  /* Basic Start Up Window.*/
  public MemWindow(String[][] szRequest,int nRequest,int nHeapSize){
    this.szRequest = szRequest;
    this.nRequest = nRequest;   
    this.nHeapSize = nHeapSize;

    /* Create Heap area.*/
    MyHeap = new Heap(nHeapSize);

    nIndex = 1;

    /* Create the whole window.*/
    CreateWindow();
    setLine(szRequest[nIndex][0]+" "+szRequest[nIndex][1] +
            " " + szRequest[nIndex][2]);
    setFree(MyHeap.HeapAvailable() + " ");
  }/* End of constructor.*/

  /* This function is called when ever window resizes of explicitly called
     by update(Graphics).*/
  public void paint(Graphics gHandle){
   MyHeap.UpdateGraphicsHeap(gHandle,40,275,50,580,nHeapSize);
  }/* End of paint.*/

  /* Creates a Frame and set the mouse listener for this frame.
     it also creates the necessary components required by this
     application.*/
  public void CreateWindow(){
   frame = new JFrame("Memory Management Simulation ver 2.1.0");
   
   /* Create window.*/
   frame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        System.exit(0);
      }
   });

   
   /* Stuff in everything in the panel and display it.*/
   JPanel MemPanel = new JPanel();
   
   JButton button;
   JButton legendInUs;
   JButton legendUnUs;                  
   JButton preMem;
   JLabel  lineMessage;
   JLabel  FreeMessage;
   JLabel  SystemMessage;
   JLabel  memMessage;
   JLabel  minMsg;
   JLabel  maxMsg;
  

   button = new JButton("Process");
   preMem = new JButton();
   preMem.setEnabled(false);
   preMem.setVisible(true);
   button.addActionListener(this);
   lineProcessed = new JTextField();
   lineMessage = new JLabel("Action:");
   memMessage  = new JLabel("Memory");
   minMsg = new JLabel(" "+0+" ");
   System.out.println(nHeapSize);
   maxMsg = new JLabel(" "+(nHeapSize-1)+" ");
   SystemProcessed = new JTextField();
   SystemMessage = new JLabel("System Message:");

   FreeProcessed = new JTextField();
   FreeMessage = new JLabel("Total Free:");
   
   MemPanel.add(button);
   legendInUs = new JButton("Allocated");
   legendInUs.setEnabled(false);
   legendInUs.setVisible(true);
   legendUnUs = new JButton("Free");
   legendUnUs.setEnabled(false);

   MemPanel.add(legendInUs);
   MemPanel.add(legendUnUs);
   MemPanel.add(lineMessage);
   MemPanel.add(lineProcessed);
   MemPanel.add(FreeMessage);
   MemPanel.add(FreeProcessed);
   MemPanel.add(SystemMessage);
   MemPanel.add(SystemProcessed);
   MemPanel.add(preMem);
   MemPanel.add(memMessage);
   MemPanel.add(minMsg);
   MemPanel.add(maxMsg);

   frame.setContentPane(MemPanel);    
   frame.setSize(new Dimension(600,700));
   frame.setLocation(new Point(500,300));
   frame.setResizable(false);
   frame.setVisible(true);

   button.setBounds(450,300,100,100);
   legendUnUs.setBounds(450,405,100,100);
   legendUnUs.setBackground(Color.green);
   legendInUs.setBounds(450,510,100,100);
   legendInUs.setBackground(Color.red);
   lineMessage.setBounds(400,200,50,50);
   lineProcessed.setBounds(450,215,100,25);
   FreeMessage.setBounds(380,150,150,50);
   FreeProcessed.setBounds(450,165,100,25);
   SystemMessage.setBounds(5,620,200,50);
   SystemProcessed.setBounds(110,635,445,25);
   preMem.setBackground(Color.green);
   preMem.setBounds(37,28,276,579);
   memMessage.setBounds(35,10,100,15);
   maxMsg.setBounds(315,20,50,15);
   minMsg.setBounds(315,600,50,15);
  }/* End of _CreateWindow.*/

  /* This tracks the events happening on the frame. Only the activity
     on the "process" button are captured and processed.*/
  public void actionPerformed(ActionEvent ae){
   String szArg = ae.getActionCommand();
   update(frame.getGraphics());
   if(szArg.equals("Process")){
     if(nIndex <= nRequest-1){
       ProcessRequest();
       update(frame.getGraphics());
     }else{
       setFree(MyHeap.HeapAvailable() + " ");
       setMessage("Completed parsing Memory file.");
     }/* End if.*/
   }/* End if.*/
  }/* End of actionPerformed.*/

  /* This function processes each request to access memory, this is basically
     similar to dispatcher which on receiving a request will see what type
     of request it is and then forward it to appropriate handler for that
     message.*/
  private void ProcessRequest(){
   
   /* Once, the file is read, now we can read start processing the 
      command.*/
   if(CheckLineError(nIndex) == true){
     setMessage("Error in line : " + (nIndex+1));
     return;
   }/* End if.*/

   if(szRequest[nIndex][0].equals("malloc") == true){
     if(MyHeap.malloc(szRequest[nIndex][1],
        Integer.parseInt(szRequest[nIndex][2])) == false)
        ShowMessage();
     else
        setMessage("Command successfully completed.");
     nIndex++;
     ShowLine();
     return;
    }/* End if.*/

    if(szRequest[nIndex][0].equals("realloc") == true){
     if(MyHeap.realloc(szRequest[nIndex][1],
                       Integer.parseInt(szRequest[nIndex][2])) == false)
       ShowMessage();
     else
       setMessage("Command successfully completed.");
     nIndex++;
     ShowLine();
     return;
    }/* End if.*/

    if(szRequest[nIndex][0].equals("free") == true){
     if(MyHeap.free(szRequest[nIndex][1]) == false)
      ShowMessage();
     else
      setMessage("Command successfully completed.");
     nIndex++;                           
     ShowLine();
     return;
    }/* End if.*/

    if(szRequest[nIndex][0].equals("compact")){
     MyHeap.compact();
     ShowMessage();
     nIndex++;
     ShowLine();
     return;
    }/* End if.*/

    nIndex++;
    setMessage("Failed to interpret command.");
   }/* End of funtion.*/

 /* Checks if intergrity of the command fails.*/
  private boolean CheckLineError(int nLineNumber){

   if(szRequest[nLineNumber][0].equals("malloc") == true ||
      szRequest[nLineNumber][0].equals("realloc") == true) {
    if(szRequest[nLineNumber][1].equals("null") == true) return true;
    if(szRequest[nLineNumber][2] == null) return true;
   }/* End if.*/

   if(szRequest[nLineNumber][0].equals("free") == true){
    if(szRequest[nLineNumber][1] == null) return true;
   }/* End if.*/

   if(szRequest[nLineNumber][0].equals("compact") == true){
    if(szRequest[nLineNumber][1] != null) return true;
   }/* End if.*/
   return false;
  }/* End of CheckLineError.*/

  /* Outputs current free memory status.*/
  public void setFree(String szString){
   FreeProcessed.setText(szString);
  }/* End of setFree.*/

  /* Outputs current Message to System message window.*/
  public void setMessage(String szString){
   SystemProcessed.setText(szString);
  }/* End of setMessage.*/

  /* Outputs current "to be processed" line.*/
  public void setLine(String szString){
   lineProcessed.setText(szString);
  }/* End of setLine.*/

  /* Show's current command that will be processed.*/
  private void ShowLine(){
   if(nIndex == nRequest){ 
    setFree(MyHeap.HeapAvailable() + " ");
    setLine("*END*");
    return;
   }/* End if.*/

   if(szRequest[nIndex][0].equals("malloc") == true ||
      szRequest[nIndex][0].equals("realloc") == true) {
      setLine(szRequest[nIndex][0]+" "+szRequest[nIndex][1] +
              " " + szRequest[nIndex][2]);
   }/* End if.*/

   if(szRequest[nIndex][0].equals("free") == true){
    setLine(szRequest[nIndex][0]+" "+szRequest[nIndex][1]);
   }/* End if.*/

   if(szRequest[nIndex][0].equals("compact") == true){
    setLine(szRequest[nIndex][0]);
   }/* End if.*/

   setFree(MyHeap.HeapAvailable() + " ");
  }/* End of ShowLine.*/

  /* Displays error messages onto the System Message window.*/
  private void ShowMessage(){
   frame.getGraphics().setColor(Color.red);
   switch(MyHeap.nErrorCode){
    case 101:
        setMessage("H101: Heap::malloc() \"Block name already used.\"");
        break;

    case 102:
        setMessage("H102: Heap::malloc() \"No contiguos memory free "+
                   "to include this block.\"");
        break;

    case 103:
        setMessage("H103: Heap::free() \"No block of this name exists "+
                   "in memory.\"");
        break;

    case 104:
        setMessage("H104: Heap::realloc() \"No block of this name "+
                   "present for reallocation.\"");
        break;

    case 105: 
        setMessage("H105: Heap::realloc() \"No enough memory available "+
                   "for reallocation.\"");
        break;

  }/* End of switch.*/
  frame.getGraphics().setColor(Color.black);
 }/* End of ShowMessage.*/
}/* End of Bankers.*/
