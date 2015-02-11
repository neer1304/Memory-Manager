/* 
 * Heap.java (Implements Heaps for Memory Management System.).
 *
 * Written by : Nitin  Motgi (nmotgi@cs.ucf.edu)
 * 
 * This program manages a block of memory (heap) by using interfaces
 * like malloc(), free(), realloc() and compact(). malloc() is basically
 * used to allocate a chuck of memory from the heap. If no space is 
 * available a failure is reported with errorcode. free() is used to free
 * the chuck of memory that was allocated using malloc(). If there is no 
 * one-one correspondence between free() and malloc() free will result in 
 * error code being generated. realloc() reallocates chunk of memory with
 * some memory added or memory removed. compact() is used for "Garbage
 * Collection".
 *
 * Portions copyright(c) 2001 to School of Electrical Engineering and 
 * Computer Science, UCF, Orlando.                   
 *
 * Use and distribution of this source code are strictly governed by 
 * terms and conditions set by the authors.
 * 
 * $Id : Heap.java, v6.0.4 02/24/2001. $
 *            
 * Revision History:
 *
 * 1. Created basic structure           Nitin,        v1.0.0  02/24/2001.
 * 2. Added Documentation.              Nitin,        v1.0.1  02/24/2001.
 * 3. Added malloc()                    Nitin,        v2.0.1  02/24/2001.
 * 4. Added free()                      Nitin,        v3.0.1  02/25/2001.  
 * 5. Modified free() to merge adj.
 *    blocks if they are free.          Nitin,        v4.0.1  02/26/2001.  
 * 6. Added realloc()                   Nitin,        v5.0.1  02/27/2001.  
 * 7. Added compact()                   Nitin,        v6.0.1  03/01/2001.  
 * 8. Final Documentation Check.        Nitin,        v6.0.2  03/07/2001.  
 * 9. Final Variable Name Check.        Nitin,        v6.0.3  03/07/2001.          
 * 10.Final Functionality Check.        Nitin,        v6.0.4  03/07/2001.          
*/

/* Import some of the libraries.*/
import java.lang.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

/* Start of heap class.*/
public class Heap{
  private Vector MemoryList;             /* Linked-List of Used/UnUsed Mem.*/
  int            nNoOfMemBlocks;         /* # of such blocks.*/
  int            nMaxHeapSize;           /* Total Size of the heap.*/
  int            nCurrentAvailable;      /* Total Heap currently available.*/
  public  int    nErrorCode;             /* Global Error Code set by the
                                            application in case to trap
                                            erros outside class domain.*/

  /* Default Constructor.*/
  public Heap(int nMaxHeapSize){
   this.nMaxHeapSize = nMaxHeapSize;
   /* Create Vector.*/
   MemoryList = new Vector();

   /* Create a "BIG" Block of this size.*/
   MemoryBlock Block = new MemoryBlock(0,nMaxHeapSize,nMaxHeapSize);
   MemoryList.addElement((Object)Block); /* Free Block is added to the list.*/
   nCurrentAvailable = nMaxHeapSize;

   /* Reset Error Code status.*/
   nErrorCode = -1;
  }/* End of Constructor.*/

  /* This function allocates memory provided name and size for the block
     that has to be allocated. The allocation is based on "First Fit"
     algorithm. If allocation fails it will return "false" to the calling
     function and will set the nErroCode=10X based on reason for the 
     error. If it succeeds it will return true and set nErrorCode = -1.*/
  public boolean malloc(String szName,int nSize){
   int nBlockIndex = 0;
   MemoryBlock Block = new MemoryBlock();

   /* Check if there is any block with the same name allocated.*/
   if(_CheckReUse(szName) != -1){
    nErrorCode = 101;               /* Indicating : Memory for same name.*/
    return false;
   }/* End if.*/

   /* Find a Block which can fit the current request.*/
   if((nBlockIndex = _CheckFreeBlock(nSize)) == -1){
    /* NOTE: THIS FACILITY was part of Version 1.2.0, but from
             the release version this has been removed because
             of the fact that, when exlcusive "compact" command
             is given in the input. I personally feel that it
             is not necessary to compact when memory needs to
             be allocated.*/
    nErrorCode = 102;              /* Indicating : No Memory available.*/     
    return false;
   }/* End if.*/

   /* Get the block.*/
   Block = (MemoryBlock)MemoryList.elementAt(nBlockIndex);

   /* Store current Block size.*/
   int nTempBlockSize = Block.nBlockSize - nSize;
   int nTempEndAddr   = Block.nEndAddr;

   /* Adjust all the fields in the records.*/
   Block.bBlockStatus = true;
   Block.nEndAddr   = Block.nStartAddr + nSize;
   Block.nBlockSize = nSize;
   Block.szBlockName = szName;

   if(nTempBlockSize != 0){
    MemoryBlock NewBlock = new MemoryBlock(Block.nStartAddr + nSize,
                                           nTempEndAddr,
                                           nTempBlockSize);
    MemoryList.insertElementAt((Object)NewBlock,nBlockIndex+1);
   }/* End if.*/

   nCurrentAvailable = nCurrentAvailable - nSize;
   nErrorCode = -1;
   return true;
  }/* End of malloc.*/

