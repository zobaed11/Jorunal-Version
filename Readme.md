# Running Instructions for Static part (A modification on S3BD search system)

The Secure Semantic Search over Encrypted Big Data in the Cloud (S3BD) is a simple to use semantic search system.  S3BD is composed of two projects, one for the client and cloud.  These two can be run on the same machine, or on two machines connected by a network.  There are three primary actions you can perform when running the system: Uploading documents, Partitioning the dataset into clusters, and Searching over the dataset.

## Uploading Documents

To start an upload, launch the cloud server and select upload as its action (the -u flag), and choose how they'll be uploaded based on your project configuration (-f for cloud and client being on the same machine, -n for them being remote). Alternatively, launch the project from the command line with the two flags following as arguments.

Once the server is running, launch the client and select upload as its action (again, the -u flag).  To perform a batch upload, enter the path for the directory with the files.  Alternatively, run it in the commandline with -u and the path following as arguments.  Single file uploads are not currently supported.

## Partitioning Dataset

To start partitioning the dataset, launch the cloud server and select partition as its action (the -p flag).  The process will start automatically.  The console will display the progress of the partitioning.  Once the console notes that it is ready to connect to the client, launch the client, select partitioning as its action, and the files will be transferred.

## Searching

To start a search, launch the cloud server and select search as its action (the -s flag).  The cloud will begin loading relevant data.

Once the server is ready to accept a search, launch the client and select search as its action, then enter the desired search query.  The client's console should soon show the results of the search.

# Running Instructions for Semi dynamic part
Exceute the py file and check the folder path where the clusters will be created. 
semi_dynamic_cluster_update.py
 
