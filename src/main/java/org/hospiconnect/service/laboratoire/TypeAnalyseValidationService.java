package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.TypeAnalyse;
import java.util.ArrayList;
import java.util.List;

public class TypeAnalyseValidationService {

    private static final TypeAnalyseValidationService instance = new TypeAnalyseValidationService();

    public static TypeAnalyseValidationService getInstance() {
        return instance;
    }

    public List<String> validate(TypeAnalyse typeAnalyse) {
        var errors = new ArrayList<String>();

        if (typeAnalyse.getLibelle() == null) {
            errors.add("Libelle ne doit pas etre vide");
        }
        if (typeAnalyse.getLibelle().trim().isEmpty() || typeAnalyse.getLibelle().length() > 255) {
            errors.add("Libelle doit contenir au moins 1 caractere et ne pas depasser 255");
        }
        if (typeAnalyse.getNom() == null) {
            errors.add("Nom ne doit pas etre vide");
        }
        if (typeAnalyse.getNom().trim().isEmpty() || typeAnalyse.getNom().length() > 255) {
            errors.add("Nom doit contenir au moins 1 caractere et ne pas depasser 255");
        }
        if (typeAnalyse.getPrix() == null) {
            errors.add("Prix ne doit pas etre vide");
        }
        if (typeAnalyse.getPrix() <= 0) {
            errors.add("Prix ne doit pas etre negatif");
        }


        var all = TypeAnalyseCrudService.getInstance().findAll();
        var nbr = all.stream().filter(a -> a.equals(typeAnalyse) && !a.getId().equals(typeAnalyse.getId()))
                .count();
        if (nbr > 0) {
            errors.add("Il existe " + nbr + " types analyse avec les memes donnees!");
        }
        return errors;
    }

}
