package com.practice.kafka.consumer;

import com.practice.kafka.services.PropertiesService;
import com.practice.kafka.services.PropertiesServiceImpl;

import java.sql.*;
import java.util.Properties;

public class JDBCTester {
    public static void main(String[] args) {

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        PropertiesService configService = new PropertiesServiceImpl();
        Properties props  = configService.LoadProperties();

        String url = props.getProperty("pg.server.url");
        String user = props.getProperty("pg.server.user");
        String password = props.getProperty("pg.server.password");
        try {
            conn = DriverManager.getConnection(url, user, password);
            st = conn.createStatement();
            rs = st.executeQuery("SELECT 'postgresql is connected' ");

            if (rs.next())
                System.out.println(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}