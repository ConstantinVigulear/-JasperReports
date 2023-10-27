package util;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;
import java.io.Serial;
import java.util.Locale;
import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.datatype.BigDecimalType;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.tableofcontents.TableOfContentsCustomizerBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;

/**
 * @author Ricardo Mariaca (r.mariaca@dynamicreports.org)
 */
public class Templates {
  public static final StyleBuilder rootStyle;
  public static final StyleBuilder boldStyle;
  public static final StyleBuilder italicStyle;
  public static final StyleBuilder boldCenteredStyle;
  public static final StyleBuilder bold12CenteredStyle;
  public static final StyleBuilder bold18CenteredStyle;
  public static final StyleBuilder bold22CenteredStyle;
  public static final StyleBuilder columnStyle;
  public static final StyleBuilder columnTitleStyle;
  public static final StyleBuilder groupStyle;
  public static final ReportTemplateBuilder reportTemplate;
  public static final CurrencyType currencyType;
  public static final ComponentBuilder<?, ?> dynamicReportsComponent;
  public static final ComponentBuilder<?, ?> footerComponent;

  static {
    rootStyle = stl.style().setPadding(2);
    boldStyle = stl.style(rootStyle).bold();
    italicStyle = stl.style(rootStyle).italic();
    boldCenteredStyle = stl.style(boldStyle);
    bold12CenteredStyle = stl.style(boldCenteredStyle).setFontSize(12);
    bold18CenteredStyle = stl.style(boldCenteredStyle).setFontSize(18);
    bold22CenteredStyle = stl.style(boldCenteredStyle).setFontSize(22);
    columnStyle = stl.style(rootStyle);
    columnTitleStyle = stl.style(columnStyle).setBorder(stl.pen1Point());
    groupStyle = stl.style(boldStyle);

    StyleBuilder crosstabGroupStyle = stl.style(columnTitleStyle);
    StyleBuilder crosstabGroupTotalStyle = stl.style(columnTitleStyle);
    StyleBuilder crosstabGrandTotalStyle = stl.style(columnTitleStyle);
    StyleBuilder crosstabCellStyle = stl.style(columnStyle).setBorder(stl.pen1Point());
    TableOfContentsCustomizerBuilder tableOfContentsCustomizer =
        tableOfContentsCustomizer().setHeadingStyle(0, stl.style(rootStyle).bold());
    reportTemplate =
        template()
            .setLocale(Locale.ENGLISH)
            .setColumnStyle(columnStyle)
            .setColumnTitleStyle(columnTitleStyle)
            .setGroupStyle(groupStyle)
            .setGroupTitleStyle(groupStyle)
            .highlightDetailEvenRows()
            .crosstabHighlightEvenRows()
            .setCrosstabGroupStyle(crosstabGroupStyle)
            .setCrosstabGroupTotalStyle(crosstabGroupTotalStyle)
            .setCrosstabGrandTotalStyle(crosstabGrandTotalStyle)
            .setCrosstabCellStyle(crosstabCellStyle)
            .setTableOfContentsCustomizer(tableOfContentsCustomizer);
    currencyType = new CurrencyType();

    dynamicReportsComponent =
        cmp.horizontalList(
            cmp.text("Example anno=2021").setHorizontalTextAlignment(HorizontalTextAlignment.LEFT));
    footerComponent =
        cmp.horizontalList(
            cmp.currentDate()
                .setPattern("dd-MM-yyyy")
                .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT),
            cmp.pageXofY().setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT));
  }

  public static class CurrencyType extends BigDecimalType {
    @Serial private static final long serialVersionUID = 1L;

    @Override
    public String getPattern() {
      return "$ #,###.00";
    }
  }

}
