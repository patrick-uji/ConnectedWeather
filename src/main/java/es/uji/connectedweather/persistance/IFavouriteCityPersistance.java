package es.uji.connectedweather.persistance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IFavouriteCityPersistance
{
	
	public void loadFavouriteCities(List<String> targetList) throws FileNotFoundException, IOException;
	public void saveFavouriteCities(List<String> cities) throws IOException;
	
}
