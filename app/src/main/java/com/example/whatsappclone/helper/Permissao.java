package com.example.whatsappclone.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class Permissao {
    /*
    Classe utilizada com metodos para validar as permissões do usuario, porém como as permissões nao são validadas,
    a classe não tem utilidade
     */
    public static boolean validarPermissao(String[] p, Activity activity, int requestCode){
        if(Build.VERSION.SDK_INT > 21){
            List<String> listaPermissao = new ArrayList<>();
            /*Percorre as permissões para garantir se foram liberadas*/
            for(String permissao : p){
                Boolean valido = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if(!valido) {
                    listaPermissao.add(permissao);
                }
            }
            /*Caso a lista de permissões seja vazia retorna true*/
            if(listaPermissao.isEmpty()){
                return true;
            }
            /*Solicita a permissão*/
            String[] permissoesArray = new String[listaPermissao.size()];
            listaPermissao.toArray(permissoesArray);
            ActivityCompat.requestPermissions(activity, permissoesArray, requestCode);
        }
        return true;
    }
}
