package com.github.st1hy.countthemcalories.core.adapter.delegate;

public interface RecyclerViewNotifier {

    void notifyDataSetChanged();

    void notifyItemChanged(int position);

    void notifyItemChanged(int position, Object payload);

    void notifyItemRangeChanged(int positionStart, int itemCount);

    void notifyItemRangeChanged(int positionStart, int itemCount, Object payload);

    void notifyItemInserted(int position);

    void notifyItemMoved(int fromPosition, int toPosition);

    void notifyItemRangeInserted(int positionStart, int itemCount);

    void notifyItemRemoved(int position);

    void notifyItemRangeRemoved(int positionStart, int itemCount);
}
