package com.example.demo.Services

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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

    fun getBestTransactionPeriodForSeller(seller: Seller): Pair<LocalDateTime, LocalDateTime>? {
        val transactions = transactionRepository.findBySeller(seller)

        if (transactions.isEmpty()) {
            return null
        }

        val transactionCounts = mutableMapOf<LocalDateTime, Int>()

        transactions.forEach { transaction ->
            val date = transaction.transactionDate.truncatedTo(ChronoUnit.DAYS)
            transactionCounts[date] = transactionCounts.getOrDefault(date, 0) + 1
        }

        var maxTransactions = 0
        var bestStart: LocalDateTime? = null
        var bestEnd: LocalDateTime? = null

        for (start in transactionCounts.keys) {
            var currentCount = 0
            var end = start

            while (transactionCounts.containsKey(end)) {
                currentCount += transactionCounts[end] ?: 0

                if (currentCount > maxTransactions) {
                    maxTransactions = currentCount
                    bestStart = start
                    bestEnd = end.plusDays(1)
                }

                end = end.plusDays(1)
            }
        }

        return if (bestStart != null && bestEnd != null) Pair(bestStart, bestEnd) else null
    }
}