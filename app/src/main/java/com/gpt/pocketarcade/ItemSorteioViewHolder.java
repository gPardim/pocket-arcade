package com.gpt.pocketarcade;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gpt.pocketarcade.databinding.ItemSorteioBinding;

public class ItemSorteioViewHolder extends RecyclerView.ViewHolder {

    private final ItemSorteioBinding binding;

    public ItemSorteioViewHolder(ItemSorteioBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(ItemSorteio item, boolean destacado,
                     ItemSorteioAdapter.OnItemLongClickListener longClickListener) {
        Context context = binding.getRoot().getContext();

        binding.textoPalavra.setText(item.getPalavra());
        binding.textoCategoriaItem.setText(item.getCategoria());

        // sempre redefine cor e borda, porque os cartoes sao reciclados
        binding.getRoot().setCardBackgroundColor(ContextCompat.getColor(context,
                destacado ? R.color.destaque_item : R.color.card_arcade));
        binding.getRoot().setStrokeColor(ContextCompat.getColor(context,
                destacado ? R.color.caramelo : R.color.borda_arcade));

        binding.getRoot().setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(item);
            return true;
        });
    }
}
