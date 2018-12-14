package es.uji.connectedweather;

import javax.swing.JComboBox;
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
	
	public void show()
	{
		if (design != null)
		{
			design.setVisible(true);
		}
	}
	
}
