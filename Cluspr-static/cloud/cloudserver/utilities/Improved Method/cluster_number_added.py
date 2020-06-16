import sys
import os
import re
directory= "/home/c00300901/eclipse-workspace/S3BD-JasonThesis/cloud/cloudserver/utilities/Improved Method/BBC/"

out="/home/c00300901/eclipse-workspace/S3BD-JasonThesis/cloud/cloudserver/utilities/Improved Method/BBC_with_number/"

FileList = os.listdir(directory)

#####################################
###########File Creation ############
####################################

for file in FileList:
    
    with open(directory+file,'r') as f:
        big=""
        for line in f:
            
            big=big+line
    with open(out+file,'w') as w:
        number = re.findall("\d+", file)[0]
        
        w.write(number+"\n")
        w.write(str(big))
        
  