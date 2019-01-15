package es.uji.connectedweather;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

import es.uji.connectedweather.servers.AccuWeatherServer;
import es.uji.connectedweather.servers.ApixuServer;
import es.uji.connectedweather.servers.IWeatherServer;
import es.uji.connectedweather.servers.OpenWeatherMapServer;

public class MainFrame
{
	
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
		usingServer = servers[0];
	}
	
	public void setServer(IWeatherServer server) {
		if (server == null) throw new InvalidParameterException();
		usingServer = server;
	}
	
	public void show()
	{
		if (design != null)
		{
			design.setVisible(true);
		}
	}
	
	public Map<String, String> getCurrentWeather(String city, List<String> params) {
		if (city == null || params == null) throw new NullPointerException();
		Map<String, String> weatherData = usingServer.getCurrentWeather(city);
		if (params.size() <= 0) return weatherData;
		if (weatherData == null) throw new InvalidParameterException();
		Map<String, String> dev = new HashMap<String, String>();
		for (String param:params) {
			if (weatherData.containsKey(param)) {
				dev.put(param, weatherData.get(param));
			}else {
				throw new InvalidParameterException();
			}
		}
		return dev;
	}
	
}
