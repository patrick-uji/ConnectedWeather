package es.uji.connectedweather;

import java.util.List;

import javax.swing.JComboBox;

import es.uji.connectedweather.dataStructure.WeatherData;
import es.uji.connectedweather.servers.AccuWeatherServer;
import es.uji.connectedweather.servers.ApixuServer;
import es.uji.connectedweather.servers.IWeatherServer;
import es.uji.connectedweather.servers.OpenWeatherMapServer;

public class MainFrame
{
	
	@SuppressWarnings("unused")
	private IWeatherServer usingServer;
	private IWeatherServer[] servers;
	private MainFrameDesign design;
	
	public MainFrame()
	{
		this.design = new MainFrameDesign(this);
		this.servers = new IWeatherServer[] {
				new OpenWeatherMapServer(),
				new AccuWeatherServer(),
				new ApixuServer()
		};
		JComboBox<String> serversComboBox = design.getServersComboBox();
		for (IWeatherServer currWeatherServer : servers)
		{
			serversComboBox.addItem(currWeatherServer.getName());
		}
		show();
	}
	
	public void setServer(IWeatherServer server) {
		usingServer = server;
	}
	
	public void show()
	{
		if (design != null)
		{
			design.setVisible(true);
		}
	}
	
	public WeatherData getCurrentWeather(String city, List<String> params) {
		return null;
	}
	
}
