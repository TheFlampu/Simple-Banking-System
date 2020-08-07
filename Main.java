package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + args[1]);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                {
                    try {
                        statement.executeUpdate(
                                "CREATE TABLE card (\n" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                        "number TEXT,\n" +
                                        "pin TEXT,\n" +
                                        "balance INTEGER DEFAULT 0\n" +
                                        ");"
                        );
                    } catch (Exception ignored) {
                    }
                    new Bank(statement).run();
                }
            }
        } catch (Exception ignored) {
        }
    }
}
