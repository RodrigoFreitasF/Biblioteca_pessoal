package com.example.bibliotecapessoal;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    private final OnItemClickListener mListener;
    private final GestureDetector mGestureDetector;

    // Contrato que a Activity implementa para receber os cliques
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onLongItemClick(View view, int position);
    }

    // Prepara o detector de gestos para toque simples e toque longo
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView,
                                     OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            // Confirma que o gesto foi um toque simples
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                return true;
            }

            // Descobre o item pressionado e avisa o toque longo
            @Override
            public void onLongPress(@NonNull MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    int pos = recyclerView.getChildAdapterPosition(child);
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener.onLongItemClick(child, pos);
                    }
                }
            }
        });
    }

    // Intercepta o toque e avisa a Activity quando for um clique simples
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            int pos = rv.getChildAdapterPosition(childView);
            if (pos != RecyclerView.NO_POSITION) {
                mListener.onItemClick(childView, pos);
            }
            return true;
        }
        return false;
    }

    // Nao usado: o toque ja e tratado em onInterceptTouchEvent
    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
    }

    // Nao usado
    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
