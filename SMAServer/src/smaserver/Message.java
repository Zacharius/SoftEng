package smaserver;
import java.sql.*;

public class Message {
	
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
