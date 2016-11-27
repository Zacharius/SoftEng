import java.sql.*;
import java.util.*;

public class DBAccess {
	public static void main(String args[]) {
		System.out.println("Enter user's name to get password: ");
		Scanner in = new Scanner(System.in);
		String user = in.nextLine();
		System.out.println("User exists? " + DBAccess.userExists(user));
		System.out.println("Password? " + DBAccess.getPassword(user));
		System.exit(0);
	}

	public static String getPassword(String user) {
		Connection con=null;
		Statement statement=null;
		ResultSet rs=null;
		String password="";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost/server?autoReconnect=true&useSSL=false", "java", "lugubr!ous19m1en");

			statement = con.createStatement();
			rs = statement.executeQuery("SELECT Password FROM user WHERE UserID = '" + user + "';");

			if (!rs.next()) {
				password = "User does not exist";
			}
			else {
				password = rs.getString(1);
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {}
				rs = null;
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e) {}
				statement = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {}
				con = null;
			}
			return password;
		}
	}

	public static boolean userExists(String user) {
		Connection con=null;
		Statement statement=null;
		ResultSet rs=null;
		boolean exists=false;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost/server?autoReconnect=true&useSSL=false", "java", "lugubr!ous19m1en");

			statement = con.createStatement();
			rs = statement.executeQuery("SELECT UserID FROM user WHERE UserID = '" + user + "';");

			if (!rs.next()) {
				exists = false;
			}
			else {
				exists = true;
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {}
				rs = null;
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e) {}
				statement = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {}
			}
			return exists;
		}
	}
}
