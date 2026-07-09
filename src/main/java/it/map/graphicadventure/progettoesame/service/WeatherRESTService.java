/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.service;

import com.google.gson.Gson;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author antoniostilla
 */
public class WeatherRESTService {
    
    public static final String API_KEY = "f030089a65dc30ce65772b1bc1904bb4"; 
    public static final String CITY = "Bari";

    // ==========================================
    // CLASSI POJO PER IL PARSING CON GSON 
    // ==========================================
    
    /**
     * Classe contenitore per mappare il JSON di OpenWeatherMap.
     */
    public static class OpenWeatherResponse {
        public WeatherData[] weather;
    }

    /**
     * Struttura dei dati meteo nel JSON.
     */
    public static class WeatherData {
        public String main;
        public String icon;
    }

    // ==========================================
    // LOGICA REST JAX-RS
    // ==========================================

    /**
     * Esegue una chiamata REST GET e restituisce l'atmosfera attuale.
     */
    public static String getCurrentAtmosphere() {
        try {
            // 1. Configurazione del Client REST JAX-RS
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("https://api.openweathermap.org/data/2.5");
            
            // 2. Chiamata GET all'endpoint /weather
            Response resp = target.path("weather")
                    .queryParam("appid", API_KEY)
                    .queryParam("q", CITY)
                    .request(MediaType.APPLICATION_JSON).get();

            // Legge l'entità della risposta come stringa JSON
            String jsonResponse = resp.readEntity(String.class);

            // 3. Parsing del JSON in Oggetti Java tramite Gson
            Gson gson = new Gson();
            OpenWeatherResponse data = gson.fromJson(jsonResponse, OpenWeatherResponse.class);

            // 4. Traduzione dei dati nell'atmosfera del gioco
            if (data != null && data.weather != null && data.weather.length > 0) {
                String mainCondition = data.weather[0].main;
                
                if ("Thunderstorm".equals(mainCondition)) {
                    return "THUNDERSTORM"; // Tuoni e Fulmini
                } else if ("Rain".equals(mainCondition) || "Drizzle".equals(mainCondition)) {
                    return "RAIN";         // Pioggia normale
                } else if ("Clouds".equals(mainCondition)) {
                    return "CLOUDS";       // Coperto/Nuvoloso
                } else if ("Clear".equals(mainCondition)) {
                    return "SUN";          // Soleggiato/Sereno
                } else {
                    return "FOG";          // Fallback per Nebbia, Foschia, ecc.
                }
            }

        } catch (Exception e) {
            System.err.println("Errore di connessione a OpenWeatherMap: " + e.getMessage());
        }
        
        // Fallback in caso di mancanza di rete
        return "DEFAULT"; 
    }
}