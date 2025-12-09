package Models;

public class Session {
    private static String currentUsername;
    private static boolean isLoggedIn = false;
    private static int currentBalance;

    public static void login(String username,int balance) {
        currentBalance = balance;
        currentUsername = username;
        isLoggedIn = true;
    }

    public static void logout() {
        currentBalance = 0;
        currentUsername = null;
        isLoggedIn = false;
    }
    public static int getCurrentBalance(){
        return currentBalance;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static boolean isLoggedIn() {
        return isLoggedIn;
    }
}
