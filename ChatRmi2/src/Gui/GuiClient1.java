package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.ChatClient3;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.border.LineBorder;

import Class.Constant;
import Class.PacketUser;

public class GuiClient1 extends JFrame {

	private JPanel contentPane;
	CardLayout card  = new CardLayout();
	JPanel panelViewChat ; 
	
	private JPanel panelinFor, panelCreateGroup, panel_1, panel_2;
	private JLabel lbCurrentUser , lblMember;
	private JTextField txtNameGroup ; 
	private JButton btnCreateGroup , btnAddMember; 
	
	private String username ; 
	private ChatClient3 chatClient;
	private String idUser ; 
	private Vector<PacketUser> vtUser = new Vector<PacketUser>();
	private Vector<GuiMainChat> vtmainchats = new Vector<GuiMainChat>() ;  
	JList listUser ; 
	private JTextField txtnputMember;
	
	private Vector<String> vtGroup = new Vector<String>();
	private JLabel lblogoUser;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					String s = JOptionPane.showInputDialog(null, "Tapez votre nom") ;
					
					GuiClient1 frame = new GuiClient1(s);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GuiClient1(String username) {
		this.setTitle("Chat Rmi");
		this.username = username ; 
		this.idUser = Math.random() + "" + this.username ;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 609, 389);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setBackground(Color.WHITE);
		
		
		try {
			Image iconmes = ImageIO.read(new File( new Constant().LINK_PATH_IMAGE + "logo1.png"));
			this.setIconImage(iconmes); 
		} catch (IOException e1) {
			
		
		}
		
		JPanel panelUsers = new JPanel();
		panelUsers.setPreferredSize(new Dimension(110,200));
		contentPane.add(panelUsers, BorderLayout.WEST);
		listUser =  new JList(vtUser);
		listUser.updateUI();
		listUser.setCellRenderer(new UserCell());
		listUser.addMouseListener(mouseClicktoShowChat());
		panelUsers.setLayout(new BorderLayout(100, 100));
	
	
		
		JScrollPane scrollPane = new JScrollPane(listUser);
		panelUsers.add(scrollPane);
		
		panelViewChat= new JPanel();
		
		contentPane.add(panelViewChat, BorderLayout.CENTER);
		panelViewChat.setLayout(card);
		
		panelinFor = new JPanel();
		panelinFor.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(panelinFor, BorderLayout.NORTH);
		panelinFor.setLayout(new BorderLayout(0, 0));
		
		panel_1 = new JPanel();
		panelinFor.add(panel_1, BorderLayout.WEST);
		panel_1.setLayout(new GridLayout(1, 0, 0, 0));
		
