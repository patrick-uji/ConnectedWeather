package es.uji.connectedweather.servers;

import org.junit.Before;
import org.junit.Test;
import es.uji.connectedweather.servers.IWeatherServer;
import static org.junit.Assert.*;

import java.time.LocalDate;

public class ApixuServerTest
{
	
	private IWeatherServer server;
	private LocalDate date;
	private String city;
	
	@Before
	public void setConfiguration() {
		server = new ApixuServer();
		date = LocalDate.now().minusDays(1);
		city = "Madrid";
	}
	
	//Testing getCurrentWeather 
	
	@Test
	public void getCurrentWeather_normal_ok()
	{
		assertNotNull(server.getCurrentWeather(city));
	}
	
	@Test
	public void getCurrentWeather_invalidCity_diccionarioNulo()
	{
		assertNull(server.getCurrentWeather("asdgdssdf"));
	}
	
	@Test(expected = NullPointerException.class)
	public void getCurrentWeather_nullCity_nullPointerException()
	{
		server.getCurrentWeather(null);
		fail("Expected NullPointerException");
	}

	//Testing getNextWeekWeather
	
	@Test
	public void getWeatherForecast_normal_ok()
	{
		assertNotNull(server.getWeatherForecast(city));
	}
	
	@Test
	public void getWeatherForecast_invalidCity_diccionarioNulo()
	{
		assertNull(server.getWeatherForecast("asdgdssdf"));
	}
	
	@Test(expected = NullPointerException.class)
	public void getWeatherForecast_nullCity_nullPointerException()
	{
		server.getWeatherForecast(null);
		fail("Expected NullPointerException");
	}
	
	//Testing getHistoricalData
	
	@Test
	public void getHistoricalData_normal_ok() {
		assertNotNull(server.getHistoricalData(city, date));
	}
	
	@Test
	public void getHistoricalData_invalidCity_null() {
		assertNull(server.getHistoricalData("uhbhdjf", date));
		
	}
	
	@Test(expected = NullPointerException.class)
	public void getHistoricalData_nullCity_nullPointerException() {
		server.getHistoricalData(null, date);
		fail("Expected NullPointerException");
	}
	
	@Test
	public void getHistoricalData_todayData_null() {
		LocalDate date = LocalDate.now();
		assertNull(server.getHistoricalData(city, date));
	}
	
	@Test
	public void getHistoricalData_futureData_null() {
		LocalDate date = LocalDate.now().plusDays(2);
		assertNull(server.getHistoricalData(city, date));
	}
	
	@Test(expected = NullPointerException.class)
	public void getHistoricalData_nullData_nullPointerException() {
		server.getHistoricalData(city, null);
		fail("Expected NullPointerException");
	}
	
}
