package com.ecovivashop.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductoBulkUploadResult {
    private int productosProcesados = 0;
    private List<String> errores = new ArrayList<>();
    private List<String> advertencias = new ArrayList<>();
    private List<String> categoriasCreadas = new ArrayList<>();
    
    public ProductoBulkUploadResult() {}
    
    public int getProductosProcesados() {
        return productosProcesados;
    }
    
    public void setProductosProcesados(int productosProcesados) {
        this.productosProcesados = productosProcesados;
    }
    
    public void incrementarProductosProcesados() {
        this.productosProcesados++;
    }
    
    public List<String> getErrores() {
        return errores;
    }
    
    public void setErrores(List<String> errores) {
        this.errores = errores;
    }
    
    public void agregarError(String error) {
        this.errores.add(error);
    }
    
    public void agregarError(int fila, String error) {
        this.errores.add("Fila " + fila + ": " + error);
    }
    
    public List<String> getAdvertencias() {
        return advertencias;
    }
    
    public void setAdvertencias(List<String> advertencias) {
        this.advertencias = advertencias;
    }
    
    public void agregarAdvertencia(String advertencia) {
        this.advertencias.add(advertencia);
    }
    
    public void agregarAdvertencia(int fila, String advertencia) {
        this.advertencias.add("Fila " + fila + ": " + advertencia);
    }
    
    public List<String> getCategoriasCreadas() {
        return categoriasCreadas;
    }
    
    public void setCategoriasCreadas(List<String> categoriasCreadas) {
        this.categoriasCreadas = categoriasCreadas;
    }
    
    public void agregarCategoriaCreada(String categoria) {
        if (!this.categoriasCreadas.contains(categoria)) {
            this.categoriasCreadas.add(categoria);
        }
    }
    
    public boolean tieneErrores() {
        return !this.errores.isEmpty();
    }
    
    public boolean tieneAdvertencias() {
        return !this.advertencias.isEmpty();
    }
    
    public boolean esExitoso() {
        return this.productosProcesados > 0 && this.errores.isEmpty();
    }
    
    public String getResumenTexto() {
        StringBuilder resumen = new StringBuilder();
        resumen.append(String.format("Productos procesados: %d", productosProcesados));
        
        if (!errores.isEmpty()) {
            resumen.append(String.format(", Errores: %d", errores.size()));
        }
        
        if (!advertencias.isEmpty()) {
            resumen.append(String.format(", Advertencias: %d", advertencias.size()));
        }
        
        if (!categoriasCreadas.isEmpty()) {
            resumen.append(String.format(", Categorías creadas: %d", categoriasCreadas.size()));
        }
        
        return resumen.toString();
    }
}
