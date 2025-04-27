package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.Analyse;

import java.util.List;

public class TypeAnalyseService {

    private static final TypeAnalyseService instance = new TypeAnalyseService();

    public static TypeAnalyseService getInstance() {
        return instance;
    }

    public String getTemplate(String typeAnalyse) {
        return switch (typeAnalyse) {
            case "bilan sanguin" -> """
        Bilan sanguin:
        Numération globulaire
        Hématies: 
        Hémoglobine: 
        Hématocrite: 
        VGM: 
        TCMH:
        Leucocytes:  
        """;
            case "analyse cholesterol" -> """
        Analyse cholesterol:
        Cholestérol total:
        Triglycérides: 
        HDL cholestérol: 
        LDL cholestérol: 
        """;
            case "bilan urinaire" -> """
        Bilan urinaire:
        Leucocytes:
        Hématies: 
        Cellules rénales: 
        Glucose: 
        Protéines: 
        """;
            case "bilan inflammatoire" -> """
        Bilan inflammatoire:
        Protéine C réactive (CRP):
        Vitesse de sédimentation (VS): 
        Fibrinogène: 
        """;
            default -> "Résultat d’analyse: ";
        };
    }


}
