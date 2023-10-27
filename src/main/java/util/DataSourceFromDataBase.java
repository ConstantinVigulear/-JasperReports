package util;

import net.sf.jasperreports.engine.JRResultSetDataSource;
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
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Constantin Vigulear
 */
public class DataSourceFromDataBase {
  private static final String XML_PATH = "src/main/resources/reports/datasource/MyDataBase.xml";
  private static final String JDBC_URL =
      "jdbc:oracle:thin:@//localhost:1521/XEPDB1?useSSL=false&amp;serverTimezone=UTC";
  private static final String USER = "crme059";
  private static final String PASSWORD = "root";
  private static final String GET_ALL_HOLIDAYS_FOR_CROSSTAB =
      "SELECT h.COUNTRY, SUBSTR(h.\"date\", 4, 2) AS \"DATE\", h.NAME fROM HOLIDAYS h ";

  private static final String GET_ALL_HOLIDAYS_FOR_BARCHART =
      "SELECT h.COUNTRY, SUBSTR(h.\"date\", 4, 2) AS \"DATE\", COUNT(*) AS \"COUNT\" FROM HOLIDAYS h GROUP BY SUBSTR(h.\"date\", 4, 2), h.COUNTRY ORDER BY \"DATE\"";
  private static final String SAVE_HOLIDAY = "insert into holidays values (?, ?, ?)";

  public DataSourceFromDataBase() {}

  public JRResultSetDataSource getResultSetDataSourceForCrossTab()
      throws SQLException, ParserConfigurationException, IOException, SAXException {

    ResultSet resultSet;

    Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    PreparedStatement getAllHolidays = connection.prepareStatement(GET_ALL_HOLIDAYS_FOR_CROSSTAB);

    //    populateDataBase(connection);

    resultSet = getAllHolidays.executeQuery();

    return new JRResultSetDataSource(resultSet);
  }

  public JRResultSetDataSource getResultSetDataSourceForBarchart()
          throws SQLException, ParserConfigurationException, IOException, SAXException {

    ResultSet resultSet;

    Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    PreparedStatement getAllHolidays = connection.prepareStatement(GET_ALL_HOLIDAYS_FOR_BARCHART);

    //    populateDataBase(connection);

    resultSet = getAllHolidays.executeQuery();

    return new JRResultSetDataSource(resultSet);
  }

  private static void populateDataBase(Connection connection)
      throws ParserConfigurationException, IOException, SAXException {

    File inputFile = new File(XML_PATH);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document document = dBuilder.parse(inputFile);
    document.getDocumentElement().normalize();
    NodeList nList = document.getElementsByTagName("holydays");

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

        Map<String, String> holiday = new HashMap<>();
        holiday.put("COUNTRY", countryValue);
        holiday.put("DATE", dateValue);
        holiday.put("NAME", nameValue);

        saveHolidayToDataBase(holiday, connection);
      }
    }
  }

  private static void saveHolidayToDataBase(Map<String, String> map, Connection connection) {
    try (PreparedStatement saveHoliday = connection.prepareStatement(SAVE_HOLIDAY)) {

      saveHoliday.setString(1, map.get("COUNTRY"));
      saveHoliday.setString(2, map.get("DATE"));
      saveHoliday.setString(3, map.get("NAME"));

      saveHoliday.executeQuery();

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
