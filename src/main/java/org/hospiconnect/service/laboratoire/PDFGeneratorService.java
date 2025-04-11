package org.hospiconnect.service.laboratoire;

import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PDFGeneratorService {

    private static final PDFGeneratorService instance = new PDFGeneratorService();

    public static PDFGeneratorService getInstance() {
        return instance;
    }

    public PDFGeneratorService() {
    }

    public void convertHtmlToPdf(String htmlBody, String outputPdfPath) {
        try (OutputStream outputStream = new FileOutputStream(outputPdfPath)) {
            ITextRenderer renderer = new ITextRenderer();

            renderer.setDocumentFromString("""
                    <html>
                        <head>
                            <style type="text/css">
                                .table-header {
                                    background-color: darkBlue;
                                    color: white;
                                    font-weight: bold;
                                }
                                table {
                                    width: 100%;
                                    border-collapse: collapse;
                                    table-layout: fixed;
                                }
                                td, th {
                                    border: 1px solid #ddd;
                                    padding: 8px;
                                    overflow-wrap: break-word;
                                }
                            </style>
                        </head>
                        <body>
                    """
                    + htmlBody
                    + """
                        </body>
                    </html>
                    """
            );
            renderer.layout();
            renderer.createPDF(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

}
