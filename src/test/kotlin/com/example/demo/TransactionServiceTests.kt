package com.example.demo

import org.mockito.Mock
import java.util.Optional
import java.math.BigDecimal
import org.mockito.kotlin.any
import org.mockito.InjectMocks
import java.time.LocalDateTime
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import java.time.temporal.ChronoUnit
import org.junit.jupiter.api.Assertions.*
import com.example.demo.Entities.Seller
import com.example.demo.Entities.PaymentType
import com.example.demo.Entities.Transaction
import com.example.demo.Services.TransactionService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import com.example.demo.Repositories.TransactionRepository

@ExtendWith(MockitoExtension::class)
class TransactionServiceTest {

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @InjectMocks
    private lateinit var transactionService: TransactionService

    @Test
    fun `test get all transactions`() {
        val transactions = listOf(
            Transaction(1, Seller(1, "John Doe", "123456789"), BigDecimal("10.00"), PaymentType.CASH, LocalDateTime.now()),
            Transaction(2, Seller(2, "Jane Smith", "987654321"), BigDecimal("20.00"), PaymentType.CARD, LocalDateTime.now())
        )
        `when`(transactionRepository.findAll()).thenReturn(transactions)

        val result = transactionService.getAllTransactions()

        assertEquals(transactions, result)
    }

    @Test
    fun `test get transaction by id found`() {
        val transaction = Transaction(1, Seller(1, "John Doe", "123456789"), BigDecimal("10.00"), PaymentType.CASH, LocalDateTime.now())
        `when`(transactionRepository.findById(1)).thenReturn(Optional.of(transaction))

        val result = transactionService.getTransactionById(1)

        assertEquals(transaction, result)
    }

    @Test
    fun `test get transaction by id not found`() {
        `when`(transactionRepository.findById(1)).thenReturn(Optional.empty())

        val result = transactionService.getTransactionById(1)

        assertNull(result)
    }

    @Test
    fun `test create transaction`() {
        val seller = Seller(1, "John Doe", "123456789")
        val amount = BigDecimal("10.00")
        val paymentType = PaymentType.CASH
        val newTransaction = Transaction(seller = seller, amount = amount, paymentType = paymentType)
        `when`(transactionRepository.save(any())).thenReturn(newTransaction)

        val result = transactionService.createTransaction(seller, amount, paymentType)

        assertEquals(newTransaction, result)
    }

    @Test
    fun `test get all transactions of seller`() {
        val seller = Seller(1, "John Doe", "123456789")
        val transactions = listOf(
            Transaction(1, seller, BigDecimal("10.00"), PaymentType.CASH, LocalDateTime.now()),
            Transaction(2, seller, BigDecimal("20.00"), PaymentType.CARD, LocalDateTime.now())
        )
        `when`(transactionRepository.findBySeller(seller)).thenReturn(transactions)

        val result = transactionService.getAllTransactionsOfSeller(seller)

        assertEquals(transactions, result)
    }

    @Test
    fun `test get best seller by date`() {
        val startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)

        val sellerFirst = Seller(1L, "John Doe", "123456789")
        val sellerSecond = Seller(2L, "Jane Smith", "987654321")
        val transactions = listOf(
            Transaction(1L, sellerFirst, BigDecimal("10.00"), PaymentType.CASH),
            Transaction(2L, sellerFirst, BigDecimal("20.00"), PaymentType.CARD),
            Transaction(3L, sellerSecond, BigDecimal("5.00"), PaymentType.CASH)
        )

        `when`(transactionRepository.findAllByTransactionDateBetween(startDate, endDate)).thenReturn(transactions)

        val result = transactionService.getBestSellerByDate(startDate, endDate)

        assertEquals(sellerFirst, result)
    }

    @Test
    fun `test get best seller by date no transactions`() {
        val startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        `when`(transactionRepository.findAllByTransactionDateBetween(startDate, endDate)).thenReturn(emptyList())

        val result = transactionService.getBestSellerByDate(startDate, endDate)

        assertNull(result)
    }

    @Test
    fun `test get sellers with less amount of transaction per period`() {
        val startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        val amountThreshold = BigDecimal("30.00")

        val sellerFirst = Seller(1L, "John Doe", "123456789")
        val sellerSecond = Seller(2L, "Jane Smith", "987654321")
        val transactions = listOf(
            Transaction(1, sellerFirst, BigDecimal("10.00"), PaymentType.CASH, LocalDateTime.now()),
            Transaction(2, sellerFirst, BigDecimal("20.00"), PaymentType.CARD, LocalDateTime.now()),
            Transaction(3, sellerSecond, BigDecimal("5.00"), PaymentType.CASH, LocalDateTime.now())
        )
        `when`(transactionRepository.findAllByTransactionDateBetween(startDate, endDate)).thenReturn(transactions)

        val result = transactionService.getSellersWithLessAmountOfTransactionPerPeriod(startDate, endDate, amountThreshold)

        assertEquals(listOf(sellerSecond), result)
    }

    @Test
    fun `test get sellers with less amount of transaction per period no transactions`() {
        val startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        val amountThreshold = BigDecimal("30.00")
        `when`(transactionRepository.findAllByTransactionDateBetween(startDate, endDate)).thenReturn(emptyList())

        val result = transactionService.getSellersWithLessAmountOfTransactionPerPeriod(startDate, endDate, amountThreshold)

        assertEquals(emptyList<Seller>(), result)
    }

    @Test
    fun `test getBestTransactionPeriodForSeller returns correct period`() {
        val seller = Seller(1, "John Doe", "123456789")
        val transactions = listOf(
            Transaction(1, seller, BigDecimal("10.00"), PaymentType.CASH, LocalDateTime.now().minusDays(2)),
            Transaction(2, seller, BigDecimal("20.00"), PaymentType.CARD, LocalDateTime.now().minusDays(1)),
            Transaction(3, seller, BigDecimal("15.00"), PaymentType.CASH, LocalDateTime.now())
        )

        `when`(transactionRepository.findBySeller(seller)).thenReturn(transactions)

        val result = transactionService.getBestTransactionPeriodForSeller(seller)

        assertNotNull(result)
        assertEquals(LocalDateTime.now().minusDays(2).truncatedTo(ChronoUnit.DAYS), result?.first)
        assertEquals(LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS), result?.second)
    }

    @Test
    fun `test getBestTransactionPeriodForSeller returns null when no transactions exist`() {
        val seller = Seller(1, "John Doe", "123456789")

        `when`(transactionRepository.findBySeller(seller)).thenReturn(emptyList())

        val result = transactionService.getBestTransactionPeriodForSeller(seller)

        assertNull(result)
    }
}