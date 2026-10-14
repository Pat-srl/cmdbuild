package org.cmdbuild;

import java.io.File;
import static java.lang.String.format;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author afelice
 */
public class TestPomDependencies {

    @Test
    public void testOptimizePOMDepenencies() {
        Path root = FileSystems.getDefault().getPath("").toAbsolutePath() // <project_root>/core/all
                .getParent() // <project_root>/core
                .getParent(); // <project_root>
        System.out.println(format("optimizePOMDepenencies - root directory found =< %s >", root.toString()));

    }
}

class ParsePOMs {

    void processPomDependencies() {
        String directoryPath = "";
        Map<String, List<String>> dependenciesMap = parsePomFiles(directoryPath);

        // Stampa la mappa risultante
        for (Map.Entry<String, List<String>> entry : dependenciesMap.entrySet()) {
            System.out.println("Project: " + entry.getKey());
            System.out.println("Dependencies: " + entry.getValue());
            System.out.println("------------");
        }
    }

    private Map<String, List<String>> parsePomFiles(String directoryPath) {
        Map<String, List<String>> dependenciesMap = new HashMap<>();

        File directory = new File(directoryPath);
        File[] pomFiles = directory.listFiles((dir, name) -> name.equals("pom.xml"));

        if (pomFiles != null) {
            for (File pomFile : pomFiles) {
                parseSinglePom(pomFile, dependenciesMap);
            }
        }

        return dependenciesMap;
    }

    private static void parseSinglePom(File pomFile, Map<String, List<String>> dependenciesMap) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pomFile);

            doc.getDocumentElement().normalize();

            String projectName = doc.getElementsByTagName("project").item(0).getAttributes().getNamedItem("artifactId").getTextContent();

            NodeList dependencies = doc.getElementsByTagName("dependency");

            List<String> dependencyList = new LinkedList<>();
            for (int i = 0; i < dependencies.getLength(); i++) {
                Node dependencyNode = dependencies.item(i);
                if (dependencyNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element dependencyElement = (Element) dependencyNode;
                    String groupId = dependencyElement.getElementsByTagName("groupId").item(0).getTextContent();
                    String artifactId = dependencyElement.getElementsByTagName("artifactId").item(0).getTextContent();
                    dependencyList.add(groupId + ":" + artifactId);
                }
            }

            dependenciesMap.put(projectName, dependencyList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
