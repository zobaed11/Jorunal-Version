#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sun Apr 28 00:34:31 2019

@author: c00300901
"""

import sys
import os 
import json

import sys
import codecs
from nltk.corpus import wordnet
from collections import defaultdict, Counter
import matplotlib.pyplot as plt
import os
import gensim
import enchant
import natsort
import shutil


def read(directory,file):
    n_open=open(directory+file,'r').readlines()
    return n_open

new_index_directory="/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/new_indexes/"
new_indexes=os.listdir(new_index_directory)


old_index_directory="/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/prev_indexes/"
old_indexes=os.listdir(old_index_directory)
old_index_word_list=[]
unique_new_words=[]
for each_index in old_indexes:
    opener=read(old_index_directory,each_index)
    for each_line in opener:
        existing_word= each_line[0:each_line.index('|')]
        old_index_word_list.append(existing_word)


   


new_new_words=[]
unique_new_words=[]
for each_index in new_indexes:
    opener=read(new_index_directory,each_index)
    for each_line in opener:
        existing_word= each_line[0:each_line.index('|')]
        new_new_words.append(existing_word)
        
#unique_new_words=set(new_new_words)-set(old_index_word_list) 


unmod_unique_new_words=set(new_new_words)-set(old_index_word_list)
d = enchant.Dict("en_US")
unique_new_words=[] #Comment the line if we do not use enchant
for each_uni in unmod_unique_new_words:
    if (" " in each_uni):
        if(d.check(each_uni[:each_uni.index(" ")])):
            unique_new_words.append(each_uni)
    else:
        if(d.check(each_uni)):
            unique_new_words.append(each_uni)


abs_directory="/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/abstracts/"
clus_directory="/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/clusters/"

prev_clus="/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/clusters/"

    

abs_files=natsort.natsorted(os.listdir(abs_directory), reverse=False)


abs_container=[]
for each_abs in abs_files:
    abs_opener=read(abs_directory, each_abs)
    one_ab=[]
    for each_line in abs_opener:
        
        if each_line[0:each_line.index('\n')].isdigit()==False:
            one_ab.append(each_line[0:each_line.index('\n')])
    abs_container.append(one_ab)
    
#model = gensim.models.KeyedVectors.load_word2vec_format(
#        '/home/c00300901/eclipse-workspace/S3BD-JasonThesis/cloud/cloudserver/GoogleNews-vectors-negative300.bin', 
#        binary=True)
threshold_read= open("/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/EncryptedSearchServer/threshold.txt", "r").read()
tr= float (threshold_read) 

#for storing updated clusters
new_clusters={}
#unique_new_words=["glue we eat"] #CHECK
for unique_new_word in unique_new_words:
#    print("index word:", unique_new_word)
    if(' ' in unique_new_word):
        only_part_unique_word=unique_new_word[:unique_new_word.index(' ')]
    else:
        only_part_unique_word=unique_new_word
    
    tracker_for_each=0
    all_abs_high_score_container=[]
    
    for each_abs in abs_container:
        each_abs_score=[]
#        print("EACH AB:", each_abs)
        for ab_word in each_abs:
            if(' 'in ab_word):
                
                try:    
                    lc=model.similarity(ab_word[ab_word.index(' ')+1:],only_part_unique_word)
                except:
                    lc=0
                try:
                    lc2=model.similarity(ab_word[:ab_word.index(' ')],only_part_unique_word)
                except:
                    lc2=0
                    
                    
                if (lc>lc2):
                    sc=lc
                else:
                    sc=lc2
                    
            else:
                try:
                    sc=model.similarity(ab_word,only_part_unique_word)
                except:
                    sc=0
            if  sc > tr: 

#                print(sc)
                each_abs_score.append(sc)
#                print("each", each_abs_score)
                tracker_for_each+=1
        try:
            each_ab_highest_score= max(each_abs_score)
#            print("each ab highest score:", each_ab_highest_score)
            
        except:
            each_ab_highest_score=0
        all_abs_high_score_container.append(each_ab_highest_score)  
            
    if tracker_for_each == 0:
        
#        print("Here:", unique_new_word)
        abs_container.append([unique_new_word])
#        print("Appending:", unique_new_word)
#        print("new cluster center") #CHECK CECH lkjvkljdjhghlkg
#        create abstract and cluster file 
#        print ("Current total abs: ",len(abs_container))
        d=open(abs_directory+'abstract_'+str(len(abs_container)-1)+ '.txt', 'w' )
        d.writelines(unique_new_word+"\n")
        d.close()
        
#        if(count == 1): loop baire initialize korte hobe
#            sys.exit()
#        count+=1
        
        c=open(clus_directory+'cluster_'+str(len(abs_container)-1)+ '.txt', 'w' )
        c.write(unique_new_word+"\n")
        c.close()
        
    else:
#        find the best cluster/abstract
        max_score_among_all_abs= max(all_abs_high_score_container)
        assigned_cluster= all_abs_high_score_container.index(max_score_among_all_abs)
        new_clusters[assigned_cluster]=unique_new_word
#        EVERYTHING WORKS. NOW SAVE IT IN CLUSTER. 
        
        if (assigned_cluster > len(abs_files)): #EXCEED CURRENT NUMBER OF CLUSTERS
            d=open(clus_directory+'cluster_'+str(assigned_cluster)+ '.txt', 'a' )
            d.write(unique_new_word+"\n")
            d.close()
        
        else:
            prev=open(prev_clus+'cluster_'+str(assigned_cluster)+ '.txt', 'a' )
            prev.write(unique_new_word+"\n")
            prev.close()            
        
        
#'''Need a counter for inserting in abstract. If abstract has more than 10. It will only add in cluster.     
        a=open(abs_directory+'abstract_'+str(assigned_cluster)+ '.txt', 'r' )
        count=len(a.readlines())
        a.close()
        if(count<11):        
            a=open(abs_directory+'abstract_'+str(assigned_cluster)+ '.txt', 'a' )        
            a.write(unique_new_word+"\n")
            a.close()
#            NO ERROR. WORKS Perfect        
        
        
            
            
       
            