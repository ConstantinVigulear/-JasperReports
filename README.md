# JasperReports

Goal: To be able to compose and fill Jasper Reports in both GUI Jaspersoft Studio and code.

Task:
1. Draw report in Jasper Studio with one table and all report bands used (add static text in bands like Title, Page Header, Summary). Do not use table, use Text Fields in Detail Band.

MyDataBase.xml

2. Fill report programmatically with same jrxml template and data from JRMapCollectionDataSource, JRBeanCollectionDataSource, JRResultSetDataSource. See Examples of data sources.

3. Create DynamicReport with the same structure as in p.1, example is here

https://dynamicreports.readthedocs.io/en/master/GettingStarted.html#step-1-start
https://dynamicreports.lbayer.com/examples/simplereport_step01/
https://dynamicreports.readthedocs.io/en/master/GettingStarted.html

4. Create crosstab report using DynamicReport like
https://dynamicreports.lbayer.com/examples/crosstabreport/
https://dynamicreports.readthedocs.io/en/master/examples/crosstab/index.html

5. Create bar chart for crosstab. Show number of Holidays per month

Evaluation: Code review, report design (table borders, cell paddings, text alignment) and test for basics theory

Resources:
http://community.jaspersoft.com/wiki/getting-started-ireport-designer
http://community.jaspersoft.com/wiki/basic-report-components
http://community.jaspersoft.com/wiki/report-structure-jaspersoft-studio Important to read !!!
https://community.jaspersoft.com/wiki/jaspersoft-studio-tutorials-archive

https://community.jaspersoft.com/documentation/tibco-jaspersoft-studio-user-guide/v630/understanding-bands read carefully about bands
https://www.tutorialspoint.com/jasper_reports/jasper_report_sections.htm (Report sections)

https://www.mkyong.com/java/reporting-in-java-using-dynamicreports-and-jasperreports/
http://www.dynamicreports.org/examples/examples-overview

https://drive.google.com/drive/folders/1fC5e3hp9buVc8A3rYkwhjoeBaA_1fkjH?usp=sharing

Details: Possible specifications for report data source:
For crosstab: number of holidays per month / country

Expected reports

 

Example of Bar Chart:



Query for this report:

select country, name, data
from specialdate
where to_char(data, 'YYYY') = 2017

Crosstab structure (see Crosstab structure.png)
SELECT ID, COUNTRY, TO_CHAR(DATA,'mm/yyyy')
FROM specialdate
where to_char(data, 'YYYY') = '2017'

Examples of data sources

JRMapCollectionDataSource
http://edwin.baculsoft.com/2012/03/a-simple-java-desktop-jasperreport-example/
http://community.jaspersoft.com/questions/541434/pass-jrmapcollectiondatasource-parameter-reportserver-wsclient

JRBeanCollectionDataSource

/* List to hold Items */
List<Item> listItems = new ArrayList<Item>();

/* Create Items */
Item iPhone = new Item();
iPhone.setName("iPhone 6S");
iPhone.setPrice(65000.00);

Item iPad = new Item();
iPad.setName("iPad Pro");
iPad.setPrice(70000.00);

/* Add Items to List */
listItems.add(iPhone);
listItems.add(iPad);

/* Convert List to JRBeanCollectionDataSource */
JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(listItems);
https://www.javaquery.com/2015/11/how-to-fill-jasper-table-using.html
https://drive.google.com/open?id=1px5XS7n3yx6--h7qdFJKIOrKpQI4zkZr



JRResultSetDataSource

Class.forName("com.mysql.jdbc.Driver");

connection = DriverManager.getConnection("jdbc:mysql:
//localhost:3306/flightstats?user=user&password=secret");
statement = connection.createStatement();
resultSet = statement.executeQuery(query);

JRResultSetDataSource resultSetDataSource = new
JRResultSetDataSource(resultSet);

System.out.println("Filling report...");
JasperFillManager.fillReportToFile("reports/DbReportDS.
jasper", new HashMap(), resultSetDataSource);
