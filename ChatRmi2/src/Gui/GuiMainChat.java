
package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import Class.Constant;
import Class.PacketUser;
import client.ChatClient3;

import javax.swing.JButton;

public class GuiMainChat extends JPanel implements ActionListener {
	
	public Vector<String> vtMess = new Vector<String>();
	public JList listMess ; 
	private JTextField txtInputMess;
	JButton btnSendMess ; 
	
	private ChatClient3 chatClient;
	public String username = ""; 
	public  String idUser = "" ;
	public String idUserReceive = ""; 
	public PacketUser packet;
	public boolean group ; 
	public boolean chatpublic ; 
	
	public GuiMainChat(String username, String idUserReceive, ChatClient3 chatClient, PacketUser packet) {
		this.username = username ; 
		this.idUser = chatClient.getIdUser() ; 
		this.chatClient = chatClient; 
		this.idUserReceive = idUserReceive ; 
		this.packet = packet ; 
		this.setLayout(new BorderLayout());
		 
	
		vtMess.add("<html><font style='font-family: Arial; font-size: 14pt; font-weight: bold; color: #b8256e;'>SALUT</font></html>");


		vtMess.add(""+  username); 
		listMess =  new JList(vtMess);
		listMess.updateUI();
		listMess.setCellRenderer(new MessCell());
	
	
		
		JScrollPane scrollPane = new JScrollPane(listMess);
		this.add(scrollPane);
		
		JPanel panelControl = new JPanel();
		add(panelControl, BorderLayout.SOUTH);
		panelControl.setLayout(new BorderLayout(0, 0));
		
		txtInputMess = new JTextField();
		panelControl.add(txtInputMess, BorderLayout.CENTER);
		txtInputMess.setColumns(10);
		txtInputMess.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					handleSendMess(); 
				}
			}
		});
	
		btnSendMess = new JButton("Send");
		btnSendMess.setBackground(Color.decode("#0e7a09")); 
		btnSendMess.setForeground(Color.WHITE); 
		btnSendMess.setPreferredSize(new Dimension(120, 40));
		btnSendMess.addActionListener(this);
		panelControl.add(btnSendMess, BorderLayout.EAST);
		
		this.group = new Constant().checkGroup(idUserReceive) ;  
		this.chatpublic = new Constant().checkChatPublic(idUserReceive);
	}
	public static void main(String[] args) {
		JFrame f = new JFrame("chat");
		f.setSize(400,200);
		f.getContentPane().setLayout(new BorderLayout());
		
		f.setVisible(true);
	f.setForeground(Color.red);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == btnSendMess){
			handleSendMess();
		}
	}
	
	private void sendMessage(String chatMessage) throws RemoteException {
		chatClient.serverIF.updateChat(username, chatMessage);
	}
	
	
	private void sendPrivate(String idUser, String message) throws RemoteException {
		String privateMessage ="<html><b>"+ chatClient.name + "</b>: " + message + "</html>";
		String mess = "<html>"+  message + ": <b>Me</b></html>" ; 
		vtMess.add(mess);
		listMess.updateUI();
		
		if(group)
			chatClient.serverIF.sendGroup(idUser, privateMessage,  chatClient.getIdUser());
		else if(chatpublic) {
			System.out.println("guimainchat ");
			chatClient.serverIF.sendToAll(privateMessage, idUser, chatClient.getIdUser() );

		}

		else 
			chatClient.serverIF.sendPP(idUser, privateMessage, chatClient.getIdUser() );


	}
	
	public void handleSendMess() {
		String message = txtInputMess.getText();
		txtInputMess.setText("");
		try {
			sendPrivate(idUserReceive ,message);
		} catch (RemoteException e1) {
			
			e1.printStackTrace();
		}
		System.out.println("Sending message : " + message);
	}
	


	
}

class MessCell implements ListCellRenderer{

	@Override
	public JPanel getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,boolean cellHasFocus) {
	
		
		JPanel panel = new JPanel();
	
		panel.setLayout(new BorderLayout());
		String s = value.toString();
		JLabel text = new JLabel(s); 
		text.setFont(new Font("Verdana", Font.PLAIN, 14)) ; 
		if( s.contains("Me")) {
			panel.add(text, BorderLayout.EAST);
		}else {
			panel.add(text, BorderLayout.CENTER);
		}
		

		return panel;
	}
	
	
}
