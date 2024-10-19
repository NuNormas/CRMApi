package com.example.demo

import org.mockito.Mock
import java.util.Optional
import org.mockito.Mockito.any
import org.mockito.InjectMocks
import org.mockito.Mockito.`when`
import org.junit.jupiter.api.Test
import com.example.demo.Entities.Seller
import org.junit.jupiter.api.Assertions.*
import com.example.demo.Services.SellerService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import com.example.demo.Repositories.SellerRepository

@ExtendWith(MockitoExtension::class)
class SellerServiceTest {

    @Mock
    private lateinit var sellerRepository: SellerRepository

    @InjectMocks
    private lateinit var sellerService: SellerService

    @Test
    fun `test get all sellers`() {
        val sellers = listOf(
            Seller(1, "John Doe", "123456789"),
            Seller(2, "Jane Smith", "987654321")
        )
        `when`(sellerRepository.findAll()).thenReturn(sellers)

        val result = sellerService.getAllSellers()

        assertEquals(sellers, result)
    }

    @Test
    fun `test get seller by id found`() {
        val seller = Seller(1, "John Doe", "123456789")
        `when`(sellerRepository.findById(1)).thenReturn(Optional.of(seller))
        
        val result = sellerService.getSellerById(1)

        assertEquals(seller, result)
    }

    @Test
    fun `test get seller by id not found`() {
        `when`(sellerRepository.findById(1)).thenReturn(Optional.empty())

        val result = sellerService.getSellerById(1)

        assertNull(result)
    }

    @Test
    fun `test create seller`() {
        val name = "John Doe"
        val contactInfo = "123456789"
        val newSeller = Seller(name = name, contactInfo = contactInfo)

        `when`(sellerRepository.save(any())).thenReturn(newSeller)

        val result = sellerService.createSeller(name, contactInfo)

        assertEquals(newSeller, result)
    }

    @Test
    fun `test update seller found`() {
        val id = 1L
        val name = "Jane Smith"
        val contactInfo = "987654321"
        val existingSeller = Seller(id, "John Doe", "123456789")
        val updatedSeller = existingSeller.copy(name = name, contactInfo = contactInfo)
        `when`(sellerRepository.findById(id)).thenReturn(Optional.of(existingSeller))
        `when`(sellerRepository.save(updatedSeller)).thenReturn(updatedSeller)

        val result = sellerService.updateSeller(id, name, contactInfo)

        assertEquals(updatedSeller, result)
    }

    @Test
    fun `test update seller not found`() {
        val id = 1L
        val name = "Jane Smith"
        val contactInfo = "987654321"
        `when`(sellerRepository.findById(id)).thenReturn(Optional.empty())
        
        val result = sellerService.updateSeller(id, name, contactInfo)

        assertNull(result)
    }

    @Test
    fun `test delete seller exists`() {
        val id = 1L
        `when`(sellerRepository.existsById(id)).thenReturn(true)

        val result = sellerService.deleteSeller(id)

        assertTrue(result)
    }

    @Test
    fun `test delete seller does not exist`() {
        val id = 1L
        `when`(sellerRepository.existsById(id)).thenReturn(false)

        val result = sellerService.deleteSeller(id)

        assertFalse(result)
    }
}