package Models;


public class UserProfile {

    private int id;
    private int balance;
    private String rank;

    public UserProfile(int id, int balance, String rank) {
        this.id = id;
        this.balance = balance;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public String getRank() {
        return rank;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

}

