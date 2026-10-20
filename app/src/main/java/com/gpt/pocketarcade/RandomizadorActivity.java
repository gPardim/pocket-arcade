package com.gpt.pocketarcade;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gpt.pocketarcade.databinding.ActivityRandomizadorBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizadorActivity extends AppCompatActivity {

    private static final String TAG = "RandomizadorActivity";

    ActivityRandomizadorBinding bind;
    MediaPlayer playerSorteio;
    ItemSorteioAdapter adapter;

    final List<ItemSorteio> itens = new ArrayList<>();
    final Random random = new Random();
    boolean modoGrade = false;

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
        bind = ActivityRandomizadorBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        Janela.ajustarBordas(bind.getRoot());

        bind.textoApelidoRandomizador.setText(getString(R.string.jogador_formato, apelido.trim()));

        // MediaPlayer criado uma unica vez e reutilizado
        playerSorteio = MediaPlayer.create(this, R.raw.som_sorteio);

        configurarSpinner();
        carregarItensIniciais();

        adapter = new ItemSorteioAdapter(itens, this::removerItem);
        bind.recyclerItens.setLayoutManager(new LinearLayoutManager(this));
        bind.recyclerItens.setAdapter(adapter);

        bind.btnAdicionar.setOnClickListener(v -> adicionarItem());
        bind.btnSortear.setOnClickListener(v -> sortear());
        bind.btnAlternarLayout.setOnClickListener(v -> alternarLayout());
        bind.btnVoltarRandomizador.setOnClickListener(v -> finish());
        bind.btnEncerrarRandomizador.setOnClickListener(v ->
                Navegacao.encerrarSessao(this, getString(R.string.modulo_randomizador)));
    }

    private void configurarSpinner() {
        String[] categorias = getResources().getStringArray(R.array.categorias_randomizador);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categorias);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        bind.spinnerCategoria.setAdapter(spinnerAdapter);
    }

    private void carregarItensIniciais() {
        String filmes = getString(R.string.categoria_filmes);
        String comidas = getString(R.string.categoria_comidas);
        String lugares = getString(R.string.categoria_lugares);
        String desafios = getString(R.string.categoria_desafios);

        itens.add(new ItemSorteio(getString(R.string.item_filme_1), filmes));
        itens.add(new ItemSorteio(getString(R.string.item_filme_2), filmes));
        itens.add(new ItemSorteio(getString(R.string.item_comida_1), comidas));
        itens.add(new ItemSorteio(getString(R.string.item_comida_2), comidas));
        itens.add(new ItemSorteio(getString(R.string.item_lugar_1), lugares));
        itens.add(new ItemSorteio(getString(R.string.item_lugar_2), lugares));
        itens.add(new ItemSorteio(getString(R.string.item_desafio_1), desafios));
        itens.add(new ItemSorteio(getString(R.string.item_desafio_2), desafios));
    }

    private void adicionarItem() {
        String valor = bind.editValor.getText().toString().trim();
        int posicaoCategoria = bind.spinnerCategoria.getSelectedItemPosition();

        if (valor.length() < 3) {
            bind.editValor.setError(getString(R.string.erro_valor_curto));
            return;
        }
        if (posicaoCategoria == 0) {
            Toast.makeText(this, R.string.erro_categoria, Toast.LENGTH_SHORT).show();
            return;
        }

        String categoria = bind.spinnerCategoria.getSelectedItem().toString();
        itens.add(new ItemSorteio(valor, categoria));
        adapter.notifyItemInserted(itens.size() - 1);
        bind.recyclerItens.scrollToPosition(itens.size() - 1);

        bind.editValor.setText("");
        Toast.makeText(this, R.string.msg_item_adicionado, Toast.LENGTH_SHORT).show();
    }

    private void removerItem(ItemSorteio item) {
        int posicao = itens.indexOf(item);
        if (posicao != -1) {
            itens.remove(posicao);
            adapter.esquecerDestaqueSe(item);
            adapter.notifyItemRemoved(posicao);
            Toast.makeText(this, getString(R.string.msg_item_removido, item.getPalavra()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sortear() {
        if (bind.spinnerCategoria.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.erro_categoria, Toast.LENGTH_SHORT).show();
            return;
        }

        String categoria = bind.spinnerCategoria.getSelectedItem().toString();
        List<ItemSorteio> candidatos = new ArrayList<>();
        for (ItemSorteio item : itens) {
            if (item.getCategoria().equals(categoria)) {
                candidatos.add(item);
            }
        }

        if (candidatos.isEmpty()) {
            Toast.makeText(this, R.string.sem_itens_categoria, Toast.LENGTH_SHORT).show();
            return;
        }

        ItemSorteio escolhido = candidatos.get(random.nextInt(candidatos.size()));

        bind.textoSorteado.setText(getString(R.string.sorteado_formato,
                escolhido.getPalavra(), escolhido.getCategoria()));
        bind.textoSorteado.setScaleX(0.6f);
        bind.textoSorteado.setScaleY(0.6f);
        bind.textoSorteado.setAlpha(0f);
        bind.textoSorteado.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator())
                .start();

        tocar(playerSorteio);
        adapter.destacar(escolhido);
        bind.recyclerItens.scrollToPosition(itens.indexOf(escolhido));
    }

    private void alternarLayout() {
        modoGrade = !modoGrade;
        if (modoGrade) {
            bind.recyclerItens.setLayoutManager(new GridLayoutManager(this, 2));
            bind.btnAlternarLayout.setText(R.string.btn_ver_lista);
        } else {
            bind.recyclerItens.setLayoutManager(new LinearLayoutManager(this));
            bind.btnAlternarLayout.setText(R.string.btn_ver_grade);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerSorteio != null && playerSorteio.isPlaying()) {
            playerSorteio.pause();
        }
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
        if (playerSorteio != null) {
            playerSorteio.release();
            playerSorteio = null;
        }
    }
}
