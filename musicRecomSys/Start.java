package musicRecomSys;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This algorithm can predict ratings. There are two input files, "base.data"
 * and "test.data". There are 5000 items in each file, and the first line is
 * userID, the second line is musicID, the third line is real rate, and the
 * fourth line is time stamp. (split by "\t") The algorithm will pick
 * "base.data" to calculate predicting rate in test data, and test.data is to
 * calculate the evaluation.
 *
 * There is one output file, the first line is userID, the second line is
 * musicID, the third line is real rate reading by test.data, the fourth line is
 * predicting rate, and the fifth line is (real rate-predicting rate). (split by
 * "\t")
 *
 * In addition, recommend deviation and mean error are the evaluation indicator,
 * which will be printed in the last line. This means our predicting ratings
 * only deviate a little statistically.
 *
 * I think we can recommend the music for each user by the maximum of predicting
 * rate grouped by userID. And we can also create a flag to mark whether users
 * have heard the music. We can recommend new songs to users by certain
 * probability. For example, the system will recommend songs that the user
 * haven't heard with 50% probability, and recommend songs that users have heard
 * with 50% probability. Or we can just recommend new songs. I think just to
 * recommend new songs will be easier.
 */
public class Start {

    public static void main1(String UserID) throws Exception {
        String fileData = "src/musicrecommendersystemtest/data/base.data";
        String fileTest = "src/musicrecommendersystemtest/data/test.data";
        //to read from database source
        Map<String, Map<String, Double>> newbase=new HashMap<String,Map<String,Double>>(); //Put everything here from the database
        //Map<String, Double> newbase_submap=new HashMap<String,Double>();
        
        Connection conToCheckDetails = connectionToDatabase.getConnection();
        Statement m_Statement = conToCheckDetails.createStatement();
        String query = "SELECT [UserID],[SongID],[Rating] FROM [MRS_test1].[dbo].[BaseData] WHERE FLAG=0";
        ResultSet m_ResultSet = m_Statement.executeQuery(query);
        while (m_ResultSet.next()) {
            String user1=m_ResultSet.getString(1);//contains the userID
            String music1=m_ResultSet.getString(2);//Contains the MusicID
            Double rating1=m_ResultSet.getDouble(3);
            if( !newbase.containsKey(user1) )  //If user not already in system
		newbase.put(user1, new HashMap<String,Double>());
            newbase.get(user1).put(music1,rating1);
//            newbase_submap.put(m_ResultSet.getString(2),m_ResultSet.getDouble(3));
//            newbase.put(m_ResultSet.getString(1),newbase_submap);
        }
        System.out.println("");
        //Map<String, Map<String, Double>> base = Compute.readData(fileData);//replaces this with newbase below

        Recommendation recommend = new Recommendation();

        System.out.println("Computing and recommending...");
        recommend.startRecommend(newbase, UserID);      //See here i have added the USERID as well
        System.out.println("Recommendation finished.\n");

        Map<String, Map<String, Double>> test = Compute.readData(fileTest);

        System.out.println("Recommend Deviation = " + Compute.recDev(recommend, newbase, test));
    }

}

class Thread1 extends Thread{
    String jLabel_1=null;
    Thread1(String labelname){
        jLabel_1=labelname;
        
    }
    public void run(){
        try {
            Connection conToCheckDetails = connectionToDatabase.getConnection();
            Statement m_Statement = conToCheckDetails.createStatement();
            
            String query = "SELECT [Rock],[HipHop],[EDM],[House],[Pop],[Latin] FROM [SongMatrix] where songID= '" + jLabel_1 + "'";
            
            ResultSet m_ResultSet = m_Statement.executeQuery(query);//Now i have all the data here regarding the bit value for these columns
            ResultSetMetaData rsmd = m_ResultSet.getMetaData();
            ArrayList<String> GenreArrayList=new ArrayList<String>();//To store the column names
            int numberOfColumns = rsmd.getColumnCount(); 
            Map Genre;
            Genre = new HashMap<String,Integer>();
            
            
            while(m_ResultSet.next()){
                for(int i=1; i<=numberOfColumns; i++){
                    Genre.put(rsmd.getColumnName(i),m_ResultSet.getInt(i));
                    if(m_ResultSet.getInt(i)==1)//To check if that column has a 1
                        GenreArrayList.add(rsmd.getColumnName(i));
                }
                
            }
            System.out.println("Genre Array List size where 1's are there : " + GenreArrayList.size());
            if(GenreArrayList.size()!=0){
                String querysubpart=" where ";
                for(int i=0; i<GenreArrayList.size(); i++){//Now i want to create a subquery based on the column in this arraylist
                    if(i!=(GenreArrayList.size()-1))
                        querysubpart=querysubpart.concat(GenreArrayList.get(i) + " = 1 AND ");
                    else
                        querysubpart=querysubpart.concat(GenreArrayList.get(i) + " = 1 ");
                }
                
                String q_GetSongsSameGenre="SELECT [SongID] FROM [SongMatrix]"+querysubpart;
                System.out.println(q_GetSongsSameGenre);
                Statement s_getsongs = conToCheckDetails.createStatement();
                ResultSet rs_Getsongs = s_getsongs.executeQuery(q_GetSongsSameGenre);
                
                ArrayList<Integer> songIDs=new ArrayList<>();
                while(rs_Getsongs.next()){
                    songIDs.add(rs_Getsongs.getInt(1));
                }
                MusicRecommenderSystemTest mrs=new MusicRecommenderSystemTest();
                int FillUserID=mrs.getUserIDint();
                //There could be apossible error cause in db userID is stored as int
                
                String subqueryforSongIds="AND SongID IN(";
                for(int k=0;k<songIDs.size();k++){
                    if(k<20){
                        if(k!=songIDs.size()-1&&k!=19)
                            subqueryforSongIds=subqueryforSongIds.concat(""+songIDs.get(k) + ", ");
                        else
                            subqueryforSongIds=subqueryforSongIds.concat(""+songIDs.get(k) + " )");
                    }
                }
                
                String q_UpdateSameGenreSongs="update [BaseData] SET Rating=Rating+10.5 WHERE UserID="+FillUserID+" "+subqueryforSongIds;
                System.out.println("Query to update Songs with same genres: "+q_UpdateSameGenreSongs);
                
                System.out.println("Song list that will be updated: (if they are in user data)");
                //for(int j=0;j<songIDs.size();j++)
                //  System.out.println(songIDs.get(j));
                
                //Now to generate the statemnet that will execute this query
                Statement s_UpdateStatement = conToCheckDetails.createStatement();
                s_UpdateStatement.executeUpdate(q_UpdateSameGenreSongs);
                
            }
            else{
                System.out.println("No songs to update/recommend as not many of this type ,but we use new songs!!");
            }
            //System.out.println(querysubpart);
//            int GenreArray[]=new int[6];
            
//            while(m_ResultSet.next()){
//                
//                GenreArray[0] = m_ResultSet.getInt(1);
//                GenreArray[1] = m_ResultSet.getInt(2);
//                GenreArray[2] = m_ResultSet.getInt(3);
//                GenreArray[3] = m_ResultSet.getInt(4);
//                GenreArray[4] = m_ResultSet.getInt(5);
//                GenreArray[5] = m_ResultSet.getInt(6);
//            }
            
//            for(int i=0;i<6;i++)
//                if(GenreArray[i]==1)
//                    System.out.println("");
            //GenreArrayList.add(Genr);
        } catch (SQLException ex) {
            Logger.getLogger(Thread1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Thread1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
