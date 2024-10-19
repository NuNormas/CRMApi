package com.example.demo.Entities

import java.math.BigDecimal
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    val seller: Seller,

    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    val paymentType: PaymentType,

    @Column(name = "transaction_date")
    val transactionDate: LocalDateTime = LocalDateTime.now()
)

enum class PaymentType {
    CASH, CARD, TRANSFER;

    companion object {
        fun suitable(value: String): PaymentType? {
            return entries.find { it.name == value }
        }
    }
}
