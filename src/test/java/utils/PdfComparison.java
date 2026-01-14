package utils;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import static org.junit.Assert.assertEquals;

public class PdfComparison {

     public static void validatePdfFields(
            String actualPdfPath,
            String expectedPdfPath,
            Map<String, String> fieldRegexMap
    ) {
        try (
            PDDocument actualDoc = PDDocument.load(new File(actualPdfPath));
            PDDocument expectedDoc = PDDocument.load(new File(expectedPdfPath))
        ) {

            PDFTextStripper stripper = new PDFTextStripper();
            String actualText = stripper.getText(actualDoc);
            String expectedText = stripper.getText(expectedDoc);

            for (Map.Entry<String, String> entry : fieldRegexMap.entrySet()) {
                String fieldName = entry.getKey();
                String regex = entry.getValue();

                Pattern pattern = Pattern.compile(regex);
                String actualValue = extract(actualText, pattern);
                String expectedValue = extract(expectedText, pattern);

                assertEquals(
                        fieldName + " mismatch",
                        expectedValue,
                        actualValue
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("PDF validation failed", e);
        }
    }

    private static String extract(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new AssertionError("Pattern not found in PDF text: " + pattern);
    }
}
