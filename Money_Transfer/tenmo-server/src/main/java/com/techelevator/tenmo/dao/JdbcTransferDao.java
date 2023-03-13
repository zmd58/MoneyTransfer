package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JdbcTransferDao used to access transfer table in database.
 */
@Component
public class JdbcTransferDao implements TransferDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * getTransfersByUserId is used to retrieve all the transfers
     * involving the user with the provided userId.
     * @param userId
     * @return List<Transfer>
     */
    @Override
    public List<TransferDto> getTransfersByUserId(int userId) {
        List<TransferDto> transfersDto = new ArrayList<>();
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount " +
                "FROM transfer t " +
                "JOIN transfer_type tt USING(transfer_type_id) " +
                "JOIN transfer_status ts USING(transfer_status_id) " +
                "JOIN account a ON a.account_id = t.account_from OR a.account_id = t.account_to " +
                "JOIN tenmo_user u USING(user_id) " +
                "WHERE a.user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            TransferDto transferDto = mapRowToTransferHistoryDto(results);
            transfersDto.add(transferDto);
        }

        return transfersDto;
    }

    /**
     * getTransfersByTransferId is used to retrieve a single transfer
     * with the provided transferId.
     * @param transferId
     * @return TransferDto
     */
    @Override
    public TransferDto getTransfersByTransferId(int transferId) {
        TransferDto transfersDto = new TransferDto();
        String sql = "SELECT t.transfer_id, tt.transfer_type_desc, ts.transfer_status_desc, t.account_from, t.account_to, t.amount " +
                "FROM transfer t " +
                "JOIN transfer_type tt USING(transfer_type_id) " +
                "JOIN transfer_status ts USING(transfer_status_id) " +
                "WHERE t.transfer_id = ?;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        while (results.next()) {
            transfersDto = mapRowToTransferHistoryDto(results);
        }
        return transfersDto;

    }

    /**
     * getPendingTransfersByTransferId is used to retrieve the pending
     * transfer that have the provided transferId.
     * @param transferId
     * @return Transfer
     */
    @Override
    public Transfer getPendingTransferByTransferId(int transferId) {
        Transfer transferById = new Transfer();
        String sql = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, t.account_from, t.account_to, t.amount" +
                "FROM transfer t " +
                //"JOIN account a ON t.account_from = a.account_id " +
                //"JOIN tenmo_user u USING(user_id) " +
                "WHERE t.transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        while (results.next()) {
            transferById = mapRowToTransfer(results);
        }
        return transferById;
    }

    /**
     * getTransfersByPendingStatus retrieves all transfers for the
     * provided currentUserId that have the status = Pending.
     * @param currentUserId
     * @return List<TransferDto>
     */
    @Override
    public List<TransferDto> getTransfersByPendingStatus(int currentUserId) {
        List<TransferDto> pendingTransfers = new ArrayList<>();
        String sql = "SELECT t.transfer_id, u.username, t.amount " +
                "FROM transfer t " +
                "JOIN transfer_type tt USING(transfer_type_id) " +
                "JOIN transfer_status ts USING(transfer_status_id) " +
                "JOIN account a ON t.account_to = a.account_id " +
                "JOIN tenmo_user u USING(user_id) " +
                "WHERE t.account_from IN (" +
                        "SELECT account_id " +
                        "FROM account " +
                        "WHERE user_id = ? " +
                "AND t.transfer_status_id = (" +
                        "SELECT transfer_status_id " +
                        "FROM transfer_status " +
                        "WHERE transfer_status_desc = 'Pending'));";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, currentUserId);
        while (results.next()) {
            TransferDto transferDto = mapRowToTransferPendingDto(results);
            pendingTransfers.add(transferDto);
        }
        if (pendingTransfers.isEmpty()){
            pendingTransfers.add(0, new TransferDto(9999, "No pending transfers", BigDecimal.valueOf(0.00)));
        }
        return pendingTransfers;
    }

    /**
     * createTransferRequest used to create a transfer within
     * the database.
     * @param transfer
     * @return Transfer
     */
    @Override
    public Transfer createTransferRequest(Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?,?,?,?,?) RETURNING transfer_id;";
        Integer transferId = null;
        try {
            transferId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransferType(), transfer.getTransferStatus(),
                    transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("User not found to create transfer request.");
        }
        if (transferId == null) {
            return null;
        }
        transfer.setId(transferId);
        return transfer;
    }

    /**
     * updateTransferRequest used to update the provided transfer
     * in the database.
     * @param transfer
     * @return Transfer
     */
    @Override
    public Transfer updateTransferRequest(Transfer transfer) {
        String sql = "UPDATE transfer SET (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?,?,?,?,?) RETURNING transfer_id;";
        Integer transferId = null;
        try {
            transferId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getTransferType(), transfer.getTransferStatus(),
                    transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("User not found to create transfer request.");
        }
        if (transferId == null) {
            return null;
        }
        transfer.setId(transferId);
        return transfer;
    }

    /**
     * mapRowToTransfer used to deserialize retrieved row set
     * from the database into a Transfer object.
     * @param rs
     * @return Transfer
     */
    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setId(rs.getInt("transfer_id"));
        transfer.setTransferType(rs.getInt("transfer_type_id"));
        transfer.setTransferStatus(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }

    /**
     * mapRowToTransferPendingDto used to deserialize retrieved row set
     * from the database into a TransferDto object that holds only transfer_id,
     * username, and amount.
     * @param rs
     * @return TransferDto
     */
    private TransferDto mapRowToTransferPendingDto(SqlRowSet rs) {
        TransferDto transferDto = new TransferDto();
        transferDto.setId(rs.getInt("transfer_id"));
        transferDto.setAccountToUsername(rs.getString("username"));
        transferDto.setAmount(rs.getBigDecimal("amount"));
        return transferDto;
    }

    /**
     * mapRowToTransferPendingDto used to deserialize retrieved row set
     * from the database into a TransferDto object with string variables
     * for transferType and transferStatus.
     * @param rs
     * @return TransferDto
     */
    private TransferDto mapRowToTransferHistoryDto(SqlRowSet rs) {
        TransferDto transferDto = new TransferDto();
        transferDto.setId(rs.getInt("transfer_id"));
        transferDto.setTransferType(rs.getString("transfer_type_desc"));
        transferDto.setTransferStatus(rs.getString("transfer_status_desc"));
        transferDto.setAccountFromId(rs.getInt("account_from"));
        transferDto.setAccountToId(rs.getInt("account_to"));
        transferDto.setAmount(rs.getBigDecimal("amount"));
        return transferDto;
    }

}
