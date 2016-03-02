package com.theironyard;

import jodd.json.JsonSerializer;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS messages (id IDENTITY, author VARCHAR, text VARCHAR)");
    }

    public static void dropTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE messages");
        conn.close();
    }

    public static void insertMessage(Connection conn, String author, String text) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?)");
        stmt.setString(1, author);
        stmt.setString(2, text);
        stmt.execute();
    }

    public static ArrayList<Message> selectMessage(Connection conn) throws SQLException {
        ArrayList<Message> messages = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages");
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String author = results.getString("author");
            String text = results.getString("text");
            messages.add(new Message(id, author, text));
        }
        return messages;
    }


    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);


        Spark.externalStaticFileLocation("public");
        Spark.init();

        Spark.get(  //only 2 arguments, no mustache
                "/get-messages",
                ((request, response) ->  {
                    ArrayList<Message> messages = selectMessage(conn);
                    JsonSerializer serializer = new JsonSerializer();
                    return serializer.serialize(messages);
                })
        );

        Spark.post(
                "/add-message",
                ((request, response) ->  {
                    String author = request.queryParams("author");
                    String text = request.queryParams("text");
                    insertMessage(conn, author, text);
                    return "";
                })
        );

    }
}




	// write your code here




