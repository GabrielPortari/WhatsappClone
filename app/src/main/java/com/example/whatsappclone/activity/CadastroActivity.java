package com.example.whatsappclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.Base64Custom;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {
    private TextInputEditText editNome, editEmail, editSenha;
    private Button buttonCadastrar;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editNome = findViewById(R.id.inputNome_cadastro);
        editEmail = findViewById(R.id.inputEmail_cadastro);
        editSenha = findViewById(R.id.inputSenha_cadastro);
        buttonCadastrar = findViewById(R.id.buttonCadastro);

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCadastroUsuario(v);
            }
        });
    }
    public void cadastrarUsuario(Usuario usuario){
        auth = ConfiguracaoFirebase.getFirebaseAuthReference();
        auth.createUserWithEmailAndPassword(
                usuario.getEmail(), //Recupera email para criar o usuario
                usuario.getSenha() //Recupera a senha para criar o usuario
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Log.i("Auth", "Cadastro feito com sucesso");
                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar!", Toast.LENGTH_SHORT).show();
                    finish();

                    try{
                        String userId = Base64Custom.codeBase64(usuario.getEmail());
                        usuario.setId(userId);
                        usuario.salvarNoFirebase();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    String exception;
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        exception = "Digite uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "Digite um email válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        exception = "Conta já cadastrada";
                    }catch (Exception e){
                        exception = "Erro ao cadastrar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, exception, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void validarCadastroUsuario(View v){
        String textNome = editNome.getText().toString();
        String textEmail = editEmail.getText().toString();
        String textSenha = editSenha.getText().toString();

        //Validação dos campos
        if(!textNome.isEmpty()){
            if(!textEmail.isEmpty()){
                if(!textSenha.isEmpty()){
                    //Cria-se o usuário para cadastrar
                    Usuario usuario = new Usuario();
                    usuario.setNome(textNome);
                    usuario.setEmail(textEmail);
                    usuario.setSenha(textSenha);

                    cadastrarUsuario(usuario);

                }else{
                    Toast.makeText(CadastroActivity.this, "Preencha todos os campos antes de continuar", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CadastroActivity.this, "Preencha todos os campos antes de continuar", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(CadastroActivity.this, "Preencha todos os campos antes de continuar", Toast.LENGTH_SHORT).show();
        }
    }
}