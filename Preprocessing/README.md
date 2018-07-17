# Dataset Generation & Processing

The NodeJS package map-dl was used to retrieve satellite images from Google Maps.  The Breezometer API was used to fetch air pollution data, including the Breezometer Air Quality Index (BAQI).  BAQI, on a scale of 0 to 100, is a holistic air pollution metric that is highly accurate, standardized, and hyperlocal.

First, a list of 57 cities, varying in size, population, location, and air quality, was generated.  Google Maps was used to approximate the geographic coordinates of the rectangle that circumscribes each city.  Then, mathematical algorithms using trigonometric functions were developed in Java to split each city’s rectangle into a grid of smaller rectangles.  Next, 7 servers were set up to distribute queries and collect data in a parallelized fashion for each of the 10,000 smaller rectangles across the 57 cities.

An excerpt of the parallelized data collection code developed in Java:
```java
List<Server> safeServers = Collections.synchronizedList(servers);
ForkJoinPool myPool = new ForkJoinPool(safeServers.size());
List<Boolean> success = new ArrayList<Boolean>();
do {
     success = myPool.submit(() -> {
          return rects.parallelStream().map((rect) -> {
               Server server;
               synchronized(safeServers) {
                    server = safeServers.remove((int)(Math.random() * safeServers.size()));
               }
               boolean worked = true;
               try {
                    // Code for downloading satellite images and air pollution data via
                    // Breezometer and map-dl queries
               } 
               catch(Exception e) {
                    worked = false;
               }
               synchronized(safeServers) {
                    safeServers.add(server);
               }
               return worked;
         }).collect(Collectors.toList());
     }).get();
     for(int i = 0; i < rects.size(); i++)
          if(success.get(i) == true) {
               success.remove(i);
               rects.remove(i);
               i--;
          }
} while(success.contains(false));
```
After running the full version of the above code, 10,000 satellite images were downloaded and a CSV of 10,000 air pollution queries was generated.  The data collection process took 2 hours.

An excerpt of the CSV:
![CSV](https://github.com/arnavbansal1/SatellitePollutionCNN/blob/master/Preprocessing/CSV.png)

The 10,000 satellite images were resized from 1280 x 1280 to 200 x 200 using Python’s
```python
scipy.misc.imresize()
```

## Examples of Proccessed Records in Dataset
![SatteliteImageExamples](https://github.com/arnavbansal1/SatellitePollutionCNN/blob/master/Preprocessing/SatelliteImageExamples.PNG)
