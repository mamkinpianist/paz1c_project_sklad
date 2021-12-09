package storage.constructors;

import java.util.Objects;

public class Categories {
    private Long idCategories;
    private String Categoria;

    public Categories(Long idCategories, String categoria) {
        this.idCategories = idCategories;
        Categoria = categoria;
    }

    public Long getIdCategories() {
        return idCategories;
    }

    public void setIdCategories(Long idCategories) {
        this.idCategories = idCategories;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    @Override
    public String toString() {
        return "Categories{" +
                "idCategories=" + idCategories +
                ", Categoria='" + Categoria + '\'' +
                '}';
    }

    public Categories(String categoria) {
        Categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categories)) return false;
        Categories that = (Categories) o;
        return Objects.equals(idCategories, that.idCategories) && Categoria.equals(that.Categoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCategories, Categoria);
    }
}
