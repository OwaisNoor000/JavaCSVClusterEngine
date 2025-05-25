package com.owais.kmeans.model;

public class Product {
    private String prodName;
    private String productTypeName;
    private String colourName;

    public Product(String prodName, String productTypeName, String colourName) {
        this.prodName = prodName;
        this.productTypeName = productTypeName;
        this.colourName = colourName;
    }

    // Getters and Setters
    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getColourName() {
        return colourName;
    }

    public void setColourName(String colourName) {
        this.colourName = colourName;
    }
}