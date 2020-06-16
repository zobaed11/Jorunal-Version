import numpy as np
import os
from shutil import copy
import natsort
import sys
import pandas as pd



'''
Graph for breaking condition
'''
a=pd.read_csv("breaking_result", sep="\n", header=None)

avg=0.0
for each in range((len(a))):
    avg+=a.iloc[each][0]            

avg=avg/10

















#sys.exit()

'''
DELETED seed0, seed1 manually from result file
'''

'''
a=pd.read_csv("result", sep="\n", header=None)
b=pd.DataFrame(columns=a.columns,index=range(50))
i=0
for each in range(len(a)):    
    if(each % 2 !=0):
        b.iat[i,0]=a.iloc[each][0]
        i+=1
        
tot_25=0.0
tot_50=0.0
tot_100=0.0
tot_20=0.0
tot_200=0.0
for i in range(len(  b)):
    if(i % 5)==0:
        tot_25+=float (b.iloc[i][0])

    if(i % 5)==1:
        tot_50+=float (b.iloc[i][0])

    if(i % 5)==2:
        tot_100+=float (b.iloc[i][0])

    if(i % 5)==3:
        tot_20+=float (b.iloc[i][0])

    if(i % 5)==4:
        tot_200+=float (b.iloc[i][0])




tot_25=tot_25/10
tot_50=tot_50/10
tot_100=tot_100/10
tot_20=tot_20/10
tot_200=tot_200/10        
'''



import matplotlib.pyplot as plt
import numpy as np
import sys


fig = plt.figure(figsize=(12.6,6))
ax  = fig.add_subplot(111)
ax.set_position([0.15,0.15,0.6,0.83])
ind = np.arange(5)

avg_bar2  = (avg, .15253)  #enchant
propose_bar1= (tot_25, tot_50,tot_100,tot_20,tot_200 ) #No enchant



ax.bar(ind[0] + 0.05,0.0, color='pink', hatch="\\\\")
#ax.bar(ind[2] + 0.60,0.0, color='white',edgecolor="black", label='No Enchant', hatch="...")
                
                 
                
                
rects2 = ax.bar(ind[0], propose_bar1[0], 0.15, color='white',edgecolor="black",hatch= "\\\\",label= "Baseline" )   
rects2 = ax.bar(ind[0] + 0.25, propose_bar1[0], 0.15, color='pink',edgecolor="black",hatch= "\\\\",label= "SDCS" ) 


rects2 = ax.bar(ind[1], propose_bar1[1], 0.15, color='white',edgecolor="black",hatch= "\\\\")
rects2 = ax.bar(ind[1] + 0.25, propose_bar1[1], 0.15, color='pink',edgecolor="black",hatch= "\\\\")

rects2 = ax.bar(ind[2], propose_bar1[2], 0.15, color='white',edgecolor="black",hatch= "\\\\")
rects2 = ax.bar(ind[2] + 0.25, avg_bar2 [0], 0.15, color='pink',edgecolor="black",hatch= "\\\\")

rects2 = ax.bar(ind[3], propose_bar1[3], 0.15, color='white',edgecolor="black",hatch= "\\\\") 
rects2 = ax.bar(ind[3]+.25, propose_bar1[3], 0.15, color='pink',edgecolor="black",hatch= "\\\\") 


rects2 = ax.bar(ind[4], propose_bar1[4], 0.15, color='white',edgecolor="black",hatch= "\\\\")  
rects2 = ax.bar(ind[4] + 0.250, avg_bar2[1], 0.15, color='pink',edgecolor="black",hatch= "\\\\")


               #1 BBC   2RFC 3 AMAZON  








#trend_line = plt.plot(high_point_x,high_point_y,marker='o', color='#5b74a8', label='Trend Line')

#plt.xlabel('Propose vs. Encrypted')
plt.ylabel('Coherence', fontsize=22)
for label in ( ax.get_yticklabels()):
    label.set_fontname('Arial')
    label.set_fontsize(14)
plt.xticks(ind+.13, ('Update1', 'Update2', 'Update3', 'Update4', "Update5"), fontsize=16)
#plt.legend(bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)

ax.legend(bbox_to_anchor=(.65, .98), loc=2, borderaxespad=0., fontsize=14)


#fig.savefig('/home/c00300901/Dropbox/semantic search-sailish/publication/new_Journal-Zobaed/Figure/semiDynamic_coherence_bbc.pdf')
plt.show()