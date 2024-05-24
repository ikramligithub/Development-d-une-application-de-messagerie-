package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatClient3IF extends Remote{

	
	public void messageFromServer(String message, String idUser1) throws RemoteException;

	public void updateUserList(String currentUsers, String idUser) throws RemoteException;
	
	public void removeListUser(String idUser) throws RemoteException ;
}



