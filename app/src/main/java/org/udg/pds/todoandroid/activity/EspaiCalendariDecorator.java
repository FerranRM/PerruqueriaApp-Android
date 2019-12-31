package org.udg.pds.todoandroid.activity;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public class EspaiCalendariDecorator extends RecyclerView.ItemDecoration {

    private final int espaiVerticalAltura;

    public EspaiCalendariDecorator(int espaiVerticalAltura){
        this.espaiVerticalAltura = espaiVerticalAltura;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = espaiVerticalAltura;
    }
}
