package banking;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Bank {
    Scanner scanner = new Scanner(System.in);
    Statement statement;

    public Bank(Statement statement) {
        this.statement = statement;
    }

    boolean exit = true;
    public void run() {
        while (exit) {
            System.out.println(
                    "1. Create an account\n" +
                            "2. Log into account\n" +
                            "0. Exit"
            );
            int action = Integer.parseInt(scanner.nextLine());
            System.out.println();
            switch (action) {
                case 0:
                    exit();
                    break;
                case 1:
                    createCard();
                    break;
                case 2:
                    login();
                    break;
            }
        }
    }

    private void exit() {
        System.out.println("Bye!");
        exit = false;
    }

    private void createCard() {
        Card card = new Card();
        System.out.printf(
                "Your card has been created\n" +
                        "Your card number:\n" +
                        "%s\n" +
                        "Your card PIN:\n" +
                        "%s\n",
                card.getNumber(),
                card.getPin()
        );
        try {
            statement.executeUpdate("INSERT INTO card (number , pin) VALUES (" + card.getNumber() +", " + card.getPin() + ");");
        } catch (Exception ignored) {
        }
        System.out.println();
    }

    private void login() {
        System.out.println("Enter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();

        try (ResultSet result = statement.executeQuery("SELECT id FROM card WHERE number =" + cardNumber + " AND pin =" + pin)){
            int id = result.getInt("id");
            cardAction(id);
        } catch (Exception ignored) {
            System.out.println("Wrong card number or PIN!");
            System.out.println();
        }
    }

    private void cardAction(int id) {
        System.out.println("You have successfully logged in!");
        System.out.println();
        while (true) {
            System.out.println(
                    "1. Balance\n" +
                            "2. Add income\n" +
                            "3. Do transfer\n" +
                            "4. Close account\n" +
                            "5. Log out\n" +
                            "0. Exit"
            );
            int action = Integer.parseInt(scanner.nextLine());
            System.out.println();
            switch (action) {
                case 0:
                    exit();
                    return;
                case 1:
                    try (ResultSet result = statement.executeQuery("SELECT balance FROM card WHERE id =" + id)) {
                        System.out.println("Balance: " + result.getInt("balance"));
                    } catch (Exception ignored) {
                    }
                    break;
                case 2:
                    try {
                        System.out.println("Enter income:");
                        int sum = Integer.parseInt(scanner.nextLine());
                        statement.executeUpdate("UPDATE card SET balance = balance + " + sum + " WHERE id =" + id);
                        System.out.println("Income was added!\n");
                    } catch (Exception ignored) {
                    }
                    break;
                case 3:
                    System.out.println("Transfer\n" +
                            "Enter card number:");
                    String otherNumber = scanner.nextLine();
                    try (ResultSet result = statement.executeQuery("SELECT number FROM card WHERE id =" + id)) {
                        if (result.getString("number").equals(otherNumber)) {
                            System.out.println("You can't transfer money to the same account!\n");
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                    if (!checkLua(otherNumber)) {
                        System.out.println("Probably you made mistake in the card number. Please try again!\n");
                        break;
                    }
                    try (ResultSet otherResult = statement.executeQuery("SELECT id, balance FROM card WHERE number =" + otherNumber)) {
                        int otherId = otherResult.getInt("id");
                        try (ResultSet result = statement.executeQuery("SELECT balance FROM card WHERE id =" + id)) {
                            System.out.println("Enter how much money you want to transfer:");
                            int sum = Integer.parseInt(scanner.nextLine());
                            if (sum > result.getInt("balance")) {
                                System.out.println("Not enough money!\n");
                                break;
                            }
                            statement.executeUpdate("UPDATE card SET balance = balance - " + sum + " WHERE id =" + id);
                            statement.executeUpdate("UPDATE card SET balance = balance + " + sum + " WHERE id =" + otherId);
                            System.out.println("Success!\n");
                        } catch (Exception ignored) {
                        }
                    } catch (Exception ignored) {
                        System.out.println("Such a card does not exist.");
                    }

                    break;
                case 4:
                   try {
                       statement.executeUpdate("DELETE FROM card WHERE id= " + id);
                       System.out.println("The account has been closed!\n");
                       return;
                   } catch (Exception ignored) {
                   }
                case 5:
                    return;
            }
        }
    }

    private boolean checkLua(String number) {
        int sum = 0;
        for (int i = 1; i <= 15; i++) {
            int num = Character.getNumericValue(number.charAt(i-1));
            if (i % 2 != 0) num *= 2;
            if (num > 9) num -= 9;
            sum += num;
        }
        return (sum + Character.getNumericValue(number.charAt(15))) % 10 == 0;
    }
}
