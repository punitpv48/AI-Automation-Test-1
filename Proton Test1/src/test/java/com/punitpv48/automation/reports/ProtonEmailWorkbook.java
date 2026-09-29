package com.punitpv48.automation.reports;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class ProtonEmailWorkbook {
    private static final String SCENARIO_ID = "TC-PROTON-EMAIL-001";
    private static final String SCENARIO = "Sign in and send a test email";
    private static final Path OUTPUT = Paths.get("test-output", "proton-email-test-results.xlsx");
    private static final String[][] PLAN = {
            {"Open the Proton sign-in page", "The official Proton account sign-in form is displayed."},
            {"Enter the configured username", "The username field contains the configured test account; no credential is written to this report."},
            {"Enter the configured password", "The password field accepts the configured secret; the secret is never written to this report."},
            {"Check Keep me signed in", "The checkbox is selected and its selected state is verified."},
            {"Click Sign in", "The account reaches its Proton Mail inbox."},
            {"Click New Message", "A new message composer is displayed."},
            {"Enter recipient", "The recipient is added: Ankit.verma@gmail.com."},
            {"Enter subject", "The subject is Test email."},
            {"Enter message body", "The body is Hi. Test."},
            {"Click Send", "Proton confirms the message was sent."}
    };
    private static final String[] HEADERS = {
            "Scenario ID", "Scenario", "Step", "Action", "Test Data", "Expected Result", "Actual Result", "Status"
    };

    private ProtonEmailWorkbook() {
    }

    public static Execution newExecution() {
        return new Execution();
    }

    public static final class Execution {
        private final List<String> actualResults = new ArrayList<>();
        private final List<String> statuses = new ArrayList<>();

        private Execution() {
            for (int step = 0; step < PLAN.length; step++) {
                actualResults.add("Not run");
                statuses.add("NOT RUN");
            }
        }

        public void passed(int step, String actual) {
            actualResults.set(step, actual);
            statuses.set(step, "PASS");
        }

        public void failed(int step, Throwable failure) {
            String message = failure.getMessage();
            if (message == null || message.isBlank()) {
                message = failure.getClass().getSimpleName();
            }
            actualResults.set(step, "Failed: " + message.substring(0, Math.min(message.length(), 350)));
            statuses.set(step, "FAIL");
        }

        public Path write() throws IOException {
            Files.createDirectories(OUTPUT.getParent());
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Email test steps");
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);

                Row header = sheet.createRow(0);
                for (int column = 0; column < HEADERS.length; column++) {
                    Cell cell = header.createCell(column);
                    cell.setCellValue(HEADERS[column]);
                    cell.setCellStyle(headerStyle);
                }

                for (int step = 0; step < PLAN.length; step++) {
                    Row row = sheet.createRow(step + 1);
                    String[] values = {
                            SCENARIO_ID,
                            SCENARIO,
                            Integer.toString(step + 1),
                            PLAN[step][0],
                            testData(step),
                            PLAN[step][1],
                            actualResults.get(step),
                            statuses.get(step)
                    };
                    for (int column = 0; column < values.length; column++) {
                        row.createCell(column).setCellValue(values[column]);
                    }
                }

                sheet.createFreezePane(0, 1);
                sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, PLAN.length, 0, HEADERS.length - 1));
                int[] widths = {22, 34, 10, 32, 38, 68, 62, 14};
                for (int column = 0; column < widths.length; column++) {
                    sheet.setColumnWidth(column, widths[column] * 256);
                }
                try (OutputStream output = Files.newOutputStream(OUTPUT)) {
                    workbook.write(output);
                }
            }
            return OUTPUT.toAbsolutePath();
        }

        private String testData(int step) {
            return switch (step) {
                case 1, 2 -> "Configured test account (value intentionally omitted)";
                case 3 -> "Keep me signed in = checked";
                case 6 -> "Ankit.verma@gmail.com";
                case 7 -> "Test email";
                case 8 -> "Hi. Test";
                default -> "-";
            };
        }
    }
}