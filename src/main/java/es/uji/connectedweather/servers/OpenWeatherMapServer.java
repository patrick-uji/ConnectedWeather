package es.uji.connectedweather.servers;

import java.util.Map;

public class OpenWeatherMapServer implements IWeatherServer
{

	@Override
	public Map<String, String> getCurrentWeather(String city)
	{
		return null;
	}

	@Override
	public Map<String, String>[] getNextWeekWeather(String city)
	{
		return null;
	}

	@Override
	public Map<String, String> getHistoricalData(String city)
	{
		return null;
	}

	@Override
	public Map<String, String> getAlarms(String city)
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "OpenWeatherMap";
	}
	
}
