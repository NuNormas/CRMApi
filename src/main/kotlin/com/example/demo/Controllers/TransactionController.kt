package com.example.demo.Controllers

import java.math.BigDecimal
import com.example.demo.Entities.PaymentType
import com.example.demo.Entities.Seller
import org.springframework.http.HttpStatus
import com.example.demo.Entities.Transaction
import com.example.demo.Helpers.MissingFieldException
import com.example.demo.Services.SellerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.example.demo.Services.TransactionService
import org.springframework.http.RequestEntity
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val sellerService: SellerService,
    private val transactionService: TransactionService
) {

    @GetMapping
    fun getAllTransactions() : List<Transaction> = transactionService.getAllTransactions()

    @GetMapping("/{id}")
    fun getTransactionById(@PathVariable id: Long): ResponseEntity<Transaction> {
        val transaction = transactionService.getTransactionById(id) ?: throw NoSuchElementException("transaction with id $id")
        return ResponseEntity.ok(transaction)
    }

    @PostMapping
    fun createTransaction(@RequestBody transactionData: Map<String, String>): ResponseEntity<Transaction> {
        val sellerIdValue = transactionData["sellerId"] ?: throw MissingFieldException("sellerId")
        val sellerId = sellerIdValue.toLongOrNull() ?: throw IllegalArgumentException("sellerId must have a type Long")
        val seller = sellerService.getSellerById(sellerId) ?: throw NoSuchElementException("Seller with id $sellerId")

        val amountValue = transactionData["amount"] ?: throw MissingFieldException("amount")
        val amount = amountValue.toBigDecimalOrNull() ?: throw IllegalArgumentException("amount must have a type BigDecimal")

        val paymentTypeValue = transactionData["paymentType"] ?: throw MissingFieldException("paymentType")
        val paymentType = PaymentType.suitable(paymentTypeValue) ?: throw IllegalArgumentException("paymentType must be \"CASH\", \"CARD\" or \"TRANSFER\"")

        val newTransaction = transactionService.createTransaction(seller, amount, paymentType)
        return ResponseEntity.status(HttpStatus.CREATED).body(newTransaction)
    }

    @GetMapping("sellers/{sellerId}")
    fun getAllTransactionOfSeller(@PathVariable sellerId: Long) : ResponseEntity<List<Transaction>> {
        val seller = sellerService.getSellerById(sellerId) ?: throw NoSuchElementException("Seller with id $sellerId")

        val transactionsOfSeller = transactionService.getAllTransactionsOfSeller(seller)
        return ResponseEntity.ok(transactionsOfSeller)
    }

    @GetMapping("best-seller/day")
    fun getBestSellerOfDay() : ResponseEntity<Seller?> {
        val startDate = LocalDateTime.now()
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val endDate = LocalDateTime.now()
            .withHour(23)
            .withMinute(59)
            .withSecond(59)
            .withNano(999999999)

        val bestSeller = transactionService.getBestSellerByDate(startDate, endDate)
        return ResponseEntity.ok(bestSeller)
    }

    @GetMapping("best-seller/month")
    fun getBestSellerOfMonth() : ResponseEntity<Seller?> {
        val startDate = LocalDateTime.now()
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val endDate = LocalDateTime.now()
            .withHour(23)
            .withMinute(59)
            .withSecond(59)
            .withNano(999999999)

        val bestSeller = transactionService.getBestSellerByDate(startDate, endDate)
        return ResponseEntity.ok(bestSeller)
    }

    @GetMapping("best-seller/quarter")
    fun getBestSellerOfQuarter() : ResponseEntity<Seller?> {
        val startDate = LocalDateTime.now().minusMonths(((LocalDateTime.now().monthValue - 1) % 3).toLong())
            .withDayOfMonth(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val endDate = LocalDateTime.now()
            .withHour(23)
            .withMinute(59)
            .withSecond(59)
            .withNano(999999999)

        val bestSeller = transactionService.getBestSellerByDate(startDate, endDate)
        return ResponseEntity.ok(bestSeller)
    }

    @GetMapping("best-seller/year")
    fun getBestSellerOfYear() : ResponseEntity<Seller?> {
        val startDate = LocalDateTime.now()
            .withDayOfYear(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val endDate = LocalDateTime.now()
            .withHour(23)
            .withMinute(59)
            .withSecond(59)
            .withNano(999999999)

        val bestSeller = transactionService.getBestSellerByDate(startDate, endDate)
        return ResponseEntity.ok(bestSeller)
    }

    @GetMapping("sellers/less-than")
    fun getSellersWithLessAmountOfTransactionPerPeriod(
        @RequestParam("amount") amount: BigDecimal,
        @RequestParam("startDate") startDateString: String,
        @RequestParam("endDate") endDateString: String
    ) : ResponseEntity<List<Seller>> {

        val startDate: LocalDateTime = try {
            LocalDateTime.parse(startDateString)
        } catch (e: Exception) {
            throw IllegalArgumentException("startDate must be in format yyyy-MM-dd'T'HH:mm:ss[.SSS]")
        }

        val endDate: LocalDateTime = try {
            LocalDateTime.parse(endDateString)
        } catch (e: Exception) {
            throw IllegalArgumentException("endDate must be in format yyyy-MM-dd'T'HH:mm:ss[.SSS]")
        }

        if (startDate.isAfter(endDate)) throw IllegalArgumentException("startDate must be earlier than endDate")
        if (endDate.isAfter(LocalDateTime.now())) throw IllegalArgumentException("endDate must be earlier than current time")

        val sellersWithLessThanAmount = transactionService.getSellersWithLessAmountOfTransactionPerPeriod(startDate, endDate, amount)
        return ResponseEntity.ok(sellersWithLessThanAmount)
    }
}