		lblogoUser = new JLabel("");
		panel_1.add(lblogoUser);
		try {
			BufferedImage bufferImage_user = ImageIO.read(new File(new Constant().LINK_PATH_IMAGE+ "logoUser.png"));
			ImageIcon imageIcon_user = new ImageIcon(bufferImage_user.getScaledInstance(60, 60, Image.SCALE_SMOOTH));
			lblogoUser.setIcon(imageIcon_user);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		lbCurrentUser = new JLabel(this.username);
		panel_1.add(lbCurrentUser);
		lbCurrentUser.setForeground(Color.decode("#b8256e"));
		Font font = new Font("Arial", Font.BOLD, 14); 
		lbCurrentUser.setFont(font);

		
		
		panelCreateGroup = new JPanel();
		panelinFor.add(panelCreateGroup, BorderLayout.EAST);
		panelCreateGroup.setLayout(new GridLayout(3,2, 5,10));
		
		txtNameGroup = new JTextField();
		panelCreateGroup.add(txtNameGroup);
		txtNameGroup.setColumns(10);
		
		btnCreateGroup = new JButton("créer");
		btnCreateGroup.setBackground(Color.decode("#b8256e"));
		btnCreateGroup.setForeground(Color.white);
		panelCreateGroup.add(btnCreateGroup);
		btnCreateGroup.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					chatClient.serverIF.createGroup(vtGroup, txtNameGroup.getText());
				} catch (RemoteException e1) {
					
					e1.printStackTrace();
				}
			}
		});
		
		txtnputMember = new JTextField();
		panelCreateGroup.add(txtnputMember);
		txtnputMember.setColumns(10);
		txtnputMember.setBackground(Color.WHITE);
		btnAddMember = new JButton("Ajouter un membre");
		btnAddMember.setBackground(Color.decode("#b8256e")); 
		btnAddMember.setForeground(Color.white);
		btnAddMember.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String s = txtnputMember.getText();
				txtnputMember.setText("");
				vtGroup.add(s);
				lblMember.setText(lblMember.getText() + " | " + s);
			}
		});
		panelCreateGroup.add(btnAddMember);
		
		lblMember = new JLabel("");
		panelCreateGroup.add(lblMember);
		try {
			getConnected(this.username);
			

		} catch (RemoteException e) {
		
		e.printStackTrace();
		} 
		
		this.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		     
			    	try {
			        	chatClient.serverIF.leaveChat(idUser);
					} catch (RemoteException e) {
						e.printStackTrace();
					}		        	
		       
		        System.exit(0);  
		    }   
		});
		
	}
	
	
	public void addCardAndUser(String nameUser, String idUser) {
		
		PacketUser packett =  new PacketUser(nameUser, new JLabel(nameUser), idUser) ;
		vtUser.add(packett);
		GuiMainChat mainchat = new GuiMainChat(nameUser, idUser, this.chatClient, packett) ; 
		vtmainchats.add(mainchat); 
		panelViewChat.add(nameUser, mainchat ) ; 
		panelViewChat.setForeground(Color.white);
		listUser.updateUI();
	}
	

	public void removeCardUser(String nameUser, String idUser) {
		for (int i = 1; i < vtUser.size(); i ++ ) {
			if(vtUser.elementAt(i).getName().equals(nameUser)) {
				vtUser.removeElementAt(i);
			}
		}
		listUser.updateUI();
	}
	
	
	public MouseListener mouseClicktoShowChat() {
		MouseListener mouseLissten = new  MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				JList theList = (JList) e.getSource(); 

				int index = theList.locationToIndex(e.getPoint()); 
				PacketUser selectpacket = (PacketUser)theList.getModel().getElementAt(index);
				selectpacket.getLblUser().setForeground(Color.decode("#b8256e"));
				card.show(panelViewChat, selectpacket.getName());
						
				
			}
			
		};
		return mouseLissten ; 
	}
	
	private void getConnected(String userName) throws RemoteException{
		
		String cleanedUserName = userName.replaceAll("\\s+","_");
		cleanedUserName = userName.replaceAll("\\W+","_");
		try {		
			chatClient = new ChatClient3(this, cleanedUserName, this.idUser, listUser );
			chatClient.startClient();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public Vector<GuiMainChat> getGuiChat(){
		return this.vtmainchats ; 
	}

	public String getIdUser() {
		return this.idUser ; 
	}
}

class UserCell implements ListCellRenderer{
	Constant constant = new Constant() ; 

	@Override
	public JPanel getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,boolean cellHasFocus) {
		
		
		JPanel panel = new JPanel();
		JLabel lb = new JLabel("") ; 
		
	
		panel.setLayout(new BorderLayout());
		PacketUser packet = (PacketUser) value; 
		panel.add(packet.getLblUser(), BorderLayout.CENTER);
		panel.add(lb, BorderLayout.WEST) ; 
		try {
			if(constant.checkGroup(packet.getIdUser()) || constant.checkChatPublic(packet.getIdUser())) {
				BufferedImage bufferImage_user = ImageIO.read(new File(new Constant().LINK_PATH_IMAGE+ "logoGroup1.png"));

				ImageIcon imageIcon_user = new ImageIcon(bufferImage_user.getScaledInstance(25, 25, Image.SCALE_SMOOTH));
				lb.setIcon(imageIcon_user);

			}else {
				BufferedImage bufferImage_user = ImageIO.read(new File(new Constant().LINK_PATH_IMAGE+ "user1.jpg"));

				ImageIcon imageIcon_user = new ImageIcon(bufferImage_user.getScaledInstance(25, 25, Image.SCALE_SMOOTH));
				lb.setIcon(imageIcon_user);

			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		  
		return panel;
	}
	
	
}


