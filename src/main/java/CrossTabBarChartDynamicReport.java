import net.sf.dynamicreports.report.builder.chart.BarChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabColumnGroupBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabRowGroupBuilder;
import net.sf.dynamicreports.report.constant.*;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.xml.sax.SAXException;
import util.DataSourceFromDataBase;
import util.DataSourceFromXML;
import util.Templates;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;

public class CrossTabBarChartDynamicReport {

  private static final String FILE_OUTPUT_PATH =
      "src/main/resources/reports/static/crosstabBarchartDynamicallyCodedFromXML.pdf";

  public static void main(String[] args) {
    new CrossTabBarChartDynamicReport().build();
  }

  private void build() {
    getJasperReportBuilder();
  }

  private void getJasperReportBuilder() {

    try (OutputStream outputStream = new FileOutputStream(FILE_OUTPUT_PATH)) {

      report()
          .setTemplate(Templates.reportTemplate)
          .summary(
              // crosstab element
              getCrosstabBuilder(),
              // bar chart element
              getBarChartBuilder())
          .pageFooter(Templates.footerComponent)
          .show()
          .toPdf(outputStream);

    } catch (IOException
        | DRException
        | SQLException
        | ParserConfigurationException
        | SAXException e) {
      throw new RuntimeException(e);
    }
  }

  private CrosstabBuilder getCrosstabBuilder()
      throws SQLException, ParserConfigurationException, IOException, SAXException {

    CrosstabRowGroupBuilder<String> rowGroup =
        ctab.rowGroup("COUNTRY", String.class).setTotalHeader("Total");
    CrosstabColumnGroupBuilder<String> columnGroup =
        ctab.columnGroup("DATE", String.class).setTotalHeader("Total");

    CrosstabBuilder crosstab =
        ctab.crosstab()
            .setCellWidth(40)
            .headerCell(cmp.text("State / Mese"))
            .rowGroups(rowGroup)
            .columnGroups(columnGroup)
            // .setDataSource(processDatasourceForCrosstabFromXML()) // fetch data directly from XML
            .setDataSource(
                new DataSourceFromDataBase().getResultSetDataSourceForCrossTab()) // fetch data from database
            .measures(
                ctab.measure("NAME", String.class, Calculation.COUNT)
                    .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT));
    return crosstab;
  }

  private BarChartBuilder getBarChartBuilder() throws SQLException, ParserConfigurationException, IOException, SAXException {

    TextColumnBuilder<String> monthColumn = col.column("Month", "DATE", type.stringType());
    TextColumnBuilder<String> countryColumn = col.column("Country", "COUNTRY", type.stringType());
    TextColumnBuilder<Integer> countColumn = col.column("Quantity", "COUNT", type.integerType());

    return cht.barChart()
//        .setDataSource(processDatasourceForBarChartFromXML()) // data directly from XML
        .setDataSource(new DataSourceFromDataBase().getResultSetDataSourceForBarchart()) // data directly from DB
        .setTitle("Bar Chart")
        .setCategory(monthColumn)
        .series(cht.serie(countColumn).setSeries(countryColumn))
        .setShowValues(true)
        .setCategoryAxisFormat(cht.axisFormat().setLabel("State"));
  }

  private JRMapCollectionDataSource processDatasourceForCrosstabFromXML() {
    JRMapCollectionDataSource initialDataSource = DataSourceFromXML.getDataSourceFromXML();

    // transform dd/mm/yyyy 12:00 AM into number of month with no 0 in the beginning
    Collection<Map<String, ?>> initialData = initialDataSource.getData().stream().toList();
    Collection<Map<String, ?>> tempData = new ArrayList<>();

    initialData.forEach(
        e -> {
          String country = e.get("COUNTRY").toString();
          String month = e.get("DATE").toString().split("/")[1];
          month = month.charAt(0) == '0' ? String.valueOf(month.charAt(1)) : month;
          String name = e.get("NAME").toString();
          String finalMonth = month;
          tempData.add(
              new HashMap<>() {
                {
                  put("COUNTRY", country);
                  put("DATE", finalMonth);
                  put("NAME", name);
                }
              });
        });

    // sort data by month
    Collection<Map<String, ?>> finalTempData = new ArrayList<>();
    finalTempData =
        tempData.stream()
            .sorted(Comparator.comparingInt(o -> Integer.parseInt(o.get("DATE").toString())))
            .toList();

    return new JRMapCollectionDataSource(finalTempData);
  }

  private JRMapCollectionDataSource processDatasourceForBarChartFromXML() {
    JRMapCollectionDataSource initialDataSource = this.processDatasourceForCrosstabFromXML();
    Collection<Map<String, ?>> initialData = initialDataSource.getData().stream().toList();

    // get a set of countries with map of month and holidays per month
    Map<String, Map<String, Integer>> countries = new HashMap<>();
    initialData.forEach(
        e -> {
          countries.put(e.get("COUNTRY").toString(), getHashMapForMonthCount());
        });

    // count holidays per month for every country
    initialData.forEach(
        e -> {
          Map<String, Integer> monthCount = countries.get(e.get("COUNTRY"));
          String monthOfHoliday = e.get("DATE").toString();
          Integer countPerMonth = monthCount.get(monthOfHoliday);
          monthCount.put(monthOfHoliday, Integer.sum(countPerMonth, 1));
        });

    Collection<Map<String, ?>> results = new ArrayList<>();

    List<String> countryNames = countries.keySet().stream().toList();

    countryNames.forEach(
        countryName -> {
          Map<String, Integer> monthCount = countries.get(countryName);
          List<String> monthNumbers = monthCount.keySet().stream().toList();
          monthNumbers.forEach(
              monthNumber -> {
                results.add(
                    new HashMap<>() {
                      {
                        put("COUNTRY", countryName);
                        put("DATE", monthNumber);
                        put("COUNT", monthCount.get(monthNumber));
                      }
                    });
              });
        });

    return new JRMapCollectionDataSource(results);
  }

  private Map<String, Integer> getHashMapForMonthCount() {
    return new HashMap<>() {
      {
        put("1", 0);
        put("2", 0);
        put("3", 0);
        put("4", 0);
        put("5", 0);
        put("6", 0);
        put("7", 0);
        put("8", 0);
        put("9", 0);
        put("10", 0);
        put("11", 0);
        put("12", 0);
      }
    };
  }
}
