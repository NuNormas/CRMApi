package com.example.demo

import java.math.BigDecimal
import java.time.LocalDateTime
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.junit.jupiter.api.Test
import com.example.demo.Entities.Seller
import org.junit.jupiter.api.Assertions.*
import org.springframework.http.HttpStatus
import com.example.demo.Entities.PaymentType
import com.example.demo.Entities.Transaction
import org.springframework.http.ResponseEntity
import com.example.demo.Services.SellerService
import com.example.demo.Services.TransactionService
import com.example.demo.Helpers.MissingFieldException
import com.example.demo.Controllers.TransactionController

class TransactionControllerTest {

    private val sellerService: SellerService = mock(SellerService::class.java)
    private val transactionService: TransactionService = mock(TransactionService::class.java)
    private val transactionController = TransactionController(sellerService, transactionService)

    @Test
    fun `should return all transactions`() {
        val transactions = listOf(
            Transaction(
                1,
                Seller(1, "John Doe", "123456789"),
                BigDecimal("10.00"),
                PaymentType.CASH,
                LocalDateTime.now()
            ),
            Transaction(
                2,
                Seller(2, "Jane Smith", "987654321"),
                BigDecimal("20.00"),
                PaymentType.CARD,
                LocalDateTime.now()
            )
        )
        `when`(transactionService.getAllTransactions()).thenReturn(transactions)

        val result = transactionController.getAllTransactions()

        assertEquals(transactions, result)
    }

    @Test
    fun `should return transaction by id`() {
        val transaction = Transaction(
            1,
            Seller(1, "John Doe", "123456789"),
            BigDecimal("10.00"),
            PaymentType.CASH,
            LocalDateTime.now()
        )
        `when`(transactionService.getTransactionById(1)).thenReturn(transaction)

        val response: ResponseEntity<Transaction> = transactionController.getTransactionById(1)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(transaction, response.body)
    }

    @Test
    fun `should throw NoSuchElementException when getting non-existent transaction by id`() {
        `when`(transactionService.getTransactionById(1)).thenReturn(null)

        assertThrows(NoSuchElementException::class.java) {
            transactionController.getTransactionById(1)
        }
    }

    @Test
    fun `should create a new transaction`() {
        val transactionData = mapOf(
            "sellerId" to "1",
            "amount" to "10.00",
            "paymentType" to "CASH"
        )
        val seller = Seller(1, "John Doe", "123456789")
        val newTransaction =
            Transaction(1, seller, BigDecimal("10.00"), PaymentType.CASH, LocalDateTime.now())
        `when`(sellerService.getSellerById(1)).thenReturn(seller)
        `when`(
            transactionService.createTransaction(
                seller,
                BigDecimal("10.00"),
                PaymentType.CASH
            )
        ).thenReturn(newTransaction)

        val response: ResponseEntity<Transaction> =
            transactionController.createTransaction(transactionData)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(newTransaction, response.body)
    }

    @Test
    fun `should throw MissingFieldException when creating a transaction without sellerId`() {
        val transactionData = mapOf(
            "amount" to "10.00",
            "paymentType" to "CASH"
        )

        assertThrows(MissingFieldException::class.java) {
            transactionController.createTransaction(transactionData)
        }
    }

    @Test
    fun `should throw MissingFieldException when creating a transaction without amount`() {
        val transactionData = mapOf(
            "sellerId" to "1",
            "paymentType" to "CASH"
        )

        val seller = Seller(1, "John Doe", "123456789")
        `when`(sellerService.getSellerById(1)).thenReturn(seller)

        assertThrows(MissingFieldException::class.java) {
            transactionController.createTransaction(transactionData)
        }
    }

    @Test
    fun `should throw MissingFieldException when creating a transaction without paymentType`() {
        val transactionData = mapOf(
            "sellerId" to "1",
            "amount" to "10.00"
        )

        val seller = Seller(1, "John Doe", "123456789")
        `when`(sellerService.getSellerById(1)).thenReturn(seller)

        assertThrows(MissingFieldException::class.java) {
            transactionController.createTransaction(transactionData)
        }
    }

    @Test
    fun `should get all transactions of a seller`() {
        val sellerId = 1L
        val seller = Seller(sellerId, "John Doe", "123456789")
        val transactions = listOf(
            Transaction(1, seller, BigDecimal("10.00"), PaymentType.CASH, LocalDateTime.now()),
            Transaction(2, seller, BigDecimal("20.00"), PaymentType.CARD, LocalDateTime.now())
        )
        `when`(sellerService.getSellerById(sellerId)).thenReturn(seller)
        `when`(transactionService.getAllTransactionsOfSeller(seller)).thenReturn(transactions)

        val response: ResponseEntity<List<Transaction>> =
            transactionController.getAllTransactionOfSeller(sellerId)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(transactions, response.body)
    }

    @Test
    fun `should throw NoSuchElementException when getting transactions of non-existent seller`() {
        val sellerId = 1L
        `when`(sellerService.getSellerById(sellerId)).thenReturn(null)

        assertThrows(NoSuchElementException::class.java) {
            transactionController.getAllTransactionOfSeller(sellerId)
        }
    }

    @Test
    fun `should get best seller of the day`() {
        val startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endDate =
            LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        val bestSeller = Seller(1, "John Doe", "123456789")
        `when`(transactionService.getBestSellerByDate(startDate, endDate)).thenReturn(bestSeller)

        val response: ResponseEntity<Seller?> = transactionController.getBestSellerOfDay()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(bestSeller, response.body)
    }

