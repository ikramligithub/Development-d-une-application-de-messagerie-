package server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

import Class.Constant;
import client.ChatClient3IF;

public class ChatServer extends UnicastRemoteObject implements ChatServerIF {
	String line = "---------------------------------------------\n";
	private Vector<Chatter> chatters;
	private static final long serialVersionUID = 1L;
	private Vector<Group> groups = new Vector<Group>(); 
	
	public ChatServer() throws RemoteException {
		super();
		chatters = new Vector<Chatter>(10, 1);
	}
	
	
	public static void main(String[] args) {
		startRMIRegistry();	
		String hostName = new Constant().HOST;
		String serviceName = "GroupChatService";
		
		if(args.length == 2){
			hostName = args[0];
			serviceName = args[1];
		}
		
		try{
			ChatServerIF hello = new ChatServer();
			Naming.rebind("rmi://" + hostName + "/" + serviceName, hello);
			System.out.println("Group Chat RMI Server is running...");
		}
		catch(Exception e){
			System.out.println("Server had problems starting");
		}	
	}

	

	public static void startRMIRegistry() {
		try{
			java.rmi.registry.LocateRegistry.createRegistry(1099);
			System.out.println("RMI Server ready");
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
	}
		
	

	public String sayHello(String ClientName) throws RemoteException {
		System.out.println(ClientName + " sent a message");
		return "Hello " + ClientName + " from group chat server";
	}
	


	public void updateChat(String name, String nextPost) throws RemoteException {
		String message =  name + " : " + nextPost + "\n";
	
	}
	

	@Override
	public void passIDentity(RemoteRef ref) throws RemoteException {	
		//System.out.println("\n" + ref.remoteToString() + "\n");
		try{
			System.out.println(line + ref.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void registerListener(String[] details) throws RemoteException {	

		registerChatter(details);
	}

	
	
	private void registerChatter(String[] details){		
		try{
			ChatClient3IF nextClient = ( ChatClient3IF )Naming.lookup("rmi://" + details[1] + "/" + details[2]);
			Chatter newchatter = new Chatter(details[0], nextClient, details[3]) ; 
			
			
			newchatter.getClient().updateUserList("public chat", "111publicChat");
			String[] currentUsers = getUserList();	
			String[] idUsers = getIdUserList(); 
			for (int i = 0 ; i < currentUsers.length ; i ++ ) {
				newchatter.getClient().updateUserList(currentUsers[i], idUsers[i]); 
			}
			
			
			chatters.addElement(newchatter);
			System.out.println("idUser: " + details[3]);
			
		
			
			updateUserList();		
		}
		catch(RemoteException | MalformedURLException | NotBoundException e){
			e.printStackTrace();
		}
	}
	
	private void updateUserList() {
		String[] currentUsers = getUserList();
		String[] idUsers = getIdUserList(); 

		for(Chatter c : chatters){
			try {
				c.getClient().updateUserList(currentUsers[currentUsers.length-1], idUsers[idUsers.length -1]);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}
	

	
	private String[] getUserList(){
	
		String[] allUsers = new String[chatters.size()];
		for(int i = 0; i< allUsers.length; i++){
			allUsers[i] = chatters.elementAt(i).getName();
		}
		return allUsers;
	}
	
	private String[] getIdUserList(){
		
		String[] allUsers = new String[chatters.size()];
		for(int i = 0; i< allUsers.length; i++){
			allUsers[i] = chatters.elementAt(i).getIdUser();
		}
		return allUsers;
	}
	
	@Override
	public void createGroup(Vector<String> members, String nameGroup) throws RemoteException {
		String idGroup = "group" + Math.random() + "" + nameGroup; 
		Vector<Chatter> ch = new Vector<Chatter>() ;
		for (String mem : members) {
			Chatter c = findChatter(mem); 
			ch.add(c) ;
			c.getClient().updateUserList(nameGroup, idGroup);
		}
		groups.add(new Group(idGroup, ch));
	}
	


	@Override
	public void sendToAll(String newMessage, String idchat, String idSender) throws RemoteException{	
		System.out.println("send to all in chat server");
		for(Chatter c : chatters){
			try {
				if(!c.getIdUser().equals(idSender))
					c.getClient().messageFromServer(newMessage, idchat);
			} 
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}	
	}

	
	@Override
	public void leaveChat(String idUser) throws RemoteException{
		
		for(Chatter c : chatters){
			if(c.getIdUser().equals(idUser)) {
				chatters.remove(c);
				break; 
			}
			
		}	
		for(Chatter c : chatters){
			System.out.println("Xóa ở " + c.name);
			c.getClient().removeListUser(idUser);

		}			
	}
	


	@Override
	public void sendPP(String idUser, String privateMessage, String idUser1) throws RemoteException{
		
		for (Chatter chatter : chatters) {
			
			if(chatter.getIdUser().equals(idUser)) {
				
				chatter.getClient().messageFromServer(privateMessage, idUser1);
			}
		}

	}
	@Override
	public void sendGroup(String idGroup, String mess, String idSender) throws RemoteException{
		for (Group gr : groups) {
			if(gr.getIdGroup().equals(idGroup)) {
				for (Chatter chatter : gr.getChatter()) {
					if(! idSender.equals(chatter.getIdUser())) {
						try {
							chatter.getClient().messageFromServer(mess, idGroup);

						}catch(Exception e) {

						}

					}
					
				}
			}
			break ; 
		}
	}
	
	
	public Chatter findChatter(String username) {
		for (Chatter chatter : chatters) {
			if(chatter.getName().equals(username))
				return chatter;
		}
		return null ; 
	}

	
}



