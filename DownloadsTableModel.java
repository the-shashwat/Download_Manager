import java.util.*;
import javax.swing.table.*;
import javax.swing.*;
class DownloadsTableModel extends AbstractTableModel implements Observer 
{
	private static final String colheads[]={"URL","Size","Progress","Status"};
	private static final Class colclass[]={String.class,String.class,JProgressBar.class,String.class};
	private ArrayList<Download> list=new ArrayList<Download>(); 
	public void addDownload(Download d)
	{
		d.addObserver(this);
		list.add(d);
		fireTableRowsInserted(getRowCount()-1,getRowCount()-1);
	}
	public void clearDownload(int index)
	{
		list.remove(index);
		fireTableRowsDeleted(index,index);
	}
	public int getColumnCount() {
		
		return colheads.length;
	}
	public int getRowCount() {
		
		return list.size();
	}
	public String getColumnName(int col)
	{
		return colheads[col];
	}
	public Class<?> getColumnClass(int col)
	{
		return colclass[col];
	}
	public Object getValueAt(int row, int col) {
		
		Download d=list.get(row);
		switch(col)
		{
			case 0 : 
				return d.getURL();
			
			case 1 : 
				int s=d.getSize();
				if(s!=-1)
					return Integer.toString(s);
				else
					return "";
				
			case 2:
				return (new Float(d.getProgress()));
				
			case 3:
				return Download.STATUSES[d.getStatus()];
		}
		return "";
	}
	public void update(Observable o, Object arg1) {
		int index=list.indexOf(o);
		fireTableRowsUpdated(index,index);
	}
	
}
