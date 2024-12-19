import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class MainApp
{
	public static void main(String[] args)
	{
		while (true)
		{
			if (BackEnd.checkInternet() != true)
			{
				int temp = JOptionPane.showOptionDialog(null,
						"You aren't connected to the Internet\nClick yes to try again", "Error 404",
						JOptionPane.OK_OPTION, 2, null, null, 1);
				
				if (temp != 0)
					break;
			}
			else
			{
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run()
					{
						new AppGui().setVisible(true);
					}
				});
				break;
			}
		}
	}
}