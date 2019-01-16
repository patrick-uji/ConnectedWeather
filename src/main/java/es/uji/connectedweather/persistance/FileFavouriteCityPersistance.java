package es.uji.connectedweather.persistance;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
public class FileFavouriteCityPersistance implements IFavouriteCityPersistance
{
	private String file = "favourites.txt";
	
	public void setFile(String file) 
	{
		this.file = file;
	}
	public String getFile() 
	{
		return file;
	}
	
	public void loadFavouriteCities(List<String> targetList) throws FileNotFoundException, IOException
	{
		targetList.clear();
		try ( Scanner favouriteCityReader = new Scanner(new File(file)) )
		{
			while (favouriteCityReader.hasNextLine())
			{
				targetList.add(favouriteCityReader.nextLine());
			}
		}
	}
	public void saveFavouriteCities(List<String> cities) throws IOException
	{
		try ( FileWriter favouriteCityWriter = new FileWriter(new File(file)) )
		{
			for (String currCity : cities)
			{
				favouriteCityWriter.write(currCity + "\r\n"); //\r\n to properly display it on Window's notepad
			}
		}
	}
}
