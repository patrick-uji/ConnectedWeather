package es.uji.connectedweather;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
	
}
