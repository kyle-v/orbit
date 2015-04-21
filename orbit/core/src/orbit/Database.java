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
	transient Connection conn = null;
	
	//orbit data (to be stored)
	HashMap<String, User> usernameToUserMap;
	

	public Database(){
		//establish connection to SQL database
		connectToSQL();
		
		//initialize all variables to default values
		usernameToUserMap = new HashMap<String, User>();
		
		//only called the first time a Database is constructed
		//subsequent database initializations read the Database object from database.txt (hence no constructor)
	}
	
	//register JDBC driver and open connection
	public void connectToSQL(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root");
			System.out.println("After get connection, conn = " + conn);
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
		if(queryUser(username) == -1 || usernameToUserMap.isEmpty()){
			return false;
		}
		else{
			if(usernameToUserMap.get(username).getPass().equals(password)){
				return true;
			}
			else{
				return false;
			}
		}
	}
	
	//returns true if new user has been created and added to database
	public boolean createUser(String username, String password){
		User newUser = new User(username, password);
		if(queryUser(username) == -1 || usernameToUserMap.isEmpty()){
			usernameToUserMap.put(username, newUser);
			addUserToSQL(username);
			return true;
		}
		else{
			return false;
		}
	}
	
	//queries sql database for ID (int) corresponding to unique username
	public int queryUser(String username){
		//defaults to user not found case (userID = -1)
		int userID = -1;
		try {
			if(conn == null){
				System.out.println("CONNECTION IS NULL: " + conn);
				connectToSQL();
			}
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
	public boolean addUserToSQL(String username){
		//only unique usernames will be added to the database
		if(queryUser(username) != -1){
			System.out.println("User already exists. Did not add new user.");
			return false;
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			String sql = "INSERT INTO Usernames (username) VALUES ('" + username + "')";
			st.executeUpdate(sql);
			st.close();
			return true;
		} catch (SQLException e) {
			System.out.println ("SQLException in Database.addUser(): " + e.getMessage());
		}
		return false;
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
	
	
	
	//DEBUG //query database for username
	public static void main(String[] args){
		Database d = new Database();
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
		d.printAllUsernames();
		
		d.disconnectFromSQL();
	}
	
	
//	//DEBUG //test client connection functionality with server
//	public static void main(String[] args){
//		try {
//			System.out.println("Starting Client");
//			Socket s = new Socket("localhost", 6789);
//			
//			System.out.println("Writing to stream");
//			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
//			Vector<String> o = new Vector<String>();
//			o.add("username");
//			o.add("password");
//			oos.writeObject(new ServerRequest("Authenticate Login", o));
//			oos.flush();
//			
//			System.out.println("Wrote to stream. Waiting for input...");
//			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
//			String response = (String)ois.readObject();
//			
//			System.out.println("Received response: " + response + ". Done.");
//			oos.close();
//			ois.close();
//			s.close();
//		} catch (IOException ioe) {
//			System.out.println("IOE: " + ioe.getMessage());
//		} catch (ClassNotFoundException e) {
//			System.out.println("ClassNotFoundException in Database.main(): " + e.getMessage());
//		}
//	}
}
