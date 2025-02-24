package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.util.Vector;


public interface ChatServerIF extends Remote {
		
	public void updateChat(String userName, String chatMessage)throws RemoteException;
	
	public void passIDentity(RemoteRef ref)throws RemoteException;
	
	public void registerListener(String[] details)throws RemoteException;
	
	public void leaveChat(String idUser)throws RemoteException;
	

	public void sendPP(String idUser, String privateMessage, String idUser1) throws RemoteException;
	
	public void sendToAll(String newMessage, String idchat, String idSender) throws RemoteException; 

	
	public void sendGroup(String idGroup, String mess, String idSender) throws RemoteException; 
	
	public void createGroup(Vector<String> members,String nameGroup) throws RemoteException ; 

}


