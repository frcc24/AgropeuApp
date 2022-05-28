package com.francocorrea.agropeuapp.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.model.Evento;

import java.util.List;

public class ListaEventosAdapter extends ArrayAdapter<Evento> {
    private final Context context;
    private final List<Evento> lst_eventos;


    public ListaEventosAdapter(@NonNull Context c, @NonNull List<Evento> objects) {
        super(c, 0, objects);
        this.context = c;
        this.lst_eventos = objects;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_lista_eventos, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        if (lst_eventos.size() > 0) {

//            if(position%2 == 0){
//                holder.constraintLayout.setBackgroundColor(context.getColor(R.color.colorPrimaryDark));
//            }else{
//                holder.constraintLayout.setBackgroundColor(context.getColor(R.color.colorPrimary));
//            }

            Evento evento = lst_eventos.get(position);

            holder.nome.setText(evento.getNome());
            holder.dataEvento.setText(evento.getDataEvento());
            holder.decricaoEvento.setText(evento.getDescEvento());

            if (evento.getImage().equals("1")) {
                holder.img.setImageResource(R.drawable.cerveja);
            } else if (evento.getImage().equals("2")) {
                holder.img.setImageResource(R.drawable.festajunina);
            } else if (evento.getImage().equals("3")) {
                holder.img.setImageResource(R.drawable.final_ano);
            } else if (evento.getImage().equals("4")) {
                holder.img.setImageResource(R.drawable.reuniao2);
            } else if (evento.getImage().equals("5")) {
                holder.img.setImageResource(R.drawable.reuniao);
            } else if (evento.getImage() == null) {
                holder.img.setImageResource(R.drawable.reuniao);
            }


        }


        return view;

    }

    public class ViewHolder {

        final TextView nome;
        final ImageView img;
        final TextView dataEvento;
        final TextView decricaoEvento;
        final ConstraintLayout constraintLayout;

        public ViewHolder(View view) {
            nome = view.findViewById(R.id.txt_nome_evento);
            img = view.findViewById(R.id.img_evento);
            dataEvento = view.findViewById(R.id.txt_data_evento);
            constraintLayout = view.findViewById(R.id.const_list_bg);
            decricaoEvento = view.findViewById(R.id.txt_desc_evento);
        }

    }

}
