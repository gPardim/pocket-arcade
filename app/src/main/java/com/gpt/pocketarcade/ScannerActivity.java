package com.gpt.pocketarcade;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.gpt.pocketarcade.databinding.ActivityScannerBinding;

import java.text.Normalizer;
import java.util.Locale;

public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "ScannerActivity";

    ActivityScannerBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        String apelido = getIntent().getStringExtra(Extras.APELIDO);
        if (!Validacao.apelidoValido(apelido)) {
            Toast.makeText(this, R.string.erro_dados_invalidos, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        bind = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        Janela.ajustarBordas(bind.getRoot());

        bind.textoApelidoScanner.setText(getString(R.string.jogador_formato, apelido.trim()));

        bind.btnAnalisar.setOnClickListener(v -> analisar());
        bind.btnLimpar.setOnClickListener(v -> limpar());
        bind.btnVoltarScanner.setOnClickListener(v -> finish());
        bind.btnEncerrarScanner.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_scanner)));
    }

    private void analisar() {
        String texto = bind.editMensagem.getText().toString().trim();

        if (texto.isEmpty()) {
            bind.editMensagem.setError(getString(R.string.erro_mensagem_vazia));
            return;
        }
        if (texto.length() < 3) {
            bind.editMensagem.setError(getString(R.string.erro_mensagem_curta));
            return;
        }

        String semEspacos = texto.replaceAll("\\s+", "");
        int palavras = texto.split("\\s+").length;
        int vogais = contarVogais(texto);
        String invertido = new StringBuilder(texto).reverse().toString();

        String normalizado = semEspacos.toLowerCase(Locale.ROOT);
        String normalizadoInvertido = new StringBuilder(normalizado).reverse().toString();
        boolean palindromo = normalizado.equals(normalizadoInvertido);

        bind.valorCaracteres.setText(String.valueOf(semEspacos.length()));
        bind.valorPalavras.setText(String.valueOf(palavras));
        bind.valorVogais.setText(String.valueOf(vogais));
        bind.valorInvertido.setText(invertido);
        bind.valorPalindromo.setText(palindromo ? R.string.resposta_sim : R.string.resposta_nao);

        mostrarPainel();
    }

    private int contarVogais(String texto) {
        String base = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        int total = 0;
        for (int i = 0; i < base.length(); i++) {
            if ("aeiou".indexOf(base.charAt(i)) >= 0) {
                total++;
            }
        }
        return total;
    }

    private void mostrarPainel() {
        bind.painelResultado.setVisibility(View.VISIBLE);
        bind.painelResultado.setAlpha(0f);
        bind.painelResultado.setTranslationY(40f);
        bind.painelResultado.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(450)
                .start();
    }

    private void limpar() {
        bind.editMensagem.setText("");
        bind.editMensagem.setError(null);
        bind.painelResultado.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
