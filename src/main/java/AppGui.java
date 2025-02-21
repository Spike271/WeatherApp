import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

import com.formdev.flatlaf.FlatClientProperties;

import net.miginfocom.swing.MigLayout;

public class AppGui extends JFrame
{
	private static final long serialVersionUID = 1;
	private JSONObject weatherData;
	
	public AppGui()
	{
		super("Weather App");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(450, 650);
		this.setLocationRelativeTo(null);
		this.setLayout(new MigLayout("fillx, insets 20", "[][right]"));
		this.setResizable(false);
		this.setIconImage(loadImageIcon("res/img/cloudy.png").getImage());
		addGuiComponets();
	}
	
	private void addGuiComponets()
	{
		Font labelFontPlain = null;
		Font labelFontBold = null;
		Font txtboxFont = null;
		
		try
		{
			File FontFile = getFontFile("res/font/AdobeCleanHan-Bold-Str.otf");
			txtboxFont = Font.createFont(Font.TRUETYPE_FONT, FontFile).deriveFont(24f);
			
			FontFile = getFontFile("res/font/Dialog Bold.ttf");
			labelFontBold = Font.createFont(Font.PLAIN, FontFile).deriveFont(48f);
			
			FontFile = getFontFile("res/font/Dialog.ttf");
			labelFontPlain = Font.createFont(Font.TRUETYPE_FONT, FontFile).deriveFont(48f);
		}
		catch (Exception e)
		{}
		
		// Search box
		JTextField searchField = new JTextField();
		searchField.setFont(txtboxFont);
		searchField.putClientProperty(FlatClientProperties.STYLE, "focusWidth: 0;" + "arc: 0;");
		this.add(searchField, "span2, w 340, h 42, wrap");
		
		// Weather image
		JLabel weatherConditionImage = new JLabel(loadImageIcon("res/img/cloudy.png"));
		this.add(weatherConditionImage, "gapy 50, span 2, center, wrap");
		
		// temperature text
		JLabel temperatureText = new JLabel("10°C");
		temperatureText.setFont(labelFontBold);
		this.add(temperatureText, "span 2, center, wrap");
		
		// Weather condition description
		JLabel WeatherConditionDesc = new JLabel("Cloudy");
		WeatherConditionDesc.setFont(labelFontBold.deriveFont(32f));
		this.add(WeatherConditionDesc, "span 2, center, wrap");
		
		// humidity image
		JLabel humidityImage = new JLabel(loadImageIcon("res/img/humidity.png"));
		this.add(humidityImage, "gapy 50, h 74, w 66, split");
		
		// humidity text
		JLabel humidityText = new JLabel("<html><b>humidity</b><br/>100%</html>");
		humidityText.setFont(labelFontPlain.deriveFont(16f));
		this.add(humidityText, "gapx 0, gapy 50, split");
		
		// wind speed image
		JLabel windspeedImage = new JLabel(loadImageIcon("res/img/windspeed.png"));
		this.add(windspeedImage, "gapx 80, gapy 50, h 74, w 66, split");
		
		// wind speed text
		JLabel windspeedText = new JLabel("<html><b>Windspeed</b><br/>15Km/h</html>");
		windspeedText.setFont(labelFontPlain.deriveFont(16f));
		this.add(windspeedText, "gapx 15, gapy 50, wrap");
		
		// Search button
		JButton searchButton = new JButton(loadImageIcon("res/img/search.png"));
		searchButton.putClientProperty(FlatClientProperties.STYLE, "arc: 0;" + "focusWidth: 0;");
		searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				searchButton.setEnabled(false);
				new Thread(() -> {
					
					String userInput = searchField.getText();
					if (userInput.replaceAll("\\s", "").length() <= 0)
						return;
					
					// Data
					weatherData = (JSONObject) BackEnd.getWeatherData(userInput);
					
					String weatherCondition = (String) weatherData.get("weather_condition");
					
					switch (weatherCondition)
						{
						case "Clear":
							weatherConditionImage.setIcon(loadImageIcon("res/img/clear.png"));
							break;
						case "Cloudy":
							weatherConditionImage.setIcon(loadImageIcon("res/img/cloudy.png"));
							break;
						case "Rain":
							weatherConditionImage.setIcon(loadImageIcon("res/img/rain.png"));
							break;
						case "Snow":
							weatherConditionImage.setIcon(loadImageIcon("res/img/snow.png"));
							break;
						}
						
					WeatherConditionDesc.setText(weatherCondition);
					double temperature = (double) weatherData.get("temperature");
					temperatureText.setText(temperature + "°C");
					
					long humidity = (long) weatherData.get("humidity");
					humidityText.setText("<html><b>Humidity</b><br/>" + humidity + "%</html>");
					
					double windSpeed = (double) weatherData.get("windspeed");
					windspeedText.setText("<html><b>Windspeed</b><br/>" + windSpeed + "Km/h</html>");
					
					searchButton.setEnabled(true);
				}).start();
			}
		});
		
		this.add(searchButton, "gapx 10, cell 0 0, h 42");
	}
	
	private ImageIcon loadImageIcon(String resourcePath)
	{
		try
		{
			String path = AppGui.class.getResource(resourcePath).getFile();
			BufferedImage image = ImageIO.read(new File(path));
			return new ImageIcon(image);
		}
		catch (IOException e)
		{
			System.out.println("could not find the file");
		}
		
		return null;
	}
	
	private File getFontFile(String resource)
	{
		String filePath = "";
		try
		{
			filePath = AppGui.class.getResource(resource).getPath();
		}
		catch (Exception e)
		{
			System.out.println("Cannot find the font!");
		}
		
		if (filePath.contains("%20"))
			filePath = AppGui.class.getResource(resource).getPath().replaceAll("%20", " ");
		
		return new File(filePath);
	}
}