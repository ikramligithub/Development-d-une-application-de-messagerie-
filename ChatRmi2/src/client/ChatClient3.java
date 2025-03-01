package client;
import java.awt.Color;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JList;
import javax.swing.JOptionPane;

import Gui.GuiClient1;
import Gui.GuiMainChat;
import server.ChatServerIF;


public class ChatClient3  extends UnicastRemoteObject implements ChatClient3IF {
	
	private static final long serialVersionUID = 7468891722773409712L;
	GuiMainChat chatGUI;
	GuiClient1 guiclient  ; 
	private String hostName = "localhost";
	private String serviceName = "GroupChatService";
	private String clientServiceName;
	public String name;
	private String idUser ; 
	public ChatServerIF serverIF;
	public JList jl; 
	protected boolean connectionProblem = false;

	
	
	public ChatClient3(GuiClient1 guiclient, String userName, String idUser, JList jl ) throws RemoteException {
		super();
		this.guiclient = guiclient;
		this.name = userName;
		this.idUser = idUser;
		this.jl = jl ;
		this.clientServiceName = "ClientListenService_" + userName;
	}

	
	public void startClient() throws RemoteException {		
		String[] details = {name, hostName, clientServiceName, idUser};	

		try {
			Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
			serverIF = ( ChatServerIF )Naming.lookup("rmi://" + hostName + "/" + serviceName);	
		} 
		catch (Exception  e) {
			connectionProblem = true;
			e.printStackTrace();
		}
		
		if(!connectionProblem){
			registerWithServer(details);
		}	
		System.out.println("Client Listen RMI Server is running...\n");
	}


	public void registerWithServer(String[] details) {		
		try{
			serverIF.passIDentity(this.ref);
			serverIF.registerListener(details);			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void messageFromServer(String message, String idUser1) throws RemoteException {
		System.out.println( message );
		System.out.println("message From server in chat lient" + idUser1);
		
		for (GuiMainChat guichat : guiclient.getGuiChat()) {
			if(guichat.idUserReceive.equals(idUser1)) {
				guichat.vtMess.add(message);
				guichat.listMess.updateUI();
				guichat.packet.getLblUser().setForeground(Color.red);
				jl.updateUI();
			}

		}

	}


	@Override
	public void updateUserList(String currentUsers, String idUser) throws RemoteException {
		if(!this.idUser.equals(idUser))
		this.guiclient.addCardAndUser(currentUsers, idUser );
	}
	
	@Override
	public void removeListUser(String idUser) throws RemoteException {
		for (GuiMainChat g : guiclient.getGuiChat()) {
			if(g.idUserReceive.equals(idUser)) {
				guiclient.removeCardUser(g.username, idUser);
			}
		}	
	}
	
	public String getIdUser() {
		return this.idUser; 
	}

}













