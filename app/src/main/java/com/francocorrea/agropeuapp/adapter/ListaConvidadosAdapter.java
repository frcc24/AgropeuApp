package com.francocorrea.agropeuapp.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.francocorrea.agropeuapp.R;
import com.francocorrea.agropeuapp.model.Convidado;

import java.util.List;

public class ListaConvidadosAdapter extends ArrayAdapter<Convidado> {
    private final Context context;
    private final List<Convidado> lst_convidados;


    public ListaConvidadosAdapter(@NonNull Context c, @NonNull List<Convidado> objects) {
        super(c, 0, objects);
        this.context = c;
        this.lst_convidados = objects;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_lista_convidados, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        if (lst_convidados.size() > 0) {

            if (position % 2 == 0) {
                holder.constraintLayout.setBackgroundColor(context.getColor(R.color.colorPrimaryDark));
            } else {
                holder.constraintLayout.setBackgroundColor(context.getColor(R.color.colorPrimary));
            }

            Convidado convidado = lst_convidados.get(position);

            holder.nome.setText(convidado.getNome());
            holder.numero.setText(convidado.getNumeroConvite());

            if (convidado.getStatus().equalsIgnoreCase("presente")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.status.setTextColor(context.getColor(R.color.colorAccent));
                    holder.numero.setTextColor(context.getColor(R.color.colorAccent));
                }
                holder.status.setText("Presente");
            } else if (convidado.getStatus().equalsIgnoreCase("inativo")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.status.setTextColor(context.getColor(R.color.red));
                    holder.numero.setTextColor(context.getColor(R.color.red));
                }
                holder.status.setText("INATIVO");
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.status.setTextColor(context.getColor(R.color.red));
                    holder.numero.setTextColor(context.getColor(R.color.red));
                }
                holder.status.setText("Ainda não chegou");
            }

        }


        return view;

    }

    public class ViewHolder {

        final TextView nome;
        final TextView numero;
        final TextView status;
        final ConstraintLayout constraintLayout;

        public ViewHolder(View view) {
            nome = view.findViewById(R.id.txt_nome_convidado);
            numero = view.findViewById(R.id.txt_NumConvite);
            status = view.findViewById(R.id.txt_status);
            constraintLayout = view.findViewById(R.id.const_list_bg);
        }

    }

}
