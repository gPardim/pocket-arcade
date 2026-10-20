package com.gpt.pocketarcade;

public class ItemSorteio {

    private final String palavra;
    private final String categoria;

    public ItemSorteio(String palavra, String categoria) {
        this.palavra = palavra;
        this.categoria = categoria;
    }

    public String getPalavra() { return palavra; }
    public String getCategoria() { return categoria; }
}
