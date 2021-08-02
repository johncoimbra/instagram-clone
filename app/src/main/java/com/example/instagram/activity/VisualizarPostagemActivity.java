package com.example.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {
    TextView textPerfilPostagem, textQtdCurtidasPostagem, textDescricaoPostagem, textVisualizarComentariosPostagem;
    ImageView imagePostagemSelecionada;
    CircleImageView imagePerfilPostagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        //inicializar componentes
        inicializarComponentes();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar Postagem");
        setSupportActionBar(toolbar);

        //Configura o botão voltar dentro desta activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24);

        //Recupera dados da activity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Postagem postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuario");

            //Exibe dados de usuário
            Uri uri = Uri.parse(usuario.getCaminhoFoto());
            Glide.with(VisualizarPostagemActivity.this)
                    .load(uri)
                    .into(imagePerfilPostagem);
            textPerfilPostagem.setText(usuario.getNome());

            //Exibe os dados da postagem
            Uri uriPostagem = Uri.parse(postagem.getCaminhoFoto());
            Glide.with(VisualizarPostagemActivity.this)
                    .load(uriPostagem)
                    .into(imagePostagemSelecionada);
            textDescricaoPostagem.setText(postagem.getDescricao());


        }
    }

    private void inicializarComponentes(){
        textPerfilPostagem = findViewById(R.id.textPerfilPostagem);
        textQtdCurtidasPostagem = findViewById(R.id.textQtdCurtidasPostagem);
        textDescricaoPostagem = findViewById(R.id.textDescricaoPostagem);
        imagePostagemSelecionada = findViewById(R.id.imagePostagemSelecionada);
        imagePerfilPostagem = findViewById(R.id.imagePerfilPostagem);

    }

    public boolean onSupportNavigateUp(){
        finish();
        return false;
    }
}