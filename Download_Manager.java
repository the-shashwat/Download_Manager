import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.util.*;
class DownloadManager extends JFrame implements Observer{
	private JTextField text;
	private JButton cancelButton,pauseButton,resumeButton,clearButton;
	private JTable table;
	private DownloadsTableModel tablemodel;
	private Download selectedDownload;
	private boolean clearing;
	public DownloadManager()
	{
		setTitle("Download Manager");
		setSize(640,480);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we)
			{
				actionExit();
			}
		});
		JMenuBar mb=new JMenuBar();
		JMenu file=new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		JMenuItem exit=new JMenuItem("Exit",KeyEvent.VK_E);
		file.add(exit);
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				actionExit();
			}
		});
		mb.add(file);
		setJMenuBar(mb);
		JPanel p1=new JPanel();
		text=new JTextField(30);
		JButton addButton=new JButton("Add Download");
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				actionAdd();
			}
		});
		p1.add(text);
		p1.add(addButton);
		JPanel p2=new JPanel();
		tablemodel=new DownloadsTableModel();
		table = new JTable(tablemodel);
		ProgressRenderer pr=new ProgressRenderer(0,100);
		pr.setStringPainted(true);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent le)
			{
				tableSelectionChanged();
			}
		});
		table.setDefaultRenderer(JProgressBar.class, pr);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowHeight((int)pr.getPreferredSize().getHeight());
		p2.setBorder(BorderFactory.createTitledBorder("Downloads"));
		p2.setLayout(new BorderLayout());
		p2.add(new JScrollPane(table),BorderLayout.CENTER);
		
		JPanel p3=new JPanel();
		cancelButton=new JButton("Cancel");
		pauseButton=new JButton("Pause");
		clearButton=new JButton("Clear");
		resumeButton=new JButton("Resume");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				actionCancel();
			}
		});
		pauseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				actionPause();
			}
		});
		clearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				actionClear();
			}
		});
		resumeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				actionResume();
			}
		});
		pauseButton.setEnabled(false);
		resumeButton.setEnabled(false);
		cancelButton.setEnabled(false);
		clearButton.setEnabled(false);
		p3.add(cancelButton);
		p3.add(pauseButton);
		p3.add(resumeButton);
		p3.add(clearButton);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(p1,BorderLayout.NORTH);
		getContentPane().add(p2,BorderLayout.CENTER);
		getContentPane().add(p3,BorderLayout.SOUTH);
	}
	private void actionResume()
	{
		selectedDownload.resume();
		updateButtons();
	}
	private void actionCancel()
	{
		selectedDownload.cancel();
		updateButtons();
	}
	private void actionPause()
	{
		selectedDownload.pause();
		updateButtons();
	}
	private void actionClear()
	{
		clearing=true;
		tablemodel.clearDownload(table.getSelectedRow());
		clearing=false;
		selectedDownload=null;
		updateButtons();
	}
	private void tableSelectionChanged()
	{
		if(selectedDownload!=null)
		{
			selectedDownload.deleteObserver(this);
		}
			if(!clearing && table.getSelectedRow()>-1)
			{
				selectedDownload=tablemodel.getDownload(table.getSelectedRow());
				selectedDownload.addObserver(this);
				updateButtons();
			}
		
	}
	private void updateButtons()
	{
		if(selectedDownload!=null)
		{
			int status=selectedDownload.getStatus();
			switch(status)
			{
				case Download.COMPLETE :
					cancelButton.setEnabled(false);
					pauseButton.setEnabled(false);
					clearButton.setEnabled(true);
					resumeButton.setEnabled(false);
					break;
				case Download.DOWNLOADING :
					cancelButton.setEnabled(true);
					pauseButton.setEnabled(true);
					clearButton.setEnabled(false);
					resumeButton.setEnabled(false);
					break;
				case Download.ERROR :
					cancelButton.setEnabled(false);
					pauseButton.setEnabled(false);
					clearButton.setEnabled(true);
					resumeButton.setEnabled(true);
					break;
				case Download.PAUSED :
					cancelButton.setEnabled(false);
					pauseButton.setEnabled(false);
					clearButton.setEnabled(true);
					resumeButton.setEnabled(true);
					break;
				default :
					cancelButton.setEnabled(false);
					pauseButton.setEnabled(false);
					clearButton.setEnabled(true);
					resumeButton.setEnabled(false);	
			}
		}
		else
		{
			cancelButton.setEnabled(false);
			pauseButton.setEnabled(false);
			clearButton.setEnabled(false);
			resumeButton.setEnabled(false);
		}
	}
	private URL verifyURL(String url)
	{
		URL vURL=null;
		if(!url.toLowerCase().startsWith("http://"))
			return null;
		else
		{
			try
			{
				vURL=new URL(url);
			}
			catch(Exception e)
			{
				return null;
			}
		}
		if(vURL.getFile().length()<2)
			return null;
		return vURL;
	}
	private void actionAdd()
	{
		String str=text.getText();
		URL u=verifyURL(str);
		if(u!=null)
		{
			tablemodel.addDownload(new Download(u));
			text.setText("");
		}
		else
		{
			JOptionPane.showMessageDialog(this,"Invalid download url","Error",JOptionPane.ERROR_MESSAGE);
		}	
	}
	private void actionExit()
	{
		System.exit(0);
	}
	public static void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				DownloadManager manager=new DownloadManager();
				manager.setVisible(true);
			}
		});
	}
	
	@Override
	public void update(Observable o, Object arg1) {
		if(selectedDownload!=null && selectedDownload.equals(o))
			updateButtons();
	}
	
}
