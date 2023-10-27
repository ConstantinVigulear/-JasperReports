import net.sf.jasperreports.engine.*;
import util.DataSourceFromXML;

public class ReportFromXML {

  private static final String TEMPLATE_PATH =
      "src/main/resources/reports/templates/ManualFromXML.jrxml";
  private static final String PDF_PATH = "src/main/resources/reports/static/CodedFromXML.pdf";

  public static void main(String[] args) {

    try {
      JasperReport report = JasperCompileManager.compileReport(TEMPLATE_PATH);

      JasperPrint print =
          JasperFillManager.fillReport(report, null, DataSourceFromXML.getDataSourceFromXML());

      JasperExportManager.exportReportToPdfFile(print, PDF_PATH);
    } catch (JRException e) {
      throw new RuntimeException(e);
    }
  }
}
