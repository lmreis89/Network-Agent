package visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Scanner;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import parser.ParseException;


import exception.NoSuchAgentException;

import util.Util;
import mail.AccountInfo;
import middleware.AgentPlatform;
import middleware.AgentPlatformImpl;
import middleware.ConfigManager;


public class PlatformGUI 
{ 

	private JFrame frame;
	private JTabbedPane tabbedPane;
	private JTextArea editor, gmailUser;
	private JPasswordField gmailPass;
	private JComboBox combo ;
	private JFileChooser filechoose;
	private String[] connTypes = {"SSL", "TLS"};
	private AgentPlatformImpl platform;
	private JTextArea console, agentID;
	private JPanel creation, settings,agentsCTable, agentsETable, platformsTable;
	private JPanel cAgents, eAgents, platforms;

	public void initGui() throws Exception
	{
		frame = new JFrame();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Agent Platform Client GUI");

		//JPanel do editor de texto
		JPanel creationPanel = new JPanel();
		creationPanel.setLayout(new BorderLayout());
		creationPanel.add( createTextArea(), BorderLayout.CENTER );
		creationPanel.add( createButtons(), BorderLayout.SOUTH );



		creation = creationPanel;
		settings = createSettingsPanel();
		agentsCTable = createcAgentsTable();
		agentsETable = createeAgentsTable();
		platformsTable = createPlatformsTable();

		//construcao das tabs
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Script Editor",creation );
		tabbedPane.addTab("Settings",settings);
		tabbedPane.addTab("Created Agents", agentsCTable);
		tabbedPane.addTab("Executing Agents", agentsETable);
		tabbedPane.addTab("Platform Nodes", platformsTable);

		frame.setJMenuBar(createMenuBar());
		frame.add(tabbedPane);
		frame.pack();   // assume the natural size!
		frame.setSize(900,600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}




	private JPanel createcAgentsTable() 
	{	

		Object[] result = platform.getCreatedAgents();
		Object[][] theData = (Object[][]) result[1];
		Object[] names = (Object[]) result[0];
		DefaultTableModel dtm = new DefaultTableModel(theData, names);
		cTable =new ListTable(dtm); 
		TableColumnModel model = cTable.getColumnModel();
		for(int i = 0; i< model.getColumnCount(); i++)
			model.getColumn(i).setMinWidth(100);
		JPanel pnl = new JPanel();
		//    	pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
		JPanel input = new JPanel();
		Border blackline = BorderFactory.createLineBorder(Color.black);
		TitledBorder title = BorderFactory.createTitledBorder(blackline, "Request Report");
		title.setTitleFont(new Font("Helvetica",Font.BOLD,15));
		title.setTitleJustification(TitledBorder.LEFT);

		agentID = new JTextArea("insert agent id to track",1,30);
		agentID.setBorder(title);
		JButton submit = new JButton("Ask For Report");
		submit.addActionListener( new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				System.out.println("Tracking agent <aID>: "+agentID.getText());
				launchReportFrame(agentID.getText());

			}
		});

		input.add(new JPanel());
		input.add(agentID);
		input.add(new JPanel());
		input.add(submit);
		input.add(new JPanel());
		input.setBorder(new EmptyBorder(15,15,15,15));

