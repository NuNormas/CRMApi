package com.example.demo.Repositories

import java.time.LocalDateTime
import com.example.demo.Entities.Seller
import com.example.demo.Entities.Transaction
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findBySeller(seller: Seller) : List<Transaction>

    fun findAllByTransactionDateBetween(startDate: LocalDateTime, endDate: LocalDateTime) : List<Transaction>
}