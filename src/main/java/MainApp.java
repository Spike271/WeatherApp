import java.awt.Font;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

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
				FlatLaf.registerCustomDefaultsSource("res.com.theme");
				FlatRobotoFont.install();
				UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.BOLD, 12));
				FlatMacLightLaf.setup();
				
				SwingUtilities.invokeLater(() -> new AppGui().setVisible(true));
				break;
			}
		}
	}
}