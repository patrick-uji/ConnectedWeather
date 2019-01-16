package es.uji.connectedweather;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Utils
{
	
	private static final JSONParser JSON_PARSER = new JSONParser();
	
	public static String makeGETRequest(URL url) throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        return readResponse(connection);
	}
	
	public static String makePOSTRequest(URL url, String requestContent) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        //connection.setRequestProperty("User-Agent", "Java client");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try ( DataOutputStream requestStream = new DataOutputStream(connection.getOutputStream()) )
        {
            requestStream.write(requestContent.getBytes()); //StandardCharsets.UTF_8
        }
        return readResponse(connection);
    }
	
	private static String readResponse(HttpURLConnection connection) throws IOException
	{
		String readLine;
        StringBuilder response = new StringBuilder();
        try ( BufferedReader responseStream = new BufferedReader(new InputStreamReader( connection.getInputStream() )) )
        {
            readLine = responseStream.readLine();
            while (readLine != null)
            {
                response.append(readLine + System.lineSeparator());
                readLine = responseStream.readLine();
            }
        }
        connection.disconnect();
        return response.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends JSONAware> T parseJSON(String json) throws ParseException
	{
		return (T)JSON_PARSER.parse(json);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T readJSONObject(JSONObject jsonObject, String... keys)
	{
		int lastIndex = keys.length - 1;
		JSONObject currJSONObject = jsonObject;
		for (int currKeyIndex = 0; currKeyIndex < lastIndex; currKeyIndex++)
		{
			currJSONObject = (JSONObject)currJSONObject.get(keys[currKeyIndex]);
		}
		return (T)currJSONObject.get(keys[lastIndex]);
	}
	
	public static float round(float value, int precision)
	{
	    int scale = (int)Math.pow(10, precision);
	    return Math.round(value * scale) / (float)scale;
	}
	
	public static double round(double value, int precision)
	{
	    int scale = (int)Math.pow(10, precision);
	    return Math.round(value * scale) / (double)scale;
	}
	
	public static <T> T timeoutTask(Callable<T> callable, int timeout, TimeUnit timeUnit, Consumer<Future<T>> callback) throws TimeoutException, InterruptedException, ExecutionException
	{
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<T> future = executor.submit(callable);
		try
		{
			return future.get(timeout, timeUnit);
        }
		catch (TimeoutException ex)
		{
			callback.accept(future);
			return null;
        }
		catch (InterruptedException | ExecutionException ex)
		{
			throw ex;
		}
	}
	
	public static void showAudioMessageDialog(String message, String title, int messageType)
	{
		playDialogAudio(messageType);
		JOptionPane.showMessageDialog(null, message, title, messageType);
	}
	
	public static int showAudioConfirmDialog(String message, String title, int optionType, int messageType)
	{
		playDialogAudio(messageType);
		return JOptionPane.showConfirmDialog(null, message, title, optionType, messageType);
	}
	
	public static void playDialogAudio(int messageType)
	{
		String soundPropertyName;
		switch (messageType)
		{
			case JOptionPane.ERROR_MESSAGE:
				soundPropertyName = "win.sound.hand";
			break;
			case JOptionPane.INFORMATION_MESSAGE:
				soundPropertyName = "win.sound.asterisk";
			break;
			case JOptionPane.WARNING_MESSAGE:
				soundPropertyName = "win.sound.exclamation";
			break;
			case JOptionPane.QUESTION_MESSAGE:
				soundPropertyName = "win.sound.question";
			break;
			default:
				soundPropertyName = null;
			break;
		}
		if (soundPropertyName != null)
		{
			Runnable runnable = (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty(soundPropertyName);
	        if (runnable != null)
	    	{
		    	runnable.run(); 
	    	}
		}
	}
	
	public static double celsiusToFahrenheit(double celsius)
	{
		return (celsius * 9.0 / 5.0) + 32;
	}
	
	public static double fahrenheitToCelsius(double fahrenheit)
	{
		return (fahrenheit - 32) * 5.0 / 9.0;
	}
	
}
