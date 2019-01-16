package es.uji.connectedweather;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import es.uji.connectedweather.frames.MainFrame;
import es.uji.connectedweather.servers.IWeatherServer;

public class MainFrameTest {
	
	private MainFrame mainFrame;
	private IWeatherServer mockServer;
	private String city;
	private List<String> params;
	
	@Before
	public void setConfiguration() {
		mockServer = Mockito.mock(IWeatherServer.class);
		mainFrame = new MainFrame();
		city = "Madrid";
		params = new ArrayList<String>();
	}
	
	//Testing getCurrentWeather
	
	@Test
	public void getCurrentWeather_normal_weatherData() {
		params.add("temperature");
		mainFrame.getCurrentWeather(city, params);
	}
	
	@Test(expected=NullPointerException.class)
	public void getCurrentWeather_nullCity_nullPointerException() {
		city = null;
		params.add("temperature");
		mainFrame.getCurrentWeather(city, params);
		fail("Expected NullPointerException");
	}
	
	@Test(expected=InvalidParameterException.class)
	public void getCurrentWeather_invalidCity_invalidParameterException() {
		String city = "kjhdfjkd";
		params.add("temperature");
		mainFrame.getCurrentWeather(city, params);
		fail("Expected InvalidParameterException");
	}
	
	@Test(expected=NullPointerException.class)
	public void getCurrentWeather_nullParameters_nullPointerException(){
		params = null;
		mainFrame.getCurrentWeather(city, params);
		fail("Expected NullPointerException");
	}
	
	@Test
	public void getCurrentWeather_emptyParams_returnAll() {
		assertNotNull(mainFrame.getCurrentWeather(city, params));
	}
	
	//Testing setServer
	
	@Test
	public void setServer_normal_void() {
		mainFrame.setServer(mockServer);
	}
	
	@Test (expected=InvalidParameterException.class)
	public void setServer_null_IllegalArgumentException() {
		mainFrame.setServer(null);
		fail("Expected InvalidParameterException");
	}

}
