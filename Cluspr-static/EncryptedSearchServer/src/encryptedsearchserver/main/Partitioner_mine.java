/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptedsearchserver.main;

import encryptedsearchserver.main.Partitioner_mine.ClusterDistancePair;
import encryptedsearchserver.utilities.Config;
import encryptedsearchserver.utilities.Constants;
import encryptedsearchserver.utilities.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import Jama.Matrix;

/**
 * Partition the overall inverted index into smaller clusters based on topic.
 * 
 * Performs K-Means clustering on the overall inverted index to create K smaller
 * clusters that can be searched over in parallel.  The clusters will be organized
 * by topic as opposed to just randomly.
 * Clustering happens by defining a distance function between two terms, and 
 * defining an averaging function on a cluster.
 * The distance function will be a function of the similarity in files between
 * two words.
 * @author Jason
 */
class Partitioner_mine {
    private Index index;
    private int k = 0;
    
    //Centroids - the averages for each cluster
    ArrayList<Centroid> centroids;
    
    //Clusters
    ArrayList<Cluster> clusters;
    //A cluster for all of the rare terms with no cooccurrences
    Cluster rarityCluster;
    ArrayList<String> PotCenters= new ArrayList<>();
    
    //As many of these are there are clusters will be created.
    private ArrayList<ArrayList<String>> abstractIndices;
    
    // A list of threads.  K will be created
    private ArrayList<Thread> threads;
    
    // Distances from terms to centroids
    HashMap<String, HashMap<Centroid, Float>> distances;
    
    // Small pair class for clusters and distances, used for sorting clusters later.
    class ClusterDistancePair {
        public Cluster cluster;
        public float distance;
    }
    
    public Partitioner_mine(Index index) {
        this.index = index;
        centroids = new ArrayList<>();
        clusters = new ArrayList<>();
        abstractIndices = new ArrayList<>();
        threads = new ArrayList<>();
        rarityCluster = new Cluster();
    }
    
    /**
     * Set the number of clusters to be created.
     * Also sets up the arrays of clusters and centroids.
     * @param k 
     */
    public void setNumberOfClusters(int k) {
    		this.k = k;
    		//int k = index.getNumberOfCluster();
    		//k=10;
    		
        centroids.ensureCapacity(k);
        clusters.ensureCapacity(k);
        for (int i = 0; i < k; i++) {
            Centroid cent = new Centroid();
            Cluster clus = new Cluster();
            clus.centroid = cent;
            centroids.add(cent);
            clusters.add(clus);
        }
        
        // Set up abstracts
        abstractIndices.ensureCapacity(k);
        for (int i = 0; i < k; i ++) {
            abstractIndices.add(new ArrayList<>());
        }
        
        // Set up threads
        threads.ensureCapacity(k);
    }
    
