package com.crawlab.request;

import com.crawlab.RessourcesUtils;
import com.crawlab.model.CurrentUserModel;
import org.json.JSONObject;

public class CurrentUserRequest {
    private CurrentUserRequest(){}

    public static CurrentUserModel getCurrentUser() {
        var payload = """
            query {
                currentUser {
                    id
                    name
                }
            }
            """;
        var resultGraphQL = GraphQLHelper.getResponseGraphQl(payload);

        // retrieve the JSON object we need
        var resultJson = new JSONObject(resultGraphQL);
        var currentUserJson = resultJson.getJSONObject("data").getJSONObject("currentUser");

        return RessourcesUtils.mapJson(currentUserJson, CurrentUserModel.class);
    }
}