    @Test
    fun `should get best seller of the month`() {
        val startDate =
            LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)
                .withNano(0)
        val endDate =
            LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        val bestSeller = Seller(1, "John Doe", "123456789")
        `when`(transactionService.getBestSellerByDate(startDate, endDate)).thenReturn(bestSeller)

        val response: ResponseEntity<Seller?> = transactionController.getBestSellerOfMonth()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(bestSeller, response.body)
    }

    @Test
    fun `should get best seller of the quarter`() {
        val startDate =
            LocalDateTime.now().minusMonths(((LocalDateTime.now().monthValue - 1) % 3).toLong())
                .withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endDate =
            LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        val bestSeller = Seller(1, "John Doe", "123456789")
        `when`(transactionService.getBestSellerByDate(startDate, endDate)).thenReturn(bestSeller)

        val response: ResponseEntity<Seller?> = transactionController.getBestSellerOfQuarter()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(bestSeller, response.body)
    }


    @Test
    fun `should get best seller of the year`() {
        val startDate = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        val bestSeller = Seller(1, "John Doe", "123456789")
        `when`(transactionService.getBestSellerByDate(startDate, endDate)).thenReturn(bestSeller)

        val response: ResponseEntity<Seller?> = transactionController.getBestSellerOfYear()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(bestSeller, response.body)
    }

    @Test
    fun `should get sellers with less amount of transactions per period`() {
        val amount = BigDecimal("10.00")
        val startDateString = "2024-01-01T00:00:00"
        val endDateString = "2024-10-19T18:59:59.99"
        val startDate = LocalDateTime.parse(startDateString)
        val endDate = LocalDateTime.parse(endDateString)
        val sellersWithLessThanAmount = listOf(
            Seller(1, "John Doe", "123456789"),
            Seller(2, "Jane Smith", "987654321")
        )
        `when`(transactionService.getSellersWithLessAmountOfTransactionPerPeriod(startDate, endDate, amount)).thenReturn(sellersWithLessThanAmount)

        val response: ResponseEntity<List<Seller>> = transactionController.getSellersWithLessAmountOfTransactionPerPeriod(amount, startDateString, endDateString)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(sellersWithLessThanAmount, response.body)
    }

    @Test
    fun `should throw IllegalArgumentException when startDate is not in correct format`() {
        val amount = BigDecimal("10.00")
        val startDateString = "invalid-date"
        val endDateString = "2024-12-31T23:59:59.999999999"

        assertThrows(IllegalArgumentException::class.java) {
            transactionController.getSellersWithLessAmountOfTransactionPerPeriod(amount, startDateString, endDateString)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when endDate is not in correct format`() {
        val amount = BigDecimal("10.00")
        val startDateString = "2024-01-01T00:00:00"
        val endDateString = "invalid-date"

        assertThrows(IllegalArgumentException::class.java) {
            transactionController.getSellersWithLessAmountOfTransactionPerPeriod(amount, startDateString, endDateString)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when startDate is after endDate`() {
        val amount = BigDecimal("10.00")
        val startDateString = "2024-12-31T23:59:59.999999999"
        val endDateString = "2024-01-01T00:00:00"

        assertThrows(IllegalArgumentException::class.java) {
            transactionController.getSellersWithLessAmountOfTransactionPerPeriod(amount, startDateString, endDateString)
        }
    }

    @Test
    fun `should throw IllegalArgumentException when endDate is after current time`() {
        val amount = BigDecimal("10.00")
        val startDateString = "2024-01-01T00:00:00"
        val endDateString = (LocalDateTime.now().plusDays(1)).toString()

        assertThrows(IllegalArgumentException::class.java) {
            transactionController.getSellersWithLessAmountOfTransactionPerPeriod(amount, startDateString, endDateString)
        }
    }

    @Test
    fun `test getBestTransactionPeriodForSeller returns valid period`() {
        val sellerId = 1L
        val seller = Seller(sellerId, "John Doe", "123456789")
        val bestPeriod = Pair(LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4))

        `when`(sellerService.getSellerById(sellerId)).thenReturn(seller)
        `when`(transactionService.getBestTransactionPeriodForSeller(seller)).thenReturn(bestPeriod)

        val response: ResponseEntity<Pair<LocalDateTime, LocalDateTime>?> = transactionController.getBestTransactionPeriodForSeller(sellerId)

        assertEquals(ResponseEntity.ok(bestPeriod), response)
    }

    @Test
    fun `test getBestTransactionPeriodForSeller throws exception when seller not found`() {
        val sellerId = 1L

        `when`(sellerService.getSellerById(sellerId)).thenReturn(null)

        assertThrows(NoSuchElementException::class.java) {
            transactionController.getBestTransactionPeriodForSeller(sellerId)
        }
    }

    @Test
    fun `test getBestTransactionPeriodForSeller throws exception when no transactions found`() {
        val sellerId = 1L
        val seller = Seller(sellerId, "John Doe", "123456789")

        `when`(sellerService.getSellerById(sellerId)).thenReturn(seller)
        `when`(transactionService.getBestTransactionPeriodForSeller(seller)).thenReturn(null)

        assertThrows(Exception::class.java) {
            transactionController.getBestTransactionPeriodForSeller(sellerId)
        }
    }
}
