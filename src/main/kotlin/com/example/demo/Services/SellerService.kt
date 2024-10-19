package com.example.demo.Services

import java.time.LocalDateTime
import com.example.demo.Entities.Seller
import org.springframework.stereotype.Service
import com.example.demo.Repositories.SellerRepository

@Service
class SellerService(private val sellerRepository: SellerRepository) {

    fun getAllSellers(): List<Seller> = sellerRepository.findAll()

    fun getSellerById(id: Long): Seller? = sellerRepository.findById(id).orElse(null)

    fun createSeller(name: String, contactInfo: String): Seller {
        val newSeller = Seller(name = name, contactInfo = contactInfo)
        return sellerRepository.save(newSeller)
    }

    fun updateSeller(id: Long, name: String, contactInfo: String): Seller? {
        val seller = sellerRepository.findById(id).orElse(null) ?: return null
        val updatedSeller = seller.copy(name = name, contactInfo = contactInfo)
        return sellerRepository.save(updatedSeller)
    }

    fun deleteSeller(id: Long): Boolean {
        return if (sellerRepository.existsById(id)) {
            sellerRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}