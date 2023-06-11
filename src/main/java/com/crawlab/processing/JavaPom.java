package com.crawlab.processing;

import com.crawlab.model.ProjectRepoModel;
import com.crawlab.model.RawModel;
import com.crawlab.request.RawFileRequest;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavaPom {

    public static Set<String> processProject(ProjectRepoModel projectRepoModel) {
        var libHashSet = new HashSet<String>();

        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            var builder = factory.newDocumentBuilder();

            var rawFiles = RawFileRequest.getRaw(projectRepoModel.getFullPath(), "pom.xml");
            for (RawModel rawFile : rawFiles) {
                var input = new ByteArrayInputStream(rawFile.getRawTextBlob().getBytes(StandardCharsets.UTF_8));
                var doc = builder.parse(input);

                // loop on each "dependency" node
                var dependencyNodeList = doc.getElementsByTagName("dependency");
                for (int i = 0; i < dependencyNodeList.getLength(); i++) {
                    var dependencyNode = dependencyNodeList.item(i);
                    if (dependencyNode.getNodeType() == Node.ELEMENT_NODE) {
                        var dependencyElem = (Element) dependencyNode;
                        // check grandparent directly (parent is always a "dependencies")
                        var grandparentNode = dependencyElem.getParentNode().getParentNode().getNodeName();
                        // retrieve lib if grandparent is known
                        if (List.of("project","dependencyManagement").contains(grandparentNode)) {
                            var groupId = dependencyElem.getElementsByTagName("groupId").item(0).getTextContent();
                            libHashSet.add(groupId);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return libHashSet;
    }
}
