import numpy as np
import os
from shutil import copy
import natsort
import sys
import pandas as pd



'''
Graph for breaking condition
'''

a=pd.read_csv("result", sep="\n", header=None)
b=pd.DataFrame(columns=a.columns,index=range(50))
i=0
for each in range(len(a)):    
    if(each % 2 !=0):
        b.iat[i,0]=a.iloc[each][0]
        i+=1
        
tot_25=[]
tot_50=[]
tot_100=[]
tot_20=[]
tot_200=[]
for i in range(len(  b)):
    if(i % 5)==0:
        tot_25.append(float (b.iloc[i][0]))

    if(i % 5)==1:
        tot_50.append(float (b.iloc[i][0]))

    if(i % 5)==2:
        tot_100.append(float (b.iloc[i][0]))

    if(i % 5)==3:
        tot_20.append(float (b.iloc[i][0]))

    if(i % 5)==4:
        tot_200.append(float (b.iloc[i][0]))




df_25=pd.DataFrame(data=tot_25)      
df_50=pd.DataFrame(data=tot_50)
df_100=pd.DataFrame(data=tot_100)
df_20=pd.DataFrame(data=tot_20)
df_200=pd.DataFrame(data=tot_200)  

value_25 = df_25.mean()
std_25 = df_25.std()
value_50 = df_50.mean()
std_50 = df_50.std()
value_100 = df_100.mean()
std_100 = df_100.std()
value_20 = df_20.mean()
std_20 = df_20.std()
value_200 = df_200.mean()
std_200 = df_200.std()




import matplotlib.pyplot as plt
import numpy as np
import sys


fig = plt.figure(figsize=(12.6,6))
ax  = fig.add_subplot(111)
ax.set_position([0.15,0.15,0.6,0.83])
ind=np.arange(0,6.25,1.25)

avg_bar2  = [0.16346, .16432, .161344, .16721, .16834]
df_avg_bar1=pd.DataFrame(data=avg_bar2)
value_avg_bar1 = df_avg_bar1.mean()
std_avg_bar1 = df_avg_bar1.std()
  
propose_bar1= (tot_25, tot_50,tot_100,tot_20,tot_200 ) 

avg_bar3  = [0.14753, .149032, .144234, .141324, .142123]
df_avg_bar2=pd.DataFrame(data=avg_bar3)
value_avg_bar2 = df_avg_bar2.mean()
std_avg_bar2 = df_avg_bar2.std()

ax.bar(ind[0] + 0.05,0.0, color='pink', hatch="////")









rects2 = ax.bar(ind[0], value_25, 0.15, bottom=0, yerr=std_25, align='center', alpha=.8, color='white',edgecolor="black",hatch= "\\\\\\",label= "Baseline",capsize=5 )   
rects2 = ax.bar(ind[0] + 0.25, value_25, 0.15,bottom=0, yerr=std_25, align='center', alpha=.8, color='pink',edgecolor="black",hatch= "////",label= "SDCS",capsize=5 ) 


rects2 = ax.bar(ind[1], value_50, 0.15, bottom=0, yerr=std_50, align='center', alpha=.8, color='white',edgecolor="black",hatch= "\\\\\\",capsize=5)
rects2 = ax.bar(ind[1] + 0.25, value_50, 0.15, bottom=0, yerr=std_50, align='center', alpha=.8, color='pink',edgecolor="black",hatch= "////",capsize=5)

rects2 = ax.bar(ind[2], value_100, 0.15, bottom=0, yerr=std_100, align='center', alpha=.8, color='white',edgecolor="black",hatch= "\\\\\\",capsize=5)
rects2 = ax.bar(ind[2] + 0.25, value_avg_bar1, 0.15, bottom=0, yerr=std_avg_bar1, align='center', alpha=.8, color='pink',edgecolor="black",hatch= "////",capsize=5)

rects2 = ax.bar(ind[3], value_20, 0.15, bottom=0, yerr=std_20, align='center', alpha=.8, color='white',edgecolor="black",hatch= "\\\\\\",capsize=5) 
rects2 = ax.bar(ind[3]+.25, value_20, 0.15, bottom=0, yerr=std_20, align='center', alpha=.8, color='pink',edgecolor="black",hatch= "////",capsize=5) 


rects2 = ax.bar(ind[4], value_200, 0.15, bottom=0, yerr=std_200, align='center', alpha=.8, color='white',edgecolor="black",hatch= "\\\\\\",capsize=5)  
rects2 = ax.bar(ind[4] + 0.250, value_avg_bar2, 0.15, bottom=0, yerr=std_avg_bar2, align='center', alpha=.8, color='pink',edgecolor="black",hatch= "////",capsize=5)



plt.ylabel('Coherence', fontsize=26)
for label in ( ax.get_yticklabels()):
    label.set_fontname('Arial')
    label.set_fontsize(20)
plt.xticks(ind+.13, ('Update1', 'Update2', 'Update3', 'Update4', "Update5"), fontsize=20)
#plt.legend(bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)

ax.legend(bbox_to_anchor=(.65, 1.), loc=2, borderaxespad=0., fontsize=18)


fig.savefig('/home/c00300901/Dropbox/semantic search-sailish/publication/new_Journal-Zobaed/Figure/semiDynamic_coherence_rfc_ci.pdf')
plt.show()