package dejay.rnd.billyG.service;

import dejay.rnd.billyG.domain.Rental;
import dejay.rnd.billyG.domain.Transaction;
import dejay.rnd.billyG.domain.TransactionHistory;
import dejay.rnd.billyG.repository.RentalRepository;
import dejay.rnd.billyG.repository.TransactionHistoryRepository;
import dejay.rnd.billyG.repository.TransactionRepository;
import dejay.rnd.billyG.repositoryImpl.RentalRepositories;
import dejay.rnd.billyG.repositoryImpl.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionRepository transactionRepository;
    LocalDateTime date = LocalDateTime.now();
    Date now_date = Timestamp.valueOf(date);

    @Transactional
    public TransactionHistory insertHistory(TransactionHistory transactionHistory) {
        return transactionHistoryRepository.save(transactionHistory);
    }

    @Transactional
    public Transaction insertTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }


    @Transactional
    public void updateTransacion(Transaction transaction) {
        transaction.setUpdateAt(now_date);
    }

}
