package general;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.uji.connectedweather.MainFrameIntegrationTest;
import es.uji.connectedweather.MainFrameUnityTest;
import es.uji.connectedweather.persistance.FileFavouriteCityPersistanceTest;
import es.uji.connectedweather.servers.AccuWeatherServerTest;
import es.uji.connectedweather.servers.ApixuServerTest;
import es.uji.connectedweather.servers.OpenWeatherMapServerTest;

@RunWith(Suite.class)
@SuiteClasses({
	AccuWeatherServerTest.class,
	ApixuServerTest.class,
	OpenWeatherMapServerTest.class,
	MainFrameIntegrationTest.class,
	MainFrameUnityTest.class,
	FileFavouriteCityPersistanceTest.class
})

public class AllTests{}
