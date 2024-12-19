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
import javax.swing.SwingConstants;

import org.json.simple.JSONObject;

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
		this.setLayout(null);
		this.setResizable(false);
		this.setIconImage(new ImageIcon("resources/img/cloudy.png").getImage());
		addGuiComponets();
	}
	
	private void addGuiComponets()
	{
		Font labelFontPlain = null;
		Font labelFontBold = null;
		Font txtboxFont = null;
		
		try
		{
			File FontFile = getFontFile("resources/font/AdobeCleanHan-Bold-Str.otf");
			txtboxFont = Font.createFont(Font.TRUETYPE_FONT, FontFile).deriveFont(24f);
			
			FontFile = getFontFile("resources/font/Dialog Bold.ttf");
			labelFontBold = Font.createFont(Font.PLAIN, FontFile).deriveFont(48f);
			
			FontFile = getFontFile("resources/font/Dialog.ttf");
			labelFontPlain = Font.createFont(Font.TRUETYPE_FONT, FontFile).deriveFont(48f);
		}
		catch (Exception e)
		{}
		
		// Search box
		JTextField searchField = new JTextField();
		searchField.setBounds(15, 15, 351, 45);
		searchField.setFont(txtboxFont);
		this.add(searchField);
		
		// Weather image
		JLabel weatherConditionImage = new JLabel(loadImageIcon("resources/img/cloudy.png"));
		weatherConditionImage.setBounds(0, 125, 450, 217);
		this.add(weatherConditionImage);
		
		// temperature text
		JLabel temperatureText = new JLabel("10°C");
		temperatureText.setBounds(0, 350, 450, 54);
		temperatureText.setFont(labelFontBold);
		temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(temperatureText);
		
		// Weather condition description
		JLabel WeatherConditionDesc = new JLabel("Cloudy");
		WeatherConditionDesc.setBounds(0, 405, 450, 36);
		WeatherConditionDesc.setFont(labelFontBold.deriveFont(32f));
		WeatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(WeatherConditionDesc);
		
		// humidity image
		JLabel humidityImage = new JLabel(loadImageIcon("resources/img/humidity.png"));
		humidityImage.setBounds(15, 500, 74, 66);
		this.add(humidityImage);
		
		// humidity text
		JLabel humidityText = new JLabel("<html><b>humidity</b> 100%</html>");
		humidityText.setBounds(90, 500, 85, 55);
		humidityText.setFont(labelFontPlain.deriveFont(16f));
		this.add(humidityText);
		
		// wind speed image
		JLabel windspeedImage = new JLabel(loadImageIcon("resources/img/windspeed.png"));
		windspeedImage.setBounds(220, 500, 74, 66);
		this.add(windspeedImage);
		
		// wind speed text
		JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15Km/h</html>");
		windspeedText.setBounds(310, 500, 85, 55);
		windspeedText.setFont(labelFontPlain.deriveFont(16f));
		this.add(windspeedText);
		
		// Search button
		JButton searchButton = new JButton(loadImageIcon("resources/img/search.png"));
		searchButton.setFocusable(false);
		searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		searchButton.setBounds(375, 13, 47, 45);
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String userInput = searchField.getText();
				if (userInput.replaceAll("\\s", "").length() <= 0)
					return;
				
				// Data
				weatherData = (JSONObject) BackEnd.getWeatherData(userInput);
				
				String weatherCondition = (String) weatherData.get("weather_condition");
				
				switch (weatherCondition)
					{
					case "Clear":
						weatherConditionImage.setIcon(loadImageIcon("resources/img/clear.png"));
						break;
					case "Cloudy":
						weatherConditionImage.setIcon(loadImageIcon("resources/img/cloudy.png"));
						break;
					case "Rain":
						weatherConditionImage.setIcon(loadImageIcon("resources/img/rain.png"));
						break;
					case "Snow":
						weatherConditionImage.setIcon(loadImageIcon("resources/img/snow.png"));
						break;
					}
					
				WeatherConditionDesc.setText(weatherCondition);
				double temperature = (double) weatherData.get("temperature");
				temperatureText.setText(temperature + "°C");
				
				long humidity = (long) weatherData.get("humidity");
				humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");
				
				double windSpeed = (double) weatherData.get("windspeed");
				windspeedText.setText("<html><b>Windspeed</b> " + windSpeed + "Km/h</html>");
			}
		});
		
		this.add(searchButton);
	}
	
	private ImageIcon loadImageIcon(String resourcePath)
	{
		try
		{
			BufferedImage image = ImageIO.read(new File(resourcePath));
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
		
		// check to see if filepath contains any folder with spaces in the name
		
		if (filePath.contains("%20"))
		{
			filePath = AppGui.class.getResource(resource).getPath().replaceAll("%20", " ");
		}
		
		return new File(filePath);
		
	}
}