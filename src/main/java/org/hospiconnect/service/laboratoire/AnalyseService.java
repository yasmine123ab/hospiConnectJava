package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.Analyse;

import java.util.List;

public class AnalyseService {

    private static final AnalyseService instance = new AnalyseService();

    private final AnalyseCrudService analyseCrudService = AnalyseCrudService.getInstance();
    private final UserServiceLight userServiceLight = UserServiceLight.getInstance();
    private final PDFGeneratorService pdfGeneratorService = PDFGeneratorService.getInstance();

    public static AnalyseService getInstance() {
        return instance;
    }

    public void exportAnalyses(String outputFile) {
        StringBuilder htmlGenerator = new StringBuilder();
        htmlGenerator.append("""
                <h1>List des analyses</h1>
                <p>Voici la liste complete des analyses</p>
                
                <table>
                    <thead>
                        <tr>
                            <th class="table-header">Patient</th>
                            <th class="table-header">Personnel</th>
                            <th class="table-header">Date Rdv</th>
                            <th class="table-header">Etat</th>
                            <th class="table-header">Date Resultat</th>
                        </tr>
                    </thead>
                    <tbody>
                """);
        List<Analyse> listAnalyses = analyseCrudService.findAll();
        if (listAnalyses.isEmpty()) {
            htmlGenerator.append("Aucune analyse trouvée");
        } else {
            for (Analyse analyse : listAnalyses) {
                htmlGenerator.append("<tr>");
                htmlGenerator.append("<td>").append(userServiceLight.findUserNameById(analyse.getIdPatient())).append("</td>");
                htmlGenerator.append("<td>").append(userServiceLight.findUserNameById(analyse.getIdPersonnel())).append("</td>");
                htmlGenerator.append("<td>").append(toStringOrNull(analyse.getDatePrelevement())).append("</td>");
                htmlGenerator.append("<td>").append(analyse.getEtat()).append("</td>");
                htmlGenerator.append("<td>").append(toStringOrNull(analyse.getDateResultat())).append("</td>");
                htmlGenerator.append("</tr>");
            }
        }
        htmlGenerator.append("""
                    </tbody>
                </table>
                """);

        pdfGeneratorService.convertHtmlToPdf(htmlGenerator.toString(), outputFile);
    }

    private String toStringOrNull(Object o) {
        return o != null ? o.toString() : "";
    }

}
