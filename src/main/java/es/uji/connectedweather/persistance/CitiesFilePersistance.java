package es.uji.connectedweather.persistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class CitiesFilePersistance implements ICitiesPersistance
{
	
	private String filePath;
	
	public CitiesFilePersistance(String filePath)
	{
		this.filePath = filePath;
	}
	
	public String getFilePath() 
	{
		return filePath;
	}
	
	public void load(List<String> targetList) throws FileNotFoundException, IOException
	{
		targetList.clear();
		try ( Scanner favouriteCityReader = new Scanner(new File(filePath)) )
		{
			while (favouriteCityReader.hasNextLine())
			{
				targetList.add(favouriteCityReader.nextLine());
			}
		}
	}
	
	public void save(List<String> cities) throws IOException
	{
		try ( FileWriter favouriteCityWriter = new FileWriter(new File(filePath)) )
		{
			for (String currCity : cities)
			{
				favouriteCityWriter.write(currCity + "\r\n"); //\r\n to properly display it on Window's notepad
			}
		}
	}
	
}
