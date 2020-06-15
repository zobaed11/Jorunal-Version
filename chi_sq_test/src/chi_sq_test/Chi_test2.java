package chi_sq_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.stat.inference.ChiSquareTest;


public class Chi_test2
{

    
    public static void main(String[] args) throws IOException
    {
    	File file = new File ("/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/EncryptedSearchServer/current_cumulative_upload_number.txt");
    	BufferedReader br = new BufferedReader (new FileReader (file));
    	double observe, expected, ava,chi;
    	String ob;
    	ob=br.readLine();
    	observe=Double.parseDouble(ob);
    	//System.out.println(observe);
    	br.close();
    	file= new File ("/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/EncryptedSearchServer/total_in_index.txt");
    	br = new BufferedReader (new FileReader (file));
    	expected=Double.parseDouble(br.readLine());
    	BufferedWriter bw = new BufferedWriter (new FileWriter(
    			"/home/c00300901/eclipse-workspace/S3BD_semi_Dynamic/EncryptedSearchServer/breaking decision.txt"));
    	if(expected<observe)
    	{
    		bw.write("break");
    		System.out.println("break");
    		bw.close();
    		System.exit(0);
    	}
    	//System.out.println(expected);
    	ava = (observe+expected)/2;
    	chi= (Math.pow(observe-ava, 2)/ ava )+ (Math.pow(expected-ava, 2)/ava);
    	System.out.println(chi);
    	
    	
    	if (chi > 3.85) {
    		bw.write("ok");
    		System.out.println("ok");}
    	else {bw.write("break");
    		System.out.println("break");
    		}
    	bw.close();
    }
}
