package com.crawlab.request;

import com.crawlab.RessourcesUtils;
import com.crawlab.model.RawModel;
import org.json.JSONObject;

import java.util.List;

public class RawFileRequest {
    private RawFileRequest(){}

    public static List<RawModel> getRaw(String fullPath, String fileName) {
        var payload = """
            query {
                 project(fullPath:"%1$s"){
                     id
                     name
                     fullPath
                     visibility
                     repository{
                         blobs(paths:["%2$s"]){
                             nodes{
                                 rawTextBlob
                             }
                         }
                     }
                 }
             }
             """;
        var formatPayload = String.format(payload, fullPath, fileName);
        var resultGraphQL = GraphQLHelper.getResponseGraphQl(formatPayload);

        // retrieve the JSON object we need
        var resultJson = new JSONObject(resultGraphQL);
        var projectsJson = resultJson
                .getJSONObject("data")
                .getJSONObject("project")
                .getJSONObject("repository")
                .getJSONObject("blobs")
                .getJSONArray("nodes");

        var o = RessourcesUtils.mapJsonToList(projectsJson, RawModel.class);
        return (List<RawModel>) o;
    }
}
