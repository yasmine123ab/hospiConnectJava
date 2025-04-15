package org.hospiconnect.service.laboratoire;

import org.hospiconnect.model.laboratoire.Analyse;
import org.hospiconnect.model.laboratoire.RdvAnalyse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RdvAnalyseValidationService {

    private static final RdvAnalyseValidationService instance = new RdvAnalyseValidationService();

    public static RdvAnalyseValidationService getInstance() {
        return instance;
    }

    public List<String> validate(RdvAnalyse rdvAnalyse) {
        var errors = new ArrayList<String>();
        if (rdvAnalyse.getIdPatient() == null) {
            errors.add("Patient ne doit pas etre vide");
        }
        if (rdvAnalyse.getDateRdv() == null || rdvAnalyse.getDateRdv().isBefore(LocalDate.now())) {
            errors.add("Date Rdv doit etre egale ou posterieure a aujourdhui");
        }
        if (rdvAnalyse.getIdDisponibilite() == null) {
            errors.add("Disponibilite ne doit pas etre vide");
        }
        var all = AnalyseRdvCrudService.getInstance().findAll();
        var nbr = all.stream().filter(a -> a.equals(rdvAnalyse) && !a.getId().equals(rdvAnalyse.getId()))
                .count();
        if (nbr > 0) {
            errors.add("Il existe " + nbr + "rdvs analyse avec les memes donnees!");
        }
        return errors;
    }










}
