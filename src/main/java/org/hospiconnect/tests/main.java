package org.hospiconnect.tests;

import org.hospiconnect.model.Materiel;
import org.hospiconnect.service.MaterielService;
import org.hospiconnect.utils.DatabaseUtils;

import java.sql.SQLException;

public class main {
    public static void main(String[] args) {
        MaterielService userService = new MaterielService();
        try {
            //  userService.insert(new User(21, "amin", "kaaboura"));
            System.out.println(userService.findAll());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
}
