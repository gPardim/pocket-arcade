package com.gpt.pocketarcade;

public final class Validacao {

    private Validacao() {}

    public static boolean apelidoValido(String apelido) {
        return apelido != null && apelido.trim().length() >= 3;
    }
}
