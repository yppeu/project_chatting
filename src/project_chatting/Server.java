
package project_chatting;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;

import db.ConfirmDB;
import db.InsertDB;
import db.SelectDB;
import db.VO;


import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class Server extends JFrame implements ActionListener {
	
	// �������̽�//
	private JPanel contentPane;
	private JTextArea chat_area1 = new JTextArea("");
	private JButton start_btn = new JButton("��������");
	private JButton chatLog_btn = new JButton("ä�÷α�");
	private JButton conLog_btn = new JButton("���ӷα�");
	private JButton exit_btn = new JButton("��������");
	private JButton client_bnt = new JButton("Ŭ���̾�Ʈ");
	private final JLabel lblNewLabel = new JLabel("New label");
	private JScrollPane scrollPane;
	//��ũ�� ���� �Ʒ� ���� ���� x
	
	// network ����
	private ServerSocket server_socket = null;
	private Socket socket = null;
	private Vector user_vc = new Vector();
	private StringTokenizer st;
	private StringTokenizer st2;
	private StringTokenizer st3;
	
	//�ð� ����
	SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
	
    
	  public Server() { // ���� ������
		init(); // ȭ�� ������ ����
		start();// start ������ ����
	}

	private void start() {// ��ư�� �׼� �־��
		
		start_btn.addActionListener(this);
		exit_btn.addActionListener(this);
		chatLog_btn.addActionListener(this);
		conLog_btn.addActionListener(this);
	}//start-end

	private void init() {//init();�� ���� ����=> ȭ�� ���� 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		setTitle("����������");
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
//===================================== start
		start_btn.setBackground(new Color(255, 69, 0));
		start_btn.setForeground(Color.WHITE);
		start_btn.setFont(new Font("���� ���", Font.BOLD, 15));
		start_btn.setBounds(37, 558, 113, 60);
		contentPane.add(start_btn);

//======================================chat log
		chatLog_btn.setBackground(new Color(255, 69, 0));
		chatLog_btn.setForeground(Color.WHITE);
		chatLog_btn.setFont(new Font("���� ���", Font.BOLD, 15));
		chatLog_btn.setBounds(162, 558, 113, 60);
		contentPane.add(chatLog_btn);
//=======================================connect log
		conLog_btn.setBackground(new Color(255, 69, 0));
		conLog_btn.setForeground(Color.WHITE);
		conLog_btn.setFont(new Font("���� ���", Font.BOLD, 15));
		conLog_btn.setBounds(287, 558, 113, 60);
		contentPane.add(conLog_btn);

//========================================client		
		client_bnt.setBackground(new Color(255,69,0));
		client_bnt.setForeground(Color.WHITE);
		client_bnt.setFont(new Font("���� ���", Font.BOLD, 15));
		client_bnt.setBounds(413, 558, 113, 60);
		contentPane.add(client_bnt);

//========================================== exit
		exit_btn.setBackground(new Color(255, 69, 0));
		exit_btn.setForeground(Color.WHITE);
		exit_btn.setFont(new Font("���� ���", Font.BOLD, 15));
		exit_btn.setBounds(537, 558, 113, 60);
		contentPane.add(exit_btn);
		
//========================================== icon
		lblNewLabel.setIcon(new ImageIcon(Server.class.getResource("/image/icon2.png")));
		lblNewLabel.setBounds(275, 10, 125, 130);
		contentPane.add(lblNewLabel);
		
//========================================== chat area
		chat_area1.setEditable(false);//���� �Ұ�
		chat_area1.setFont(new Font("���� ���", Font.PLAIN, 15));
		chat_area1.setBorder(new LineBorder(new Color(255, 69, 0), 1, true));
		chat_area1.setBounds(130, 153, 418, 387);
		scrollPane = new JScrollPane(chat_area1);//��ũ�� �߰�
		scrollPane.setBorder(new LineBorder(new Color(255, 69, 0), 1, true));
		scrollPane.setBounds(130, 153, 418, 387);
		
		chat_area1.setCaretPosition(chat_area1.getDocument().getLength());
		DefaultCaret caret = (DefaultCaret)chat_area1.getCaret();//??
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		chat_area1.setLineWrap(true); 
		
		contentPane.add(scrollPane);							
		
		

		this.setVisible(true);//ȭ�� ���
		
		//Ŭ���̾�Ʈ ��ư�� ������ Ŭ���̾�Ʈ ���� ����
		client_bnt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Client();
				
			
			}
		});
		
		
		

	}//init - end
	
	
	private void Server_strat() {//server_start ����
		try {
		
			server_socket = new ServerSocket(7770);
			

			
		} catch (IOException e) {
			// ���� �� ��� �޽��� ����
		}

		if (server_socket != null) {
			Connection(); // ���Ͽ� ���� ���ε� �Ǹ� connection ������ ����
		}
	}

	private void Connection() {//Ŀ���� ������

		Thread th = new Thread(new Runnable() {// �����忡 runnable ������ ����
			
			@Override
			public void run() {//runnable ����

				while (true) {
					try {
						
						socket = server_socket.accept();//�������� ����
						
						UserInfo user = new UserInfo(socket);
						user.start();

					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "��������Ϸ�!", "Ȯ��", JOptionPane.ERROR_MESSAGE);
						break;
					}

				} // while-end
			}
		});//runnable-end
		th.start();//������ ����

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == start_btn) {
			Server_strat(); // ���ϻ��� �� ������
			chat_area1.append("network connect ok...!\n");
			start_btn.setEnabled(false);
			exit_btn.setEnabled(true);
			try {
				ConfirmDB con = new ConfirmDB();
				int num = con.confirm();
				if (num == 1 ) {
					chat_area1.append("db table not exist!\ntable create..!\ndb connect ok..!\n");
				}else {chat_area1.append("db connect ok..!\n");}
			} catch (ClassNotFoundException | SQLException e1) {
			}
			
			
		} else if (e.getSource() == exit_btn) {
			if(server_socket == null) {
				exit_btn.setEnabled(false);
				//client.setVisible(false);
			}else {
			try {
				start_btn.setEnabled(true);
				server_socket.close();
				user_vc.removeAllElements();
				chat_area1.setText("");
			} catch (IOException e1) {}}
			
		} else if (e.getSource() == chatLog_btn) { //////////////ä�÷α׺���
			try {
				SelectDB sel = new SelectDB();
				ArrayList<VO> vo = sel.select_chat();
				
				for (VO v : sel.getArr_vo()) {
					chat_area1.append(format1.format(v.getTime())+"\n");
					chat_area1.append(v.getStr()+"\n");	
				}	
			} catch (ClassNotFoundException e1) {
			} catch (SQLException e1) {
			}
			
		} else if (e.getSource() == conLog_btn) { ////////////////////���Էα׺���
			try {
				SelectDB sel = new SelectDB();
				ArrayList<VO> vo = sel.select_con();
				
				for (VO v : sel.getArr_vo()) {
					chat_area1.append(format1.format(v.getTime())+"\n");
					chat_area1.append(v.getStr()+"\n");	
				}	
			} catch (ClassNotFoundException e1) {
			} catch (SQLException e1) {
			}
		}
	}

	

