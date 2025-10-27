package com.pluralsight.ecommerce.model;

import jakarta.persistence.*;

import javax.validation.constraints.NotBlank;

@Entity
@Table(name="categories")
public class Category
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="category_name")
    private @NotBlank String categoryName;
    private @NotBlank String description;
    private @NotBlank String imageUrl;

    public Category () {}

    public Category(@NotBlank String categoryName, @NotBlank String description, @NotBlank String imageUrl) {
        this.categoryName = categoryName;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
