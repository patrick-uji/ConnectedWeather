package es.uji.connectedweather.persistance;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
public class FileFavouriteCityPersistance implements IFavouriteCityPersistance
{
	public void loadFavouriteCities(List<String> targetList) throws FileNotFoundException, IOException
	{
		try ( Scanner favouriteCityReader = new Scanner(new File("favourites.txt")) )
		{
			while (favouriteCityReader.hasNextLine())
			{
				targetList.add(favouriteCityReader.nextLine());
			}
		}
	}
	public void saveFavouriteCities(List<String> cities) throws IOException
	{
		try ( FileWriter favouriteCityWriter = new FileWriter(new File("favourites.txt")) )
		{
			for (String currCity : cities)
			{
				favouriteCityWriter.write(currCity + "\n");
			}
		}
	}
}
