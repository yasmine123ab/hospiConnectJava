package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.Analyse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnalyseValidationService {

    private static final AnalyseValidationService instance = new AnalyseValidationService();

    public static AnalyseValidationService getInstance() {
        return instance;
    }

    public List<String> validate(Analyse analyse) {
        var errors = new ArrayList<String>();
        if (analyse.getDatePrelevement() == null || analyse.getDatePrelevement().isBefore(LocalDate.now())) {
            errors.add("Date prelevement doit etre egale ou posterieure a aujourdhui");
        }
        if (analyse.getIdPatient() == null) {
            errors.add("Patient ne doit pas etre vide");
        }
        if (analyse.getIdPersonnel() == null) {
            errors.add("Personnel ne doit pas etre vide");
        }
        if (analyse.getIdTypeAnalyse() == null) {
            errors.add("Type analyse ne doit pas etre vide");
        }
        if (analyse.getEtat() == null) {
            errors.add("Etat ne doit pas etre vide");
        }
        if ( analyse.getDateResultat() != null && analyse.getDateResultat().isBefore(LocalDate.now())) {
            errors.add("Date Resultat doit etre egale ou posterieure a aujourdhui");
        }
        var all = AnalyseCrudService.getInstance().findAll();
        var nbr = all.stream().filter(a -> a.equals(analyse) && !a.getId().equals(analyse.getId()))
                .count();
        if (nbr > 0) {
            errors.add("Il existe " + nbr + " analyses avec les memes donnees!");
        }
        return errors;
    }

}

