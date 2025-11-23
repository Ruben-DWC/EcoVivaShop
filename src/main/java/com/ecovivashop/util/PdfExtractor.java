package com.ecovivashop.util;

import java.io.File;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class PdfExtractor {

    @SuppressWarnings({"StringConcatenationInsideStringBufferAppend", "UseSpecificCatch", "CallToPrintStackTrace"})
    public static void main(String[] args) {
        try {
            // Adjust path if necessary, assuming run from project root
            String pdfPath = "DCI_SI53_INDICACIONES Y RUBRICA _PROY_24C2A.pdf";
            File file = new File(pdfPath);
            if (!file.exists()) {
                System.out.println("ERROR: File not found at " + file.getAbsolutePath());
                return;
            }

            PdfReader reader = new PdfReader(pdfPath);
            int n = reader.getNumberOfPages();
            StringBuilder text = new StringBuilder();
            System.out.println("Extracting text from " + n + " pages...");

            for (int i = 1; i <= n; i++) {
                text.append("--- Page " + i + " ---\n");
                text.append(PdfTextExtractor.getTextFromPage(reader, i));
                text.append("\n");
            }
            reader.close();

            System.out.println("--- START PDF CONTENT ---");
            System.out.println(text.toString());
            System.out.println("--- END PDF CONTENT ---");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
