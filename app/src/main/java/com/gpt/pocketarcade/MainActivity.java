package com.gpt.pocketarcade;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.gpt.pocketarcade.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ActivityMainBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        EdgeToEdge.enable(this);

        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        Janela.ajustarBordas(bind.getRoot());

        bind.btnEntrar.setOnClickListener(v -> entrar());
    }

    private void entrar() {
        String apelido = bind.editApelido.getText().toString().trim();

        if (apelido.length() < 3) {
            bind.editApelido.setError(getString(R.string.erro_apelido_curto));
            bind.editApelido.startAnimation(AnimationUtils.loadAnimation(this, R.anim.tremer));
            return;
        }

        bind.btnEntrar.setEnabled(false);
        bind.btnEntrar.animate()
                .scaleX(1.08f)
                .scaleY(1.08f)
                .alpha(0.8f)
                .setDuration(350)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bind.btnEntrar.animate().setListener(null);
                        bind.btnEntrar.setScaleX(1f);
                        bind.btnEntrar.setScaleY(1f);
                        bind.btnEntrar.setAlpha(1f);
                        bind.btnEntrar.setEnabled(true);
                        abrirCentral(apelido);
                    }
                })
                .start();
    }

    private void abrirCentral(String apelido) {
        Intent intent = new Intent(this, CentralActivity.class);
        intent.putExtra(Extras.APELIDO, apelido);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        setIntent(intent);

        String modulo = intent.getStringExtra(Extras.ULTIMO_MODULO);
        if (modulo != null && !modulo.trim().isEmpty()) {
            bind.textoUltimoModulo.setText(getString(R.string.ultimo_modulo_formato, modulo));
        }
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
