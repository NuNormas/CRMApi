package com.example.demo.Services

import java.math.BigDecimal
import java.time.LocalDateTime
import com.example.demo.Entities.Seller
import com.example.demo.Entities.PaymentType
import com.example.demo.Entities.Transaction
import org.springframework.stereotype.Service
import com.example.demo.Repositories.TransactionRepository

@Service
class TransactionService(private val transactionRepository: TransactionRepository) {

    fun getAllTransactions(): List<Transaction> = transactionRepository.findAll()

    fun getTransactionById(id: Long): Transaction? = transactionRepository.findById(id).orElse(null)

    fun createTransaction(
        seller: Seller,
        amount: BigDecimal,
        paymentType: PaymentType
    ): Transaction {
        val newTransaction =
            Transaction(seller = seller, amount = amount, paymentType = paymentType)
        return transactionRepository.save(newTransaction)
    }

    fun getAllTransactionsOfSeller(seller: Seller): List<Transaction> {
        return transactionRepository.findBySeller(seller)
    }

    fun getBestSellerByDate(startDate: LocalDateTime, endDate: LocalDateTime): Seller? {
        val allTransactionsForPeriod =
            transactionRepository.findAllByTransactionDateBetween(startDate, endDate)

        val sellersSum = allTransactionsForPeriod.groupBy { it.seller }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        return sellersSum.maxByOrNull { it.value }?.key
    }

    fun getSellersWithLessAmountOfTransactionPerPeriod(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        amountThreshold: BigDecimal
    ): List<Seller> {
        val allTransactionsForPeriod =
            transactionRepository.findAllByTransactionDateBetween(startDate, endDate)

        val sellersSum = allTransactionsForPeriod.groupBy { it.seller }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        return sellersSum.filter { it.value < amountThreshold }.keys.toList()
    }
}