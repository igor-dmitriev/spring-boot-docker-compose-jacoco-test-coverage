package com.igor;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.html.HTMLFormatter;

import java.io.File;
import java.io.IOException;

public class ReportGenerator {
  private final File executionDataFile;
  private final File classesDirectory;
  private final File sourceDirectory;
  private final File reportDirectory;

  private ExecFileLoader execFileLoader;

  public ReportGenerator() {
    this.executionDataFile = new File("jacocoreport/coverage.exec");
    this.classesDirectory = new File( "build/classes");
    this.sourceDirectory = new File( "src");
    this.reportDirectory = new File( "jacocoreport/report");
  }

  public void create() throws IOException {
    loadExecutionData();

    // Run the structure analyzer on a single class folder to build up
    // the coverage model. The process would be similar if your classes
    // were in a jar file. Typically you would create a bundle for each
    // class folder and each jar you want in your report. If you have
    // more than one bundle you will need to add a grouping node to your
    // report
    final IBundleCoverage bundleCoverage = analyzeStructure();
    createReport(bundleCoverage);
  }

  private void createReport(final IBundleCoverage bundleCoverage)
      throws IOException {

    // Create a concrete report visitor based on some supplied
    // configuration. In this case we use the defaults
    final HTMLFormatter htmlFormatter = new HTMLFormatter();
    final IReportVisitor visitor = htmlFormatter.createVisitor(new FileMultiReportOutput(reportDirectory));

    // Initialize the report with all of the execution and session
    // information. At this point the report doesn't know about the
    // structure of the report being created
    visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(),
        execFileLoader.getExecutionDataStore().getContents());

    // Populate the report structure with the bundle coverage information.
    // Call visitGroup if you need groups in your report.
    visitor.visitBundle(bundleCoverage, new DirectorySourceFileLocator(
        sourceDirectory, "utf-8", 4));

    // Signal end of structure information to allow report to write all
    // information out
    visitor.visitEnd();

  }

  private void loadExecutionData() throws IOException {
    execFileLoader = new ExecFileLoader();
    execFileLoader.load(executionDataFile);
  }

  private IBundleCoverage analyzeStructure() throws IOException {
    final CoverageBuilder coverageBuilder = new CoverageBuilder();
    final Analyzer analyzer = new Analyzer(
        execFileLoader.getExecutionDataStore(), coverageBuilder);

    analyzer.analyzeAll(classesDirectory);

    return coverageBuilder.getBundle("/");
  }

  /**
   * Starts the report generation process
   *
   * @param args Arguments to the application. This will be the location of the eclipse projects
   *             that will be used to generate reports for
   */
  public static void main(final String[] args) throws IOException {
    final ReportGenerator generator = new ReportGenerator();
    generator.create();
  }
}
