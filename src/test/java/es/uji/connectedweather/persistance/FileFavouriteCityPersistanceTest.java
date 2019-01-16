package es.uji.connectedweather.persistance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileFavouriteCityPersistanceTest {
	
	private static final String TEST_DIRECTORY = "dirToRunTesting";
	private static final String TEST_FILE = "fileUnderTesting.txt";
	
	private FileFavouriteCityPersistance persistance;
	private List<String> favoritesCities;
	private static File directory;
	
	@BeforeClass
	public static void createDir() {
		directory = new File(TEST_DIRECTORY);
		directory.mkdirs();
	}
	
	@Before
	public void setConfiguration() {
		String path = TEST_DIRECTORY + File.separator + TEST_FILE;
		persistance = new FileFavouriteCityPersistance();
		persistance.setFile(path);
		favoritesCities = new ArrayList<String>();
	}
	
	@AfterClass
	public static void removeConfiguration() {
		directory.delete();
	}
	
	//Testing loadFavouriteCities
	
	@Test
	public void loadFavouriteCities_notExistingFile_AllSafe() {
		try {
			persistance.loadFavouriteCities(favoritesCities);
			fail("Expected FileNotFoundException");
		} catch (FileNotFoundException e) {
			//ok
		} catch (Exception e) {
			fail("Expected FileNotFoundException");
		}
	}
	
	@Test
	public void loadFavouriteCities_emptyFile_emptyList() {
		File file = new File(persistance.getFile());
		try {
			file.createNewFile();
			persistance.loadFavouriteCities(favoritesCities);
			assertEquals(favoritesCities.size(), 0);
		}catch(Exception e) {
			e.printStackTrace();
			fail("Exception throwed");
		}finally {
			file.delete();
		}
	}
	
	@Test
	public void loadFavouriteCities_notEmptyFavoriteList_emptyList() {
		File file = new File(persistance.getFile());
		try {
			file.createNewFile();
			favoritesCities.add("a");
			persistance.loadFavouriteCities(favoritesCities);
			assertEquals(favoritesCities.size(), 0);
		}catch(Exception e) {
			e.printStackTrace();
			fail("Exception throwed");
		}finally {
			file.delete();
		}
	}
	
	@Test
	public void loadFavouriteCities_nullList_NullPointerException() {
		File file = new File(persistance.getFile());
		try {
			file.createNewFile();
			persistance.loadFavouriteCities(null);
			fail("Expected NullPointerException");
		}catch(NullPointerException e) {
			//ok
		}catch(Exception e) {
			e.printStackTrace();
			fail("Exception not expected throwed");
		}finally {
			file.delete();
		}
	}
	
	//Testing saveFavouriteCities

	

}
