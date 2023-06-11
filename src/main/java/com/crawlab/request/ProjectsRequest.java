package com.crawlab.request;

import com.crawlab.RessourcesUtils;
import com.crawlab.model.ProjectsModel;
import com.crawlab.model.metadata.PageInfoModel;
import com.crawlab.model.metadata.QueryComplexity;
import com.crawlab.model.ProjectModel;
import org.json.JSONObject;

import java.util.List;

public class ProjectsRequest {
    private ProjectsRequest(){}

    /**
     * Return a {@link ProjectsModel} with a list of project and the {@link PageInfoModel}
     * with data regarding pagination.
     * @param search Search pattern on the name, path and description (can be NULL)
     * @param after Identifier from which we want the result
     */
    public static ProjectsModel getProjectList(String search, String after) {
        var payload = """
            query {
                queryComplexity {
                    score
                    limit
                }
                # liste les projets (recherche à partir de 3 caractères)
                projects(search:"%1$s", after:"%2$s"){
                    pageInfo {
                        endCursor
                        hasNextPage
                    }
                    nodes{
                        id
                        name
                        fullPath
                        group{
                            name
                            fullPath
                        }
                        visibility
                    }
                }
            }
            """;
        search = search == null ? "" : search;
        after = after == null ? "" : after;
        var formatPayload = String.format(payload, search, after);
        var resultGraphQL = GraphQLHelper.getResponseGraphQl(formatPayload);

        // retrieve the JSON object we need
        var resultJson = new JSONObject(resultGraphQL);
        var queryComplexityJson = resultJson
                .getJSONObject("data")
                .getJSONObject("queryComplexity");
        var pageInfoJson = resultJson
                .getJSONObject("data")
                .getJSONObject("projects")
                .getJSONObject("pageInfo");
        var projectsJson = resultJson
                .getJSONObject("data")
                .getJSONObject("projects")
                .getJSONArray("nodes");

        var queryComplexity = RessourcesUtils.mapJson(queryComplexityJson, QueryComplexity.class);
        var pageInfo = RessourcesUtils.mapJson(pageInfoJson, PageInfoModel.class);
        var projectListObj = RessourcesUtils.mapJsonToList(projectsJson, ProjectModel.class);
        @SuppressWarnings("unchecked")
        var projectList = (List<ProjectModel>) projectListObj;

        var projects = new ProjectsModel();
        projects.setQueryComplexity(queryComplexity);
        projects.setPageInfo(pageInfo);
        projects.setProjects(projectList);
        return projects;
    }
}
