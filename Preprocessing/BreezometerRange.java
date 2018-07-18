import java.io.*;
import java.util.*;
import org.json.*;
import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.*;
import com.opencsv.*;

public class BreezometerRange 
{
	public static void main(String[] args) throws IOException, UnirestException, JSONException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter geographic coordinates in [North, East, South, West] format: ");
		String line = br.readLine();
		String[] split = line.substring(1, line.length()-1).split(", ");
		double north = Double.parseDouble(split[0]), east = Double.parseDouble(split[1]), south = Double.parseDouble(split[2]), west = Double.parseDouble(split[3]);
		System.out.println("Enter side length: ");
		double sideLen = Double.parseDouble(br.readLine());
		br.close();
		final String apiKey = "[Redacted]";
		CSVWriter csv = new CSVWriter(new PrintWriter(new BufferedWriter(new FileWriter("BreezometerRangeTestNewKey.csv"))));
		csv.writeNext(new String[]{"Latitude", "Longitude", "Date and Time", "Air Quality Index (AQI)", "Air Quality (AQ) Description", "Dominant Pollutant Name"});
		int sum = 0, num = 0;
		for(double lat = north; lat <= south; lat += (south - north)*1.0/sideLen)
			for(double lon = east; lon <= west; lon += (west - east)*1.0/sideLen)
			{
				JSONObject obj = Unirest.get("https://api.breezometer.com/baqi/?lat=" + lat + "&lon=" + lon + "&key=" + apiKey).asJson().getBody().getObject();
				csv.writeNext(new String[]{Double.toString(lat), Double.toString(lon), obj.get("datetime").toString(), obj.get("breezometer_aqi").toString(), obj.get("breezometer_description").toString(), obj.get("dominant_pollutant_canonical_name").toString()});
				sum += Integer.parseInt(obj.get("breezometer_aqi").toString());
				num++;
			}
		csv.close();
		System.out.println("Average Air Quality Index (AQI) in " + line + " is " + (sum*1.0/num));
	}
}