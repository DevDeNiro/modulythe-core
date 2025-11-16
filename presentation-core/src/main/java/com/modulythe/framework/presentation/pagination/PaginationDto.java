package com.modulythe.framework.presentation.pagination;

public class PaginationDto {
    private String size; // Nombre d'éléments par page
    private String page; // Numéro de la page
    private String sort; // Nom de la propriété de tri
    private String direction; // Direction du tri (asc ou desc)

    public PaginationDto() {
    }

    public PaginationDto(String size, String page, String sort, String direction) {
        this.size = size;
        this.page = page;
        this.sort = sort;
        this.direction = direction;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}