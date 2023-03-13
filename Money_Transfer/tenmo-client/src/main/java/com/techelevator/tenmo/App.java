package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

/**
 * Class to run client side program application.
 */
public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final ConsoleService consoleService = new ConsoleService();
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private NumberFormat currency = NumberFormat.getCurrencyInstance();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection;
        while (currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection == 0) {
                break;
            } else {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account.");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    // handle logout functionality of user by clearing currentUser and redirect to login menu
    private void handleLogout() {
        currentUser = null;
        run();
    }

    private void mainMenu() {
        int menuSelection;
        while (true) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance(currentUser.getUser().getId());
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 6) {
                handleLogout();
            } else if (menuSelection == 0) {
                break;
            } else {
                System.out.println("Invalid Selection");
            }
        }
    }

    private void viewCurrentBalance(int userId) {
        Account account = accountService.getAccountByUserId(userId);

        if (account != null) {
            System.out.println("Your current account balance is: " + currency.format(account.getBalance()));
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void viewTransferHistory() {
        List<TransferDto> response = accountService.getTransferHistory(currentUser);
        consoleService.printTransferHistory(response, currentUser);

        int menuSelection = consoleService.promptForInt(
                "---------\n" +
                "Please enter transfer ID to view details (0 to cancel): ");
        if (menuSelection > 0) {
            consoleService.viewTransferDetails(response, menuSelection);
        }
    }

    private void viewPendingRequests() {
        consoleService.printPendingRequests(accountService.viewPendingRequests(currentUser));

        Integer transferIdToApproveDeny = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        if (transferIdToApproveDeny > 3000 && transferIdToApproveDeny < 4000) {
            //approve/deny menu call
            int menuSelection = consoleService.promptForInt(
                    "1: Approve\n" +
                    "2: Reject\n" +
                    "0: Don't approve or reject\n" +
                    "---------\n" +
                    "Please choose an option: ");
            if (menuSelection == 1) {
                accountService.transferRequestApproval(currentUser, transferIdToApproveDeny, true);
            } else if (menuSelection == 2) {
                accountService.transferRequestApproval(currentUser, transferIdToApproveDeny, false);
            } else if (menuSelection == 0) {
                return;
            } else {
                System.out.println("Invalid Selection.");
            }
        } else if (transferIdToApproveDeny < 0 || transferIdToApproveDeny > 0){
            System.out.println("Please enter a valid transfer ID number.");
        } else {
            return;
        }
    }

	private void sendBucks() {
        consoleService.printUsersSendList(accountService.getUsers());
        Integer userIdToSendTo = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        if (userIdToSendTo == currentUser.getUser().getId()) {
            System.out.println("Cannot send to same account.");
            return;
        } else if (userIdToSendTo == 0) {
            return;
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        System.out.println(accountService.sendBucks(currentUser, userIdToSendTo, amount));
	}

	private void requestBucks() {
        consoleService.printUsersSendList(accountService.getUsers());
        int userIdToRequestFrom = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        if (userIdToRequestFrom == 0) {
            return;
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        System.out.println(accountService.createTransferRequest(currentUser, userIdToRequestFrom, amount));
	}

}
