package es.uji.connectedweather.servers;
import java.util.Map;
public interface IWeatherServer
{
	
	public Map<String, String> getCurrentWeather(String city);
	public Map<String, String>[] getNextWeekWeather(String city);
	public Map<String, String> getHistoricalData(String city);
	public Map<String, String> getAlarms(String city);
	public String getName();
	
}
