package util;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataSourceFromXML {
  private static final String XML_PATH = "src/main/resources/reports/datasource/MyDataBase.xml";
  public static JRMapCollectionDataSource getDataSourceFromXML() {
    try {
      File inputFile = new File(XML_PATH);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document document = dBuilder.parse(inputFile);
      document.getDocumentElement().normalize();
      NodeList nList = document.getElementsByTagName("holydays");

      // Jasper Report Data source
      return new JRMapCollectionDataSource(populateCollection(nList));

    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Collection<Map<String, ?>> populateCollection(NodeList nList) {
    // List of data for JRMapCollectionDataSource
    Collection<Map<String, ?>> results = new ArrayList<>();

    // Populate collection
    for (int temp = 0; temp < nList.getLength(); temp++) {
      Node nNode = nList.item(temp);

      if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        Element eElement = (Element) nNode;

        String countryValue =
            eElement.getElementsByTagName("COUNTRY").item(0).getTextContent().equals("Italia")
                ? "IT"
                : "MD";
        String dateValue =
            eElement.getElementsByTagName("DATE").item(0).getTextContent() + " 12:00 AM";
        String nameValue = eElement.getElementsByTagName("NAME").item(0).getTextContent();

        results.add(
            new HashMap<>() {
              {
                put("COUNTRY", countryValue);
                put("DATE", dateValue);
                put("NAME", nameValue);
              }
            });
      }
    }

    return results;
  }
}
