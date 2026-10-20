package com.gpt.pocketarcade;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.gpt.pocketarcade.databinding.ActivityCodigoSecretoBinding;
import com.gpt.pocketarcade.databinding.ItemTentativaBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodigoSecretoActivity extends AppCompatActivity {

    private static final String TAG = "CodigoSecretoActivity";
    private static final int TAMANHO_CODIGO = 4;
    private static final int MAX_TENTATIVAS = 8;

    ActivityCodigoSecretoBinding bind;

    final int[] codigo = new int[TAMANHO_CODIGO];
    int tentativas = 0;
    boolean partidaEncerrada = false;
    long inicio;

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
        bind = ActivityCodigoSecretoBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        Janela.ajustarBordas(bind.getRoot());

        bind.textoApelidoCodigo.setText(getString(R.string.jogador_formato, apelido.trim()));

        bind.btnTentar.setOnClickListener(v -> tentar());
        bind.btnNovaPartidaCodigo.setOnClickListener(v -> novaPartida());
        bind.btnVoltarCodigo.setOnClickListener(v -> finish());
        bind.btnEncerrarCodigo.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_codigo)));

        novaPartida();
    }

    private void novaPartida() {
        List<Integer> digitos = new ArrayList<>();
        for (int d = 0; d <= 9; d++) {
            digitos.add(d);
        }
        Collections.shuffle(digitos);
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            codigo[i] = digitos.get(i);
        }

        tentativas = 0;
        partidaEncerrada = false;
        inicio = System.currentTimeMillis();

        bind.containerHistorico.removeAllViews();
        bind.textoResultadoCodigo.setVisibility(View.GONE);
        bind.btnNovaPartidaCodigo.setVisibility(View.GONE);
        bind.editTentativa.setText("");
        bind.editTentativa.setError(null);
        bind.editTentativa.setEnabled(true);
        bind.btnTentar.setEnabled(true);
        atualizarContador();
    }

    private void tentar() {
        if (partidaEncerrada) {
            return;
        }

        String tentativa = bind.editTentativa.getText().toString().trim();

        if (tentativa.isEmpty()) {
            bind.editTentativa.setError(getString(R.string.erro_tentativa_vazia));
            return;
        }
        if (!tentativa.matches("\\d{4}")) {
            bind.editTentativa.setError(getString(R.string.erro_tentativa_tamanho));
            return;
        }
        if (temDigitoRepetido(tentativa)) {
            bind.editTentativa.setError(getString(R.string.erro_tentativa_repetida));
            return;
        }

        tentativas++;
        int certos = avaliar(tentativa);
        bind.editTentativa.setText("");
        atualizarContador();

        if (certos == TAMANHO_CODIGO) {
            encerrarPartida(true);
        } else if (tentativas >= MAX_TENTATIVAS) {
            encerrarPartida(false);
        }
    }

    // Adiciona a linha do historico e devolve quantos digitos estao na posicao certa.
    private int avaliar(String tentativa) {
        ItemTentativaBinding linha = ItemTentativaBinding.inflate(
                getLayoutInflater(), bind.containerHistorico, false);
        TextView[] caixas = {linha.digito1, linha.digito2, linha.digito3, linha.digito4};

        int certos = 0;
        int outraPosicao = 0;

        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            int digito = tentativa.charAt(i) - '0';
            caixas[i].setText(String.valueOf(digito));

            if (digito == codigo[i]) {
                caixas[i].setBackgroundResource(R.drawable.bg_digito_certo);
                certos++;
            } else if (existeNoCodigo(digito)) {
                caixas[i].setBackgroundResource(R.drawable.bg_digito_posicao);
                outraPosicao++;
            } else {
                caixas[i].setBackgroundResource(R.drawable.bg_digito_ausente);
            }
        }

        linha.textoTentativaLinha.setText(
                getString(R.string.linha_tentativa, tentativas, certos, outraPosicao));
        bind.containerHistorico.addView(linha.getRoot(), 0);
        return certos;
    }

    private boolean existeNoCodigo(int digito) {
        for (int valor : codigo) {
            if (valor == digito) {
                return true;
            }
        }
        return false;
    }

    private boolean temDigitoRepetido(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            for (int j = i + 1; j < texto.length(); j++) {
                if (texto.charAt(i) == texto.charAt(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void encerrarPartida(boolean venceu) {
        partidaEncerrada = true;
        long segundos = (System.currentTimeMillis() - inicio) / 1000;

        if (venceu) {
            bind.textoResultadoCodigo.setText(
                    getString(R.string.codigo_vitoria, tentativas, segundos));
            bind.textoResultadoCodigo.setTextColor(getColor(R.color.resultado_vitoria));
        } else {
            bind.textoResultadoCodigo.setText(
                    getString(R.string.codigo_derrota, codigoComoTexto(), segundos));
            bind.textoResultadoCodigo.setTextColor(getColor(R.color.resultado_derrota));
        }

        bind.textoResultadoCodigo.setVisibility(View.VISIBLE);
        if (venceu) {
            bind.textoResultadoCodigo.setScaleX(0.6f);
            bind.textoResultadoCodigo.setScaleY(0.6f);
            bind.textoResultadoCodigo.setAlpha(0f);
            bind.textoResultadoCodigo.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(600)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        } else {
            bind.textoResultadoCodigo.setAlpha(1f);
            bind.textoResultadoCodigo.setScaleX(1f);
            bind.textoResultadoCodigo.setScaleY(1f);
            bind.textoResultadoCodigo.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.tremer));
        }

        bind.editTentativa.setEnabled(false);
        bind.btnTentar.setEnabled(false);
        bind.btnNovaPartidaCodigo.setVisibility(View.VISIBLE);
    }

    private String codigoComoTexto() {
        StringBuilder texto = new StringBuilder();
        for (int digito : codigo) {
            texto.append(digito);
        }
        return texto.toString();
    }

    private void atualizarContador() {
        int atual = Math.min(tentativas + 1, MAX_TENTATIVAS);
        bind.textoTentativas.setText(getString(R.string.tentativas_formato, atual, MAX_TENTATIVAS));
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
