package musicRecomSys;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class Recommendation {

	// user_item_rating_map
	Map<String, Map<String, Double>> usr_mus_rate;
	Set<String> users, music;

	Double mean; // mean

	Map<String, Double> base_user = new HashMap<String, Double>();
	Map<String, Double> base_music = new HashMap<String, Double>();

	int factor = 20; // number of factors

	Map<String, Double[]> predict_user = new HashMap<String, Double[]>();
	Map<String, Double[]> predict_music = new HashMap<String, Double[]>();

	Double gamma = 0.005;

	Double lambda_bu = 0.02; // base_user
	Double lambda_bm = 0.02; // base_music
	Double lambda_pu = 0.05; // predict_user
	Double lambda_pm = 0.05; // predict_music

	final int IterNum = 30; // iteration times

	public void startRecommend(Map<String, Map<String, Double>> rate, String userID) throws ClassNotFoundException, SQLException {
		this.usr_mus_rate = rate;
		initialize();
		this.mean = Compute.computeMean(rate);
		computeBuBmPuPm();
		pred_output(userID);  //ADD THE USERID HERE
	}

	void initialize() {
		this.music = Compute.getMusicSet(usr_mus_rate);
		this.users = Compute.getUserSet(usr_mus_rate);

		for (String usr : users) {
			base_user.put(usr, 0.0);
		}

		for (String mus : music) {
			base_music.put(mus, 0.0);
		}

		for (String usr : users) {
			Double[] vec = new Double[factor];
			for (int i = 0; i < getFactor(); i++)
				vec[i] = Math.random() / 100;
			predict_user.put(usr, vec);
		}

		for (String mus : music) {
			Double[] vec = new Double[factor];
			for (int i = 0; i < getFactor(); i++)
				vec[i] = Math.random() / 100;
			predict_music.put(mus, vec);
		}
	}


	//This is to predict rating according to latent factor model. The result is stored in predict_user and predict_music.
	//In predict_user and predict_music we will predict all songs for all users no matter whether the user have rated the song.
	void computeBuBmPuPm() {
		for (int index = 0; index < this.IterNum; index++) { //iteration times
			for (String usr : usr_mus_rate.keySet()) { // each user
				for (String mus : usr_mus_rate.get(usr).keySet()) { // each music for the user
					Double e_um = error(usr, mus);
					Double b_u = base_user.get(usr);// user
					Double b_m = base_music.get(mus);// item
					Double[] p_u = predict_user.get(usr);// user value
					Double[] p_m = predict_music.get(mus);// item value

					base_user.put(usr, b_u + gamma * (e_um - lambda_bu * b_u));
					base_music.put(mus, b_m + gamma * (e_um - lambda_bm * b_m));

					//@@ math formula according to the
					Double[] pu1 = Compute.scalarVecProd(-lambda_pu, p_u);
					Double[] pm1 = Compute.scalarVecProd(e_um, p_m);
					Double[] sum_pu1_pm1 = Compute.vecVecSum(pu1,pm1);
					Double[] pm2 = Compute.scalarVecProd(gamma, sum_pu1_pm1);
					Double[] sum_pu_pm2 = Compute.vecVecSum(p_u, pm2);
					predict_user.put(usr, sum_pu_pm2);
					pm1 = Compute.scalarVecProd(-lambda_pm, p_m);
					pu1 = Compute.scalarVecProd(e_um, p_u);
					sum_pu1_pm1 = Compute.vecVecSum(pu1,pm1);
					pm2 = Compute.scalarVecProd(gamma, sum_pu1_pm1);
					sum_pu_pm2 = Compute.vecVecSum(p_m, pm2);
					predict_music.put(mus, sum_pu_pm2);
				}
			}
		}
	}

	//@@ new: to output predict rating.
        //Here i am making a change to calculate the userID omly for the particular UserID
	public void pred_output(String UserID) throws ClassNotFoundException, SQLException{
		final String filePredict = "src/musicrecommendersystemtest/data/Output/PredictRating.data";
		final String filePredictTop3 = "src/musicrecommendersystemtest/data/Output/PredictRatingTop3.data";
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(filePredict));
                        BufferedWriter bwnew=new BufferedWriter(new FileWriter(filePredictTop3));
                        //********************Remeber commentin g this to check for particular USerid
//			for(String usr : predict_user.keySet()) 
                        //Wherever i see USERID below is because i replaced the previous usr variable
                        {// usr is userID
                            double Top3[]={0,0,0};
                            String Top3songs[]={null,null,null};
				for(String mus : predict_music.keySet()) {//mus is musicID
                                        
					double pred_rate = pred_rate(UserID, mus); //pred_rate is the predicting rate
                                        
                                        
                                        //*************to get top 3
                                         double temp=pred_rate;
                                            if(temp>Top3[0]){
                                            double tempswap1=Top3[0];
                                            String TopMusic1=Top3songs[0];
                                            Top3[0]=temp;
                                            Top3songs[0]=mus;
                                            
                                            if(tempswap1>Top3[1]){
                                                double tempswap2=Top3[1];
                                                String TopMusic2=Top3songs[1];
                                                Top3[1]=tempswap1;
                                                Top3songs[1]=TopMusic1;
                                                
                                                if (tempswap2 > Top3[2]) {
                                                    double tempswap3 = Top3[2];
                                                    Top3[2] = tempswap2;
                                                    Top3songs[2]=TopMusic2;
                                                }
                                                
                                            }
                                                
                                        }
                                        else if(temp>Top3[1]&&temp<Top3[0]&&temp>Top3[2]){
                                            double tempswap=Top3[1];
                                            String Topmusic2=Top3songs[1];
                                            Top3[1]=temp;
                                            Top3songs[1]=mus;
                                            
                                            Top3[2]=tempswap;
                                            Top3songs[2]=Topmusic2;
                                        }
                                        else if(temp<Top3[1]&&temp>Top3[2])
                                            Top3[2]=temp;
                                            Top3songs[2]=mus;
                                        //******************************* Noted the top 3
					bw.write(UserID+"\t"+mus+"\t"+pred_rate);
					bw.newLine();
	//				System.out.println(usr+"\t"+mus+"\t"+pred_rate);
				}
                                
                                
                                for(int i=0;i<3;i++){
                                    int countOfsuchSong=0;
                                    bwnew.write(UserID+"\t"+Top3songs[i]+"\t"+Top3[i]);
                                    System.out.println(UserID+"\t"+Top3songs[i]+"\t"+Top3[i]);
                                    bwnew.newLine();
                                    
                                    //Select count(SONGID) FROM BASE.DATA WHERE USERID=UserID AND SONGID=Top3songs[i]
                                    Connection conToCheckDetails = connectionToDatabase.getConnection();
                                    Statement m_Statement = conToCheckDetails.createStatement();
                                    Statement Insert_Statement = conToCheckDetails.createStatement();
                                    String query = "SELECT count([SongID]) FROM [BaseData] WHERE UserID="+UserID+"AND SongID="+Top3songs[i];
                                    ResultSet m_ResultSet = m_Statement.executeQuery(query);
                                    while (m_ResultSet.next()) {
                                        countOfsuchSong = Integer.parseInt(m_ResultSet.getString(1));
                                    }
                                    if(countOfsuchSong==0)
                                    {
                                        String Insert_query = "INSERT INTO BaseData VALUES ("+UserID+","+Top3songs[i]+","+Top3[i]+","+0+","+1+")";
                                        Insert_Statement.executeUpdate(Insert_query);
                                        System.out.println("Inserted into base data, for UserID: "+ UserID+" Value : "+Top3songs[i] );
                                    }
                                }
                                
                                
                                DisplayRecoFrame DispRecos_frame=new DisplayRecoFrame();
                                DispRecos_frame.setVisible(true);
                                DispRecos_frame.main1(Top3songs);
                                    
			}
                        bwnew.close();
			bw.close();
                        
		}
		catch (IOException e) {
			System.err.println(filePredict+" Write Error!");
			e.printStackTrace();
		}
	}

	// predict rating
	public Double pred_rate(String usr, String mus) {
		Double base_usr = 0.0, base_mus = 0.0, pu_pm = 0.0;
		if (base_user.containsKey(usr))
			base_usr = base_user.get(usr);
		if (base_music.containsKey(mus))
			base_mus = base_music.get(mus);
		if (predict_user.containsKey(usr) && predict_music.containsKey(mus))
			pu_pm = Compute.vecVecProd(predict_user.get(usr), predict_music.get(mus));

		return mean + base_usr + base_mus + pu_pm;
	}

	Double error(String usr, String mus) {
		return usr_mus_rate.get(usr).get(mus) - pred_rate(usr, mus);
	}

	int getFactor() {
		return factor;
	}

	void setFactor(int f) {
		factor = f;
	}

	Double getLambda_bu() {
		return lambda_bu;
	}

	void setLambda_bu(Double lambda_bu) {
		this.lambda_bu = lambda_bu;
	}

	Double getLambda_bm() {
		return lambda_bm;
	}

	void setLambda_bm(Double lambda_bi) {
		this.lambda_bm = lambda_bi;
	}

	Double getLambda_pu() {
		return lambda_pu;
	}

	void setLambda_pu(Double lambda_pu) {
		this.lambda_pu = lambda_pu;
	}

	Double getLambda_pm() {
		return lambda_pm;
	}

	void setLambda_pm(Double lambda_qi) {
		this.lambda_pm = lambda_qi;
	}

}
