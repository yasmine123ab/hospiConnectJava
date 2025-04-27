package org.hospiconnect.service.laboratoire;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResultatAnalyseAiPdfService {

    private static final ResultatAnalyseAiPdfService instance = new ResultatAnalyseAiPdfService();

    public static ResultatAnalyseAiPdfService getInstance() {
        return instance;
    }

    private ResultatAnalyseAiPdfService() {}

    public void generateAnalysePdf(String analyseResultatText, String outputPath) {
        // Appel à OpenAI pour obtenir une analyse développée
        String commentaireAI = OpenAiService.getInstance().askOpenAi(analyseResultatText);

        // Préparer le contenu XHTML propre pour Flying Saucer
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String htmlContent = """
            <html xmlns="http://www.w3.org/1999/xhtml">
            <head>
                <style type="text/css">
                    body {
                        font-family: "Helvetica", "Arial", sans-serif;
                        color: #003366;
                        background-color: #ffffff;
                        margin: 50px;
                        font-size: 14px;
                        line-height: 1.8;
                    }
                    .logo {
                        width: 120px;
                        display: block;
                        margin-left: auto;
                        margin-right: auto;
                        margin-bottom: 30px;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 50px;
                    }
                    h1 {
                        color: #0055A4;
                        font-size: 26px;
                        margin-bottom: 10px;
                    }
                    h2 {
                        color: #0055A4;
                        font-size: 20px;
                        margin-top: 50px;
                        margin-bottom: 10px;
                    }
                    p {
                        text-align: justify;
                        margin-top: 20px;
                        margin-bottom: 20px;
                    }
                    .remark {
                        text-align: justify;
                        margin-top: 10px;
                        margin-bottom: 25px;
                        font-size: 14px;
                        line-height: 1.8;
                    }
                    .date {
                        text-align: right;
                        font-size: 12px;
                        color: #666666;
                        margin-top: 10px;
                    }
                    hr {
                        border: 0;
                        height: 2px;
                        background: #0055A4;
                        margin: 40px 0;
                    }
                </style>
            </head>
            <body>
                <img src="imagesLabo/logo.png" class="logo" alt="Logo" />
                <div class="header">
                    <h1>Résultat d'Analyse Médicale</h1>
                </div>
                <div class="date">%s</div>
                <hr />
                <h2>Détails de l'Analyse</h2>
                <p>%s</p>
                <hr />
                <h2>Évaluation de notre chatbot HospiChat</h2>
                %s
            </body>
            </html>
            """.formatted(
                now,
                analyseResultatText.replace("\n", "<br />"),
                formatCommentaires(commentaireAI)
        );

        // Générer le PDF
        PDFGeneratorService.getInstance().convertHtmlToPdf(htmlContent, outputPath);
    }

    private String formatCommentaires(String commentaireAI) {
        String[] lignes = commentaireAI.split("\n");
        StringBuilder sb = new StringBuilder();

        StringBuilder bloc = new StringBuilder(); // pour construire un seul gros bloc
        for (String ligne : lignes) {
            if (ligne.trim().isEmpty()) {
                // Si ligne vide = fin d'un paragraphe -> on ferme le bloc précédent
                if (bloc.length() > 0) {
                    sb.append("<p class=\"remark\">").append(bloc.toString().trim()).append("</p>");
                    bloc.setLength(0); // reset
                }
            } else {
                // Sinon on continue d'ajouter à l'intérieur du même bloc
                bloc.append(ligne.trim()).append(" ");
            }
        }

        // Si dernier bloc non vide
        if (bloc.length() > 0) {
            sb.append("<p class=\"remark\">").append(bloc.toString().trim()).append("</p>");
        }

        return sb.toString();
    }

}
