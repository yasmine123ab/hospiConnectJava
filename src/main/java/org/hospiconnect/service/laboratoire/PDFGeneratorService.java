package org.hospiconnect.service.laboratoire;

import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PDFGeneratorService {

    private static final PDFGeneratorService instance = new PDFGeneratorService();

    public static PDFGeneratorService getInstance() {
        return instance;
    }

    private PDFGeneratorService() {
    }

    public void convertHtmlToPdf(String htmlBody, String outputPdfPath) {
        try (OutputStream outputStream = new FileOutputStream(outputPdfPath)) {
            ITextRenderer renderer = new ITextRenderer();

            // Important : Base URI pour que les images et le CSS soient correctement chargés
            String baseUrl = new File("target/classes/").toURI().toURL().toString();

            // Construire le HTML complet
            String completeHtml = """
                <html xmlns="http://www.w3.org/1999/xhtml">
                <head>
                    <style type="text/css">
                        body {
                            font-family: "Helvetica", "Arial", sans-serif;
                            font-size: 14px;
                            color: #003366;
                            margin: 40px;
                            line-height: 1.8;
                        }
                        .table-header {
                            background-color: #00008B; /* Dark Blue */
                            color: white;
                            font-weight: bold;
                        }
                        table {
                            width: 100%;
                            border-collapse: collapse;
                            table-layout: fixed;
                            margin-top: 20px;
                        }
                        td, th {
                            border: 1px solid #ddd;
                            padding: 8px;
                            overflow-wrap: break-word;
                        }
                        h1 {
                            color: #0055A4;
                            text-align: center;
                            margin-bottom: 30px;
                        }
                        img {
                            display: block;
                            margin-left: auto;
                            margin-right: auto;
                            width: 120px;
                        }
                    </style>
                </head>
                <body>
                """ + htmlBody + """
                </body>
                </html>
            """;

            renderer.setDocumentFromString(completeHtml, baseUrl);
            renderer.layout();
            renderer.createPDF(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
