import java.io.*;
import java.util.*;
import org.json.*;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.*;
import com.opencsv.*;

public class BreezometerPoint 
{
	public static void main(String[] args) throws IOException, UnirestException, JSONException
	{
		System.out.print("Enter latitude and longitude, separated by a space: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine());
		double lat = Double.parseDouble(st.nextToken()), lon = Double.parseDouble(st.nextToken());
		br.close();
		final String apiKey = "[Redacted]";
		JSONObject obj = Unirest.get("https://api.breezometer.com/baqi/?lat=" + lat + "&lon=" + lon + "&key=" + apiKey).asJson().getBody().getObject();
		CSVWriter csv = new CSVWriter(new PrintWriter(new BufferedWriter(new FileWriter("BreezometerPoint.csv"))));
		csv.writeNext(new String[]{"Latitude", "Longitude", "Date and Time", "Air Quality Index (AQI)", "Air Quality (AQ) Description", "Dominant Pollutant Name"});
		csv.writeNext(new String[]{Double.toString(lat), Double.toString(lon), obj.get("datetime").toString(), obj.get("breezometer_aqi").toString(), obj.get("breezometer_description").toString(), obj.get("dominant_pollutant_canonical_name").toString()});
		csv.close();
	}
}