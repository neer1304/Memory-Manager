/* 
 * MemoryBlock.java (Implements Data Structure for MemoryBlock.)
 *
 * Written by : Nitin  Motgi (nmotgi@cs.ucf.edu)
 * 
 * Portions copyright(c) 2001 to School of Electrical Engineering and 
 * Computer Science, UCF, Orlando.                   
 *
 * Use and distribution of this source code are strictly governed by 
 * terms and conditions set by the authors.
 * 
 * $Id : MemoryBlock.java, v1.0.4 02/24/2001. $
 *            
 * Revision History:
 *
 * 1. Created basic structure           Nitin,        v1.0.0  02/24/2001.
 * 2. Added Documentation.              Nitin,        v1.0.1  02/24/2001.
 * 3. Final Documentation Check.        Nitin,        v1.0.2  03/07/2001.  
 * 4. Final Variable Name Check.        Nitin,        v1.0.3  03/07/2001.          
 * 5.Final Functionality Check.         Nitin,        v1.0.4  03/07/2001.          
*/

/* Defination of Memory block. This structure is private to the usage
   in this file.Hence, the scope of this class is only this file.
   NOTE : THE BLOCKS ARE ADDRESS AS WELL SIZE ORDERED. AND, WE USE
          THE SAME LIST TO KEEP "USED" AND "UN-USED" BLOCKS. */

class MemoryBlock extends Object{
 public boolean bBlockStatus;          /* Status this Memory USED/UNUSED.*/
 public int     nStartAddr;            /* Start of Block. +ve Integer.*/
 public int     nEndAddr;              /* End of Block. +ve Integer.*/
 public int     nBlockSize;            /* Size of Block.*/
 public String  szBlockName;           /* Name of the Block. Invalid if
                                          bBlockStatus = UNUSED.*/

 /* Default Constructor.*/
 public MemoryBlock(){
 }/* End of Constructor.*/

 /* Constructor with Parameters.*/
 public MemoryBlock(boolean bBlockStatus, int nStartAddr, 
                    int nEndAddr, int nBlockSize, String szBlockName){
  this.bBlockStatus = bBlockStatus;
  this.nStartAddr   = nStartAddr;
  this.nEndAddr     = nEndAddr;
  this.nBlockSize   = nBlockSize;
  this.szBlockName  = szBlockName;
 }/* End of Constructor.*/

 /* Constructor mostly used to construct free block.*/
 public MemoryBlock(int nStartAddr,int nEndAddr,int nBlockSize){
  bBlockStatus = false;         /* Indicating that it's free.*/
  this.nStartAddr = nStartAddr;
  this.nEndAddr   = nEndAddr;
  this.nBlockSize = nBlockSize;
 }/*End of Constructor.*/

}/* End of MemoryBlock.*/


