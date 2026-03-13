import java.util.*;

public class SocialMedia {

    // username -> userId
    private HashMap<String, Integer> users = new HashMap<>();

    // username -> attempt frequency
    private HashMap<String, Integer> attempts = new HashMap<>();

    // Check availability
    public boolean checkAvailability(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
        return !users.containsKey(username);
    }

    // Register user
    public void registerUser(String username, int userId) {
        if (checkAvailability(username)) {
            users.put(username, userId);
            System.out.println("User registered successfully.");
        } else {
            System.out.println("Username already taken.");
        }
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!users.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String dotVersion = username.replace("_", ".");
        if (!users.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }

        return suggestions;
    }

    // Most attempted username
    public String getMostAttempted() {
        String most = "";
        int max = 0;

        for (String key : attempts.keySet()) {
            int count = attempts.get(key);
            if (count > max) {
                max = count;
                most = key;
            }
        }

        return most + " (" + max + " attempts)";
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        SocialMedia system = new SocialMedia();

        while (true) {
            System.out.println("\n1. Register User");
            System.out.println("2. Check Username Availability");
            System.out.println("3. Suggest Alternatives");
            System.out.println("4. Most Attempted Username");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {

                case 1:
                    System.out.print("Enter username: ");
                    String username = sc.nextLine();

                    System.out.print("Enter user ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    system.registerUser(username, id);
                    break;

                case 2:
                    System.out.print("Enter username to check: ");
                    String checkUser = sc.nextLine();

                    if (system.checkAvailability(checkUser))
                        System.out.println("Username is available.");
                    else
                        System.out.println("Username is already taken.");
                    break;

                case 3:
                    System.out.print("Enter username: ");
                    String suggestUser = sc.nextLine();

                    System.out.println("Suggestions: " +
                            system.suggestAlternatives(suggestUser));
                    break;

                case 4:
                    System.out.println("Most attempted username: " +
                            system.getMostAttempted());
                    break;

                case 5:
                    System.out.println("Exiting...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}