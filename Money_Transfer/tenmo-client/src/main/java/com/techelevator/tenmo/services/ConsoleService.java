package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * handles all the prints to the console
 */
public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println(" _       __     __                             __           ____________                    \n" +
                "| |     / /__  / /________  ____ ___  ___     / /_____     /_  __/ ____/___  ____ ___  ____ \n" +
                "| | /| / / _ \\/ / ___/ __ \\/ __ `__ \\/ _ \\   / __/ __ \\     / / / __/ / __ \\/ __ `__ \\/ __ \\\n" +
                "| |/ |/ /  __/ / /__/ /_/ / / / / / /  __/  / /_/ /_/ /    / / / /___/ / / / / / / / / /_/ /\n" +
                "|__/|__/\\___/_/\\___/\\____/_/ /_/ /_/\\___/   \\__/\\____/    /_/ /_____/_/ /_/_/ /_/ /_/\\____/ ");
        System.out.println("   _____       ____          ______           __        _____                              ____                                   __\n" +
                "  / ___/____ _/ __/__       / ____/___ ______/ /_      / ___/___  _______  __________     / __ \\____ ___  ______ ___  ___  ____  / /______\n" +
                "  \\__ \\/ __ `/ /_/ _ \\     / /_  / __ `/ ___/ __/      \\__ \\/ _ \\/ ___/ / / / ___/ _ \\   / /_/ / __ `/ / / / __ `__ \\/ _ \\/ __ \\/ __/ ___/\n" +
                " ___/ / /_/ / __/  __/    / __/ / /_/ (__  ) /_ _     ___/ /  __/ /__/ /_/ / /  /  __/  / ____/ /_/ / /_/ / / / / / /  __/ / / / /_(__  )\n" +
                "/____/\\__,_/_/  \\___( )  /_/    \\__,_/____/\\__/( )   /____/\\___/\\___/\\__,_/_/   \\___/  /_/    \\__,_/\\__, /_/ /_/ /_/\\___/_/ /_/\\__/____/\n" +
                "                    |/                         |/                                                  /____/                                ");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("6: Logout");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

    public void printPendingRequests(List<TransferDto> transferPendingList) {

        System.out.println("-------------------------------------------\n" +
                "Pending Transfers\n" +
                "ID          To                     Amount\n" +
                "-------------------------------------------");
        for (TransferDto transferDto : transferPendingList) {
            System.out.printf("%-11d %-22s $%4.2f\n",
                    transferDto.getId(), transferDto.getAccountToUsername(), transferDto.getAmount());
        }
        System.out.println("---------\n");
    }

    public Integer printSendRequest(){
        System.out.println("Enter ID of user you are sending to (0 to cancel):");
        String input = scanner.nextLine();
        Integer userId = Integer.parseInt(input);
        return userId;
    }



    public void printUsersSendList(List<User> users) {

        System.out.printf("-------------------------------------------\n" +
                          "Users\n" +
                          "ID          Name\n" +
                          "-------------------------------------------\n");
        for (User user : users) {
            System.out.println(user.getId()+ "        " + user.getUsername());
        }
    }

    public void printTransferHistory(List<TransferDto> transferHistoryList, AuthenticatedUser authenticatedUser) {
        System.out.printf("-------------------------------------------\n" +
                "Transfers\n" +
                "ID          From/To                 Amount\n" +
                "-------------------------------------------\n");
        for (TransferDto transferDto : transferHistoryList) {
            if (transferDto.getAccountToUsername().equals(authenticatedUser.getUser().getUsername())) {
                System.out.printf("%-11d From: %-16s $%4.2f\n",
                        transferDto.getId(), transferDto.getAccountFromUsername(), transferDto.getAmount());
            } else if (transferDto.getAccountFromUsername().equals(authenticatedUser.getUser().getUsername())) {
                System.out.printf("%-11d To:   %-16s $%4.2f\n",
                        transferDto.getId(), transferDto.getAccountToUsername(), transferDto.getAmount());
            } else {
                System.out.println("It skipped the if");
            }
        }
    }

    public void viewTransferDetails(List<TransferDto> listOfTransferDtos, int transferId) {
        TransferDto transferDetails = listOfTransferDtos.stream().filter(transferDto -> transferDto.getId() == transferId).findFirst().orElse(null);
        if (transferDetails != null) {
            System.out.printf(
                    "--------------------------------------------\n" +
                            "Transfer Details\n" +
                            "--------------------------------------------\n" +
                            " Id: %d\n" +
                            " From: %s\n" +
                            " To: %s\n" +
                            " Type: %s\n" +
                            " Status: %s\n" +
                            " Amount: %4.2f\n",
                    transferDetails.getId(),
                    transferDetails.getAccountFromUsername(),
                    transferDetails.getAccountToUsername(),
                    transferDetails.getTransferType(),
                    transferDetails.getTransferStatus(),
                    transferDetails.getAmount());
        } else {
            System.out.println("ERROR: Transfer does not exist.");
        }
    }
}
