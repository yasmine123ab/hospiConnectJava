package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.DisponibiliteAnalyse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DisponibiliteAnalyseValidationService {

    private static final DisponibiliteAnalyseValidationService instance = new DisponibiliteAnalyseValidationService();

    public static DisponibiliteAnalyseValidationService getInstance() {
        return instance;
    }

    public List<String> validate(DisponibiliteAnalyse dispoAnalyse) {
        var errors = new ArrayList<String>();
        if (dispoAnalyse.getDispo() == null || dispoAnalyse.getDispo().isBefore(LocalDate.now())) {
            errors.add("Date disponibilité doit etre egale ou posterieure a aujourdhui");
        }
        if (dispoAnalyse.getDebut() == null) {
            errors.add("Heure Debut ne doit pas etre vide");
        }
        if (dispoAnalyse.getFin() == null) {
            errors.add("Heure Fin ne doit pas etre vide");
        }
        if (dispoAnalyse.getDebut() != null && dispoAnalyse.getFin() != null) {
            if (!dispoAnalyse.getDebut().isBefore(dispoAnalyse.getFin())) {
                errors.add("Heure Début doit être antérieure à l'Heure de Fin");
            }
        }
        if (dispoAnalyse.getNbrPlaces() == null || dispoAnalyse.getNbrPlaces() == 0) {
            errors.add("Nbr places ne doit pas etre egal a 0");
        }

        var all = DisponibiliteAnalyseCrudService.getInstance().findAll();
        var nbr = all.stream().filter(a -> a.equals(dispoAnalyse) && !a.getId().equals(dispoAnalyse.getId()))
                .count();
        if (nbr > 0) {
            errors.add("Il existe " + nbr + "dispos analyses avec les memes donnees!");
        }
        return errors;
    }







}
