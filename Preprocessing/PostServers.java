import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.opencsv.CSVWriter;

public class PostServers 
{
	public static BufferedReader br;
	public static CSVWriter csv; 
	public static void postServers(ArrayList<CoordRect> rects, ArrayList<Server> servers) throws InterruptedException, ExecutionException 
	{
		List<Server> safeServers = Collections.synchronizedList(servers);
		ForkJoinPool myPool = new ForkJoinPool(safeServers.size());
		List<Boolean> success = new ArrayList<Boolean>();
		HttpClient httpclient = HttpClients.createDefault();
		do
		{
			success = myPool.submit(() -> 
			{
				return rects.parallelStream().map((rect) -> 
				{
					Server server;
					synchronized(safeServers) 
					{
						server = safeServers.remove((int)(Math.random() * safeServers.size()));
					}
					boolean worked = true;
					try 
					{
						double lat = (rect.n + rect.s)/2.0, lon = (rect.e + rect.w)/2.0;
						HttpPost httppost = new HttpPost(server.url + "/breezometer");
						ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("lat", Double.toString(lat)));
						params.add(new BasicNameValuePair("lon", Double.toString(lon)));
						params.add(new BasicNameValuePair("apikey", server.apikey));
						httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
						HttpResponse response;
						response = httpclient.execute(httppost);
						HttpEntity entity = response.getEntity();
						if(entity == null)
							worked = false;
						else
						{
							InputStream instream = entity.getContent();
							br = new BufferedReader(new InputStreamReader(instream));
							String streamLine = br.readLine();
							if(streamLine.contains("html"))
								worked = false;
							else
							{
								JSONObject obj = new JSONObject(streamLine);
								File f = new File(rect.n + " " + rect.e + " " + rect.s + " " + rect.w + " " + rect.scale + ".png");
								FileUtils.copyURLToFile(new URL(server.url + "/mapdl?north=" + rect.n + "&east=" + rect.e + "&south=" + rect.s + "&west=" + rect.w + "&scale=" + rect.scale), f);
								BufferedImage image = ImageIO.read(f);
								if(image.getHeight() != 1280 || image.getWidth() != 1280)
									worked = false;
								else
									csv.writeNext(new String[]{rect.city, Double.toString(lat), Double.toString(lon), obj.get("datetime").toString(), obj.get("breezometer_aqi").toString(), obj.get("breezometer_description").toString(), obj.get("dominant_pollutant_canonical_name").toString(), f.getName()});
							}
							br.close();
						}
					} 
					catch(Exception e)
					{
						worked = false;
					}
					synchronized(safeServers)
					{
						safeServers.add(server);
					}
					return worked;
				}).collect(Collectors.toList());
			}).get();
			for(int i = 0; i < rects.size(); i++)
				if(success.get(i) == true) 
				{
					success.remove(i);
					rects.remove(i);
					i--;
				}
		}while(success.contains(false));
	}
	public static void main(String[] args) throws IOException, JSONException, UnirestException, MalformedURLException, InterruptedException, ExecutionException 
	{
		long start = System.currentTimeMillis();
		br = new BufferedReader(new FileReader("coordinates"));
		ArrayList<CoordRect> rects = new ArrayList<CoordRect>();
		String line;
		while((line = br.readLine()) != null)
		{
			String[] inputs = line.split(", ");
			rects.add(new CoordRect(inputs[0], Double.parseDouble(inputs[1]), Double.parseDouble(inputs[2]), Double.parseDouble(inputs[3]), Double.parseDouble(inputs[4]), Double.parseDouble(inputs[5])));
		}
		br.close();
		br = new BufferedReader(new FileReader("servers"));
		ArrayList<Server> servers = new ArrayList<Server>();
		while((line = br.readLine()) != null)
		{
			String[] inputs = line.split(", ");
			servers.add(new Server(inputs[0], inputs[1]));
		}
		br.close();
		csv = new CSVWriter(new PrintWriter(new BufferedWriter(new FileWriter("data.csv"))));
		csv.writeNext(new String[]{"City", "Latitude", "Longitude", "Date and Time", "Air Quality Index (AQI)", "Air Quality (AQ) Description", "Dominant Pollutant Name", "Satellite Image Filename"});
		postServers(rects, servers);
		csv.close();
		System.out.println((System.currentTimeMillis() - start) / 1000.0);
		System.exit(0);
	}
}
class CoordRect 
{
	public String city;
	public double n, e, s, w, scale;
	public CoordRect(String city, double n, double e, double s, double w, double scale) 
	{
		this.city = city;
		this.n = n;
		this.e = e;
		this.s = s;
		this.w = w;
		this.scale = scale;
	}
}
class Server
{
	public String url, apikey;
	public Server(String url, String apiKey)
	{
		this.url = url;
		this.apikey = apiKey;
	}
}