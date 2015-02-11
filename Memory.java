/* 
 * Memory.java (Implements Memory Management System.).
 *
 * Written by : Nitin  Motgi (nmotgi@cs.ucf.edu)
 * 
 * This file implements routines to collect the data from input file,
 * process command line and also process request as given in the input
 * file. The parsing of input file can have exceptions.
 *
 * Portions copyright(c) 2001 to School of Electrical Engineering and 
 * Computer Science, UCF, Orlando.                   
 *                                    
 * Use and distribution of this source code are strictly governed by 
 * terms and conditions set by the authors.
 * 
 * $Id : Memory.java, v1.4.5 02/24/2001. $
 *            
 * Revision History:
 *
 * 1. Created basic structure           Nitin,        v1.0.0  02/24/2001.
 * 2. Added Documentation.              Nitin,        v1.0.1  02/24/2001.
 * 3. Final Documentation Check.        Nitin,        v1.4.3  03/07/2001.  
 * 4. Final Variable Name Check.        Nitin,        v1.4.4  03/07/2001.          
 * 5. Final Functionality Check.        Nitin,        v1.4.5  03/07/2001.          
*/

/* Import some of the libraries.*/
import java.lang.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Memory{
  /* stores the name of the file.*/
  public  static  String szFileName;
  
  /* Stores the complete program.*/
  private static  int        nNoOfRequest;
    
  /* Process the command line and simulates the requests for processors.*/
  public static void main(String[] szArgs){
   String[][] szRequest;
     
   /* Process command line and perform integrity check.*/
   if(szArgs.length == 0){
    System.out.println("** ERROR : No Input file specified.");
    System.out.println("Usage : Memory <input file>");
    return;
   }/* End if.*/

   /* Store file name.*/
   szFileName = szArgs[0];

   /* Scratch pad for max request is allocated.*/
   szRequest = new String[500][3];

   /* Process command line. Will return object containing set of
      parameters that will be used during simulation after parsing
      the input.*/
   ProcessCommandLine(szRequest);
   MemWindow MyWindow = new MemWindow(szRequest,nNoOfRequest,
                                      Integer.parseInt(szRequest[0][2]));
  }/* End main.*/


  /* This function parses the input file and fills in the data structure
     will properties that are required to run the simulation.
     The parameters are returned as class which extends Object.*/
  private static void ProcessCommandLine(String[][] szRequest){
   /* Buffer to store the line.*/
   String szLineBuffer;
   String szLowerCaseLine;

   /* Track Line #.*/
   int nLineCount=0;

   /* Column count.*/
   int nColumnCount;

   /* Store all the delimters in the string for processing input.*/
   String szDelimiter = new String(":, ");

   /* Current Token.*/
   String szToken;              /* Current Token that is being proceesed.*/        

   try{
    /* Open the Input file.*/
    BufferedReader In = new BufferedReader(new FileReader(szFileName));

    /* Initialise Line count.*/
    nLineCount  = 0;
    
    /* Read each line and process it.*/
    while((szLineBuffer = In.readLine()) != null){
     /* Check if first line of the character is EOL.
        if so then skip.*/
     if(szLineBuffer.length() == 0) continue;

      /* Check if the Line is comment or no, if yes then
         it useless to continue tokenizing the string.*/
      if(szLineBuffer.length() >= 2)
       if(szLineBuffer.charAt(0) == '/') 
        if(szLineBuffer.charAt(1) == '/') continue;
        else {
         System.out.println(szFileName + ": Line: " +
         nLineCount + " Improper commenting. (skipping)");
         continue;
        }/* End if.*/

       /* Turn the line into lower case.*/
       szLowerCaseLine = szLineBuffer.toLowerCase();
       
       /* Tokenize all the Objects in the string.*/
       StringTokenizer Tokens = new StringTokenizer(szLowerCaseLine,
                                                    szDelimiter);
       nColumnCount = 0;
       while(Tokens.hasMoreTokens()){
        try{
            szToken = Tokens.nextToken();      
            szRequest[nLineCount][nColumnCount] = szToken;
            nColumnCount++;
        }/* End of try.*/
        catch(NumberFormatException e){
         e.printStackTrace();
        }/* End of catch.*/
       }/* End of while.*/
       nLineCount++;  
    }/* End of while.*/
    In.close();
   }catch(Exception e){
    System.out.println("** ERROR : Input file " + szFileName + " not found.");
    System.out.println("Abnormal Termination.");
    System.exit(-1);
   }/* End of try catch Block.*/
   nNoOfRequest = nLineCount;
  }/* End of ProcessCommandLine.*/
}/* End of Memory.*/
