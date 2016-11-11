
package musicRecomSys;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class MusicRecommenderSystemTest {
    static int userIDint;
    void loginbybutton(String username,String Password) throws Exception{
        System.out.println("Hey i got the : username : " + username + "  " +Password);
        //Lets est connection with DB
        Connection conToCheckDetails = connectionToDatabase.getConnection();
        Statement m_Statement = conToCheckDetails.createStatement();
        String query = "SELECT UserID FROM [UserDetails] where username= '" + username + "'and password = '" + Password + "'";
        //System.out.println("Execued the query without error");
        ResultSet m_ResultSet = m_Statement.executeQuery(query);
        try{
        while(m_ResultSet.next())
            userIDint = m_ResultSet.getInt(1);
        if(userIDint!=0){
        System.out.println("UserID affiliated with this username : " + userIDint);
        String userID = Integer.toString(userIDint);
        Start startReco1 = new Start();
        startReco1.main1(userID); //start the recommendation
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Invalid , Kindly check your Credentials!","Error",JOptionPane.ERROR_MESSAGE);
            
        }
        }
        catch(Exception e)
        {
            System.out.println("Hey Invalid credentials!! ");
            
        }
    }
    public int getUserIDint(){
        return userIDint;
    }
}


//    public static void main(String[] args) throws ClassNotFoundException, SQLException, Exception {
//
////        Login l1=new Login();
////        boolean loggedin=l1.LoginBox(); //Once user login , run the reco system, and show him 3 rows from the table
////        int userIDint=l1.getUserIdFromLoginDetails();
//        
//
//    }

        //String PredictionResultFile = "src/musicrecommendersystemtest/data/Output/output.data";
//        while (!loggedin)
//        {
//            loggedin=l1.LoginBox();
//        }
//        Connection con=connectionToDatabase.getConnection();
//        Statement gettopsongs = con.createStatement();  //If i need a connection returned ffrom the connection class i put that in con
//        String query = "select top(3) LikesCount FROM[SongDatabase] ORDER BY LikesCount DESC";
//        ResultSet m_ResultSet = gettopsongs.executeQuery(query);
//        ResultSetMetaData rsmd = m_ResultSet.getMetaData();//better way to use if i want to iterate over all the columns
//        while (m_ResultSet.next()) {
//        System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "+ m_ResultSet.getString(3));
//        
            /*Statement select = conn.createStatement();
ResultSet result = select.executeQuery("SELECT * FROM D724933.ECOCHECKS WHERE ECO = '"+localeco+"' AND CHK_TOOL = '"+checknames[i]+"'");
ResultSetMetaData rsmd = result.getMetaData();

int numberOfColumns = rsmd.getColumnCount();    
List data = new ArrayList<Map>();
Map mapp2;

while(result.next()) { // process results one row at a time
    mapp2 = new HashMap<String, String>();
    for(int i=1; i<=numberOfColumns; i++) {
        mapp2.put(rsmd.getColumnName(i), rs.getString(i));
    }
    data.add(mapp2);
}
                    */

    //}

    
class Login {

    String username;
    String password;
    int userid;
    public boolean LoginBox() {
        JFrame LoginFrame = new JFrame("Login Process");
        LoginFrame.setSize(50, 50);
        JTextField tf_username = new JTextField(10);
        JTextField tf_password = new JTextField(10);
        JPanel p=new JPanel();
        p.add(tf_username);
        p.add(tf_password);
        LoginFrame.add(BorderLayout.NORTH,p);
        JButton submitButton = new JButton("Submit");
        LoginFrame.add(BorderLayout.SOUTH,submitButton);
//        LoginFrame.add(tf_username);
//        LoginFrame.add(tf_password);
        //LoginFrame.add(submitButton);
        LoginFrame.setVisible(true);
        LoginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        username = tf_username.getText();
        password = tf_password.getText();
        submitButtonClicked sbc=new submitButtonClicked(username,password);
        submitButton.addActionListener(sbc);
        if(sbc.getCount()==1){
            userid=sbc.getUserID();
            return true; 
        }
        else
            return false;
        
        
    }
    public int getUserIdFromLoginDetails(){
            return userid;}
}
class submitButtonClicked implements ActionListener
{
    String username;
    String password;
    int count;
    int userID;
    public submitButtonClicked(String us,String pwd){
        username=us;
        password=pwd;
    }
    
    public int getUserID(){
        return userID;
    }
    
    public void actionPerformed(ActionEvent e) {
        System.out.println("Submit button clicked");
        try {
            System.out.println("Shouldnt be here~!!!!!!!!");
            Connection conToCheckDetails = connectionToDatabase.getConnection();
            Statement m_Statement = conToCheckDetails.createStatement();
            String query = "SELECT userID,count(*) FROM [UserDetails] where username= '" + username + "'and password = '" + password + "'";
            ResultSet m_ResultSet = m_Statement.executeQuery(query);
            count=Integer.parseInt(m_ResultSet.getString(2));
             
            if(count ==1){
                userID=Integer.parseInt(m_ResultSet.getString(2));
                System.out.println("details found : "+userID);
            }
            else
                System.out.println("Wrong details!!");
            
        } 
        
        catch(Exception exp) {
        }
        
 
    }
    public int getCount()
    {
        return count;
    }
    
}
class connectionToDatabase
{
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        Connection con;
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  //To get the connec tion type
        con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=MRS_test1;user=vishal09;password=12345;");
        //connection name
        System.out.println("Connected to database !");
        return con;
        }
}