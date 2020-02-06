package org.eugene.telegram.util;

import org.eugene.telegram.component.Weather;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import org.telegram.telegrambots.meta.api.objects.Location;

/**
 * Utils class with static methods
 */
public class BotUtil {
    private static final String API_CALL_TEMPLATE_CITY =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&APPID=%s";

    private static final String API_CALL_TEMPLATE_COORDINATES =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric&APPID=%s";

    private static final String RESULT_INFO_TEMPLATE = "Location: %s\nWeather: %s\nTemperature: %s ℃\nHumidity: %s";

    public static String getWeatherByLocation(Location location, String weatherToken) {
        Weather weather =new Weather();
        String urlString = String.format
                (API_CALL_TEMPLATE_COORDINATES, location.getLatitude(), location.getLongitude(), weatherToken);
        writeWeather(weather, urlString);
        return String.format(RESULT_INFO_TEMPLATE, weather.getName(), weather.getMain(),
                                                   weather.getTemp(), weather.getHumidity());
    }


    public static String getWeatherByCity(String city, String weatherToken) {
        Weather weather = new Weather();
        String urlString = String.format(API_CALL_TEMPLATE_CITY, city, weatherToken);
        writeWeather(weather, urlString);
        return String.format(RESULT_INFO_TEMPLATE, weather.getName(), weather.getMain(),
                weather.getTemp(), weather.getHumidity());
    }

    private static void writeWeather(Weather weather, String urlToParse) {
        URL url = null;
        try {
            url = new URL(urlToParse);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Scanner in = null;
        try {
            in = new Scanner((InputStream) url.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder result = new StringBuilder();
        while (in.hasNext()) {
            result.append(in.nextLine());
        }

        JSONObject object = new JSONObject(result.toString());
        weather.setName(object.getString("name"));

        JSONObject main = object.getJSONObject("main");
        weather.setTemp(main.getDouble("temp"));
        weather.setHumidity(main.getDouble("humidity"));

        JSONArray getArray = object.getJSONArray("weather");
        for (int i = 0; i < getArray.length(); i++) {
            JSONObject obj = getArray.getJSONObject(i);
            weather.setIcon((String) obj.get("icon"));
            weather.setMain((String) obj.get("main"));
        }
    }
}