  /* This function frees memory which is already allocated. It actual
     marks the block as free. If it cannot find the block with specified
     name it will return "false" and set nErrorCode=103. If found it will
     return "true" with nErrorCode set to -1.*/
  public boolean free(String szName){
   int nBlockIndex=0;
   int nSize = MemoryList.size();
   MemoryBlock Block = new MemoryBlock();

   /* Check if there is any block with the same name allocated.*/
   /* If allocated then this will return "false" it that case u can
      free this block of code.*/
   if((nBlockIndex=_CheckReUse(szName)) == -1){
    nErrorCode = 103;               /* Indicating : Memory for same name.*/
    return false;
   }/* End if.*/

   Block = (MemoryBlock)MemoryList.elementAt(nBlockIndex);
   Block.bBlockStatus = false;
   Block.szBlockName  = "free";

   _MergeBlocks(nBlockIndex,Block);

   nErrorCode = -1;
   return true;
  }/* End of free.*/

  /* This function reallocates a chunk of memory by first freeing the 
     current allocated memory and then by newly allocating for a new
     size. Will return "true" if sucessfully reallocated or else return
     false and set error code to = reason.*/
  public boolean realloc(String szName, int nSize){
   int nBlockIndex = 0;       
   
   /* First try freeing that Block. Once, if you can free that block
      go a head with reallocation.*/
   if(free(szName) == false){ 
    nErrorCode = 104;                 /* Indicating : Freeing failed in   
                                                      realloc.*/
    return false;
   }/* End if.*/

   /* Now, try allocating a new Block with the same name.*/
   if(malloc(szName,nSize) == false){
    nErrorCode = 105;                 /* Indicating : malloc failed in 
                                                      realloc.*/
    return false;
   }/* End if.*/
   nErrorCode = -1;
   return true;   
  }/* End of realloc.*/

  /* Check if the Block with the same name is present in the list.
     If it is present, then return index indicating "same name" block
     present in allocated list else return -1.
     This is "HELPER FUNCTION." */
  private int _CheckReUse(String szName){
   int nSize = MemoryList.size();               /* Get # of blocks.*/
   MemoryBlock Block = new MemoryBlock();

   /* Traverse through the list to find if any of them is already in 
      use.*/
   for(int nIndex=0; nIndex < nSize; nIndex++){
    Block = (MemoryBlock)MemoryList.elementAt(nIndex);    
    
    /* Check if it is in USE block.*/
    if(Block.bBlockStatus == true){
     if((Block.szBlockName).equals(szName) == true) return nIndex;
    }/* End if.*/
              
   }/* End for.*/
   return -1;
  }/* End of _CheckReUse.*/

  /* Check if there is any block which is free in the UNUSED List, if there
     is any return the index or else return -1 saying that such block is
     not available and recommends to use compact after this statement.
     
     This basically finds the first hole in the list big enough for the
     request. This is fast to do in an address sorted list, because it just
     traverses the lit untill it finds a large enough one.*/
  private int _CheckFreeBlock(int nBlockSize){
   int nSize = MemoryList.size();
   MemoryBlock Block = new MemoryBlock();

   for(int nIndex=0; nIndex < nSize; nIndex++){
    Block = (MemoryBlock)MemoryList.elementAt(nIndex);

    if(Block.bBlockStatus == false){
     if(Block.nBlockSize >= nBlockSize) return nIndex;
    }/* End if.*/
   }/* End for.*/
   return -1;
  }/* End of _CheckFreeBlock.*/

  /* This function returns current available memory.*/
  public int HeapAvailable(){
   return nCurrentAvailable;
  }/* End of HeapAvailable.*/

  /* This function updates the Graphics window. It takes handle to the
     frame graphics and uses that to write MemoryList on the screen with
     "red" for Used Block and "green" for unused block.*/
  public void UpdateGraphicsHeap(Graphics gHandle,int nX1,int nX2,
                                 int nY1,int nY2, int nHeapSize){

   int nSize = MemoryList.size();         /* Get the size of Memory Block.*/

   /* How much memory "per pixel" weighs find that.*/
   float nPerSize = (float)(nY2/(float)nHeapSize);
   float nStartPixel, nEndPixel;

   /* Start computing from the bottom.*/
   int nBottom = nY1+nY2;
   MemoryBlock Block = new MemoryBlock();

   /* Pass thru the memory list and print what ever is present.*/
   /* Absolutely no modifications done.*/
   for(int nIndex=0; nIndex < nSize; nIndex++){
    Block = (MemoryBlock)MemoryList.elementAt(nIndex);
    nStartPixel = (float)(Block.nBlockSize * nPerSize);
    nEndPixel   = (float)(Block.nEndAddr   * nPerSize);

    if(Block.bBlockStatus == true)
     gHandle.setColor(Color.red);
    else
     gHandle.setColor(Color.green);
    gHandle.fillRect(nX1,nBottom-(int)nEndPixel,nX2,
                    (int)nStartPixel);

    gHandle.setColor(Color.black);
    gHandle.drawRect(nX1,nBottom-(int)nEndPixel,nX2,(int)nStartPixel);
     
    if(Block.nEndAddr == (nHeapSize-1)) continue;
    // gHandle.setColor(Color.black);
    // gHandle.drawString((Block.nEndAddr-1)+"",nX2-3,nBottom-(int)nEndPixel+12);
   }/* End of for.*/
  }/* End of UpdateGraphicsHeap.*/