    public void partition() {
        //First just make sure we actually have a k
        if (k == 0)
            return;
        
        //Randomly pick k centroids
        //initializeMoreDiverseCentroids();
        
        //initializeDiverseCentroids();
         myCenterSelection();
        //TODO: Do the averages and do this a few times over
        //Compute distance for each term against each centroid
        for (int i = 0; i < Config.numClusterRepititions; i++) {
            //Clear the clusters
            for (Cluster cluster : clusters) {
                cluster.subIndex.clear();
            }
            
            computeDistances();
            threads.clear();
          //  computeAverages();
           // threads.clear();
            
            System.out.println("\n\n\n\n------------------------------------\n\n\n\n");
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    
    // ----------- CLUSTERS STUFF ---------------
    
    //Randomly pick k centroids from the original postingList
    /**
     * Initialize Centroids.
     * Does initial setup of centroids.  These will be what the first cluster form
     * around.
     * Current method of picking entries is just random, since there is nothing
     * else to go off of.
     * Precondition: The posting index is full; has > k entries.
     * Post-Condition: The first centroids will be set and the index entries
     * will be ready to have distances calculated.
     */

    private void initializeDiverseCentroids() {
        // Pick the top diverse centroids by going through the list and dumping
        // them into a max heap, that is always limited to a size of k.
/*        
        PriorityQueue<String> topCentroids = new PriorityQueue<String>(10, new Comparator<String>() {
            public int compare (String T1, String T2) {
                if (index.postingList.getOrDefault(T1, new HashMap<>()).values().size() < index.postingList.getOrDefault(T2, new HashMap<>()).values().size()) return -1;
                if (index.postingList.getOrDefault(T1, new HashMap<>()).values().size() == index.postingList.getOrDefault(T2, new HashMap<>()).values().size()) return 0;
                return 1;
            }
        }); */
        
    	
    	// Reading C matri
    	
        
        PriorityQueue<String> topCentroids = new PriorityQueue<String>(10, new Comparator<String>() { //initail capcacity. not harmful whether it is 10 or other
            public int compare (String T1, String T2) {
                if (index.postingList.getOrDefault(T1, new HashMap<>()).values().size() < index.postingList.getOrDefault(T2, new HashMap<>()).values().size()) return -1;
                if (index.postingList.getOrDefault(T1, new HashMap<>()).values().size() == index.postingList.getOrDefault(T2, new HashMap<>()).values().size()) return 0;
                return 1;
            }
        }); 
        
        // Now go through the whole list and dump them in
        for (String term : index.postingList.keySet()) {
            topCentroids.add(term);
            if (topCentroids.size() > k)
                topCentroids.poll();
        }
        
        // Now pop them off and put them in the actual centroids list
        for (int i = 0; i < k; i++) {
            String term = topCentroids.poll();
            centroids.get(i).term = term;
            centroids.get(i).files = index.postingList.get(term);
        }
        
        
      
    }
    
    
    
    private void initializeMoreDiverseCentroids() {
        System.out.println("\n\nStarting to initialize centroids.");
        // We still pick the top centroids but no longer limit it by k.
        
        int maxHeapSize = Math.min(k * 3000, index.postingList.size());
        System.out.println("Posting list size:  "+index.postingList.size());
        
        PriorityQueue<String> unsortedCentroids = new PriorityQueue<String>(maxHeapSize, new Comparator<String>() {
            public int compare (String T1, String T2) {
                if (index.postingList.getOrDefault(T1, new HashMap<>()).values().size() < index.postingList.getOrDefault(T2, new HashMap<>()).values().size()) return -1;
                if (index.postingList.getOrDefault(T1, new HashMap<>()).values().size() == index.postingList.getOrDefault(T2, new HashMap<>()).values().size()) return 0;
                return 1;
            }
        });
        
        // Now go through the whole list and dump them in
        for (String term : index.postingList.keySet()) {
            unsortedCentroids.add(term);
            if (unsortedCentroids.size() > maxHeapSize) {
                unsortedCentroids.poll();
                //System.out.println("This Big: " + unsortedCentroids.size());
            }
        }
        
        System.out.println("This Big: " + unsortedCentroids.size());
        
        // Now we need to sort them from max num Files to min num Files
        ArrayList<String> topCentroids = new ArrayList<String>();
        
        for (int i = 0; i < maxHeapSize; i++) {
            topCentroids.add(unsortedCentroids.poll());
        }
        
        
        System.out.println("This Big: " + topCentroids.size());
        
        
        // We keep a list of the files that we've seen, so we can compare against them.
        HashSet<String> seenFiles = new HashSet<>();
        
        // Go through potential centroids, picking those that have less than 50% in common with the
        // centroids above them.
        int i = 0;
        int numTossed = 0;
        do {
        	System.out.print("1st time"+ topCentroids.size());
        	
            String term = topCentroids.remove(topCentroids.size() - 1);
            HashMap<String, Integer> files = index.postingList.get(term);
            
            if ("warc".equals(term.toLowerCase()) || "conversion".equals(term.toLowerCase()))
                continue;
            
            // Check this against files in the seenFiles list
            int sameFileCount = 0;
            for (String file : files.keySet()) {
                if (seenFiles.contains(file)) sameFileCount++;
            }
            
            // If they share more than half of the same files, toss it.
            System.out.println(term + " shares: " + sameFileCount + " out of " + files.keySet().size() + " of their files");
            if (((float)sameFileCount / (float)files.keySet().size()) > 0.6) {
                System.out.println("Tossing out " + term + " for being too similar.");
                numTossed++;
                continue;
            }
            
            // If it's gotten to here, then we can accept it and put it in the centroids list.
            centroids.get(i).term = term;
            centroids.get(i).files = files;
            
            // And we should also put all of those files into the seenFiles
            seenFiles.addAll(files.keySet());
            
            i++;
            if(topCentroids.size()==0) break;
        } while (i < k);
        System.out.println("Tossed out " + numTossed + " potential centroids total.");
    }
    
    
    // MY METHOD
    
    
    private void myCenterSelection() {
    	//read Matrix
    	Matrix C= null;
     try{
    	 C = Matrix.read(new BufferedReader(new FileReader("/home/c00300901/eclipse-workspace/S3BD-JasonThesis/cloud/cloudserver/utilities/awsCmat.txt")));
    	 //C.print(2, 2);
     }
        catch (IOException e) {
            e.printStackTrace();
        }
    	
    	
     	
        System.out.println("\n\nStarting to initialize centroids.");
        // We still pick the top centroids but no longer limit it by k.
        
        int maxHeapSize = index.postingList.size();
        System.out.println(maxHeapSize);
       
        System.out.println("Posting list size:  "+index.postingList.size());
        
        PriorityQueue<String> unsortedCentroids = new PriorityQueue<String>(maxHeapSize, new Comparator<String>() {
            public int compare (String T1, String T2) {
                if (index.postingList.getOrDefault(T1, new HashMap<>()).values().size() < index.postingList.getOrDefault(T2, new HashMap<>()).values().size()) return -1;
                if (index.postingList.getOrDefault(T1, new HashMap<>()).values().size() == index.postingList.getOrDefault(T2, new HashMap<>()).values().size()) return 0;
                return 1;
            }
        });
/*        Iterator<String> through = unsortedCentroids.iterator() ;
        while(through.hasNext() ) {
                System.out.print(through.next() + " ") ;
        }
        System.exit(0);
        */
        
        // Now go through the whole list and dump them in
        for (String term : index.postingList.keySet()) {
            unsortedCentroids.add(term);
            if (unsortedCentroids.size() > maxHeapSize) {
                unsortedCentroids.poll(); //nothing to be pulled
                //System.out.println("This Big: " + unsortedCentroids.size());
            }
        }
        System.out.println(unsortedCentroids.peek());
        System.out.println("This Big: " + unsortedCentroids.size());
        ;
        // Now we need to sort them from max num Files to min num Files
        ArrayList<String> topCentroids = new ArrayList<String>();
        
        for (int i = 0; i < maxHeapSize; i++)   {    topCentroids.add(unsortedCentroids.poll());  
        }
        
        java.util.Collections.reverse(topCentroids);
/*        for (int i =0; i<topCentroids.size();i++)
        	{System.out.println(topCentroids.get(i));
        	}*/
        System.out.println(topCentroids.get(0));
        
        System.out.println("This Big: " + topCentroids.size());
        
        
        // We keep a list of the files that we've seen, so we can compare against them.
        HashSet<String> seenFiles = new HashSet<>();
        
        // Go through potential centroids, picking those that have less than 50% in common with the
        // centroids above them.
        
        //Seed Power List
       
        HashMap<String, Double> seedPower = new HashMap<String, Double>();
        //Read topic array
        BufferedReader br4= null;
        String currentLine=null;
        String candidate=null;
        ArrayList<String> cInput=new ArrayList<String>();
        try {
        	br4 = new BufferedReader(new FileReader(new File("/home/c00300901/eclipse-workspace/S3BD-JasonThesis/cloud/cloudserver/utilities/awsTopics.txt")));
        	

        		
            while ((currentLine = br4.readLine()) != null) {
                // System.out.println(currentLine);
            	cInput.add(currentLine);
            	//System.out.println(currentLine);

            }
            br4.close();
          // System.out.println("dggf" +cInput.size());
           
	        for (int i=0;i<cInput.size();i++)   //instead of topCentroids
	        {
	        	 candidate= topCentroids.get(i); //instead of topCentroids
	        	HashMap<String, Integer> files = index.postingList.get(candidate);
	            int sameFileCount = 0;
	            for (String file : files.keySet()) {
	                if (seenFiles.contains(file)) sameFileCount++;
	                
	            }
	            
	           
	            double fileContribution= (double)(files.keySet().size()-sameFileCount) / (double)files.keySet().size();
	            double totalContribution=0;
	            
	            //System.out.println(candidate);
	           // System.out.println(files);

            	//System.out.println(fileContribution + candidate);

         
	            if (candidate.equals("type")) {
	            	System.out.println(candidate + files.keySet().size() + "\t"+ sameFileCount);
	            }
	            
	            if (candidate .equals("content")) {
	            	System.out.println(candidate + files.keySet().size() + "\t"+ sameFileCount);
	            //	int cEntry=	cInput.indexOf("content");
	            	//System.out.println(cEntry+ "\n"+"\n" + C.get(0,0));
	            	//System.exit(0);
	            }
	            

	            if (fileContribution > 0.5 && cInput.contains(candidate)) {
	            	
	            	//System.out.println(fileContribution+ "MTT");
	            	
		            
	            	int cEntry=	cInput.indexOf(candidate);
	            	//System.out.println(cEntry + candidate);
	            	//System.exit(0);
	            	//System.out.println("mam");
	            	//System.out.println(C.get(312, 312));
	            	totalContribution=(double) (fileContribution* C.get(cEntry,cEntry) * (1-C.get(cEntry,cEntry)) );
	            	totalContribution= fileContribution ;
	            	seenFiles.addAll(files.keySet());
	            	//System.out.println(totalContribution + candidate);
	            	//System.exit(0);
	            	seedPower.put(candidate, new Double(totalContribution));
	            	
	            }
	            
	            else {
	            	
	            	totalContribution=(double) (0); //make it way more SMALLER. O/w it will be like fileCOnt * 1 . Super Big -_- 
	            }
	            	
	            
	            
	           // System.out.println(candidate + totalContribution);
	           
	        }
	        System.out.println(seedPower);
	        
	        
        }catch (IOException e) {
        	
        }
        
        //Sort 
        List<Map.Entry<String, Double> > list = 
                new LinkedList<Map.Entry<String, Double> >(seedPower.entrySet());
        
        java.util.Collections.sort(list, new Comparator<Map.Entry<String, Double> >() { 
            public int compare(Map.Entry<String, Double> o1,  
                               Map.Entry<String, Double> o2) 
            { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }); 
        
        //store after ascendingly sorted
        HashMap<String, Double> temp = new LinkedHashMap<String, Double>(); 
        
        int uptoK=0;
        for (Map.Entry<String, Double> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        	//System.out.println(aa.getKey() + aa.getValue());
        	PotCenters.add(aa.getKey());
/*        	if (uptoK > 305)
        	{
        		break;
        	}
        	uptoK++;*/
        } 
        
        System.out.println(PotCenters);
        
        java.util.Collections.reverse(PotCenters);
        System.out.println(PotCenters);
        
        System.out.println("gdfgd"+PotCenters.size());
    	
       
        
        for (int h=0; h<PotCenters.size();h++)
        {
        	
        	if (temp.get(PotCenters.get(h)) <.001)
        	{	
        		//System.out.println("Problem::" + h);
        		//System.out.println(PotCenters.get(h));
        		
        	}
        }
        
        //System.exit(0);
        System.out.println("PotCenters:" + PotCenters.size());
        System.out.println("Centroids: " + centroids.size());
        
        
        for (int j=0;j<centroids.size();j++)
        {
        	centroids.get(j).term= PotCenters.get(j);
        	centroids.get(j).files= index.postingList.get(PotCenters.get(j));
        			
        	//System.out.println(PotCenters.get(j));
        	
        	
        }
        
       // System.exit(0);
        
        

    }
    
    
    
    
    
    
    
  
    
    
    
    
    
    
    
    
    
    //Computes distances from each term to each centroid, then add each term to the cluster it's closest to
    /**
     * Compute Distances.
     * Uses our term to centroid distance function to measure distances from the terms
     * to the centroids.
     * Our distance function uses the co-occurence of the words in files.
     * We compute the distance from each term to every centroid (n*k work) in order
     * to find the closest cluster.
     * 
     * Pre-Conditions: Centroids have been set up (either initialized or averaged)
     * and the posting list has stuff in it.
     * Post-Conditions: All terms are assigned to a cluster.  Or more accurately, 
     * all clusters are filled with terms (since they are just a copy).
     */
    private void computeDistances() {
        //Maps a term to its distance from a centroid
        distances = new HashMap<>();
        
        // To avoid sync later, run through all terms and initialize the hash maps.
        for (String term : index.postingList.keySet()) {
            distances.put(term, new HashMap<>());
        }
        
        //System.out.println("Distances:" + distances.keySet());
        //System.exit(0);
        
        int timesClusterOverlapped = 0;
        
        System.out.println("Spawning threads...");
        for (Centroid cent : centroids) {
        	
        		Thread t = new Thread(new PartitionThread(index, cent, distances));
        		t.start();
        		threads.add(t);
        	
        }
        
        // Now that all threads have started, we need to wait for them all to finish.
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Partitioner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        

        
        System.out.println("\nPutting terms in proper clusters...");
        

       float maxTerms = ((float)Config.clusterRatio / k) * index.postingList.size();
       maxTerms=600;

        
        //Go through each term and find the clusters whose centroid it's closest to
        for (String term : distances.keySet()) {
            

            
            
            PriorityQueue<ClusterDistancePair> clusterHeap = new PriorityQueue(k, new Comparator<ClusterDistancePair>() {
                public int compare (ClusterDistancePair c1, ClusterDistancePair c2) {
                    if (c1.distance > c2.distance) return 1;
                    if (c1.distance == c2.distance) return 0;
                    return -1;
                } 
             });
            
            // Build the min heap by checking the distance of each cluster
            for (Cluster cluster : clusters) {
                // Get distance and associate it with the cluster in a pair
                float distanceToCluster = distances.get(term).getOrDefault(cluster.centroid, Float.POSITIVE_INFINITY);
                ClusterDistancePair cdp = new ClusterDistancePair();
                cdp.cluster = cluster; cdp.distance = distanceToCluster;
                
                clusterHeap.add(cdp);
            }
                    
            // Find the closest cluster and put the term in there
            for (int i = 0; i < k; i++) {
                Cluster closestCluster = clusterHeap.poll().cluster;
                // Make sure this cluster can still be added to.
                if (closestCluster.subIndex.keySet().size() < maxTerms) {
                    closestCluster.subIndex.put(term, index.postingList.get(term));
                    break;
                }
            }
                    
 
        }
        
        // Now give each of those clusters a name based on their  centroid
        for (Cluster cluster : clusters) {
            if (cluster.name == null) {
                cluster.name = cluster.centroid.term;
            }
        }

        
        
    }

    /**
     * Creates a thread for each cluster to find its average distance and thus
     * average term.
     */
    private void computeAverages() {
        System.out.println("Computing Averages...");
        for (Cluster cluster : clusters) {
            Thread t = new Thread(new AveragerThread(cluster, distances));
            t.start();
            threads.add(t);
        }
        
        // Now that all threads have started, we need to wait for them all to finish.
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Partitioner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // Put those new centroids in the clusters.
        centroids.clear();
        for (Cluster cluster : clusters) {
            centroids.add(cluster.centroid);
        }
        
        
        if (!Config.suppressText) {
            for (Cluster cluster : clusters) {
                System.out.println("Cluster " + cluster.name + " now has centroid " + cluster.centroid.term);
            }
        }
    }
    
    /**
     * Compute the distance between the given centroid and term.
     * Uses co-occurrence of files as a measure of distance.
     * @param centroid
     * @param term
     * @return 
     */
    private Float computeDistance(Centroid centroid, String term) {
        // Get all the files to go through ( f in set I[T] )
        HashMap<String, Integer> files = index.postingList.get(term);
        //We also need the total sum of times this term has appeared in any file
        int termCount = 0; //This is |I[T]|
        for (String file : files.keySet()) {
            termCount += files.get(file);
        }
        
        //We also need the total sum of times the centroid has appeared in a file
        //NOTE: Not a true measure of how many times the centroid term has appeared, as it may not be an actual value from the postingList
        int centroidCount = 0; //This is |I[Ci]|
        for (String file : centroid.files.keySet()) {
            centroidCount += centroid.files.get(file);
        }
        
        float distance = 0;
        
        for (String file : files.keySet()) {
            float ratioOfFileForTerm; // c(f, I[T]) / |I[T]|
            ratioOfFileForTerm = (float)files.get(file) / (float)termCount; //Gets the count of the term in this file divided by the total number of times that term appeared.
            
            float ratioOfFileForBoth;
            ratioOfFileForBoth = ((float)files.get(file) + (float)centroid.files.getOrDefault(file, 0)) / ((float)termCount + (float)centroidCount);
            
            double logPart = Math.log(ratioOfFileForTerm / ratioOfFileForBoth);
            
            if (!Config.suppressText)
                System.out.println("Centroid: " + centroid.term + ", Term: " + term + ", Ratio: " + ratioOfFileForTerm + ", Log Part: " + ratioOfFileForTerm + " / " + ratioOfFileForBoth);
            
            distance += ratioOfFileForTerm * logPart;
        }
        
        if (!Config.suppressText)
            System.out.println("Centroid: " + centroid.term + ", Term: " + term + ", Distance: " + distance);
        
        return distance;
    }
    
    
    
    //Print out the cluster and their members
    private void printClusters() {
        System.out.println("\nPrinting Clusters...\n");
        
        int count = 0;
        for (Cluster cluster : clusters) {
            System.out.println("Cluster " + count++ + ": ");
            System.out.println("Centroid: " + cluster.centroid);
            System.out.println("Sub Index: " + cluster.subIndex);
            System.out.println();
        }
        
        System.out.println("Rarity Cluster: ");
        System.out.println("Centroid: " + rarityCluster.centroid);
        System.out.println("Sub Index: " + rarityCluster.subIndex);
    }

    
    // -----------  ABSTRACT INDICES STUFF -------------
    
    
    //Take the first n values from each of the clusters and assign it to its respective 
    /**
     * Create Abstract Indices.
     * Samples a very small portion of each cluster and creates an abstract index
     * to represent it.
     * These abstract indices are then small enough to send over to the client.
     * 
     * Pre-Conditions: Clusters have been set and fully created.
     * Post-Conditions: An abstract index is created for each cluster.  The clusters
     * and abstracts are only linked by their positions in their respective lists.
     * This may require a TODO: keep the abstract as a part of the cluster object.
     * Possible alternatives: 
     */
    public void createAbstractIndices() {
        for (int clusterCount = 0; clusterCount < clusters.size(); clusterCount++) {
            Cluster cluster = clusters.get(clusterCount);
            
            //Go through the terms in the cluster, adding them til we hit the limit
            int termsAdded = 0;
            for (String term : cluster.subIndex.keySet()) {
                if (termsAdded > Config.abstractIndexCount)
                    break;
                abstractIndices.get(clusterCount).add(term);
                termsAdded++;
            }
        }
    }
    
    /**
     * Does the same as the function above, but it reads in clusters from the index.
     */
    public void createAbstractIndicesFromIndex() {
        //Set<Cluster> indexClusters = (Set)index.clusters.values();
        
        abstractIndices.ensureCapacity(k);
        System.out.println("absSize: "+abstractIndices.size());
        for (int i = 0; i < k; i ++) {
            abstractIndices.add(new ArrayList<>());
        }
        
        
        
        for (Cluster cluster : index.clusters.values()) {
//        for (int clusterCount = 0; clusterCount < clusters.size(); clusterCount++) {
//            Cluster cluster = indexClusters.get(clusterCount);
            
            if (cluster.name == "rare")
                break;
            
            System.out.println("Mama"+cluster.name);
            
            //Go through the terms in the cluster, adding them til we hit the limit
            int termsAdded = 0;
            for (String term : cluster.subIndex.keySet()) {
                if (termsAdded > Config.abstractIndexCount)
                    break;
                
                abstractIndices.get(Integer.parseInt(cluster.name)).add(term);
                termsAdded++;
            }
        }
        System.out.println(Config.k);
    }
    
    private void printAbstractIndices() {
        System.out.println("\nPrinting Abstract Indices: ");
        
        for (int i = 0; i < abstractIndices.size(); i++) {
            System.out.println("Abstract index for cluster " + i + ": ");
            System.out.print("Terms: ");
            for (int j = 0; j < abstractIndices.get(i).size(); j++) {
                System.out.print(abstractIndices.get(i).get(j) + ", ");
            }
            System.out.println();
        }
    }
    
    
    // ----------- SENDING TO CLIENT STUFF ----------------
    
    
    //Open up a server socket, send the indices to the client.
    /*
    Data sent in the following fashion:
    1. Number of clusters
    2. Loop for each abstract index
        a. Send the number of terms to be sent
        b. Loop for each term in the abstracted index
            1) Send term
    */
    public void sendAbstractIndicesToClient() {
        ServerSocket serv;
        Socket sock = null;
        DataOutputStream dos = null;
        
        try {
            serv = new ServerSocket(Config.socketPort);
            System.out.println("Now listening on port " + Config.socketPort + ".  Please connect client...");
            sock = serv.accept();
            System.out.println("Accepted connection to: " + sock);
            
            dos = new DataOutputStream(sock.getOutputStream());            
            // Need to tell them how many clusters there were so they can prepare to accept
            dos.writeInt(abstractIndices.size());
            
            //Loop for each abstract index
            for (int i = 0; i < abstractIndices.size(); i++) {
                //First we should tell them the name of this index (link to cluster)
                //For now the name is just its position in the list
                dos.writeUTF(new Integer(i).toString());
                
                ArrayList<String> terms = abstractIndices.get(i);
                //Need to tell them how many terms in this list 
                dos.writeInt(terms.size());
                
                //Loop for each term in this list and send it
                for (String term : terms)
                    dos.writeUTF(term);
            }
        } catch (IOException ex) {
            Logger.getLogger(Partitioner.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("All abstract information sent to the client!");
    }
    
    
    // -------------- WRITING TO FILE STUFF ----------
    /**
     * Goes through each cluster and tells it to write its sub index to a file.
     * The clusters are only named based on their number.
     * This is the only link they have to their respective abstracts, so it is 
     * integral to maintain.
     */
    public void writeClustersToFile() {
        System.out.println("Starting to write the clusters to file storage...");
        
        wipeCurrentClusterFiles();
        
        try {
            for (int i = 0; i < clusters.size(); i++) {
        	//for (int i = 0; i < PotCenters.size(); i++) {
                System.out.println("Writing cluster " + i + " to a file...");
                
                clusters.get(i).writeClusterToFile(Integer.toString(i));
            }
            
            //Now write the rare cluster
            System.out.println("Writing rarity cluster to a file...");
            rarityCluster.writeClusterToFile("rare");
        } catch(IOException ex) {
            System.err.println(this.getClass().getName() + ": Error writing cluster to file.");
        }
        
        System.out.println("Clusters successfully written to files!");
    }
    
    /**
     * Deletes all of the cluster files currently on the file system.
     */
    private void wipeCurrentClusterFiles() {
        List<String> files = Util.getAbsoluteFilePathsFromFolder(Constants.clusterLocation);
        if (files.size() > 0) {
            System.out.println("Now deleting all previous on file cluster information...");
            for (String fileName : files) {
                File file = new File(Constants.clusterLocation + File.separator + fileName);
                file.delete();
            }
        }
    }
}
