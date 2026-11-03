package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public final class ExcelUtils {
	private static final int TITLE_COLUMN = 1;  // B: Test Case Title
	private static final int ACTUAL_RESULT_COLUMN = 4; // E: Actual Result
	private static final int STATUS_COLUMN = 5; // F: Test status
	private static final int DATE_COLUMN = 6;   // G: Execution Date
	private static final int SCREENSHOT_COLUMN = 8; // I: Test screenshot

	private ExcelUtils() {
	}

	/** Updates spreadsheet rows with scenario result, Actual Result text, and execution time. */
	public static void updateScenarioResult(Path workbookPath, String scenarioName, String status,
			List<String> actualResults, List<Path> stepScreenshots, int scenarioLine, String executionStatus,
			String evidenceSheetName)
			throws IOException {
		if (!"Passed".equals(status) && !"Failed".equals(status)) {
			throw new IllegalArgumentException("Status must be Passed or Failed");
		}

		try (Workbook workbook = loadWorkbook(workbookPath)) {
			Sheet sheet = workbook.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();
			String expectedTitle = normalize(scenarioName);
			Date executionTime = new Date();
			List<Row> matchingRows = new ArrayList<>();

			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null) {
					continue;
				}

				Cell titleCell = row.getCell(TITLE_COLUMN);
				if (titleCell == null || !expectedTitle.equals(normalize(formatter.formatCellValue(titleCell)))) {
					continue;
				}
				matchingRows.add(row);
			}

			if (matchingRows.isEmpty()) {
				throw new IOException("No Excel rows found for scenario title: " + scenarioName);
			}
			if ("Passed".equals(status) && actualResults.size() != matchingRows.size()) {
				throw new IOException("Expected " + matchingRows.size() + " actual-result entries for '"
						+ scenarioName + "' but received " + actualResults.size());
			}

			for (int rowIndex = 0; rowIndex < matchingRows.size(); rowIndex++) {
				Row row = matchingRows.get(rowIndex);

				Cell statusCell = row.getCell(STATUS_COLUMN);
				if (statusCell == null) {
					statusCell = row.createCell(STATUS_COLUMN);
				}
				statusCell.setCellValue(status);
				applyStatusColor(workbook, statusCell, status);

				Cell actualResultCell = row.getCell(ACTUAL_RESULT_COLUMN);
				if (actualResultCell == null) {
					actualResultCell = row.createCell(ACTUAL_RESULT_COLUMN);
				}
				if ("Passed".equals(status)) {
					actualResultCell.setCellValue(actualResults.get(rowIndex));
				} else {
					actualResultCell.setCellValue("Failed: " + scenarioName
							+ ". See the Extent report for the failed step and error details.");
				}

				Cell dateCell = row.getCell(DATE_COLUMN);
				if (dateCell == null) {
					dateCell = row.createCell(DATE_COLUMN);
				}
				dateCell.setCellValue(executionTime);
				CellStyle dateStyle = workbook.createCellStyle();
				dateStyle.cloneStyleFrom(dateCell.getCellStyle());
				dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat()
						.getFormat("yyyy-mm-dd hh:mm:ss"));
				dateCell.setCellStyle(dateStyle);
			}

			for (Row row : matchingRows) {
				Cell screenshotReference = row.getCell(SCREENSHOT_COLUMN);
				if (screenshotReference != null) {
					screenshotReference.setBlank();
				}
			}
			List<Integer> evidenceRowIndexes = appendStepEvidence(workbook, scenarioName, scenarioLine,
					executionStatus, executionTime, matchingRows, stepScreenshots, evidenceSheetName);
			for (int i = 0; i < evidenceRowIndexes.size() && i < matchingRows.size(); i++) {
				Cell screenshotReference = matchingRows.get(i).getCell(SCREENSHOT_COLUMN);
				if (screenshotReference == null) {
					screenshotReference = matchingRows.get(i).createCell(SCREENSHOT_COLUMN);
				}
				screenshotReference.setCellValue("Step Evidence row " + (evidenceRowIndexes.get(i) + 1));
			}

			try (FileOutputStream output = new FileOutputStream(workbookPath.toFile())) {
				workbook.write(output);
			}
		}
	}

	private static List<Integer> appendStepEvidence(Workbook workbook, String scenarioName, int scenarioLine,
			String status, Date executionTime, List<Row> matchingRows, List<Path> stepScreenshots,
			String evidenceSheetName) throws IOException {
		Sheet evidenceSheet = workbook.getSheet(evidenceSheetName);
		if (evidenceSheet == null) {
			evidenceSheet = workbook.createSheet(evidenceSheetName);
		}
		if (evidenceSheet.getPhysicalNumberOfRows() == 0) {
			Row header = evidenceSheet.createRow(0);
			header.createCell(0).setCellValue("Scenario");
			header.createCell(1).setCellValue("Feature Line");
			header.createCell(2).setCellValue("Step #");
			header.createCell(3).setCellValue("Step / Test Case Description");
			header.createCell(4).setCellValue("Test Status");
			header.createCell(5).setCellValue("Execution Date");
			header.createCell(6).setCellValue("Screenshot");
			evidenceSheet.setColumnWidth(0, 42 * 256);
			evidenceSheet.setColumnWidth(1, 14 * 256);
			evidenceSheet.setColumnWidth(2, 10 * 256);
			evidenceSheet.setColumnWidth(3, 52 * 256);
			evidenceSheet.setColumnWidth(4, 16 * 256);
			evidenceSheet.setColumnWidth(5, 22 * 256);
		}

		List<Integer> evidenceRowIndexes = new ArrayList<>();
		XSSFSheet xssfEvidenceSheet = (XSSFSheet) evidenceSheet;
		XSSFDrawing drawing = null;
		for (int i = 0; i < stepScreenshots.size(); i++) {
			int rowIndex = evidenceSheet.getLastRowNum() + 1;
			Row evidenceRow = evidenceSheet.createRow(rowIndex);
			evidenceRowIndexes.add(rowIndex);
			evidenceRow.createCell(0).setCellValue(scenarioName);
			evidenceRow.createCell(1).setCellValue(scenarioLine);
			evidenceRow.createCell(2).setCellValue(i + 1);
			String description = "";
			if (i < matchingRows.size()) {
				Cell descriptionCell = matchingRows.get(i).getCell(2);
				if (descriptionCell != null) {
					description = new DataFormatter().formatCellValue(descriptionCell);
				}
			}
			evidenceRow.createCell(3).setCellValue(description);
			Cell statusCell = evidenceRow.createCell(4);
			statusCell.setCellValue(status);
			applyStatusColor(workbook, statusCell, status);
			Cell dateCell = evidenceRow.createCell(5);
			dateCell.setCellValue(executionTime);
			CellStyle dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat()
					.getFormat("yyyy-mm-dd hh:mm:ss"));
			dateCell.setCellStyle(dateStyle);

			Path screenshotPath = stepScreenshots.get(i);
			if (screenshotPath == null || !Files.exists(screenshotPath)) {
				evidenceRow.createCell(6).setCellValue("Screenshot unavailable");
			} else {
				if (drawing == null) {
					drawing = (XSSFDrawing) xssfEvidenceSheet.getDrawingPatriarch();
					if (drawing == null) {
						drawing = xssfEvidenceSheet.createDrawingPatriarch();
					}
				}
				int pictureIndex = workbook.addPicture(Files.readAllBytes(screenshotPath), Workbook.PICTURE_TYPE_PNG);
				XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 6, rowIndex, 7, rowIndex + 1);
				drawing.createPicture(anchor, pictureIndex);
				xssfEvidenceSheet.setColumnWidth(6, 42 * 256);
				evidenceRow.setHeightInPoints(130f);
			}
		}
		return evidenceRowIndexes;
	}

	private static void applyStatusColor(Workbook workbook, Cell statusCell, String status) {
		CellStyle style = workbook.createCellStyle();
		style.cloneStyleFrom(statusCell.getCellStyle());
		style.setFillForegroundColor("Passed".equals(status)
				? IndexedColors.LIGHT_GREEN.getIndex()
				: IndexedColors.ROSE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		statusCell.setCellStyle(style);
	}

	private static Workbook loadWorkbook(Path workbookPath) throws IOException {
		try (FileInputStream input = new FileInputStream(workbookPath.toFile())) {
			return WorkbookFactory.create(input);
		}
	}

	private static String normalize(String value) {
		return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase();
	}
}

