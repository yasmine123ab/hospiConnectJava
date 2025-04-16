package org.hospiconnect.service;

import org.hospiconnect.dao.ReservationDAO;
import org.hospiconnect.model.Reservation;

import java.util.List;

public class ReservationService {
    private ReservationDAO reservationDAO;
    
    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
    }
    
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }
    
    public Reservation getReservationById(int id) {
        return reservationDAO.getReservationById(id);
    }
    
    public boolean saveReservation(Reservation reservation) {
        if (reservation.getId() == 0) {
            return reservationDAO.createReservation(reservation);
        } else {
            return reservationDAO.updateReservation(reservation);
        }
    }
    
    public boolean deleteReservation(int id) {
        return reservationDAO.deleteReservation(id);
    }
}
