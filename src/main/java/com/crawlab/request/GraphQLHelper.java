package com.crawlab.request;

import com.crawlab.LoggerManager;
import com.crawlab.RessourcesUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GraphQLHelper {
    private GraphQLHelper(){}

    /**
     * Construct GraphQL query and return the result
     * @param payload A JSON formatted String (accept commented lines)
     * @return The result String from GitLab API
     */
    public static String getResponseGraphQl(String payload) {
        var prop = RessourcesUtils.getPropertiesFromFile("config.properties");
        var httpPost = new HttpPost(prop.getProperty("gitlab.uri"));
        httpPost.addHeader("PRIVATE-TOKEN", prop.getProperty("gitlab.privateToken"));
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-Type", "application/json");

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("query", sanitizeJson(payload));

        // open a connection and return the body response
        try (var client = HttpClients.createDefault()){
            var entity = new StringEntity(jsonObj.toString());
            httpPost.setEntity(entity);
            // send HTTP request
            var response = client.execute(httpPost);

            // get the response with a Buffer
            var reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            var line = "";
            var builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();

        } catch (IOException e) {
            LoggerManager.log.severe(e.getMessage());
            throw new RuntimeException("Error requesting Gitlab API");
        }
    }

    /**
     * Remove commented line from JSON String
     */
    private static String sanitizeJson(String jsonString) {
        // split String in an array
        String[] lines = jsonString.split("\n");
        // search pattern on each line
        for(int i=0;i<lines.length;i++){
            // remove "commented" line
            if(lines[i].trim().startsWith("#")){
                lines[i]="";
            }
        }
        // unsplit the array in a valid JSON String object
        return String.join("\n", lines);
    }
}