		cAgents = new JPanel();
		cAgents.add(cTable);
		JPanel masterPanel = new JPanel();
		JButton button = new JButton("Refresh");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{

				Object[] result = platform.getCreatedAgents();
				Object[][] theData = (Object[][]) result[1];
				Object[] names = (Object[]) result[0];
				DefaultTableModel dtm = new DefaultTableModel(theData, names);
				cTable.setModel(dtm) ;
				TableColumnModel model = cTable.getColumnModel();
				for(int i = 0; i< model.getColumnCount(); i++)
					model.getColumn(i).setMinWidth(100);



			}
		});
		masterPanel.add(cAgents);
		masterPanel.add(button);
		pnl.add(masterPanel);
		pnl.add(input);

		return pnl;
	}

	private JPanel createeAgentsTable() {	
		Object[] result = platform.getExecutingAgents();
		Object[][] theData = (Object[][]) result[1];
		Object[] names = (Object[]) result[0];
		DefaultTableModel dtm = new DefaultTableModel(theData, names);
		eTable =new ListTable(dtm); 
		TableColumnModel model = eTable.getColumnModel();
		for(int i = 0; i< model.getColumnCount(); i++)
			model.getColumn(i).setMinWidth(100);
		eAgents = new JPanel();
		eAgents.add(eTable);
		JPanel masterPanel = new JPanel();
		JButton button = new JButton("Refresh");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{

				Object[] result = platform.getExecutingAgents();
				Object[][] theData = (Object[][]) result[1];
				Object[] names = (Object[]) result[0];
				DefaultTableModel dtm = new DefaultTableModel(theData, names);

				eTable.setModel(dtm) ;
				TableColumnModel model = eTable.getColumnModel();
				for(int i = 0; i< model.getColumnCount(); i++)
					model.getColumn(i).setMinWidth(100);

			}
		});
		masterPanel.add(eAgents);
		masterPanel.add(button);

		return masterPanel;
	}
	JTable pTable, cTable, eTable;
	private JPanel createPlatformsTable() {	
		Object[] result = platform.getPlatforms();
		Object[][] theData = (Object[][]) result[1];
		Object[] names = (Object[]) result[0];
		DefaultTableModel dtm = new DefaultTableModel(theData, names);
		pTable =new ListTable(dtm); 
		pTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnModel model = pTable.getColumnModel();
		for(int i = 0; i< model.getColumnCount(); i++)
			model.getColumn(i).setMinWidth(150);
		platforms = new JPanel();
		platforms.add(pTable);
		JPanel masterPanel = new JPanel();
		JButton button = new JButton("Refresh");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{


				Object[] result = platform.getPlatforms();

				Object[][] theData = (Object[][]) result[1];
				Object[] names = (Object[]) result[0];
				DefaultTableModel dtm = new DefaultTableModel(theData, names);
				pTable.setModel(dtm) ;
				TableColumnModel model = pTable.getColumnModel();
				for(int i = 0; i< model.getColumnCount(); i++)
					model.getColumn(i).setMinWidth(150);

			}
		});
		masterPanel.add(platforms);
		masterPanel.add(button);

		return masterPanel;
	}


	private JPanel createSettingsPanel() 
	{	
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

		//top panel
		JPanel firstSubPanel = new JPanel();
		firstSubPanel.setLayout(new GridLayout(3,1));
		firstSubPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		JLabel label1 = new JLabel("TEXT1"), label2 = new JLabel("TEXT2"), label3 = new JLabel("TEXT3");
		label1.setText("The agents use a google email service to send email messages.");
		label1.setFont(new Font("Helvetica",Font.ITALIC,15));
		label2.setText(	"There is already a defaut email account set for this platform.");
		label2.setFont(new Font("Helvetica",Font.ITALIC,15));
		label3.setText(
				"If you wish to change this account, fill in your gmail information in the " +
				"form below and press submit");
		label3.setFont(new Font("Helvetica",Font.ITALIC,15));

		firstSubPanel.add(label1);
		firstSubPanel.add(label2);
		firstSubPanel.add(label3);


		//middle panel
		JPanel secondSubPanel = new JPanel();
		Border blackline = BorderFactory.createLineBorder(Color.black);
		TitledBorder title = BorderFactory.createTitledBorder(blackline, "Agent e-Mail Account Information");
		title.setTitleFont(new Font("Helvetica",Font.BOLD,15));
		title.setTitleJustification(TitledBorder.LEFT);
		secondSubPanel.setLayout(new GridLayout(2,3));
		secondSubPanel.setBorder(title);

		gmailUser = new JTextArea("",1,30);
		gmailUser.setFont(new Font("Verdana", Font.BOLD, 14));
		gmailUser.setWrapStyleWord(true);
		JPanel qwe = new JPanel();
		qwe.setLayout(new BoxLayout(qwe, BoxLayout.X_AXIS));
		JLabel lab = new JLabel("TEXT3");lab.setText("Username: ");
		qwe.add(lab);
		qwe.add(gmailUser);

		gmailPass = new JPasswordField("");
		JPanel asd = new JPanel();
		asd.setLayout(new BoxLayout(asd, BoxLayout.X_AXIS));
		JLabel lab2 = new JLabel("TEXT4");lab2.setText("Password: ");
		asd.add(lab2);
		asd.add(gmailPass);

		combo = new JComboBox();
		for(int i = 0; i< connTypes.length ; i++)
			combo.addItem(connTypes [i]);

		JPanel zxc = new JPanel();
		zxc.setLayout(new BoxLayout(zxc, BoxLayout.X_AXIS));
		JLabel lab3 = new JLabel("TEXT5");lab3.setText("Security: ");
		zxc.add(lab3);
		zxc.add(combo);



		secondSubPanel.add(qwe);
		secondSubPanel.add(new JPanel());
		secondSubPanel.add(asd);
		secondSubPanel.add(zxc);
		secondSubPanel.add(new JPanel());
		JButton submit = new JButton("Submit");
		submit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String user = gmailUser.getText();
				String pass = new String( gmailPass.getPassword() );
				boolean isSSL = connTypes[combo.getSelectedIndex()].equals("SSL");

				platform.setMailAccount(new AccountInfo(user, pass, isSSL));

			}

		});
		secondSubPanel.add(submit);

		//Bottom Panel
		JPanel thirdSubPanel = new JPanel();
		thirdSubPanel.setLayout(new GridLayout(3,1));
		thirdSubPanel.add(new JTextArea(20,20));
		thirdSubPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		//TODO

		panel.add(firstSubPanel);
		panel.add(secondSubPanel);
		panel.add(thirdSubPanel);
		return panel;
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar bar = new JMenuBar();
		bar.add(createFileMenu());
		bar.add(createMenuHelp());

		return bar;
	}

	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add(createItemFile("New"));
		menu.add(new JSeparator());
		menu.add(createItemFile("Exit"));
		return menu;
	}

	private JMenuItem createItemFile(String text)
	{
		JMenuItem item = new JMenuItem(text);

		class ListenerItemMenu implements ActionListener
		{

			public void actionPerformed(ActionEvent e) {
				try
				{
					if(e.getActionCommand().equals("New"))
					{
						editor.setText("");
						Scanner in = new Scanner(new FileInputStream(new File("src/startCode.code")));
						while(in.hasNext())
							editor.append(in.nextLine()+"\n");
						editor.setCaretPosition( editor.getText().length() );

					}
					else if(e.getActionCommand().equals("Exit"))
						System.exit(0);
				}catch(Exception ex)
				{

				}
			}	
		}

		item.addActionListener(new ListenerItemMenu());
		return item;	
	}


	private JMenuItem createItemMenuHelp(String text) 
	{
		JMenuItem item = new JMenuItem(text);

		class ListenerItemMenu implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				if(e.getActionCommand().equals("About"))
					showDialog(ABOUT_MESSAGE);
			}
		}

		item.addActionListener(new ListenerItemMenu());
		return item;
	}
	private JMenu createMenuHelp()
	{
		JMenu menu = new JMenu("Help");		

		menu.add(createItemMenuHelp("About"));
		return menu;
	}

	private Component createTextArea() throws Exception
	{
		try{
			editor = new JTextArea("",35,80);

			// very important next 2 lines
			editor.setLineWrap(true);
			editor.setWrapStyleWord(true);

			//fill with start code
			Scanner in = new Scanner(new FileInputStream(new File("src/startCode.code")));
			while(in.hasNext())
				editor.append(in.nextLine()+"\n");
			editor.setCaretPosition( editor.getText().length() );

			// add it to a scrollpane
			JScrollPane jsp =new JScrollPane(editor);
			jsp.setBorder(new EmptyBorder(10, 10, 10, 10) );
			return jsp;
		}
		catch(Exception e)
		{
			System.out.println("missing start code file at src/startCode.code !!!! ");
			throw e;
		}
	}


	private JPanel createButtons()
	{
		JPanel butPanel = new JPanel();
		butPanel.setLayout(new GridLayout(1,7));
		butPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		JButton compile = new JButton("Verify"), exit = new JButton("Exit")
		,importScript=new JButton("Import File"), execute = new JButton("Execute");


		class ButtonListener implements ActionListener
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(e.getActionCommand().equals("Verify"))
				{

					try{
						platform.checkCode(getTextAreaStream());
					}catch(Exception ex)
					{
						showDialog("There are errors in the code");
						return;
					}
					showDialog("The code is syntactically correct");



				}
				else if(e.getActionCommand().equals("Exit"))
				{

					System.exit(0);
				}			
				else if(e.getActionCommand().equals("Execute"))
				{

					try {
						launchConsoleFrame();
					} catch (SocketException e1) {
						showDialog("Something went wrong!!");
					} catch (ParseException e1) {
						showDialog("The code is not syntactically correct. Parser Error:\n"+e1.getMessage());
					}
				}
				else // "Import File"
				{
					try
					{

						filechoose = new JFileChooser("/");
						int val = filechoose.showOpenDialog(frame);
						if (val == JFileChooser.APPROVE_OPTION) {
							File file = filechoose.getSelectedFile();
							//This is where a real application would open the file.
							editor.setText("");

							Scanner in = new Scanner(new FileInputStream(file));
							while(in.hasNext())
								editor.append(in.nextLine()+"\n");
							editor.setCaretPosition( editor.getText().length() );
						} else {

						}
					}
					catch(FileNotFoundException ex)
					{
						showDialog("this dialog will never be seen");
					}

				}
			}

		}

		compile.addActionListener(new ButtonListener());
		exit.addActionListener(new ButtonListener());
		importScript.addActionListener(new ButtonListener());
		execute.addActionListener(new ButtonListener());
		butPanel.add(compile);
		butPanel.add(new JPanel());
		butPanel.add(importScript);
		butPanel.add(new JPanel());
		butPanel.add(execute);
		butPanel.add(new JPanel());
		butPanel.add(exit);

		return butPanel;
	}

	public InputStream getTextAreaStream() {
		return new ByteArrayInputStream(editor.getText().getBytes());

	}

	private void showDialog(String message)
	{
		JOptionPane.showMessageDialog(null, message);
	}

	private void launchConsoleFrame() throws SocketException, ParseException 
	{	
		JFrame f = new JFrame("User Console");
		f.setSize(frame.getSize());
		JPanel p = new JPanel();

		platform.parseAgent(getTextAreaStream());
		console.setText("");
		console.append("Console init ok....\n");
		JScrollPane jsp =new JScrollPane(console);
		p.add(jsp);
		f.add(p);

		f.setVisible(true);

	} 

	private void launchReportFrame(String aID) 
	{	
		JFrame f = new JFrame("Tracking agent "+ aID);
		f.setSize(frame.getSize());
		JPanel p = new JPanel();
		String out="platform exception";
		try {
			out = platform.getAgentReport(aID).toString();

		} catch (Exception e) {
			try {
				out = platform.getRemoteAgentReport(aID).toString();
			} catch (SocketException e1) {
				showDialog("Connection Problem");
				return;
			} catch (NoSuchAgentException e1) {
				showDialog("The agent does not exist in any of the platforms");
			}
		} 
		p.add(new JTextArea(out));

		f.add(p);
		f.setVisible(true);

	} 

	public static void main(String[] args) throws SocketException, UnknownHostException 
	{
		// Swing GUIs should be created and altered on the EDT.

		System.setProperty("java.security.policy", "policy.all");
		System.setProperty("java.rmi.server.hostname", Util.getRealAddress());
		System.setProperty("javax.net.ssl.keyStore","certificate/platform.key");
		System.setProperty("javax.net.ssl.keyStorePassword","password");
		System.setProperty("javax.net.ssl.trustStore","certificate/platformtrust.key");
		System.setProperty("javax.net.ssl.trustStorePassword","password");
		
		if( System.getSecurityManager() == null) {
			System.setSecurityManager( new RMISecurityManager());
		}
		
		ConfigManager man = ConfigManager.getInstance();
		man.runConfigurations(new Scanner(System.in));


		JTextArea consoleArea = new JTextArea();
		TextAreaPrintStream out = new TextAreaPrintStream(consoleArea, System.out);
		Map<String, String> configs = man.getConfigs();
		PlatformGUI pg;
		try {
			AgentPlatformImpl plat = new AgentPlatformImpl(configs.get("name"),
					configs.get("peerHost"), configs.get("peerName"),configs.get("codeBase"),  out);

			pg = new PlatformGUI(consoleArea, plat);
			pg.initGui();

			try {
				if(configs.get("security").charAt(0) == 'y')
				{
					Registry r = LocateRegistry.createRegistry(3001, new SslRMIClientSocketFactory(),
							new SslRMIServerSocketFactory(null, null, true));

					AgentPlatform platform = (AgentPlatform) UnicastRemoteObject.exportObject(plat, 3001,  new SslRMIClientSocketFactory(), 
							new SslRMIServerSocketFactory(null, null, true));

					r.rebind(configs.get("name"), platform);
				}
				else
				{
					Registry r = LocateRegistry.createRegistry(3001);
					AgentPlatform platform = (AgentPlatform) UnicastRemoteObject.exportObject(plat,3001);
					
					r.rebind(configs.get("name"), platform);
				}

			} catch(RemoteException e)
			{
				System.err.println("Registry already created.\n");
				if(configs.get("security").charAt(0) == 'y')
				{
					Registry r = LocateRegistry.getRegistry(3001);

					AgentPlatform platform = (AgentPlatform) UnicastRemoteObject.exportObject(plat, 3001,  new SslRMIClientSocketFactory(), 
							new SslRMIServerSocketFactory(null, null, true));

					r.rebind(configs.get("name"), platform);
				}
				else
				{
					Registry r = LocateRegistry.getRegistry(3001);
					AgentPlatform platform = (AgentPlatform) UnicastRemoteObject.exportObject(plat,3001);
					
					r.rebind(configs.get("name"), platform);
				}
			}


		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	public PlatformGUI(JTextArea consoleArea, AgentPlatformImpl ap ) 
	{
		this.platform = ap;
		this.console = consoleArea;
	}

	private static String ABOUT_MESSAGE = "Agent Platform Client GUI\n\n" +
			"Developed by:\n"+
			"Pedro Amaral\nLuis Reis\n\n"+
			"Middleware Systems and Technologies,\n"+
			"Mestrado em Engenharia Informática,\n"+
			"Departamento de Informática,\n"+
			"Faculdade de Ciencias e Tecnologia,\n"+
			"Universidade Nova de Lisboa";

}
