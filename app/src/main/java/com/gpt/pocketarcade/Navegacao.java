package com.gpt.pocketarcade;

import android.app.Activity;
import android.content.Intent;

public final class Navegacao {

    private Navegacao() {}

    // Volta para a Tela 1 removendo as telas intermediarias da pilha.
    public static void encerrarSessao(Activity origem, String nomeModulo) {
        Intent intent = new Intent(origem, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Extras.ULTIMO_MODULO, nomeModulo);
        origem.startActivity(intent);
    }
}
