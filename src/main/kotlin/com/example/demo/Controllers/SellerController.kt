package com.example.demo.Controllers

import com.example.demo.Entities.Seller
import com.example.demo.Helpers.MissingFieldException
import org.springframework.http.HttpStatus
import com.example.demo.Services.SellerService
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/sellers")
class SellerController(private val sellerService: SellerService) {

    @GetMapping
    fun getAllSellers(): List<Seller> = sellerService.getAllSellers()

    @GetMapping("/{id}")
    fun getSellerById(@PathVariable id: Long): ResponseEntity<Seller> {
        val seller =
            sellerService.getSellerById(id) ?: throw NoSuchElementException("Seller with id $id")
        return ResponseEntity.ok(seller)
    }

    @PostMapping
    fun createSeller(@RequestBody sellerData: Map<String, String>): ResponseEntity<Seller> {
        val name = sellerData["name"] ?: throw MissingFieldException("name")
        val contactInfo = sellerData["contactInfo"] ?: throw MissingFieldException("contactInfo")

        if (name.all { it.isDigit() }) {
            throw IllegalArgumentException("name cannot be a number")
        }

        val newSeller = sellerService.createSeller(name, contactInfo)
        return ResponseEntity.status(HttpStatus.CREATED).body(newSeller)
    }

    @PutMapping("/{id}")
    fun updateSeller(
        @PathVariable id: Long,
        @RequestBody sellerData: Map<String, String>
    ): ResponseEntity<Seller> {
        val name = sellerData["name"] ?: throw MissingFieldException("name")
        val contactInfo = sellerData["contactInfo"] ?: throw MissingFieldException("contactInfo")
        val updatedSeller = sellerService.updateSeller(id, name, contactInfo) ?: throw NoSuchElementException("seller with id $id")

        return ResponseEntity.ok(updatedSeller)
    }

    @DeleteMapping("/{id}")
    fun deleteSeller(@PathVariable id: Long): ResponseEntity<String> {
        val ifDeleted = sellerService.deleteSeller(id)
        return if (ifDeleted) {
            ResponseEntity.ok("Seller has been deleted")
        } else {
            throw NoSuchElementException("seller with id $id")
        }
    }
}