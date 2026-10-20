package com.gpt.pocketarcade;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class Janela {

    private Janela() {}

    // Afasta o conteudo das barras do sistema e do teclado.
    public static void ajustarBordas(View raiz) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets bordas = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bordas.left, bordas.top, bordas.right, bordas.bottom);
            return insets;
        });
    }
}
