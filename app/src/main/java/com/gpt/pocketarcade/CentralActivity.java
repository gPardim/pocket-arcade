package com.gpt.pocketarcade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.gpt.pocketarcade.databinding.ActivityCentralBinding;

public class CentralActivity extends AppCompatActivity {

    private static final String TAG = "CentralActivity";

    ActivityCentralBinding bind;
    String apelido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        apelido = getIntent().getStringExtra(Extras.APELIDO);
        if (!Validacao.apelidoValido(apelido)) {
            Toast.makeText(this, R.string.erro_dados_invalidos, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        apelido = apelido.trim();

        EdgeToEdge.enable(this);
        bind = ActivityCentralBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        Janela.ajustarBordas(bind.getRoot());

        bind.textoSaudacao.setText(getString(R.string.saudacao_formato, apelido));
        animarPaineis();

        bind.btnAbrirScanner.setOnClickListener(v -> abrir(ScannerActivity.class));
        bind.btnAbrirArena.setOnClickListener(v -> abrir(ArenaActivity.class));
        bind.btnAbrirCodigo.setOnClickListener(v -> abrir(CodigoSecretoActivity.class));
        bind.btnAbrirRandomizador.setOnClickListener(v -> abrir(RandomizadorActivity.class));
        bind.btnVoltarApresentacao.setOnClickListener(v -> finish());
    }

    private void animarPaineis() {
        View[] paineis = {
                bind.painelScanner,
                bind.painelArena,
                bind.painelCodigo,
                bind.painelRandomizador
        };
        for (int i = 0; i < paineis.length; i++) {
            Animation entrada = AnimationUtils.loadAnimation(this, R.anim.entrada_painel);
            entrada.setStartOffset(i * 120L);
            paineis[i].startAnimation(entrada);
        }
    }

    private void abrir(Class<? extends Activity> destino) {
        Intent intent = new Intent(this, destino);
        intent.putExtra(Extras.APELIDO, apelido);
        startActivity(intent);
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