  /* Compact performs Garbage collection on the memory. It basically moves
     all the programs to lower memory addresses and free "empty" space to
     higher memory addresses.*/
  public void compact(){
   int nSize = MemoryList.size();
   String szBlockName[] = new String[nSize];
   int    nBlockSize[]  = new int[nSize];

   MemoryBlock Block = new MemoryBlock();
   int nIndex,nIndexF;

   nIndex=0;
   nIndexF=0;
   /* Collect all the allocated Blocks in the memory. I have used "TWO PASS"
      memory compaction to get better performance.*/
   while(nIndex < nSize){
    Block = (MemoryBlock)MemoryList.elementAt(nIndex);
    if(Block.bBlockStatus == true && nIndex != 0){
     szBlockName[nIndexF] = Block.szBlockName;
     nBlockSize[nIndexF] = Block.nBlockSize;
     nIndexF++;
    }/* End if.*/
    nIndex++;
   }/* End for.*/

   nIndex=0;  
   /* Now move all the collected used memory blocks to lower memory
      location.*/
   while(nIndex < nIndexF){
    free(szBlockName[nIndex]);
    malloc(szBlockName[nIndex],nBlockSize[nIndex]);
    nIndex++;
   }/* End of for.*/
  }/* End of compact.*/

  /* This is used to in free() to check and merge any adjacent blocks
     that are present. If there are any adjacent blocks present which are
     also free, then they are merged to form larger blocks.*/
  private void _MergeBlocks(int nBlockIndex,MemoryBlock Block){
   int nSize = MemoryList.size();     /* Get No of Vectors.*/

   MemoryBlock LeftBlock = new MemoryBlock();
   MemoryBlock RightBlock = new MemoryBlock();

   /* If it's the only block then don't delete it just 
      mark it empty.*/
   if(nBlockIndex == 0 && nSize == 1){
    Block.bBlockStatus = false;
    /* Update Current Memory Status.*/
    nCurrentAvailable = nCurrentAvailable + Block.nBlockSize;
    return;
   }/* End if.*/

   /* Check the adjacent Blocks if they are also free if free then for
      a large hole. This is for end condition when only right is present*/
   if(nBlockIndex == 0 && nSize > 1){
    /* There is no left block. work on right block.*/
    RightBlock = (MemoryBlock)MemoryList.elementAt(nBlockIndex+1);
    if(RightBlock.bBlockStatus == false){
     /* Mainpulate the address field.*/
     RightBlock.nStartAddr =  Block.nStartAddr;
     RightBlock.nBlockSize += Block.nBlockSize;
     MemoryList.removeElementAt(nBlockIndex);
    }/* End if.*/ 
   }else{
     /* Check when the block you have found is the end block and you want
        to merge it will the left adjacent block.*/
        if(nBlockIndex == nSize-1){
         LeftBlock = (MemoryBlock)MemoryList.elementAt(nBlockIndex-1);
         if(LeftBlock.bBlockStatus == false){
          /* Mainpulate the address field.*/
          LeftBlock.nEndAddr   =  Block.nEndAddr;
          LeftBlock.nBlockSize += Block.nBlockSize;
          MemoryList.removeElementAt(nBlockIndex);
         }/* End if.*/
        }else{
          /* When you have to merge the right and left block.*/
          RightBlock = (MemoryBlock)MemoryList.elementAt(nBlockIndex+1);
          LeftBlock  = (MemoryBlock)MemoryList.elementAt(nBlockIndex-1);
          if(LeftBlock.bBlockStatus  == false &&
             RightBlock.bBlockStatus == false){
             LeftBlock.nEndAddr = RightBlock.nEndAddr;
             LeftBlock.nBlockSize += (RightBlock.nBlockSize+Block.nBlockSize);
             MemoryList.removeElementAt(nBlockIndex+1);
             MemoryList.removeElementAt(nBlockIndex);
          }
          if(LeftBlock.bBlockStatus  == false &&
             RightBlock.bBlockStatus == true){
             /* Mainpulate the address field.*/
             LeftBlock.nEndAddr   =  Block.nEndAddr;
             LeftBlock.nBlockSize += Block.nBlockSize;
             MemoryList.removeElementAt(nBlockIndex);
          }/* End if.*/
          if(RightBlock.bBlockStatus  == false &&
             LeftBlock.bBlockStatus == true){
             /* Mainpulate the address field.*/
             RightBlock.nStartAddr   =  Block.nStartAddr;
             RightBlock.nBlockSize += Block.nBlockSize;
             MemoryList.removeElementAt(nBlockIndex);
          }/* End if.*/
        }/* End if.*/
   }/* End if.*/
   nCurrentAvailable = nCurrentAvailable + Block.nBlockSize;
  }/* End of MergeBlocks.*/
}/* End of Heap.*/

