/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2021
   --------------------------------------
*/

package dev.dileepabandara.finedeliver.HelperClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.dileepabandara.finedeliver.R;

public class SpinnerItemAdapter extends ArrayAdapter<String> {

    Context context;
    String[] names;
    int[] images;

    public SpinnerItemAdapter(@NonNull Context context, String[] names, int[] images) {
        super(context, R.layout.spinner_item_category, names);
        this.context = context;
        this.names = names;
        this.images = images;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_item_category, null);
        TextView t1 = (TextView) row.findViewById(R.id.txtSpinnerItemCategory);
        ImageView i1 = (ImageView) row.findViewById(R.id.imgSpinnerItemCategory);
        t1.setText(names[position]);
        i1.setImageResource(images[position]);

        return row;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_item_category, null);
        TextView t1 = (TextView) row.findViewById(R.id.txtSpinnerItemCategory);
        ImageView i1 = (ImageView) row.findViewById(R.id.imgSpinnerItemCategory);
        t1.setText(names[position]);
        i1.setImageResource(images[position]);

        return row;
    }
}
