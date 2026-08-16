package com.nadson.orderflow.modules.products.domain;

import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithGeneratedOrProvidedId() {
        Product productAutoId = new Product(null, "Burger", new BigDecimal("20.00"));
        assertNotNull(productAutoId.getId());
        assertEquals("Burger", productAutoId.getName());
        assertEquals(new BigDecimal("20.00"), productAutoId.getPrice());

        UUID customId = UUID.randomUUID();
        Product productCustomId = new Product(customId, "Soda", new BigDecimal("5.00"));
        assertEquals(customId, productCustomId.getId());
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product product = new Product(null, "Burger", new BigDecimal("20.00"));
        product.update("Double Burger", new BigDecimal("28.00"));

        assertEquals("Double Burger", product.getName());
        assertEquals(new BigDecimal("28.00"), product.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrUpdatingWithInvalidData() {
        assertThrows(BusinessRuleException.class, () -> new Product(null, null, new BigDecimal("20.00")));
        assertThrows(BusinessRuleException.class, () -> new Product(null, "   ", new BigDecimal("20.00")));
        assertThrows(BusinessRuleException.class, () -> new Product(null, "Burger", null));
        assertThrows(BusinessRuleException.class, () -> new Product(null, "Burger", BigDecimal.ZERO));
        assertThrows(BusinessRuleException.class, () -> new Product(null, "Burger", new BigDecimal("-5.00")));

        Product product = new Product(null, "Burger", new BigDecimal("20.00"));
        assertThrows(BusinessRuleException.class, () -> product.update("", new BigDecimal("20.00")));
        assertThrows(BusinessRuleException.class, () -> product.update("Burger", new BigDecimal("-1.00")));
    }
}