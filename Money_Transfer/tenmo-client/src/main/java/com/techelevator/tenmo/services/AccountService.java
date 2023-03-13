package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Used to make calls to the AccountController on the server side
 * to get access and retrieve information about accounts from database.
 */
public class AccountService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private String authToken = null;

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * sends request to retrieve account matching the provided userId.
     * @param userId
     * @return Account
     */
    public Account getAccountByUserId(int userId) {
        // TODO implement getBalance
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "/transfer/" + userId, HttpMethod.GET, makeAuthEntity(),
                    Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    /**
     * sends request to retrieve account balance matching the current user.
     * @param authenticatedUser
     * @return BigDecimal
     */
    public BigDecimal getBalance(AuthenticatedUser authenticatedUser) {
        Account account = null;
        try {
            ResponseEntity<Account> response = restTemplate.exchange(baseUrl + "/transfer/" + authenticatedUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(),
                    Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account.getBalance();
    }

    /**
     * sends request to retrieve transfer history involving the current user.
     * @param authenticatedUser
     * @return List<TransferDto>
     */
    public List<TransferDto> getTransferHistory(AuthenticatedUser authenticatedUser){
        setAuthToken(authenticatedUser.getToken());
        List<TransferDto> transfers = new ArrayList<>();
        try {
            ResponseEntity<TransferDto[]> response =
                    restTemplate.exchange(baseUrl + "transfer/history/users/" + authenticatedUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), TransferDto[].class);
            transfers = List.of(Objects.requireNonNull(response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        if(transfers.isEmpty()) {
            BasicLogger.log("No transfer history");
        }

        return transfers;
    }

    /**
     * sends request to retrieve all requests with status = pending for current user.
     * @param authenticatedUser
     * @return List<TransferDto>
     */
    public List<TransferDto> viewPendingRequests(AuthenticatedUser authenticatedUser) {
        setAuthToken(authenticatedUser.getToken());
        try {
            ResponseEntity<TransferDto[]> response = restTemplate.exchange(baseUrl + "transfer/pending/" + authenticatedUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), TransferDto[].class);
            return List.of(Objects.requireNonNull(response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * sends request to update pending transfer to make status = Approved.
     * @param authenticatedUser
     * @param transferId
     * @param approved
     * @return String
     */
    public String transferRequestApproval(AuthenticatedUser authenticatedUser, int transferId, boolean approved){
        setAuthToken(authenticatedUser.getToken());
        try {
            restTemplate.exchange(baseUrl + "transfer/pending/" + authenticatedUser.getUser().getId(), HttpMethod.PUT, makeAuthEntity(), Transfer.class);
            return "The transfer has been updated.";
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return null;
    }

    /**
     * sends request to execute a transfer to send bucks to the provided userId.
     * @param authenticatedUser
     * @param userIdToSendTo
     * @param amount
     * @return String
     */
    public String sendBucks(AuthenticatedUser authenticatedUser, Integer userIdToSendTo, BigDecimal amount) {
        setAuthToken(authenticatedUser.getToken());
        Account sender = getAccountByUserId(authenticatedUser.getUser().getId());
        if ((sender.getBalance()).compareTo(amount) < 0)  {
           return  "You do not have enough funds to complete this transaction";
        }
        if (amount.compareTo(BigDecimal.valueOf(0)) <= 0) {
            return  "Amount must be greater than 0";
        }
        try {
            restTemplate.exchange(baseUrl + "/transfer/send/" + authenticatedUser.getUser().getId()
                            + "/" + userIdToSendTo + "/" + amount, HttpMethod.POST, makeAuthEntity(), Boolean.class);
            createTransferSend(authenticatedUser, userIdToSendTo, amount);

            return  "Amount Sent Successfully.";
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return  "Amount Sent Unsuccessfully";
    }

    /**
     * sends request to create transfer request for current user to request from the
     * provided userIdToRequestFrom.
     * @param authenticatedUser
     * @param userIdToRequestFrom
     * @param amount
     * @return String
     */
    public String createTransferRequest(AuthenticatedUser authenticatedUser, int userIdToRequestFrom ,BigDecimal amount) {
        Account accountRequesting = getAccountByUserId(authenticatedUser.getUser().getId());
        Account accountFrom = getAccountByUserId(userIdToRequestFrom);
        if (accountFrom == null) {
            return  "User not found.";
        }

        int accountRequestingId = accountRequesting.getAccountId();
        int accountFromId = accountFrom.getAccountId();
        Transfer newTransfer = null;
        if (accountRequestingId > 0 && accountFromId > 0) {
            newTransfer = new Transfer(1, 1, accountFromId, accountRequestingId, amount );
        } else {
            return  "Could not find users with the given Id";
        }

        setAuthToken(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(newTransfer, makeAuthEntity().getHeaders());
        try {
            restTemplate.exchange(baseUrl + "/transfer",HttpMethod.POST, entity, Transfer.class).getBody();
            return "Request Created.";

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return "Request Could Not Be Created";
    }

    /**
     * sends request to create send transfer with the provided users and amount.
     * @param authenticatedUser
     * @param userIdToRequestFrom
     * @param amount
     * @return String
     */
    public String createTransferSend(AuthenticatedUser authenticatedUser, int userIdToRequestFrom ,BigDecimal amount) {
        Account accountRequesting = getAccountByUserId(authenticatedUser.getUser().getId());
        Account accountFrom = getAccountByUserId(userIdToRequestFrom);
        if (accountFrom == null) {
            return  "User not found.";
        }

        int accountRequestingId = accountRequesting.getAccountId();
        int accountFromId = accountFrom.getAccountId();
        Transfer newTransfer = null;
        if (accountRequestingId > 0 && accountFromId > 0) {
            newTransfer = new Transfer(2, 2, accountFromId, accountRequestingId, amount );
        } else {
            return  "Could not find users with the given Id";
        }

        setAuthToken(authenticatedUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(newTransfer, makeAuthEntity().getHeaders());
        try {
            restTemplate.exchange(baseUrl + "/transfer",HttpMethod.POST, entity, Transfer.class).getBody();
            return "Request Created.";

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return "Request Could Not Be Created";
    }

    /**
     * sends request to retrieve all the current users.
     * @return List<User>
     */
    public List<User> getUsers() {
        List<User> users = List.of(restTemplate.getForObject(baseUrl + "/transfer/users", User[].class));
        if (users != null) {
            return users;
        }
        System.out.println( "Could not find users");
        return null;
    }

    /**
     * creates Authentication entity.
     * @return HttpEntity<Void>
     */
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders(); 
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

}
