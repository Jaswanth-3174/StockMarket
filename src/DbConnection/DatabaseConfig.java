package DbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbConnection{
    static void main(){
        String url = "jdbc:mysql://localhost:3306/trading_app";
        String username = "jaswanth";
        String password = "root";
        String query = "select * from users";
        try{
            Connection con = DriverManager.getConnection(url, username, password);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            rs.next();
            System.out.println(rs.getString(2) + "");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
