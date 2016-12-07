/*
	Created by Nick Dix
*/
package smaserver;
import java.sql.*;
import java.util.*;

/*
class Message {	
	private String senderID;
	private String receiverID;
	private String content;
	private Timestamp timeRec;
	private Timestamp time2Read;
	private int messageType;
	private int messageID;
	private int ID;
	public Message(String s, String r, String c, Timestamp tr, Timestamp t2, int mt, int mi, int id) {
		senderID = s;
		receiverID = r;
		content = c;
		timeRec = tr;
		time2Read = t2;
		messageType = mt;
		messageID = mi;
		ID = id;
	}

	public String getSenderID() { return senderID; }
	public String getReceiverID() { return receiverID; }
	public String getContent() { return content; }
	public Timestamp getTimeRec() { return timeRec; }
	public Timestamp getTime2Read() { return time2Read; }
	public int getMessageType() { return messageType; }
	public int getMessageID() { return messageID; }
	public int getID() { return ID; }
}
*/
public class DBAccess {
	public static void main(String args[]) {
		/*
		System.out.println("Enter sender's id: ");
		Scanner in = new Scanner(System.in);
		String sender = in.nextLine();
		System.out.println("Enter receiver's id: ");
		String receiver = in.nextLine();
		System.out.println("Enter content: ");
		String content = in.nextLine();
		System.out.println("Enter message type (int): ");
		Timestamp time = new Timestamp(System.currentTimeMillis());
		int messageType = in.nextInt();
		System.out.println("Enter message id: ");
		int messageID = in.nextInt();
		System.out.println("Message added? "
			+ DBAccess.addMessage(sender, receiver, content, 
			time, messageType, messageID));
		
		Timestamp time = new Timestamp(System.currentTimeMillis());
		System.out.println("Message deleted? "
			+ DBAccess.deleteMessage(1, true));
			//+ DBAccess.addMessage("blob", "bolb", "This is a sentence.", 
			//time, 0, 0));
		*/
		Timestamp time = new Timestamp(System.currentTimeMillis());
		DBAccess.addMessage("sender", "receiver", "content", time, -1, -1);
		ArrayList<Message> m = DBAccess.getMessages("receiver");
		System.out.println(m);
		int id = (m.get(0)).getID();
		System.out.println(id);
		System.out.println(DBAccess.deleteMessage(id, false));
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
	
	public static ArrayList<Message> getMessages(String receiver) {
		Connection con=null;
		Statement statement=null;
		ResultSet rs=null;
		ArrayList<Message> messages = new ArrayList<Message>();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost/server?autoReconnect=true&useSSL=false", "java", "lugubr!ous19m1en");

			statement = con.createStatement();
			rs = statement.executeQuery("SELECT * FROM message WHERE ReceiverID = '" + receiver + "';");

			while (rs.next()) {
				Message msg = new Message(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4), rs.getTimestamp(5), rs.getInt(6), rs.getInt(7), rs.getInt(8));
				messages.add(msg);
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
			return messages;
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
				con = null;
			}
			return exists;
		}
	}

	public static boolean changePassword(String user, String newpass) {
		Connection con=null;
		Statement statement=null;
		ResultSet rs=null;
		boolean changed=false;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost/server?autoReconnect=true&useSSL=false", "java", "lugubr!ous19m1en");

			statement = con.createStatement();
			statement.executeUpdate("UPDATE user SET Password = '" + newpass + "' WHERE UserID = '" + user + "';");
			changed = true;

		}
		catch (Exception e) {
			System.out.println(e);
			changed = false;
		}
		finally {
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
			return changed;
		}
	}

	public static boolean changePubKey(String user, String pubKey) {
		Connection con=null;
		Statement statement=null;
		boolean changed=false;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost/server?autoReconnect=true&useSSL=false", "java", "lugubr!ous19m1en");

			statement = con.createStatement();
			statement.executeUpdate("UPDATE user SET PublicKey = '" + pubKey + "' WHERE UserID = '" + user + "';");
			changed = true;
		}
		catch (Exception e) {
			System.out.println(e);
			changed = false;
		}
		finally {
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
			return changed;
		}
	}
	
	public static boolean addUser(String user, String password) {
		Connection con=null;
		Statement statement=null;
		boolean changed=false;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost/server?autoReconnect=true&useSSL=false", "java", "lugubr!ous19m1en");

			statement = con.createStatement();
			statement.executeUpdate("INSERT INTO user VALUES ('" + user + "', '" + password + "', '');");
			changed = true;
		}
		catch (Exception e) {
			System.out.println(e);
			changed = false;
		}
		finally {
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
			return changed;
		}
	}
	
	public static boolean addMessage(String sender, String receiver
		, String content, Timestamp time2read, int messageType
		, int messageID) {
		Connection con=null;
		Statement statement=null;
		boolean changed=false;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost/server?autoReconnect=true&useSSL=false", "java", "lugubr!ous19m1en");
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			String sqlComm = "INSERT INTO message VALUES ('" + sender + "', '" + receiver + "', '" + content + "', '" + currentTime.toString() + "', '" + time2read.toString() + "', " + messageType + ", " + messageID + ", NULL);";
			statement = con.createStatement();
			
			statement.executeUpdate(sqlComm);
			changed = true;
		}
		catch (Exception e) {
			System.out.println(e);
			changed = false;
		}
		finally {
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
			return changed;
		}
	}
	
	public static boolean deleteMessage(int ID, boolean time2delete) {
		Connection con=null;
		Statement statement1=null;
		boolean changed=false;
		String sqlComm = "";
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
				"jdbc:mysql://localhost/server?autoReconnect=true&useSSL=false", "java", "lugubr!ous19m1en");
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			statement1 = con.createStatement();
			
			if (!time2delete) {
				sqlComm = "DELETE FROM message WHERE ID = '" + ID + "';";
				statement1.executeUpdate(sqlComm);
			}
			else {
				sqlComm = "DELETE FROM message WHERE TIMESTAMPDIFF(HOUR, TimeRec, CURRENT_TIMESTAMP()) >= 24;";
				System.out.println(sqlComm);
				statement1.executeUpdate(sqlComm);
				
			}
			changed = true;
		}
		catch (Exception e) {
			System.out.println(e);
			changed = false;
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {}
				rs = null;
			}
			if (statement1 != null) {
				try {
					statement1.close();
				} catch (Exception e) {}
				statement1 = null;
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {}
				con = null;
			}
			return changed;
		}
	}
}
