package orbit;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Database implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//functional local data members
	Connection conn = null;
	
	//orbit data (to be stored)
	HashMap<String, User> usernameToUserMap = new HashMap<String, User>();
	

	public Database(){
		//establish connection to SQL database
		connectToSQL();
		
		//initialize all variables to default values
		//only called the first time a Database is constructed
		//subsequent database initializations read the Database object from database.txt (hence no constructor)
	}
	
	//register JDBC driver and open connection
	public void connectToSQL(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root");
		} catch (SQLException sqle) {
			System.out.println ("SQLException in Database.connectToSQL(): " + sqle.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println ("ClassNotFoundException in Database.connectToSQL(): " + cnfe.getMessage());
		}
	}
	
	//close connection
	public void disconnectFromSQL(){
		try {
			if(conn != null){
				conn.close();
			}
		} catch (SQLException e) {
			System.out.println ("SQLException in Database.disconnectFromSQL(): " + e.getMessage());
		}
	}
	
	//returns true if the username/password combination is valid
	public boolean authenticateLogin(String username, String password){
		return true;
	}
	
	//queries sql database for ID (int) corresponding to unique username
	public int queryUser(String username){
		//defaults to user not found case (userID = -1)
		int userID = -1;
		try {
			Statement st = conn.createStatement();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Usernames WHERE username=?");
			ps.setString(1, username); // set first variable in prepared statement
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				userID = rs.getInt("userID");
				System.out.println ("userID = " + userID);
			}
			rs.close();
			st.close();
		} catch (SQLException sqle) {
			System.out.println ("SQLException in Database.queryUser(): " + sqle.getMessage());
		}
		return userID;
	}
	
	//adds new user to the database
	public void addUserToSQL(String username){
		//only unique usernames will be added to the database
		if(queryUser(username) != -1){
			System.out.println("User already exists. Did not add new user.");
			return;
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			String sql = "INSERT INTO Usernames (username) VALUES ('" + username + "')";
			st.executeUpdate(sql);
			st.close();
		} catch (SQLException e) {
			System.out.println ("SQLException in Database.addUser(): " + e.getMessage());
		}
	}
	
	//DEBUG //prints all usernames put in two columns (userID, username)
	public void printAllUsernames(){
		int userID = -1;
		String username = "";
		
		try {
			Statement st = conn.createStatement();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Usernames");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				userID = rs.getInt("userID");
				username = rs.getString("username");
				System.out.println(userID + " " + username);
			}
			rs.close();
			st.close();
		} catch (SQLException sqle) {
			System.out.println ("SQLException in Database.queryUser(): " + sqle.getMessage());
		}
	}
	
	
	//DEBUG //deletes the user with the specified username
	public void deleteUser(String username){
		try{
			String query = "delete from Usernames where username = ?";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, username);
			ps.execute();
		} catch (SQLException e) {
			System.out.println ("SQLException in Database.deleteUser(): " + e.getMessage());
		}
	}
	
	
	
//	//DEBUG //query database for username
//	public static void main(String[] args){
//		Database d = new Database();
//		Scanner in = new Scanner(System.in);	 
//		
//		System.out.print("Enter a username to search for: ");
//		String username = in.nextLine();
//		
//		System.out.println("Searching for username \"" + username + "\"");
//		int userID = d.queryUser(username);
//		
//		if(userID == -1){
//			System.out.println("No result for username \"" + username + "\"");
//			System.out.println("Adding new user: " + username);
//			d.addUserToSQL(username);
//		}
//		else{
//			System.out.println("Added new user: \"" + username + "\" with user number: " + userID);
//		}
//		System.out.println("userID username\n---------------");
//		d.printAllUsernames();
//		
//		System.out.print("Enter a username to delete: ");
//		username = in.nextLine();
//		System.out.println("Deleting user with username \"" + username + "\'");
//		d.deleteUser(username);
//		System.out.println("Deleted user \"" + username + "\"");
//		d.printAllUsernames();
//		
//		d.disconnectFromSQL();
//	}
}
