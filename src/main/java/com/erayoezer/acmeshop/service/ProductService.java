package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.Product;
import com.erayoezer.acmeshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public Optional<Product> update(Long id, Product productDetails) {
        Optional<Product> optionalProduct = findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            save(product);
            return optionalProduct;
        } else {
            return Optional.empty();
        }
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Optional<Long> deleteById(Long id) {
        if (existsById(id)) {
            productRepository.deleteById(id);
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }
}
