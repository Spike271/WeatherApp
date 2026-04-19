import java.awt.Cursor;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.formdev.flatlaf.FlatClientProperties;

import net.miginfocom.swing.MigLayout;

public class AppGui extends JFrame
{
	public AppGui()
	{
		super("Weather App");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(450, 650);
		this.setLocationRelativeTo(null);
		this.setLayout(new MigLayout("fillx, insets 20", "[][right]"));
		this.setResizable(false);
        setIconImage(new ImageIcon(ApplicationUtils.jarFilePath + "res/img/cloudy.png").getImage());

		addGuiComponents();
	}
	
	private void addGuiComponents()
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
		catch (Exception _) {}

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
        if (labelFontBold != null) WeatherConditionDesc.setFont(labelFontBold.deriveFont(32f));
		this.add(WeatherConditionDesc, "span 2, center, wrap");
		
		// humidity image
		JLabel humidityImage = new JLabel(loadImageIcon("res/img/humidity.png"));
		this.add(humidityImage, "gapy 50, h 74, w 66, split");
		
		// humidity text
		JLabel humidityText = new JLabel("<html><b>humidity</b><br/>100%</html>");
        if (labelFontPlain != null) humidityText.setFont(labelFontPlain.deriveFont(16f));
		this.add(humidityText, "gapx 0, gapy 50, split");
		
		// wind speed image
		JLabel windSpeedImage = new JLabel(loadImageIcon("res/img/windspeed.png"));
		this.add(windSpeedImage, "gapx 80, gapy 50, h 74, w 66, split");
		
		// wind speed text
		JLabel windSpeedText = new JLabel("<html><b>Windspeed</b><br/>15Km/h</html>");
        if (labelFontPlain != null) windSpeedText.setFont(labelFontPlain.deriveFont(16f));
		this.add(windSpeedText, "gapx 15, gapy 50, wrap");
		
		// Search button
		JButton searchButton = new JButton(loadImageIcon("res/img/search.png"));
		searchButton.putClientProperty(FlatClientProperties.STYLE, "arc: 0;" + "focusWidth: 0;");
		searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		searchButton.addActionListener(_ -> {
            searchButton.setEnabled(false);
            Thread.ofVirtual().start(() -> {

                String userInput = searchField.getText();
                if (userInput.replaceAll("\\s", "").isEmpty())
                    return;

                try
                {
                    Map<String, Object> weatherData = BackEnd.getWeatherData(userInput);
                    if (weatherData.isEmpty()) return;

                    String weatherCondition = (String) weatherData.get("weather_condition");

                    switch (weatherCondition)
                    {
                        case "Clear" -> weatherConditionImage.setIcon(loadImageIcon("res/img/clear.png"));
                        case "Cloudy" -> weatherConditionImage.setIcon(loadImageIcon("res/img/cloudy.png"));
                        case "Rain" -> weatherConditionImage.setIcon(loadImageIcon("res/img/rain.png"));
                        case "Snow" -> weatherConditionImage.setIcon(loadImageIcon("res/img/snow.png"));
                    }

                    WeatherConditionDesc.setText(weatherCondition);
                    double temperature = (double) weatherData.get("temperature");
                    temperatureText.setText(temperature + "°C");

                    long humidity = (long) weatherData.get("humidity");
                    humidityText.setText("<html><b>Humidity</b><br/>" + humidity + "%</html>");

                    double windSpeed = (double) weatherData.get("windspeed");
                    windSpeedText.setText("<html><b>Windspeed</b><br/>" + windSpeed + "Km/h</html>");
                }
                catch (Exception _) {}
                finally
                {
                    searchButton.setEnabled(true);
                }
            });
        });
		
		this.add(searchButton, "gapx 10, cell 0 0, h 42");
	}
	
	private ImageIcon loadImageIcon(String resourcePath)
	{
		try
		{
			String path = ApplicationUtils.jarFilePath + resourcePath;
			BufferedImage image = ImageIO.read(new File(path));
			return new ImageIcon(image);
		}
		catch (IOException e) {
			System.out.println("could not find the file");
		}
		return null;
	}
	
	private File getFontFile(String resource)
	{
        resource = ApplicationUtils.jarFilePath + resource;
		return new File(resource);
	}
}