import wordcorrection.SymSpell;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.System.gc;


public class Main {


    public static void main(String[] args) {
        gc();
        SimilarityCaller sc = new SimilarityCaller();
        SymSpell spellfixer;
        String regexIndexDelimiter = " ";


        BufferedReader br = null, br2 = null;
        BufferedWriter bw = null;
        File directory = new File("/home/c00300901/Music/Extended verison Spring-19/threshold_estimation/cluster");



        ArrayList<Double> all_minimum = new ArrayList<Double>();

        try {

            int fileCount = directory.list().length;
            System.out.println("Total documents: "+fileCount);
          
            spellfixer = new SymSpell(Paths.get("big.txt"));
            for (int fi = 0; fi < fileCount; fi++) {
                br = new BufferedReader(new FileReader("/home/c00300901/Music/Extended verison Spring-19/threshold_estimation/cluster/cluster_" + Integer.toString(fi) + ".txt"));

                ArrayList<String> corrected_cluster = new ArrayList<String>();
                ArrayList<Double> clus_comps = new ArrayList<Double>();
                String currentLine;
                String cor;


                try {
                    //spellfixer = new SymSpell(Paths.get(ClassLoader.getSystemResource("big.txt").getPath()));

                    while ((currentLine = br.readLine()) != null) {
                        String[] lineTokens = new String[10];
                        lineTokens = currentLine.split(regexIndexDelimiter);
                        //String stemmed = Ste.stem(lineTokens[0]);

                        corrected_cluster.add(spellfixer.correct(lineTokens[0]));

                    }
                    double total_sim_in_a_cl = 0;
                    int valid_pair = 0;
                    String word1, word2;
                    for (int i = 0; i < corrected_cluster.size() - 1; i++) {//System.out.println(corrected_cluster.get(i));
                         word1 = corrected_cluster.get(i);

                        if (word1.length() > 2) { //DO NOT considering 2letter 1 letter words
                            // int cor_sim;
                            for (int j = i + 1; j < corrected_cluster.size(); j++) {
                                 word2 = corrected_cluster.get(j);
                                // System.out.println(word1 + ":" +word2);
                                if (word1 !=word2) {
                                    double comps = sc.computeWUP(word1, word2);
                                    if (comps > 0 && comps<=1) {
                                        total_sim_in_a_cl += comps; //two letter word, one letter word cause disturbance here.
                                        //System.out.println(word1 + " :: " + word2); //just checked every pair sample
                                        valid_pair++;
                                        clus_comps.add(comps);
                                        //System.out.println(comps + " " + word1 + word2);

                                    }
                                }


                            }
                        }
                    }
                    double smallest=clus_comps.get(0);
                    for (double x : clus_comps)

                    {
                        if (x<smallest)
                            smallest=x;
                    }
                    all_minimum.add(smallest);
                    System.out.println(smallest);

                    //System.out.println(Collections.min(clus_comps));



/*                   double avg; avg = total_sim_in_a_cl / valid_pair;
                    avg = Math.round(avg * 1000d) / 1000d;
                    System.out.println(avg);
                    double variance = 0, sd = 0;
                    bw = new BufferedWriter(new FileWriter("store.txt"));
                    for (int i = 0; i < clus_comps.size(); i++) {
                        bw.write(String.valueOf(clus_comps.get(i)));
                        bw.write("\n");

                        variance += Math.pow((clus_comps.get(i) - avg), 2);
                    }
                    bw.close();

                    sd = (Math.sqrt(variance / valid_pair));
                    double threshold;
                    threshold = avg - sd;
                    System.out.println(sd);*/


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

          //  for (int i=0;i<all_minimum.size();i++) //check all_minimum list
               // System.out.println(all_minimum.get(i));
            
            Double value= Collections.min(all_minimum);
        System.out.println("New Estimated Threshold:"+ value);
        bw = new BufferedWriter (new FileWriter ("/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/EncryptedSearchServer/threshold.txt"));
    	bw.write(value.toString());
    	bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
