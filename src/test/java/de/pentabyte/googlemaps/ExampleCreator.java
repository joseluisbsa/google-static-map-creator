package de.pentabyte.googlemaps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import de.pentabyte.googlemaps.StaticMap.Maptype;

/**
 * @author michael hoereth
 *
 */
public class ExampleCreator {
	private static String GOOGLEAPI_PROPERTYNAME = "GOOGLEAPI";
	private String googleApiKey;

	@ClassRule
	public static SystemPropertyPreCondition check = new SystemPropertyPreCondition(GOOGLEAPI_PROPERTYNAME);

	@Before
	public void before() {
		googleApiKey = System.getProperty(GOOGLEAPI_PROPERTYNAME);
	}

	@Test
	public void createLocation() throws ClientProtocolException, IOException {
		StaticMap map = new StaticMap(400, 200, googleApiKey);
		map.setLocation(new StaticLocation("Eiffeltower"), 16);
		map.setMaptype(Maptype.hybrid);

		create(map, "location.png");
	}

	@Test
	public void createMarkers() throws ClientProtocolException, IOException {
		StaticMap map = new StaticMap(400, 200, googleApiKey);
		map.setMaptype(Maptype.hybrid);

		map.addMarker(new StaticMarker("Eiffeltower"));

		StaticMarker notreDame = new StaticMarker(48.853000, 2.349983);
		notreDame.setLabel('N');
		notreDame.setColor("orange");
		map.addMarker(notreDame);

		create(map, "markers.png");
	}

	@Test
	public void createCustomMarker() throws ClientProtocolException, IOException {
		StaticMap map = new StaticMap(400, 200, googleApiKey);
		map.setMaptype(Maptype.hybrid);

		StaticMarker m1 = new StaticMarker(50.844943, 6.856998);
		m1.setCustomIconUrl("http://cableparks.info/poi.png");
		map.addMarker(m1);

		StaticMarker m2 = new StaticMarker(50.844782, 6.856730);
		m2.setCustomIconUrl("http://cableparks.info/poi_2.png");
		map.addMarker(m2);

		create(map, "customMarkers.png");
	}

	private void create(StaticMap map, String filename) throws ClientProtocolException, IOException {
		File myUrl = new File("src/test/resources/" + filename + ".txt");
		FileWriter writer = new FileWriter(myUrl);
		writer.write(map.toString().replace(googleApiKey, "YOUR-API-KEY"));
		writer.close();

		File myFile = new File("src/test/resources/" + filename);

		CloseableHttpClient client = HttpClients.createDefault();
		try (CloseableHttpResponse response = client.execute(new HttpGet(map.toString()))) {
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() != 200)
				throw new RuntimeException(response.getStatusLine().toString());
			if (entity != null) {
				try (FileOutputStream outstream = new FileOutputStream(myFile)) {
					entity.writeTo(outstream);
				}
			}
		}
	}

}