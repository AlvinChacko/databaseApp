/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvin
 */
public class Database {

    static Connection c = null;
    static Statement stmt = null;

    public static void connecttoDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:../projects.db");
        } catch (Exception e) {
            System.err.println("Problem Encountered");
        }
        //System.out.println("Opened database successfully");
    }

    public static void select(int i) {
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(selectqueries(i));
            System.out.println("\n-----------------Result-----------------------------");
            while (rs.next()) {
                String name = rs.getString("Name");
                //int id = rs.getInt("ID");
                System.out.println(name);
            }
            stmt.close();
            rs.close();
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public static String selectqueries(int i) {
        String query = null;
        switch (i) {
            case 1:
                query = "SELECT * FROM projects";
                break;
            case 2:
                query = "SELECT Name, MAX(Time_Spent) AS Time_Spent FROM Projects WHERE Completed = 1;";
                break;
            case 3:
                query = "SELECT Name FROM Language WHERE ID IN (SELECT LAN_ID FROM Projects);";
                break;
            case 4:
                query = "SELECT Name FROM Language WHERE ID= (SELECT LAN_ID FROM Projects GROUP BY LAN_ID ORDER BY COUNT(LAN_ID) DESC LIMIT 1)";
                break;
            case 5:
                query = "SELECT l1.Name, SUM(Time) as SUM_time FROM Resources JOIN Lang_Res as one, Lang_Res as sec ON one.LAN_ID=sec.LAN_ID AND one.RES_ID = sec.RES_ID JOIN Language l1 ON l1.ID=sec.LAN_ID WHERE Resources.ID=sec.RES_ID GROUP BY sec.LAN_ID ORDER BY SUM_time DESC LIMIT 1;";
                break;
            case 6:
                query = "SELECT l1.Name, SUM(Time) as SUM_time FROM Resources JOIN Lang_Res as one, Lang_Res as sec ON one.LAN_ID=sec.LAN_ID AND one.RES_ID = sec.RES_ID JOIN Language l1 ON l1.ID=sec.LAN_ID WHERE Resources.ID=sec.RES_ID GROUP BY sec.LAN_ID ORDER BY SUM_time ASC LIMIT 1;";
                break;
            case 7:
                query = "SELECT Name from Projects WHERE ID IN (SELECT PROJ_ID from Proj_Extra, Extra_Work WHERE Extra_Work.ID = Proj_Extra.EXTRA_ID AND Extra_Work.Amount < 30* Extra_Work.Time); ";
                break;
            case 8:
                query = "SELECT l1.Name, l1.Skill_Level, SUM(Time) as SUM_time FROM Resources JOIN Lang_Res as one, Lang_Res as sec ON one.LAN_ID=sec.LAN_ID AND one.RES_ID = sec.RES_ID JOIN Language l1 ON l1.ID=sec.LAN_ID WHERE Resources.ID=sec.RES_ID GROUP BY sec.LAN_ID ORDER BY SUM_time DESC LIMIT 1;";
                break;
            case 9:
                query = "SELECT l1.Name, l1.Skill_Level, SUM(Time) as SUM_time FROM Resources JOIN Lang_Res as one, Lang_Res as sec ON one.LAN_ID=sec.LAN_ID AND one.RES_ID = sec.RES_ID JOIN Language l1 ON l1.ID=sec.LAN_ID WHERE Resources.ID=sec.RES_ID GROUP BY sec.LAN_ID ORDER BY SUM_time ASC LIMIT 1;";
                break;
            case 10:
                query = "SELECT Name FROM Extra_Work WHERE ID = (SELECT EXTRA_ID FROM Client_Extra GROUP BY EXTRA_ID ORDER BY Count(EXTRA_ID) DESC LIMIT 1);";
                break;

        }
        return query;
    }

    public static void selectqueries(int i, String[] strpar, int[] intpar) {
        try {
            PreparedStatement insert_statement = null;
            switch (i) {
                case 1:
                    insert_statement = c.prepareStatement("INSERT INTO Projects(Name, Type, Deadline, LAN_ID) VALUES (?, ?, ?, ?)");
                    insert_statement.clearParameters();
                    insert_statement.setString(1, strpar[0]);
                    insert_statement.setString(2, strpar[1]);
                    insert_statement.setString(3, strpar[2]);
                    insert_statement.setInt(4, intpar[0]);
                    break;
                case 2:
                    insert_statement = c.prepareStatement("INSERT INTO Language(Name, Type, Skill_Level) VALUES (?, ?, ?)");
                    insert_statement.clearParameters();
                    insert_statement.setString(1, strpar[0]);
                    insert_statement.setString(2, strpar[1]);
                    insert_statement.setInt(3, intpar[0]);
                    break;
            }
            insert_statement.executeUpdate();
            insert_statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\n-----------------Inserted successfully-----------------------------");
    }

    public static void displayinsertoptions() {
        String[] strpar = new String[100];
        int[] intpar = new int[100];
        System.out.println("[1] Insert into projects");
        System.out.println("[2] Insert into programming language");
        Scanner sc = new Scanner(System.in);
        int input = sc.nextInt();
        sc.nextLine();
        switch (input) {
            case 1:
                System.out.println("Please input the name of the project:");
                strpar[0] = sc.nextLine();
                System.out.println("Please input the type of the project:");
                strpar[1] = sc.nextLine();
                System.out.println("Please input the deadline of the project. Ex: Jan 22 2014 :");
                strpar[2] = sc.nextLine();
                System.out.println("Please input the programming language for the project:");
                strpar[3] = sc.nextLine();
                try {
                    PreparedStatement search = c.prepareStatement("SELECT ID FROM Language WHERE Name = ? ;");
                    search.setString(1, strpar[3]);
                    ResultSet rs = search.executeQuery();
                    if (!rs.next()) {
                        System.out.println("\nProgramming Language doesnt exist, if new language please insert into the language table and try again");
                    } else {
                        intpar[0] = rs.getInt("ID");
                        selectqueries(1, strpar, intpar);
                    }
                    rs.close();
                    search.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 2:
                System.out.println("Please input the name of the language:");
                strpar[0] = sc.nextLine();
                System.out.println("Please input the type of the language:");
                strpar[1] = sc.nextLine();
                System.out.println("Please input the skill level of the language :");
                intpar[0] = sc.nextInt();
                selectqueries(2, strpar, intpar);
                break;
            default:
                System.out.println("Invalid Input");
                break;
        }
    }

    public static void displaymainmenu() {
        //Menu Printing
        System.out.println("Welcome to the Project Mangement System (PMS)");
        System.out.println("Please select what you would like to do today:");
        System.out.println("[1] Insert a new entry");
        System.out.println("[2] View all projects");
        System.out.println("[3] Check most time consumed project");
        System.out.println("[4] List of all the programming languages used");
        System.out.println("[5] Most programming language used in projects");
        System.out.println("[6] Programming language took the most time to learn");
        System.out.println("[7] Programming language took the least time to learn");
        System.out.println("[8] Check if the projects required ongoing work and if the time is compensated correctly.");
        System.out.println("[9] Skill level of the language that took the least time");
        System.out.println("[10] Skill level of the language that took the most time");
        System.out.println("[11] Most extra work done for a client");
        System.out.println("[0] Exit");
        System.out.println("\n");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        connecttoDB();
        displaymainmenu();
        Scanner sc = new Scanner(System.in);
        try {
            int input = sc.nextInt();
            while (input != 0) {
                switch (input) {
                    case 1:
                        try {
                            displayinsertoptions();
                        } catch (InputMismatchException ex) {
                            System.out.println("\nPLEASE ENTER A VALID INTEGER ONLY");
                        }

                        break;
                    case 2:
                        select(1);
                        break;
                    case 3:
                        select(2);
                        break;
                    case 4:
                        select(3);
                        break;
                    case 5:
                        select(4);
                        break;
                    case 6:
                        select(5);
                        break;
                    case 7:
                        select(6);
                        break;
                    case 8:
                        select(7);
                        break;
                    case 9:
                        select(8);
                        break;
                    case 10:
                        select(9);
                        break;
                    case 11:
                        select(10);
                        break;
                    default:
                        System.out.println("Not a valid input");
                        break;
                }
                System.out.println("\nWhat would you like to do next?");
                displaymainmenu();
                input = sc.nextInt();
            }
        } catch (InputMismatchException ex) {
            System.out.println("\nPLEASE ENTER A VALID INTEGER ONLY");
        }
        System.out.println("\nBye Bye");
    }

}
