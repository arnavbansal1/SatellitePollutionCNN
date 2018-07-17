The NodeJS package map-dl was used to retrieve satellite images from Google Maps.  The Breezometer API was used to fetch air pollution data, including the Breezometer Air Quality Index (BAQI).  BAQI, on a scale of 0 to 100, is a holistic air pollution metric that is highly accurate, standardized, and hyperlocal.

First, a list of 57 cities, varying in size, population, location, and air quality, was generated.  Google Maps was used to approximate the geographic coordinates of the rectangle that circumscribes each city.  Then, mathematical algorithms using trigonometric functions were developed in Java to split each city’s rectangle into a grid of smaller rectangles.  Next, 7 servers were set up to distribute queries and collect data in a parallelized fashion for each of the 10,000 smaller rectangles across the 57 cities.

After running the full version of the above code, 10,000 satellite images were downloaded and a CSV of 10,000 air pollution queries was generated.  The data collection process took 2 hours.

The 10,000 satellite images were resized from 1280 x 1280 to 200 x 200 using Python’s

```python
scipy.misc.imresize()
```
