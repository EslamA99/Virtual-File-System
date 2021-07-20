package com.company;

import java.io.Serializable;

public class FileD implements Serializable {
      String filePath ;
      int[] allocatedBlocks;
      boolean deleted ;

     public FileD(String filePath, int[] allocatedBlocks, boolean deleted) {
          this.filePath = filePath;
          this.allocatedBlocks = allocatedBlocks;
          this.deleted = deleted;
     }

     public String getFilePath() {
          return filePath;
     }

     public void setFilePath(String filePath) {
          this.filePath = filePath;
     }

     public int[] getAllocatedBlocks() {
          return allocatedBlocks;
     }

     public void setAllocatedBlocks(int[] allocatedBlocks) {
          this.allocatedBlocks = allocatedBlocks;
     }

     public boolean isDeleted() {
          return deleted;
     }

     public void setDeleted(boolean deleted) {
          this.deleted = deleted;
     }
}
