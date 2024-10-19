package com.example.demo

import org.mockito.Mockito.*
import org.junit.jupiter.api.Test
import com.example.demo.Entities.Seller
import org.junit.jupiter.api.Assertions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import com.example.demo.Services.SellerService
import com.example.demo.Controllers.SellerController
import com.example.demo.Helpers.MissingFieldException

class SellerControllerTest {
    private val sellerService: SellerService = mock(SellerService::class.java)
    private val sellerController = SellerController(sellerService)

    @Test
    fun `should return all sellers`() {
        val sellers = listOf(Seller(1, "John Doe", "123456789"), Seller(2, "Jane Smith", "987654321"))
        `when`(sellerService.getAllSellers()).thenReturn(sellers)

        val result = sellerController.getAllSellers()

        assertEquals(sellers, result)
    }

    @Test
    fun `should return seller by id`() {
        val seller = Seller(1, "John Doe", "123456789")
        `when`(sellerService.getSellerById(1)).thenReturn(seller)

        val response: ResponseEntity<Seller> = sellerController.getSellerById(1)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(seller, response.body)
    }

    @Test
    fun `should create a new seller`() {
        val sellerData = mapOf("name" to "John Doe", "contactInfo" to "123456789")
        val newSeller = Seller(1, "John Doe", "123456789")
        `when`(sellerService.createSeller("John Doe", "123456789")).thenReturn(newSeller)

        val response: ResponseEntity<Seller> = sellerController.createSeller(sellerData)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(newSeller, response.body)
    }

    @Test
    fun `should throw MissingFieldException when creating a seller without name`() {
        val sellerData = mapOf("contactInfo" to "123456789")

        assertThrows(MissingFieldException::class.java) {
            sellerController.createSeller(sellerData)
        }
    }

    @Test
    fun `should update an existing seller`() {
        val sellerData = mapOf("name" to "Jane Smith", "contactInfo" to "987654321")
        val updatedSeller = Seller(1, "Jane Smith", "987654321")
        `when`(sellerService.updateSeller(1, "Jane Smith", "987654321")).thenReturn(updatedSeller)

        val response: ResponseEntity<Seller> = sellerController.updateSeller(1, sellerData)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedSeller, response.body)
    }


    @Test
    fun `should delete a seller by id`() {
        `when`(sellerService.deleteSeller(1)).thenReturn(true)

        val response: ResponseEntity<String> = sellerController.deleteSeller(1)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Seller has been deleted", response.body)
    }

    @Test
    fun `should throw NoSuchElementException when deleting non-existent seller`() {
        `when`(sellerService.deleteSeller(1)).thenReturn(false)

        assertThrows(NoSuchElementException::class.java) {
            sellerController.deleteSeller(1)
        }
    }
}