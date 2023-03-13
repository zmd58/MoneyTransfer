package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    List<TransferDto> getTransfersByUserId(int accountId);
    TransferDto getTransfersByTransferId(int transferId);

    Transfer getPendingTransferByTransferId(int transferId);

    List<TransferDto> getTransfersByPendingStatus(int currentUserId);

    Transfer createTransferRequest(Transfer transfer);

    Transfer updateTransferRequest(Transfer transfer);
}
