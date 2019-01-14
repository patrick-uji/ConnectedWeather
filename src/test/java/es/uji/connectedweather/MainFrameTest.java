package es.uji.connectedweather;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import es.uji.connectedweather.servers.IWeatherServer;

public class MainFrameTest {
	
	private MainFrame mainFrame;
	
	@SuppressWarnings("unused")
	private IWeatherServer mockServer;
	
	
	@Test
	public void getCurrentWeather_normal_weatherData() {
		String city = "Sagunto";
		List<String> params = new ArrayList<String>();
		params.add("t");
		mainFrame.getCurrentWeather(city, params);		
	}
	
	@Test(expected=NullPointerException.class)
	public void getCurrentWeather_nullCity_nullPointerException() {
		String city = null;
		List<String> params = new ArrayList<String>();
		params.add("t");
		mainFrame.getCurrentWeather(city, params);		
		
	}
	
	@Test(expected=InvalidParameterException.class)
	public void getCurrentWeather_invalidCity_invalidParameterException() {
		String city = "kjhdfjkd";
		List<String> params = new ArrayList<String>();
		params.add("t");
		mainFrame.getCurrentWeather(city, params);		
		
	}
	
	@Test(expected=NullPointerException.class)
	public void getCurrentWeather_nullParameters_nullPointerException(){
		String city = "Sagunto";
		List<String> params = null;
		mainFrame.getCurrentWeather(city, params);		
		
	}
	
	@Test(expected=InvalidParameterException.class)
	public void getCurrentWeather_invalidParameter_invalidParameterException() {
		String city = "Sagunto";
		List<String> params = new ArrayList<String>();
		mainFrame.getCurrentWeather(city, params);		
		
	}
	
	@Test(expected=IOException.class)
	public void getCurrentWeather_conectionLost_IOException() {
		String city = "Sagunto";
		List<String> params = new ArrayList<String>();
		params.add("t");
		mainFrame.getCurrentWeather(city, params);		
		
	}

}
