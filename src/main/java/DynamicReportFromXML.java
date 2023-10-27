import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import util.DataSourceFromXML;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class DynamicReportFromXML {

  private final static String FILE_OUTPUT_PATH = "src/main/resources/reports/static/DynamicallyCodedFromXML.pdf";
  private final static String PICTURE_PATH = "src/main/resources/reports/images/cedacri.png";

  public static void main(String[] args) {
    new DynamicReportFromXML().build();
  }

  private void build() {

    try (OutputStream outputStream =
        new FileOutputStream(FILE_OUTPUT_PATH)) {

      // Styles
      StyleBuilder titleStyle = stl.style().setFontSize(26);
      StyleBuilder columnTitleStyle =
          stl.style().setBackgroundColor(new Color(112, 205, 255)).setBorder(stl.pen1Point());
      StyleBuilder columnStyle =
          stl.style()
              .setBorder(stl.pen1Point())
              .setLeftPadding(5)
              .setFontName("SansSerif")
              .setFontSize(14);
      StyleBuilder pictureStyle =
          stl.style().setTopPadding(5).setHorizontalImageAlignment(HorizontalImageAlignment.CENTER);

      // Create report
      report()
          .title(
              cmp.horizontalList(
                  cmp.text("Holidays").setStyle(titleStyle),
                  cmp.image(PICTURE_PATH)))
          .pageHeader(
              cmp.text("Page Header").setHorizontalTextAlignment(HorizontalTextAlignment.CENTER))
          .columns(
              col.column("country", "COUNTRY", type.stringType())
                  .setFixedWidth(120)
                  .setTitleFixedHeight(30)
                  .setTitleStyle(columnTitleStyle),
              col.column("name", "NAME", type.stringType())
                  .setFixedHeight(30)
                  .setFixedWidth(250)
                  .setTitleStyle(columnTitleStyle),
              col.column("date", "DATE", type.stringType())
                  .setTitleStyle(columnTitleStyle)) // adds columns
          .setColumnStyle(columnStyle)
          .columnFooter(
              cmp.text("Column Footer").setHorizontalTextAlignment(HorizontalTextAlignment.CENTER))
          .pageFooter(
              cmp.horizontalList(
                  cmp.currentDate()
                      .setPattern("dd-MM-yyyy")
                      .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
                  cmp.pageXofY().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT)))
          .summary(
              cmp.image("src/main/resources/reports/images/taf.jpg")
                  .setStyle(pictureStyle)
                  .setWidth(235)
                  .setHeight(239))
          .setDataSource(DataSourceFromXML.getDataSourceFromXML())
          .show()
          .toPdf(outputStream);

    } catch (IOException | DRException e) {
      throw new RuntimeException(e);
    }
  }
}
