package es.uji.connectedweather.servers;
import java.time.LocalDate;
import java.util.Map;
public interface IWeatherServer
{
	
	public Map<String, String> getCurrentWeather(String city);
	public Map<String, String>[] getNextWeekWeather(String city);
	public Map<String, String> getHistoricalData(String city, LocalDate date);
	public Map<String, String> getAlerts(String city);
	public String getName();
	
}
