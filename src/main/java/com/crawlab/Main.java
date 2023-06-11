package com.crawlab;

import com.crawlab.model.NodeModel;
import com.crawlab.model.ProjectModel;
import com.crawlab.model.ProjectRepoModel;
import com.crawlab.model.ProjectsModel;
import com.crawlab.processing.JavaPom;
import com.crawlab.request.CurrentUserRequest;
import com.crawlab.request.ProjectsRequest;
import com.crawlab.request.RepositoryRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;

/**
 * Interactive GraphQL console here : https://gitlab.com/-/graphql-explorer
 */
public class Main {

    public static void main(String[] args) {
        // setup logger configuration
        try {
            var inputStream = RessourcesUtils.getFileFromResourceAsStream("logging.properties");
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (IOException e) {
            LoggerManager.log.severe(e.getMessage());
            throw new RuntimeException("Error reading logging.properties file");
        }

        var currentUser = CurrentUserRequest.getCurrentUser();
        LoggerManager.log.info(currentUser.getName());

        // get blacklist items from config file
        var prop = RessourcesUtils.getPropertiesFromFile("config.properties");
        var groupBlackListProp = prop.getProperty("project.group.blacklist");
        var groupBlackList = Arrays.stream(groupBlackListProp.split(",")).map(String::trim).toList();

        // retrieve and filter all projects (GitLab send 100 elements for each page)
        var hasNextPage = true;
        var afterCursor = "";
        var filteredList = new ArrayList<ProjectRepoModel>();
        while (hasNextPage) {
            var projList = ProjectsRequest.getProjectList(null, afterCursor);
            filteredList.addAll(filterProjectsNodes(projList, groupBlackList, "pom.xml"));
            // update info for the next loop
            hasNextPage = projList.getPageInfo().getHasNextPage();
            afterCursor = projList.getPageInfo().getEndCursor();
        }

        // processing raw files
        var javaLibHashMap = new HashMap<String, Integer>();
        for (ProjectRepoModel projectRepo : filteredList) {
            LoggerManager.log.info("Processing project with POM : " + projectRepo.getFullPath());
            var javaLibList = JavaPom.processProject(projectRepo);
            mergeList(javaLibHashMap, javaLibList);
        }
        LoggerManager.log.info(String.format("Result : %s ", javaLibHashMap));
    }

    private static List<ProjectRepoModel> filterProjectsNodes(ProjectsModel projectsModel,
                                                              List<String> groupBlackList,
                                                              String fileName) {
        var filteredList = new ArrayList<ProjectRepoModel>();
        for (ProjectModel projectModel : projectsModel.getProjects()) {
            if (projectModel.getGroup() == null ||
                    groupBlackList.contains(projectModel.getGroup().getName()))
                // the project is blacklisted
                continue;

            LoggerManager.log.info("Search file in project : " + projectModel.getFullPath());
            ProjectRepoModel projectRepo = RepositoryRequest.getFileList(projectModel.getFullPath());

            if (projectRepo.getNodes().isEmpty())
                // this repo has no file, we continue with the next one
                continue;

            for (NodeModel node : projectRepo.getNodes()) {
                if (node.getName().equals(fileName)) {
                    filteredList.add(projectRepo);
                    break;
                }
            }
        }
        return filteredList;
    }

    private static void mergeList(HashMap<String, Integer> map, Set<String> set) {
        for (String item : set) {
            if (map.containsKey(item)) {
                map.compute(item, (s, integer) -> integer += 1);
            }
            else {
                map.put(item, 1);
            }
        }
    }
}
