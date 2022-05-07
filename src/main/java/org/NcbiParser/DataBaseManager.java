package org.NcbiParser;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public final class DataBaseManager {
    private static String path_;
    private static Connection connection_;

    private DataBaseManager(){
        path_ = "jdbc:sqlite:/";
    }

    public static void setDataBase(String path){
        path_ = "jdbc:sqlite:/" + path;
    }

    public static void connectionToDb() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection_ = null;
        try{
            connection_ = DriverManager.getConnection(path_);

            if(connection_ != null){
                System.out.println("connected");
            }

        }catch(SQLException eio)
        {
            System.err.println(eio.getMessage());
        }
    }

    public static void createTableDb(String sql) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Statement st = null;
        try{
            st = connection_.createStatement();
            st.executeUpdate(sql);
            System.out.println("table created");
        }catch (Exception ioe){
            System.out.println(ioe.getMessage());
        }

    }

    public static boolean needAnUpdate(UpdateRow row){
        Statement s;
        try{
            String req = "SELECT * FROM FILES " +
                    "WHERE ncs = \"" + row.getNcs() + "\" AND organism = \"" + row.getOrganism() + "\";";
            s = connection_.createStatement();
            ResultSet rs = s.executeQuery(req);

            if(!rs.isBeforeFirst()){ // résultat vide donc pas à jour forcément
                return true;
            }

            String parseTexteDate = rs.getString("date");
            java.util.Date prevParseDate = new SimpleDateFormat("yyyy/MM/dd").parse(parseTexteDate);
            java.util.Date newParseDate = new SimpleDateFormat("yyyy/MM/dd").parse(row.getModifyDate());

            return (prevParseDate.before(newParseDate) && !prevParseDate.equals(newParseDate));
        }catch(Exception eio){
            System.out.println(eio.getMessage());
            return false;
        }
    }

    public static void closeDb() throws SQLException {
        try
        {
            connection_.close();
        }catch (SQLException ioe){
            System.out.println(ioe.getMessage());
        }
    }


    public static void insertOverviewTable(ArrayList<String> kindomNames,ArrayList<String> GroupNames,ArrayList<String> subGroupNames,
                                           ArrayList<String> organismNames) throws SQLException {
        PreparedStatement ps;
        try{
            String compiledQuery = "INSERT OR IGNORE INTO OVERVIEW (kingdom, groupe, subgroup, organisme, organelle)" +
                    " VALUES" + "(?, ?, ?, ?, ?)";
            ps = connection_.prepareStatement(compiledQuery);

            for(int i = 0; i <kindomNames.size(); i++) {
                ps.setString(1, kindomNames.get(i));
                ps.setString(2, GroupNames.get(i));
                ps.setString(3, subGroupNames.get(i));
                ps.setString(4, organismNames.get(i));
                ps.setString(5, null);
                ps.addBatch();
            }

            ps.executeBatch();

        }catch(Exception eio){
            throw new RuntimeException("Error batch");
        }


    }

    public static void insertIndexesTables(ArrayList<String> GroupNames,ArrayList<String> subGroupNames,
                                           ArrayList<String> organismNames,ArrayList<String> gcc,
                                           ArrayList<String> lastModify){
        PreparedStatement ps;
        try{
            String compiledQuery = "INSERT OR IGNORE INTO INDEXES (groupe, subgroup,organisme, GC, last_modify)" +
                    " VALUES" + "(?, ?, ?, ?, ?)";
            ps = connection_.prepareStatement(compiledQuery);

            for(int i = 0; i <GroupNames.size(); i++) {
                ps.setString(1, GroupNames.get(i));
                ps.setString(2, subGroupNames.get(i));
                ps.setString(3, organismNames.get(i));
                ps.setString(4, gcc.get(i));
                ps.setString(5, lastModify.get(i));
                ps.addBatch();
            }

            ps.executeBatch();

        }catch(Exception eio){
            throw new RuntimeException("Error batch");
        }

    }


    public static Connection getConnection_(){
        return connection_;
    }
}
