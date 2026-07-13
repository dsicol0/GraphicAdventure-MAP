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
 * Servizio RESTful che agisce da Client per l'API esterna OpenWeatherMap.
 *
 */
public class WeatherRESTService {
    
    public static final String API_KEY = "f030089a65dc30ce65772b1bc1904bb4"; 
    public static final String CITY = "Bari";


    /**
     * Classe contenitore utilizzata per mappare il JSON di risposta di OpenWeatherMap.
     * La libreria Gson sfrutterà la Reflection (identificazione dei tipi a run-time) 
     * per popolare automaticamente questi attributi partendo dalla stringa JSON.
     */
    public static class OpenWeatherResponse {
        public WeatherData[] weather;
    }

    /**
     * Struttura annidata che rappresenta il singolo blocco di dati meteo nel JSON.
     */
    public static class WeatherData {
        public String main;
        public String icon;
    }

    /**
     * Esegue una chiamata REST e restituisce una stringa 
     * rappresentante l'atmosfera attuale per la GUI.
     *
     * Costruisce l'URI in modo dinamico aggiungendo i parametri ({@code queryParam}) 
     * necessari per l'autenticazione e la ricerca. Successivamente, estrae il payload 
     * della risposta e lo affida alla libreria {@link Gson} per la deserializzazione
     * dal formato testuale JSON agli oggetti Java.
     *
     * @return Una stringa formattata (es. "RAIN", "SUN") che il motore grafico 
     * utilizzerà per applicare il filtro visivo corretto. Restituisce "DEFAULT" 
     * in caso di errore di rete.
     */
    public static String getCurrentAtmosphere() {
        try {
            
            // Inizializzazione del Client
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("https://api.openweathermap.org/data/2.5");
            
            // Costruzione ed esecuzione della richiesta GET richiedendo esplicitamente il formato JSON
            Response resp = target.path("weather")
                    .queryParam("appid", API_KEY)
                    .queryParam("q", CITY)
                    .request(MediaType.APPLICATION_JSON).get();

            // Estrazione del body (payload) della risposta sotto forma di Stringa
            String jsonResponse = resp.readEntity(String.class);

            // Deserializzazione del JSON in oggetti Java tramite Gson
            Gson gson = new Gson();
            OpenWeatherResponse data = gson.fromJson(jsonResponse, OpenWeatherResponse.class);

            // Lettura del dato estratto e conversione nel formato interno del gioco
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
        
        
        return "DEFAULT"; 
    }
}