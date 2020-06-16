/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientencryptedsearch.main;

import clientencryptedsearch.utilities.ClientMetrics;
import clientencryptedsearch.utilities.Config;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author jason
 */
public class ClientEncryptedSearch {

    /**
     * @param args search arguments.
     * If this has been called from outside with arguments, it won't ask for input.
     */
	//static long begin=0;
    public static void main(String[] args) {
        
        
        if (args.length == 0) {//No args
            //Then we should get args from the user
            args = getUserInput();
        }
        
        ClientEncryptedSearch esc = new ClientEncryptedSearch(args);
    }
    
    public ClientEncryptedSearch(String[] args) {
        //Load properties
        Config.loadProperties();
        
        //Determine what the user wants to do
        switch (args[0]) {
            case "-u":
                upload(args[1]);
                break;
            case "-s":
                //Begin timing for search
            	
                search(args[1]);
                
                
                break;
            case "-p":
                partition();
            case "-a":
                calcAbstracts(args[1]);
        }
    }

    private static String[] getUserInput() {
        String[] args = new String[2];
        System.out.println("Welcome to S3C: The Secure Semantic Search over Encrypted Data in the Cloud.");
        System.out.println("You are using the client version");
        System.out.print("What would you like to do?  Options: \n"
                + "\tUpload -u\n"
                + "\tSearch -s\n"
                + "\tPartition Index -p\n"
                + "\tCalc Abstracts -a\n"
                + "Choice: ");
        
        //Get input
        Scanner scan;
        scan = new Scanner(System.in);
        
        String choice = scan.nextLine();
        args[0] = choice;
        
        switch (choice) {
            case "-u": //Upload
                System.out.println("Batch Upload: Enter folder to be uploaded");
                args[1] = scan.nextLine();
                break;
            case "-s": //Search
                System.out.println("Enter search query: ");
                args[1] = scan.nextLine();
                //begin = System.currentTimeMillis();
                break;
            case "-p": //Partition
                System.out.println("The system will now attempt to partition your document collection index.");
                System.out.println("Please ensure that the server is also running this option.");
                args[1] = "";
                break;
            case "-a": //Calculate abstracts 
                System.out.println("The system will now calculate which abstracts would be searched with your inputted query.");
                System.out.println("Enter search query: ");
                args[1] = scan.nextLine();
            default:
                System.out.println("I'm sorry, I do not recognize that input");
                break;
        }
        
        return args;
    }
    
    public void upload(String inputFolder) {
        File folder = new File(inputFolder);
        
        //The folder must exist
        if (!folder.exists()) {
            System.err.println("Error: Could not find requested folder.\nFrom: Main\nFolder: " + inputFolder);
            System.exit(0);
        }
        
        //Create new uploader object to perform the upload
        Uploader up = new Uploader();
        //Perform upload with desired input folder.
        up.upload(inputFolder, Config.fileTransferType);
        
    }
    
    
    public void search(String query) {        
        // Start timing
        
        
        //Use the query to prepare the hashed query set to send to server
        ClientSearcher searcher = new ClientSearcher(query); //Constructor just initializes
        //Rank our abstracts based on the query and send it over.
        long consider1 = System.currentTimeMillis();
        
        searcher.rankAbstracts();
        searcher.sendAbstracts();
        
        long consider1end= System.currentTimeMillis();
        //Split and weight the query
        long consider1portion= consider1end-consider1;
        searcher.constructQuery();
        //Search!
        long begin = System.currentTimeMillis();
        searcher.search();
        
        searcher.acceptResults();
        searcher.processResults();
        
        long end = System.currentTimeMillis();
        long consider2portion= end-begin;
        System.out.println("Total Search time: "+ (consider1portion+consider2portion));
        System.out.println("Total Search time in cluster: "+ (consider2portion));
        int emni=0;
		if (Config.calcMetrics)
           // ClientMetrics.writeSearchTime(end-begin, query);
        	 emni = 6;
    }

    public void partition() {
        
        RetrievePartitions retriever = new RetrievePartitions();
        retriever.retrieve();
        retriever.writeAbstractsToFile();
                
    }

    private void calcAbstracts(String query) {
        //Use the query to prepare the hashed query set to send to server
        ClientSearcher searcher = new ClientSearcher(query); //Constructor just initializes
        //Rank our abstracts based on the query and send it over.
        searcher.rankAbstracts();
    }
}
