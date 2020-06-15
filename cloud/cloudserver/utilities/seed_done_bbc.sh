#CHANGE IN SEED NUMBER (one time)
rm /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/clusters/*
cp /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/kept_clusters/bbc/* /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/clusters
rm /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/abstracts/*
cp /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/kept_abstracts/bbc/* /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/abstracts

mv /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/new_indexes/* /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/prev_indexes/

mv /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/prev_indexes/* /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/index_keeper/bbc/seed/prev_indexes/

mv /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/index_keeper/bbc/Index_500.txt /home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/cloud/cloudserver/utilities/prev_indexes/


rm /home/c00300901/eclipse-workspace/S3BD-JasonThesis/cloud/cloudserver/watch/*
