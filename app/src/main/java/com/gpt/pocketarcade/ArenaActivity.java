package com.gpt.pocketarcade;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.gpt.pocketarcade.databinding.ActivityArenaBinding;

import java.util.Random;

public class ArenaActivity extends AppCompatActivity {

    private static final String TAG = "ArenaActivity";
    private static final int VITORIAS_PARA_VENCER = 2;

    ActivityArenaBinding bind;
    MediaPlayer playerVitoria;
    MediaPlayer playerDerrota;
    MediaPlayer playerEmpate;

    final Random random = new Random();
    final int[] icones = {R.drawable.ic_pedra, R.drawable.ic_papel, R.drawable.ic_tesoura};
    String[] nomesJogadas;

    int placarJogador = 0;
    int placarDispositivo = 0;
    boolean partidaEncerrada = false;

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
        bind = ActivityArenaBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        Janela.ajustarBordas(bind.getRoot());

        nomesJogadas = getResources().getStringArray(R.array.jogadas);

        // MediaPlayers criados uma unica vez e reutilizados
        playerVitoria = MediaPlayer.create(this, R.raw.som_vitoria);
        playerDerrota = MediaPlayer.create(this, R.raw.som_derrota);
        playerEmpate = MediaPlayer.create(this, R.raw.som_empate);

        bind.textoApelidoArena.setText(getString(R.string.jogador_formato, apelido.trim()));
        atualizarPlacar();

        bind.btnPedra.setOnClickListener(v -> jogar(0));
        bind.btnPapel.setOnClickListener(v -> jogar(1));
        bind.btnTesoura.setOnClickListener(v -> jogar(2));
        bind.btnNovaPartidaArena.setOnClickListener(v -> novaPartida());
        bind.btnVoltarArena.setOnClickListener(v -> finish());
        bind.btnEncerrarArena.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_arena)));
    }

    private void jogar(int escolhaJogador) {
        if (partidaEncerrada) {
            return;
        }

        int escolhaDispositivo = random.nextInt(3);
        mostrarEscolha(bind.imagemEscolhaJogador, escolhaJogador, R.string.desc_escolha_jogador);
        mostrarEscolha(bind.imagemEscolhaDispositivo, escolhaDispositivo, R.string.desc_escolha_dispositivo);

        int resultado = (escolhaJogador - escolhaDispositivo + 3) % 3;
        int mensagem;
        int cor;
        MediaPlayer som;

        if (resultado == 0) {
            mensagem = R.string.rodada_empate;
            cor = R.color.resultado_neutro;
            som = playerEmpate;
        } else if (resultado == 1) {
            placarJogador++;
            mensagem = R.string.rodada_vitoria;
            cor = R.color.resultado_vitoria;
            som = playerVitoria;
        } else {
            placarDispositivo++;
            mensagem = R.string.rodada_derrota;
            cor = R.color.resultado_derrota;
            som = playerDerrota;
        }

        bind.textoMensagemRodada.setText(mensagem);
        bind.textoMensagemRodada.setTextColor(ContextCompat.getColor(this, cor));
        animarTexto(bind.textoMensagemRodada);
        tocar(som);
        atualizarPlacar();

        if (placarJogador == VITORIAS_PARA_VENCER || placarDispositivo == VITORIAS_PARA_VENCER) {
            encerrarPartida();
        }
    }

    private void mostrarEscolha(ImageView imagem, int escolha, int descricaoRes) {
        imagem.setImageResource(icones[escolha]);
        imagem.setContentDescription(getString(descricaoRes, nomesJogadas[escolha]));
        imagem.setVisibility(View.VISIBLE);
        imagem.setScaleX(0.3f);
        imagem.setScaleY(0.3f);
        imagem.setAlpha(0f);
        imagem.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void animarTexto(View texto) {
        texto.setAlpha(0f);
        texto.setTranslationY(30f);
        texto.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(450)
                .start();
    }

    private void encerrarPartida() {
        partidaEncerrada = true;
        boolean venceu = placarJogador > placarDispositivo;

        bind.textoResultadoFinal.setText(venceu ? R.string.partida_vencida : R.string.partida_perdida);
        bind.textoResultadoFinal.setTextColor(ContextCompat.getColor(this,
                venceu ? R.color.resultado_vitoria : R.color.resultado_derrota));
        bind.textoResultadoFinal.setVisibility(View.VISIBLE);
        bind.textoResultadoFinal.setScaleX(0.6f);
        bind.textoResultadoFinal.setScaleY(0.6f);
        bind.textoResultadoFinal.setAlpha(0f);
        bind.textoResultadoFinal.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator())
                .start();

        definirJogadasAtivas(false);
    }

    private void novaPartida() {
        placarJogador = 0;
        placarDispositivo = 0;
        partidaEncerrada = false;

        bind.imagemEscolhaJogador.setVisibility(View.INVISIBLE);
        bind.imagemEscolhaDispositivo.setVisibility(View.INVISIBLE);
        bind.textoMensagemRodada.setText(R.string.escolha_jogada);
        bind.textoMensagemRodada.setTextColor(ContextCompat.getColor(this, R.color.texto_principal));
        bind.textoResultadoFinal.setVisibility(View.GONE);

        atualizarPlacar();
        definirJogadasAtivas(true);
    }

    private void definirJogadasAtivas(boolean ativas) {
        ImageButton[] botoes = {bind.btnPedra, bind.btnPapel, bind.btnTesoura};
        for (ImageButton botao : botoes) {
            botao.setEnabled(ativas);
            botao.setAlpha(ativas ? 1f : 0.5f);
        }
    }

    private void atualizarPlacar() {
        bind.textoPlacar.setText(getString(R.string.placar_formato, placarJogador, placarDispositivo));
    }

    private void tocar(MediaPlayer player) {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            player.pause();
        }
        player.seekTo(0);
        player.start();
    }

    private void pausar(MediaPlayer player) {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    private MediaPlayer liberar(MediaPlayer player) {
        if (player != null) {
            player.release();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausar(playerVitoria);
        pausar(playerDerrota);
        pausar(playerEmpate);
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
        playerVitoria = liberar(playerVitoria);
        playerDerrota = liberar(playerDerrota);
        playerEmpate = liberar(playerEmpate);
    }
}
