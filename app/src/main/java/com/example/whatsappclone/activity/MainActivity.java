package com.example.whatsappclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ViewPagerAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.fragment.ConversasFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsappClone");
        setSupportActionBar(toolbar);

        auth = ConfiguracaoFirebase.getFirebaseAuthReference();
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);

        //Configuração das abas e fragments
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
        //Fim configuração das abas e fragments
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem busca = menu.findItem(R.id.menu_pesquisa);
        SearchView editBusca = (SearchView) busca.getActionView();

        editBusca.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ConversasFragment fragment = (ConversasFragment) viewPagerAdapter.createFragment(0);
                viewPager2.setCurrentItem(0);
                tabLayout.getTabAt(0).select();
                fragment.recarregarConversas();
                return false;
            }
        });
        editBusca.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ConversasFragment fragment = (ConversasFragment) viewPagerAdapter.createFragment(0);
                viewPager2.setCurrentItem(0);
                tabLayout.getTabAt(0).select();
                if(!newText.isEmpty() || newText != null){
                    fragment.buscaAoDigitar(newText.toLowerCase());
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_sair){
            deslogarUsuario();
            finish();
        }
        if(item.getItemId() == R.id.menu_config){
            abrirConfiguracoes();
        }
        if(item.getItemId() == R.id.menu_pesquisa){

        }
        return super.onOptionsItemSelected(item);
    }
    public void deslogarUsuario(){
        try {
            auth.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void abrirConfiguracoes(){
        startActivity(new Intent(MainActivity.this, ConfigActivity.class));
    }
}