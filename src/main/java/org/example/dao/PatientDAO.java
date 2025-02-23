package org.example.dao;

import org.example.db.MCPConnection;
import org.example.dto.PatientsFilterDto;
import org.example.models.Doctors;
import org.example.models.Patients;

import java.sql.*;
import java.util.ArrayList;

public class PatientDAO {

   // private static final   String URL = "jdbc:sqlite:C:\\Users\\dev\\IdeaProjects\\FinalProject\\hospital.db";

    //Patient REGISTER
    private static final String INSERT_PAT = "insert into PATIENTS (name, email, password, phone, dateOfBirth) values (?, ?, ?, ?, ?)";

    //Patient and LOGIN
    private static final String LOGIN_PAT = "select * from PATIENTS where email = ? AND password = ?";


    //----------not needed---------
    private static final String SELECT_ONE_PAT = "select * from PATIENTS where patientId = ?";
    private static final String SELECT_PAT_WITH_NAME = "select * from PATIENTS where name = ?";
    private static final String SELECT_PAT_WITH_PHONE = "select * from PATIENTS where phone = ?";
    private static final String SELECT_ALL_PATS = "select * from PATIENTS";
    private static final String UPDATE_PAT = "update PATIENTS set name = ?, email = ?, password = ?, dateOfBirth = ? where patientId = ?";
    private static final String DELETE_PAT = "delete from PATIENTS where patientId = ?";
    //----------not needed---------


    public void insertPat(Patients p) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = MCPConnection.getConn();
             PreparedStatement st = conn.prepareStatement(INSERT_PAT)) {
            // st.setInt(1, p.getPatientId());
            st.setString(1, p.getName());
            st.setString(2, p.getEmail());
            st.setString(3, p.getPassword());
            st.setString(4, p.getPhone());
            st.setString(5, p.getDateOfBirth().toString());
            st.executeUpdate();
        }
    }
    public void updatePat(Patients p) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = MCPConnection.getConn();
             PreparedStatement st = conn.prepareStatement(UPDATE_PAT)) {
            st.setInt(5, p.getPatientId());
            st.setString(1, p.getName());
            st.setString(2, p.getEmail());
            st.setString(3, p.getPassword());
            st.setString(4, p.getDateOfBirth().toString());
            st.executeUpdate();
        }
    }


    public Patients selectPat(int patientId) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = MCPConnection.getConn();
             PreparedStatement st = conn.prepareStatement(SELECT_ONE_PAT)) {
            st.setInt(1, patientId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Patients(rs);
            } else {
                return null;
            }
        }
    }


    public ArrayList<Patients> selectAllPats(PatientsFilterDto filter) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = MCPConnection.getConn()) {
            PreparedStatement st;
            if (filter.getPatName() != null) {
                st = conn.prepareStatement(SELECT_PAT_WITH_NAME);
                st.setString(1, filter.getPatName());
            } else if (filter.getPatPhone() != null) {
                st = conn.prepareStatement(SELECT_PAT_WITH_PHONE);
                st.setString(1, filter.getPatPhone());

            } else {
                st = conn.prepareStatement(SELECT_ALL_PATS);
            }
            ResultSet rs = st.executeQuery();
            ArrayList<Patients> pats = new ArrayList<>();
            while (rs.next()) {
                pats.add(new Patients(rs));
            }

            return pats;
        }
    }
    public Patients PatientsLogin(String patEmail, String password) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = MCPConnection.getConn();
             PreparedStatement st = conn.prepareStatement(LOGIN_PAT)) {
            st.setString(1, patEmail);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new Patients(rs);
            } else {
                return null;
            }
        }

    }

}
