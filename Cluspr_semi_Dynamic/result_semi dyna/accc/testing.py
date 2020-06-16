#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu May  7 18:30:07 2020

@author: zobaed
"""


import os
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np






fig = plt.figure(figsize=(8,4.5))
ax  = fig.add_subplot(111)
ax.set_position([0.15,0.15,0.6,0.83])
ind = np.arange(3)

propose_bar1 = (.141,.167,.128)
avg_bar2 = (0.0641,0.077,0.056)

ax.bar(ind[0] + 0.05,0.0, color='pink', label='SCS',hatch="\\\\")
ax.bar(ind[2] + 0.60,0.0, color='white',edgecolor="black", label='Traditional', hatch="...")
                
                 
                
                
rects2 = ax.bar(ind[0] + 0.15, propose_bar1[0], 0.15, color='pink',edgecolor="black",hatch= "\\\\" )    
rects2 = ax.bar(ind[1] + 0.15, propose_bar1[1], 0.15, color='pink',edgecolor="black",hatch= "\\\\")
rects2 = ax.bar(ind[2] + 0.15, propose_bar1[2], 0.15, color='pink',edgecolor="black",hatch= "\\\\") 
            
rects2 = ax.bar(ind + 0.30, avg_bar2, 0.15, color='white',edgecolor="black",hatch= "...")

               #1 BBC   2RFC 3 AMAZON  








#trend_line = plt.plot(high_point_x,high_point_y,marker='o', color='#5b74a8', label='Trend Line')

#plt.xlabel('Propose vs. Encrypted')
plt.ylabel('Coherence', fontsize=22)
for label in ( ax.get_yticklabels()):
    label.set_fontname('Arial')
    label.set_fontsize(14)
plt.xticks(ind+0.23, ('BBC', 'RFC', 'ACCC'), fontsize=20)
#plt.legend(bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)

ax.legend(bbox_to_anchor=(.6, .97), loc=2, borderaxespad=0., fontsize=14)


#fig.savefig('/home/c00300901/Dropbox/semantic search-sailish/publication/new_Journal-Zobaed/Figure/Pro_en.pdf')
plt.show()