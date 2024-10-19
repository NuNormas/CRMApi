package com.example.demo.Repositories

import com.example.demo.Entities.Seller
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository

@Repository
interface SellerRepository : JpaRepository<Seller, Long>