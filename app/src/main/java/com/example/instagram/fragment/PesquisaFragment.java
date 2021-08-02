package com.example.instagram.fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.instagram.R;
import com.example.instagram.activity.PerfilAmigoActivity;
import com.example.instagram.adapter.AdapterPesquisa;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.material.resources.CancelableFontCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PesquisaFragment extends Fragment {

    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisa;
    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;
    private AdapterPesquisa adapterPesquisa;

    private String idUsuarioLogado;


    public PesquisaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerViewPesquisa = view.findViewById(R.id.recyclerViewPesquisa);

        //Configuracoes iniciais
        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdentificadoUsuario();

        //Configura o recyclerView
        recyclerViewPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPesquisa.setHasFixedSize(true);

        //Configura o adapter que está construído no AdapterPesquisa.java
        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        recyclerViewPesquisa.setAdapter(adapterPesquisa);

        //Configurar evento de clique do recyclerViewPesquisa
        recyclerViewPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaUsuarios.get(position); // recupera a posição que o usuario clicou no touch
                        Intent i = new Intent(getActivity(), PerfilAmigoActivity.class);
                        i.putExtra("usuarioSelecionado", usuarioSelecionado);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        //Configurar searchView
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisarUsuarios(textoDigitado);
                return true;
            }
        });
        return view;
    }

    private void pesquisarUsuarios(String texto){
        //limpar lista
        listaUsuarios.clear();
        //Pesquisa os usuários caso tenha texto na pesquisa
        if(texto.length() >= 2){
            Query query = usuariosRef.orderByChild("nome")
                    .startAt(texto)
                    .endAt(texto + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //limpar lista
                    listaUsuarios.clear();
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        //Verifica se é usuário logado e remove da lista
                        Usuario usuario = ds.getValue(Usuario.class);

                        //verifica se o id do usuário logado é igual ao usuário que está sendo recuperado
                        if(idUsuarioLogado.equals(usuario.getId()))
                            continue;

                        //Adiciona usuário na lista
                        listaUsuarios.add(usuario);
                    }
                    adapterPesquisa.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}