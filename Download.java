import java.util.*;
import java.io.*;
import java.net.*;
class Download extends Observable implements Runnable
{
	private static final int MAX_BUFFER=1024;
	public static final String STATUSES[]={"Downloading","Paused","Complete","Cancelled","Error"};
	public static final int DOWNLOADING=0;
	public static final int PAUSED=1;
	public static final int COMPLETE=2;
	public static final int CANCELLED=3;
	public static final int ERROR=4;
	private URL url;
	private int size;
	private int downloaded;
	private int status;
	public Download(URL url)
	{
		this.url=url;
		size=-1;
		downloaded=0;
		status=DOWNLOADING;
	}
	public String getURL()
	{
		return url.toString();
	}
	public int getSize()
	{
		return size;
	}
	public float getProgress()
	{
		return ((float)downloaded/size)*100;
	}
	public int getStatus()
	{
		return status;
	}
	public void resume()
	{
		status=DOWNLOADING;
		stateChanged();
		download();
	}
	public void pause()
	{
		status=PAUSED;
		stateChanged();
	}
	private void error()
	{
		status=ERROR;
		stateChanged();
	}
	public void cancel()
	{
		status=CANCELLED;
		stateChanged();
	}
	private void download()
	{
		Thread t=new Thread(this);
		t.start();
	}
	private String getFileName(URL url)
	{
		String filename=url.getFile();
		return filename.substring(filename.lastIndexOf('/')+1);
	}
	public void run()
	{
		RandomAccessFile file = null;
		InputStream stream = null;
		try
		{
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setRequestProperty("Range",downloaded+"-");
			connection.connect();
			if(connection.getResponseCode()/100!=2)
				error();
			int length=connection.getContentLength();
			if(length<1)
				error();
			if(size!=-1)
			size=length;
			stream=connection.getInputStream();
			file=new RandomAccessFile(getFileName(url),"rw");
			file.seek(downloaded);
			while(status==DOWNLOADING)
			{
				byte buffer[];
				if(size-downloaded>=MAX_BUFFER)
					buffer=new byte[MAX_BUFFER];
				else
					buffer=new byte[size-downloaded];
				int read=stream.read(buffer);
				if(read==-1)
					break;
				file.write(buffer,0,read);
				downloaded+=read;
				stateChanged();
			}
			if(status==DOWNLOADING)
			{
				status=COMPLETE;
				stateChanged();
			}
			
		}
		catch(Exception e)
		{
			error();
		}
		finally
		{
			if(stream!=null)
			{
				try
				{
					stream.close();
				}
				catch(Exception e)
				{
				}
			}
			if(file!=null)
			{
				try
				{
					file.close();
				}
				catch(Exception e)
				{
				}
			}
		}
	}
	public void stateChanged()
	{
		setChanged();
		notifyObservers();
		
	}
}