//userinfo�� ������ ����� ���� //����Ǳ� ������ userinfo�� �޾� �۾��� ����
	class UserInfo extends Thread {
		//stream -> ������ ����� // ���Ϲ������� �귯���� ��, ���� ��� �Ұ�
		private InputStream is;//�Է� //Ŭ���̾�Ʈ���� ������ �ٽ� �鿩��
		private OutputStream os;//���
		private DataInputStream dis;//���Ŀ� �´� Ÿ�Ժ��� ��ȯ ���
		private DataOutputStream dos;//���Ŀ� �´� Ÿ�Ժ��� ��ȯ  //Ŭ���̾�Ʈ �ʿ��� �޾Ƶ���(1) 306

		private Socket user_socket;
		private String nickname = "";
//============================������
		public UserInfo(Socket soc) {//���� ������ ����
					//Socket soc�� ���� ����
			this.user_socket = soc;//304 , 205
			//soc�� user socket ��
			UserNetwork();//������ ����
		}
  
		private void UserNetwork() {//������ ����
			try {
				
				is = user_socket.getInputStream();
			
				dis = new DataInputStream(is);
				
				os = user_socket.getOutputStream();
				
				dos = new DataOutputStream(os);
				
				nickname = dis.readUTF();
				
				chat_area1.append(nickname + " ����\n");
			
				
				try {
					InsertDB insert = new InsertDB(); 
					insert.log_insert(nickname + " ����\n");
				} catch (ClassNotFoundException e) {
				} catch (SQLException e) {	
				}//catch-end
				
				
				
				
				chat_area1.append("���� ���ӵ� ����� �� : " + (int)(user_vc.size()+1)+"\n"); 

				BroadCast("NewUser/" + nickname);

				// �ڽſ��� ���� ����ڸ� �޾ƿ��� �κ�
				for (int i = 0; i < user_vc.size(); i++) {
					UserInfo u =  (UserInfo) user_vc.elementAt(i);
								//cast
					send_Message("OldUser/" + u.nickname);
				}

				user_vc.add(this);

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Stream���� ����!", "Error!", JOptionPane.ERROR_MESSAGE);

			}

		}

		public void run() {
			while (true) {
				try {
					String msg = dis.readUTF();
					InMessage(msg);
					chat_area1.append(nickname+" �����κ��� ���� �޼���: " +msg+"\n");
					try {
						InsertDB insert = new InsertDB();
						insert.chat_insert(msg);
					} catch (ClassNotFoundException e) {
					} catch (SQLException e) {	
					}
					
				} catch (IOException e) {
					try {
					dos.close();
					dis.close();
					user_socket.close();
					user_vc.remove(this);
					chat_area1.append(nickname+" ����\n");
					try {
						InsertDB insert = new InsertDB();
						insert.log_insert(nickname+" ����\n");
					} catch (ClassNotFoundException e1) {
					} catch (SQLException e2) {	
					}
					
					BroadCast("User_out/"+nickname);
					user_vc.remove(this);
					chat_area1.append("���� ���ӵ� ����� �� : " + (int)(user_vc.size())+"\n");
					break;
					}catch (IOException e1) {}//�޼��� ����
					}
			}
		}// run-end

		private void InMessage(String str) {
			st = new StringTokenizer(str, "/");//"whisper/"+nickname+"@"+textField.getText().trim()+"@"+user
			//         whisper/������@rudals!����
			String protocol = st.nextToken(); // whisper
			String message = st.nextToken(); // ������@rudals ! ����

			if (protocol.equals("Note")) {
				st = new StringTokenizer(message, "@");
				String user = st.nextToken();
				String note = st.nextToken();

				
				for (int i = 0; i < user_vc.size(); i++) {
					UserInfo u = (UserInfo) user_vc.elementAt(i);

					if (u.nickname.equals(user)) {
						u.send_Message("Note/" + nickname + "@" + note);
					}
				}
			} // Note_end
			else if (protocol.equals("Chatting")) { 
				BroadCast("Chatting/" + message); 
			}
			else if (protocol.equals("whisper")) { //whisper
				UniCast("whisper/"+message);
				
			}
			
		}

		private void UniCast(String str) {//"whisper/"+nickname+"@"+textField.getText().trim()+"@"+user
			
			st2 = new StringTokenizer(str,"to");
			
			
			String nick = st2.nextToken();//"whisper/"+nickname
			String get_ncik = st2.nextToken();//user
			
			
			for (int i = 0; i<user_vc.size();i++) {
			
				UserInfo u = (UserInfo) user_vc.elementAt(i);
				
				if(u.nickname.equals(get_ncik)) {
					u.send_Message(str);
					}
				if(u.nickname.equals(nickname)) {
					u.send_Message(str);
				}
			}
			
			
			
		}

		private void BroadCast(String str) {
			// ��ü ����ڿ��� �޼��� ������ ����
			for (int i = 0; i < user_vc.size(); i++) {
				UserInfo u = (UserInfo) user_vc.elementAt(i);

				u.send_Message(str);
			}
		}

		private void send_Message(String str) {
			try {
				dos.writeUTF(str); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}

	}//userinfo-end

	public static void main(String[] args) {
		new Server();
		
	}

}//server - end
