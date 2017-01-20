import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;
class ProgressRenderer extends JProgressBar implements TableCellRenderer 
{
	ProgressRenderer(int min,int max)
	{
		super(min,max);
	}
	public Component getTableCellRendererComponent(JTable table, Object arg1, boolean arg2, boolean arg3, int arg4,
			int arg5) {
		setValue((int)((Float)arg1).floatValue());
		return this;
	}
}
