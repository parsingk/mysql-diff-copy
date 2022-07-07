package com.lib.dbdiffcopy.utils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetUtil {
    public static Integer getInt(ResultSet rs, String columnLabel) throws SQLException {
        try {
            return rs.getInt(columnLabel);
        } catch (SQLException e) {
            if (e.getSQLState().equals("S0022")) {
                return null;
            }
            throw e;
        }
    }

    public static String getString(ResultSet rs, String columnLabel) throws SQLException {
        try {
            return rs.getString(columnLabel);
        } catch (SQLException e) {
            if (e.getSQLState().equals("S0022")) {
                return null;
            }
            throw e;
        }
    }

    public static Date getDate(ResultSet rs, String columnLabel) throws SQLException {
        try {
            return rs.getDate(columnLabel);
        } catch (SQLException e) {
            if (e.getSQLState().equals("S0022")) {
                return null;
            }
            throw e;
        }
    }

    public static Long getLong(ResultSet rs, String columnLabel) throws SQLException {
        try {
            return rs.getLong(columnLabel);
        } catch (SQLException e) {
            if (e.getSQLState().equals("S0022")) {
                return null;
            }
            throw e;
        }
    }

    public static Double getDouble(ResultSet rs, String columnLabel) throws SQLException {
        try {
            return rs.getDouble(columnLabel);
        } catch (SQLException e) {
            if (e.getSQLState().equals("S0022")) {
                return null;
            }
            throw e;
        }
    }

    public static Float getFloat(ResultSet rs, String columnLabel) throws SQLException {
        try {
            return rs.getFloat(columnLabel);
        } catch (SQLException e) {
            if (e.getSQLState().equals("S0022")) {
                return null;
            }
            throw e;
        }
    }

    public static List<String> getStringArray(ResultSet rs, String columnLabel) throws SQLException {
        try {
            String str = rs.getString(columnLabel);

            String[] arr = str.substring(1, str.length() -1).split(",");
            ArrayList<String> list = new ArrayList<>();
            for (String c : arr) {
                c = c.trim();
                if(c.length() == 1) {
                    list.add(c);
                    continue;
                }

                list.add(c.substring(1, c.length() -1));
            }

            return list;
        } catch (SQLException e) {
            if (e.getSQLState().equals("S0022")) {
                return null;
            }
            throw e;
        }
    }
}
