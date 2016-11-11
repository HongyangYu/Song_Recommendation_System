package musicRecomSys;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Compute {
	
	//product
	public static Double[] scalarVecProd(Double w, Double[] a) {
		Double[] b = new Double[a.length];
		for(int f=0; f<a.length; f++) 
			b[f] = w*a[f];
		return b;
	}
	
	//product
	public static double vecVecProd(Double[] a, Double[] b) {
		double prod = 0.0;
		for(int i=0; i<a.length; i++) 
			prod = prod+a[i]*b[i];
		return prod;
	}	
	
	//sum
	public static Double[] vecVecSum(Double[] a, Double[] b) {
		Double[] a_sum_b = new Double[a.length];
		for(int f=0; f<a.length; f++) 
			a_sum_b[f] = b[f] + a[f];
		return a_sum_b;
	}
	
	//minus
	public static Double[] vecVecMinus(Double[] a, Double[] b) {
		Double[] a_minus_b = new Double[a.length];
		for(int i=0; i<a.length; i++) 
			a_minus_b[i] = b[i] - a[i];
		return a_minus_b;
	}
	
	//mean
	public static double computeMean(Map<String,Map<String,Double>> r) {
		double sum = 0;
		int count = 0;
		for(String u : r.keySet()) {
			for(String i : r.get(u).keySet()) {
				sum += r.get(u).get(i);
				count++;
			}
		}
		return sum/count;
	}
	
	public static Set<String> getMusicSet(Map<String,Map<String,Double>> map) {
		Set<String> musicSet = new HashSet<String>();
		
		for(String usr : map.keySet()) {
			for(String mus : map.get(usr).keySet()) {
				musicSet.add(mus);
			}
		}
		return musicSet;
	}	

	public static Set<String> getUserSet(Map<String,Map<String,Double>> map) {
		return map.keySet();
	}	
	
	
	//because we only recommend high rate, we only care about music whose real rate=4~5. 
	public static double recDev(Recommendation rec, Map<String,Map<String,Double>> r, Map<String,Map<String,Double>> test) {
		System.out.println("\nComputing Deviation...");
		double devSum = 0, recDev=0, meanErr=0;
		int count = 0;
		final String fileEvaluation = "src/musicrecommendersystemtest/data/Evaluation.data";
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(fileEvaluation));
			for(String usr : test.keySet()) {// usr is userID
                            
				for(String mus : test.get(usr).keySet()) {//mus is musicID
					double real_rate = test.get(usr).get(mus); //real_rate is the real rate
					double pred_rate = rec.pred_rate(usr, mus); //pred_rate is the predicting rate
                                       
                                        
                                        
                                            
					bw.write(usr+"\t"+mus+"\t"+real_rate+"\t"+pred_rate+"\t"+(real_rate-pred_rate));
					bw.newLine();
					if(real_rate>3){
						recDev+= (real_rate - pred_rate)*(real_rate - pred_rate);
						meanErr+= Math.abs( real_rate - pred_rate ); //@@
					}
					devSum += (real_rate - pred_rate)*(real_rate - pred_rate);
					count++;	
				}
			}		
			bw.close();
                        
                        //Now************** here i will write the output of prediction to the database as well
                        
		} 
		catch (IOException e) {
			System.err.println(fileEvaluation+" Write Error!");
			e.printStackTrace();
		}
		
		System.out.println("Mean Error: "+(meanErr/count));
		
		System.out.println("Total deviation: "+Math.sqrt(devSum/count));
		
		return Math.sqrt(recDev/count);
	}
	
	//read data from base.data and test.data
	public static Map<String,Map<String,Double>> readData(String filename) throws Exception {
		Map<String,Map<String,Double>> r = new HashMap<String,Map<String,Double>>(); //user, <item, rating>
		
		System.out.println("Reading file " + filename + " ...");
		try{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ( (line = br.readLine()) != null )  {
				String[] array = line.split("\t");//@@
				if (array.length == 1){
					array = line.split(" ");
					if (array.length == 1)
						continue;
				}
                                //Read a line till now
				String user = array[0];
				String music = array[1];
				Double rating = Double.parseDouble(array[2]);
				//Get the 3 things, then add to map But: 
				if( !r.containsKey(user) )  //If user not already in system
					r.put(user, new HashMap<String,Double>());
				
				r.get(user).put(music,rating);
			}
			
		} catch (IOException e) {
			System.err.println(filename+" Read Error!");
			e.printStackTrace();
		}
		return r;
	}
	
}
