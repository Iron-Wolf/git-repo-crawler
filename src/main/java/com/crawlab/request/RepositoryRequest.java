package com.crawlab.request;

import com.crawlab.RessourcesUtils;
import com.crawlab.model.ProjectRepoModel;
import org.json.JSONObject;

public class RepositoryRequest {
    private RepositoryRequest(){}

    public static ProjectRepoModel getFileList(String search) {
        var payload = """
            query {
                 project(fullPath:"%s"){
                     id
                     name
                     fullPath
                     visibility
                     repository{
                         tree{
                             blobs{
                                 nodes{
                                     name
                                 }
                             }
                         }
                     }
                 }
             }
             """;
        var formatPayload = String.format(payload, search);
        var resultGraphQL = GraphQLHelper.getResponseGraphQl(formatPayload);

        // retrieve the JSON object we need
        var resultJson = new JSONObject(resultGraphQL);
        var projectsJson = resultJson
                .getJSONObject("data")
                .getJSONObject("project");

        return RessourcesUtils.mapJson(projectsJson, ProjectRepoModel.class);
    }
}
