package banking;

public class Card {
    private String number;
    private String pin;
    private long balance;

    public Card() {
        number = "400000" + addControlSum(generateNumber(9));
        pin = generateNumber(4);
        balance = 0L;
    }

    private String generateNumber(int size) {
        String number = "";
        for (int i = 0; i < size; i++) {
            number = number.concat(String.valueOf((int)(Math.random() * 9)));
        }
        return number;
    }

    private String addControlSum(String number) {
       int sum = 8;
       for (int i = 1; i < 10; i++) {
           int num = Character.getNumericValue(number.charAt(i-1));
           if (i % 2 != 0) num *= 2;
           if (num > 9) num -= 9;
           sum += num;
       }
       return number + (sum % 10 == 0 ? 0 : 10 - sum % 10);
    }

    public String getNumber() {
        return number;
    }

    public long getBalance() {
        return balance;
    }

    public String getPin() {
        return pin;
    }


}
