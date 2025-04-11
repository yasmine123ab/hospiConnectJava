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
        if (analyse.getDatePrelevement()==null || !analyse.getDatePrelevement().isAfter(LocalDate.now())) {
            errors.add("Date prelevement doit etre posterieure a aujourdhui");
        }

        return errors;
    }
}
