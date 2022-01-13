package sk.tobas;

import sk.tobas.model.Datasource;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        Datasource datasource = new Datasource();
        if (!datasource.open()) {
            System.out.println("Can't open datasource");
            return;
        }

        try {
            System.out.println(datasource.queryArtists().toString());
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        datasource.close();
    }
}
