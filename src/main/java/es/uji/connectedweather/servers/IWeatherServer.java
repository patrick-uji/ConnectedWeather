package es.uji.connectedweather.servers;
import java.time.LocalDate;
import java.util.Map;
import es.uji.connectedweather.TemperatureUnit;
public interface IWeatherServer
{
	
	public void setTemperatureUnits(TemperatureUnit units);
	public Map<String, String> getCurrentWeather(String city);
	public Map<String, String>[] getWeatherForecast(String city);
	public Map<String, String> getHistoricalData(String city, LocalDate date);
	public Map<String, String> getAlerts(String city);
	public String getName();
	
}
