package org.hospiconnect.service;

import org.hospiconnect.dao.ConsultationDAO;
import org.hospiconnect.model.Consultation;

import java.util.List;

public class ConsultationService {
    private ConsultationDAO consultationDAO;
    
    public ConsultationService() {
        this.consultationDAO = new ConsultationDAO();
    }
    
    public List<Consultation> getAllConsultations() {
        return consultationDAO.getAllConsultations();
    }
    
    public Consultation getConsultationById(int id) {
        return consultationDAO.getConsultationById(id);
    }
    
    public boolean saveConsultation(Consultation consultation) {
        if (consultation.getId() == 0) {
            return consultationDAO.createConsultation(consultation);
        } else {
            return consultationDAO.updateConsultation(consultation);
        }
    }
    
    public boolean deleteConsultation(int id) {
        return consultationDAO.deleteConsultation(id);
    }
}
