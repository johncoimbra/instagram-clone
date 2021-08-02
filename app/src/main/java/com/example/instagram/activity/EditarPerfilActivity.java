package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.Permissao;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
    private String identificadorUsuario;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        inicializarComponentes();

        //VALIDAR PERMISSOES
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        //Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        identificadorUsuario = UsuarioFirebase.getIdentificadoUsuario();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);

        //Configura o botão voltar dentro desta activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24);

        //Recuperar os dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual(); //recupera usuario atual(logado)
        editNomePerfil.setText(usuarioPerfil.getDisplayName().toUpperCase());
        editEmailPerfil.setText(usuarioPerfil.getEmail());
        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Glide.with(EditarPerfilActivity.this)
                    .load(url)
                    .into(imageEditarPerfil);
        }else{
            imageEditarPerfil.setImageResource(R.drawable.avatar);
        }

        //Salvar alterações do nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtualizado = editNomePerfil.getText().toString();
                //atualizar o nome no perfil firebase
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                //atualizar nome do usuario no banco de dados no Firebase
                usuarioLogado.setNome(nomeAtualizado);
                usuarioLogado.atualizar();
                Toast.makeText(EditarPerfilActivity.this,
                        "Dados alterados com sucesso",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //Alterar foto do usuario
        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                //Selecao apenas da galeria
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }
                //Caso tenha sido escolhido uma imagem
                if(imagem != null){
                    //configurar imagem na tela do usuario
                    imageEditarPerfil.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child(identificadorUsuario + ".jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Recuperar local da foto
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizaFotoUsuario(url);
                                }
                            });

                            Toast.makeText(EditarPerfilActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void atualizaFotoUsuario(Uri url){
        //Atualizar foto no Perfil
        UsuarioFirebase.atualizarFotoUsuario(url);
        //Atualizar foto no Firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizar();
        Toast.makeText(EditarPerfilActivity.this,
                "Sua foto foi atualizada!",
                Toast.LENGTH_SHORT).show();
    }

    public void inicializarComponentes(){
        imageEditarPerfil       = findViewById(R.id.imagePerfil);
        textAlterarFoto         = findViewById(R.id.textAlterarFoto);
        editNomePerfil          = findViewById(R.id.editNomePerfil);
        editEmailPerfil         = findViewById(R.id.editEmailPerfil);
        buttonSalvarAlteracoes  = findViewById(R.id.buttonSalvarAlteracoes);
        editEmailPerfil.setFocusable(false); //usuario nao consegue selecionar o campo email
    }

    public boolean onSupportNavigateUp(){
        finish();
        return false;
    }
}