package project_chatting;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Client extends JFrame implements ActionListener {

	
	
	//gui �ҷ���(2)
	// Join GUI ����
	private JFrame Join_GUI = new JFrame();
	private JPanel Join_Pane;
	private JTextField nickname_txt;
	private JButton enter_btn = new JButton("�����ϱ�");
	private String user;

//	
//	wrb.setBounds(563, 540, 105, 23);
//	contentPane.add(wrb);
	
	
	// Client GUI ����
	private JPanel contentPane;
	private JTextField textField;
	private JTextArea chat_area = new JTextArea(); // ä��â
	private JButton send_btn = new JButton("");
	private JButton exit_btn = new JButton("������");
	//private JButton noteSend_btn = new JButton("����������");
	private JRadioButton wrb = new JRadioButton("�ӼӸ�");
	private JRadioButton rdbtnNewRadioButton= new JRadioButton("�Ϲ� ��ȭ");
	private ButtonGroup group = new ButtonGroup();
	private JList User_list = new JList(); // ���������� ����Ʈ
//���� �ҷ���(3)
	// network ����
	private Socket socket;
	private String ip = "127.0.0.1";
	private String nickname = "";
	private int port = 7770;
	private OutputStream os;
	private InputStream is;
	private DataInputStream dis;
	private DataOutputStream dos;

	// �׿� ������
	Vector user_list = new Vector();
	StringTokenizer st;
	StringTokenizer st2;
	Client() {// ������ �޼ҵ� //������ ����(4)
		Join_init();// Join ȭ��â ���(5) 152
		start();
	}

	private void start() {
		enter_btn.addActionListener(this);
		send_btn.addActionListener(this);
		exit_btn.addActionListener(this);
		wrb.addActionListener(this);
		rdbtnNewRadioButton.addActionListener(this);
		//noteSend_btn.addActionListener(this);
		
	}

	private void Main_init() {//182
		setTitle("��ȭâ");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 700);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 0, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		chat_area.setEditable(false);
		chat_area.setBounds(14, 30, 535, 533);
		chat_area.setBorder(new LineBorder(new Color(255, 69, 0)));
		chat_area.setCaretPosition(chat_area.getDocument().getLength());
		chat_area.setFont(new Font("���� ���", Font.PLAIN, 17));
		chat_area.setLineWrap(true);
		
		DefaultCaret caret = (DefaultCaret)chat_area.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane scrollPane2 = new JScrollPane(chat_area);
		scrollPane2.setBounds(14, 30, 535, 533);
		scrollPane2.setBorder(new LineBorder(new Color(255, 69, 0)));
		contentPane.add(scrollPane2);

		send_btn.setIcon(new ImageIcon(Client.class.getResource("/image/enter.png")));
		send_btn.setForeground(new Color(255, 255, 255));
		send_btn.setBounds(563, 575, 105, 66);
		send_btn.setBackground(new Color(255, 69, 0));
		send_btn.setFont(new Font("���� ���", Font.BOLD, 20));
		contentPane.add(send_btn);

		textField = new JTextField();
		textField.setBounds(14, 575, 654, 66);
		
		contentPane.add(textField);
		textField.setColumns(10);
		textField.setBorder(new LineBorder(new Color(255, 69, 0)));
		textField.setFont(new Font("���� ���", Font.PLAIN, 17));
		textField.addKeyListener(new KeyAdapter() {
			@Override
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {//����Ű �̺�Ʈ
					if (textField.getText().length() != 0 && rdbtnNewRadioButton.isSelected()) {
						send_massage("Chatting/"+nickname+"@"+textField.getText().trim());
						textField.setText("");
					}	
					else if(textField.getText().length() != 0 && wrb.isSelected()) {
						
						user = (String) User_list.getSelectedValue();
						
						if(nickname.equals(User_list.getSelectedValue())) {
							JOptionPane.showMessageDialog(null,"���� �г��ӿ��� �ӼӸ� �Ҽ� �����ϴ�");
							textField.setText("");
						}
						else if(!nickname.equals(User_list.getSelectedValue()) && textField.getText().length() != 0) {
							send_massage("whisper/"+nickname+"@"+textField.getText().trim()+"to"+user);
							textField.setText("");
						}
					
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(563, 76, 105, 426);
		contentPane.add(scrollPane);

		exit_btn.setForeground(new Color(255, 255, 255));
		exit_btn.setFont(new Font("���� ���", Font.BOLD, 20));
		exit_btn.setBackground(new Color(255, 69, 0));
		exit_btn.setBounds(563, 30, 105, 34);
		contentPane.add(exit_btn);

		//noteSend_btn.setFont(new Font("���� ���", Font.BOLD, 14));
	//	noteSend_btn.setBackground(new Color(255, 69, 0));
	//	noteSend_btn.setBounds(563, 501, 105, 62);
	//	noteSend_btn.setForeground(new Color(255, 255, 255));
//		contentPane.add(noteSend_btn);
		
		wrb.setFont(new Font("���� ���", Font.BOLD, 14));
		wrb.setBackground(new Color(255, 69, 0));
		wrb.setBounds(563, 508, 105, 23);
		wrb.setForeground(new Color(255, 255, 255));
		contentPane.add(wrb);

		
		rdbtnNewRadioButton.setFont(new Font("���� ���", Font.BOLD, 14));
		rdbtnNewRadioButton.setBackground(new Color(255, 69, 0));
		rdbtnNewRadioButton.setBounds(563, 540, 105, 23);
		rdbtnNewRadioButton.setForeground(new Color(255, 255, 255));
		contentPane.add(rdbtnNewRadioButton);
		
		
		group.add(rdbtnNewRadioButton);
		group.add(wrb);
		
		rdbtnNewRadioButton.setSelected(true);
		
		scrollPane.setViewportView(User_list);
		User_list.setBounds(563, 76, 105, 413);
		User_list.setFont(new Font("���� ���", Font.BOLD, 20));
		User_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.setVisible(true);
	}

	private void Join_init() {//(6) 69
//gui
		Join_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Join_GUI.setBounds(100, 100, 700, 700);
		Join_GUI.setTitle("�г��� �Է� â");
		Join_Pane = new JPanel();
		Join_Pane.setBackground(Color.BLACK);
		Join_Pane.setBorder(new EmptyBorder(5, 5, 5, 5));
		Join_GUI.setContentPane(Join_Pane);
		Join_Pane.setLayout(null);

		enter_btn.setForeground(Color.WHITE);
		JLabel mainLabel = new JLabel("");
		mainLabel.setIcon(new ImageIcon(Client.class.getResource("/image/icon3.png")));
		mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainLabel.setFont(new Font("���� ���", Font.BOLD, 40));
		mainLabel.setBounds(223, 141, 241, 249);
		Join_Pane.add(mainLabel);

		nickname_txt = new JTextField();
		nickname_txt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {//�г��� textf//(7)
				if(e.getKeyCode() == 10) {
					if (nickname_txt.getText().length() < 1 || nickname_txt.getText().length() > 8) {
						JOptionPane.showMessageDialog(null, "�г����� 1~8���ڸ� ����");
					} else {
						nickname = nickname_txt.getText(); // �г��� �ؽ�Ʈ�� �г��ӿ� ����
						Network();//��Ʈ��ũ ����(7)211
						Join_GUI.dispose();//������
						Main_init();//82 main client ȭ��â ���(8)
					}
					
				}
			}
		});
		nickname_txt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {//���콺 ������ �ؽ�Ʈ�ʵ� ���� ����
				nickname_txt.setText("");
			}
		});
		nickname_txt.setForeground(UIManager.getColor("Button.shadow"));
		nickname_txt.setHorizontalAlignment(SwingConstants.CENTER);
		nickname_txt.setText("�г��� �Է�");
		nickname_txt.setFont(new Font("���� ���", Font.BOLD, 20));
		nickname_txt.setBounds(263, 437, 165, 66);
		Join_Pane.add(nickname_txt);
		nickname_txt.setColumns(10);
		nickname_txt.setBorder(new LineBorder(new Color(0, 0, 0), 5, true));

		enter_btn.setFont(new Font("���� ���", Font.BOLD, 22));
		enter_btn.setBackground(new Color(255, 69, 0));
		enter_btn.setBounds(263, 516, 165, 57);
		Join_Pane.add(enter_btn);

		Join_GUI.setVisible(true);
	}//join - end

	private void Network() {//180

		try {
			socket = new Socket(ip, port);

			if (socket != null) {
				Connection();
			}
			// ���������ȵȻ��¿��� ����������� 
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(null, "�����������", "Error!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "�����������", "Error!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	private void Connection() {// �޼��� ����κ�
		try {
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {// stream���� ����
			JOptionPane.showMessageDialog(null, "�������", "Error!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} // Stream ���� ��

		send_massage(nickname); // ù ���ӽ� ID ����

		// User_list�� ����� �߰�
		user_list.add(nickname);//Vector
		User_list.setListData(user_list);//���� ����Ʈ�� �߰�

		Thread th = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						String msg = dis.readUTF();
						inmessage(msg);

					} catch (IOException e) {
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							break;
						} catch (IOException e1) {

						}

					}

				}
			}
		});
		th.start();	
	}

	private void inmessage(String str) {// �����κ��� ������ ��� �޼���
		st = new StringTokenizer(str, "/"); //"Chatting/"+ dd@dd

		String protocol = st.nextToken();//Chatting
		String Message = st.nextToken();//dd@dd

		if (protocol.equals("NewUser")) {// ���ο� ������
			user_list.add(Message);
			User_list.setListData(user_list);
			chat_area.append(Message + " ���� �����ϼ̽��ϴ�\n");
			
		} else if (protocol.equals("OldUser")) {
			user_list.add(Message);
			User_list.setListData(user_list);
			
		} else if (protocol.equals("Note")) {
			st = new StringTokenizer(Message, "@");
			String user = st.nextToken();
			String note = st.nextToken();
			JOptionPane.showMessageDialog(null, note, user + " �� ���κ��� ����", JOptionPane.CLOSED_OPTION);
			
		} else if (protocol.equals("Chatting")) {//Chatting
			st = new StringTokenizer(Message, "@");//dd@dd
			String nick = st.nextToken();//dd(�г���)
			String msg = st.nextToken();//dd(����)
			chat_area.append(nick + " : " + msg + "\n");
			
			//=>dd(�г���) : dd(����)
		}
		else if(protocol.equals("whisper")) {//"whisper/"+nickname+"@"+textField.getText().trim()!user
			st = new StringTokenizer(Message,"@");
			String nick = st.nextToken();
			String msg = st.nextToken();
			
			chat_area.append(nick+"���� �ӼӸ�: "+msg+"\n");
		}
		else if (protocol.equals("User_out")) {
			chat_area.append(Message + " ���� �����ϼ̽��ϴ�\n");
			user_list.remove(Message);
			User_list.setListData(user_list);

		}
	}

	private void send_massage(String str) { // �������� �޼��������� �޼���
		try {	
			dos.writeUTF(str);//dos�� ���� ���  DataOutStram
		} catch (IOException e) {
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == enter_btn) {
			if (nickname_txt.getText().length() < 1 || nickname_txt.getText().length() > 8) {
				JOptionPane.showMessageDialog(null, "�г����� 1~8���ڸ� ����");
			} else {
				nickname = nickname_txt.getText(); // �г��� �޾ƿ���
				Network();
				Join_GUI.dispose();
				Main_init();// main client ȭ��â ���
				chat_area.append(nickname_txt.getText() + " ���� �����ϼ̽��ϴ�\n");
			}
		} else if (e.getSource() == send_btn  ) {
			if (textField.getText().length() != 0 && rdbtnNewRadioButton.isSelected()) {
				send_massage("Chatting/" + nickname + "@" + textField.getText().trim());
				textField.setText("");
			}	
			else if(textField.getText().length() != 0 && wrb.isSelected()) {
				
				user = (String) User_list.getSelectedValue();
				
				if(nickname.equals(User_list.getSelectedValue())) {
					JOptionPane.showMessageDialog(null,"���� �г��ӿ��� �ӼӸ� �Ҽ� �����ϴ�");
					textField.setText("");
				}
				else if(!nickname.equals(User_list.getSelectedValue()) && textField.getText().length() != 0) {
					send_massage("whisper/"+nickname+"@"+textField.getText().trim()+" to "+user);
					textField.setText("");
				}
			
			}
			
		}
		
		else if (e.getSource() == exit_btn) {
			try {
				dos.close();
				dis.close();
				socket.close();
				this.dispose();
			} catch (Exception e1) {
			}

		}
		
		

	}

	public static void main(String[] args) {
		new Client();//�������� Ŭ���̾�Ʈ ��ư ������ ����(1)
	}

	

}