package com.example.whatsappclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editEmail, editSenha;
    private Button botaoLogin;
    private TextView textCadastro;
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuthReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.inputEmail_login);
        editSenha = findViewById(R.id.inputSenha_login);
        botaoLogin = findViewById(R.id.buttonLogin);
        textCadastro = findViewById(R.id.textView_login);
        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarAutenticacao(v);
            }
        });
        textCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaCadastro(v);
            }
        });
    }
    public void fazerLogin(Usuario usuario){
        auth.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("Auth", "Login feito com sucesso");
                    abrirTelaPrincipal();
                    finish();
                }else{
                    String exception;
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        exception = "Usuário não cadastrado";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "Email ou senha incorreto";
                    }catch (Exception e){
                        exception = "Erro ao cadastrar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();                }
            }
        });
    }
    public void validarAutenticacao(View view){
        String textEmail = editEmail.getText().toString();
        String textSenha = editSenha.getText().toString();

        //Validação dos campos
        if(!textEmail.isEmpty()){
            if(!textSenha.isEmpty()){
                Usuario usuario = new Usuario();
                usuario.setEmail(textEmail);
                usuario.setSenha(textSenha);
                fazerLogin(usuario);
            }else{
                Toast.makeText(LoginActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this, "Preencha todos os campos para continuar", Toast.LENGTH_SHORT).show();
        }
    }
    public void abrirTelaCadastro(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}