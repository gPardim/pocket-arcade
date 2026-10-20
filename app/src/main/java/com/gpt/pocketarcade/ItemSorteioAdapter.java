package com.gpt.pocketarcade;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gpt.pocketarcade.databinding.ItemSorteioBinding;

import java.util.List;

public class ItemSorteioAdapter extends RecyclerView.Adapter<ItemSorteioViewHolder> {

    public interface OnItemLongClickListener {
        void onItemLongClick(ItemSorteio item);
    }

    private final List<ItemSorteio> itens;
    private final OnItemLongClickListener longClickListener;
    private ItemSorteio destacado;

    public ItemSorteioAdapter(List<ItemSorteio> itens, OnItemLongClickListener longClickListener) {
        this.itens = itens;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ItemSorteioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSorteioBinding binding = ItemSorteioBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemSorteioViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemSorteioViewHolder holder, int position) {
        ItemSorteio item = itens.get(position);
        holder.bind(item, item == destacado, longClickListener);
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    // Destaca o item sorteado atualizando somente os cartoes envolvidos.
    public void destacar(ItemSorteio novo) {
        int anterior = destacado == null ? -1 : itens.indexOf(destacado);
        destacado = novo;
        if (anterior != -1) {
            notifyItemChanged(anterior);
        }
        int atual = itens.indexOf(novo);
        if (atual != -1) {
            notifyItemChanged(atual);
        }
    }

    public void esquecerDestaqueSe(ItemSorteio item) {
        if (destacado == item) {
            destacado = null;
        }
    }
}
