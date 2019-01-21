package es.uji.connectedweather.servers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class AccuWeatherServerTest
{
	private String city;
	private LocalDate date;
	private IWeatherServer server;
	
	@Before
	public void setConfiguration()
	{
		city = "Madrid";
		server = new AccuWeatherServer();
		date = LocalDate.now().minusDays(1);
	}
	
	//Testing getCurrentWeather 
	
	@Test
	public void getCurrentWeather_normal_ok()
	{
		assertNotNull(server.getCurrentWeather(city));
	}
	
	@Test
	public void getCurrentWeather_invalidCity_null()
	{
		assertNull(server.getCurrentWeather("asdgdssdf"));
	}
	
	@Test(expected = NullPointerException.class)
	public void getCurrentWeather_nullCity_nullPointerException()
	{
		server.getCurrentWeather(null);
		fail("Expected NullPointerException");
	}
	
	//Testing getWeatherForecast
	
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
	public void getWeatherForecast_nullCity_nullPoidaysToSubtractnterException()
	{
		server.getWeatherForecast(null);
		fail("Expected NullPointerException");
	}
	
	//Testing getHistoricalData
	
	@Test
	public void getHistoricalData_normal_ok()
	{
		assertNotNull(server.getHistoricalData(city, date));
	}
	
	@Test
	public void getHistoricalData_invalidCity_null()
	{
		assertNull(server.getHistoricalData("uhbhdjf", date));
	}
	
	@Test(expected = NullPointerException.class)
	public void getHistoricalData_nullCity_nullPointerException()
	{
		server.getHistoricalData(null, date);
		fail("Expected NullPointerException");
	}
	
	@Test
	public void getHistoricalData_todayData_null()
	{
		assertNull( server.getHistoricalData(city, LocalDate.now()) );
	}
	
	@Test
	public void getHistoricalData_futureData_null()
	{
		assertNull( server.getHistoricalData(city, LocalDate.now().plusDays(2)) );
	}
	
	@Test(expected = NullPointerException.class)
	public void getHistoricalData_nullData_nullPointerException()
	{
		server.getHistoricalData(city, null);
		fail("Expected NullPointerException");
	}
	
	//Testing getAlerts
	
	@Test
	public void getAlerts_normal_ok()
	{
		assertNotNull(server.getAlerts(city));
	}
	
	@Test
	public void getAlerts_invalidCity_null()
	{
		assertNull(server.getAlerts("asdgdssdf"));
	}
	
	@Test(expected = NullPointerException.class)
	public void getAlerts_nullCity_nullPoidaysToSubtractnterException()
	{
		server.getAlerts(null);
		fail("Expected NullPointerException");
	}
	
}
