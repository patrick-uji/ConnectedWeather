package es.uji.connectedweather.persistance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface ICitiesPersistance
{
	
	public void load(List<String> targetList) throws FileNotFoundException, IOException;
	public void save(List<String> cities) throws IOException;
	
}
