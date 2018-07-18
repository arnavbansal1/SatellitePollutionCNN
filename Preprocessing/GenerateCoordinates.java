import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GenerateCoordinates 
{
	public static void main(String[] args) throws IOException 
	{
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("coordinates")));
		String[] cities = {"Wichita, 37.658029, -97.287956, 37.676440, -97.207609"};
		for(int q = 0; q < cities.length; q++)
		{
			String[] split = cities[q].split(", ");
			String city = split[0];
			double n = Double.parseDouble(split[1]), e = Double.parseDouble(split[2]), s = Double.parseDouble(split[3]), w = Double.parseDouble(split[4]), scale = 1.0 * 1000.0;
			double height = dist(n, w, s, w) / scale, width = dist(n, w, n, e) / scale, totalHeight = n - s, totalWidth = e - w;
			int totalImages = (int)(Math.ceil(height) * Math.ceil(width));
			for(int i = 0; i < height; i++)
				for(int j = 0; j < width; j++)
				{
					double tN = ((totalHeight*1.0/height) * i), tE = ((totalWidth*1.0/width) * j), tS = ((totalHeight*1.0/height) * (i + 1)), tW = ((totalWidth*1.0/width) * (j + 1));
					if(tN < 0)
						tN = n + tN;
					else
						tN = n - tN;
					if(tS < 0)
						tS = n + tS;
					else
						tS = n - tS;
					if(tE < 0)
						tE = e + tE;
					else
						tE = e - tE;
					if(tW < 0)
						tW = e + tW;
					else
						tW = e - tW;
					pw.println(city + ", " + tN + ", " + tE + ", " + tS + ", " + tW + ", " + (Math.max(dist(tN, tW, tS, tW), dist(tN, tW, tN, tE)) + 0.1)/1000.0);
				}
		}
		pw.close();
		System.exit(0);
	}
	public static double rad(double d)
	{
		return d * (Math.PI/180.0);
	}
	public static double dist(double x1, double y1, double x2, double y2)
	{
		double R = 6371e3, phi1 = rad(x1), phi2 = rad(x2), deltaPhi = rad(x2 - x1), deltaLambda = rad(y2 - y1);
		double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) + Math.cos(phi1) * Math.cos(phi2) * Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c;
	}
}