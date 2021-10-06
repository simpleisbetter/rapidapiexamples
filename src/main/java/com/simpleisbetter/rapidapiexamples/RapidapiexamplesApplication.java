package com.simpleisbetter.rapidapiexamples;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import com.google.gson.Gson;

public class RapidapiexamplesApplication {

	public static void main(String[] args) throws Exception {

		// Any kind of credential should never be embedded in the source code, as there are bots that scan github checkins that 
		// will steal your credentials (like and API key) and use them for their owners purposes.

		// to run this, ensure you have a System Environment varibale called RAPID_API_KEY set to your own api key from rapid-api.

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(
				"https://weather-com.p.rapidapi.com/v3/wx/forecast/hourly/1day?geocode=34.080911%2C-118.270406&units=e&language=en"))
				.header("x-rapidapi-host", "weather-com.p.rapidapi.com")
				.header("x-rapidapi-key", System.getenv("RAPID_API_KEY"))
				.method("GET", HttpRequest.BodyPublishers.noBody()).build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200) {

			// Simple JSON Serializer/Deserializer
			Gson gson = new Gson();
			WeatherDto dto = gson.fromJson(response.body(), WeatherDto.class);

			// With the index of the Max Temp we can get to the matching values of the rest
			// of the day
			OptionalInt maxTempIndex = IntStream.range(0, dto.getTemperature().size()).parallel()
					.reduce((a, b) -> dto.getTemperature().get(a) < dto.getTemperature().get(b) ? b : a);

			System.out.printf("Max Temp %d at %s%n", dto.getTemperature().get(maxTempIndex.getAsInt()),
					dto.getValidTimeLocal().get(maxTempIndex.getAsInt()));
		} else {
			System.out.println("Something went wrong.");
			System.out.println(response.body());
		}

	}

}
