package com.example.officeorder;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.officeorder.Fragment.AnnounceFragment;
import com.example.officeorder.Fragment.CartFragment;
import com.example.officeorder.Fragment.HomeFragment;
import com.example.officeorder.Fragment.ProfileFragment;
import com.example.officeorder.Model.UserData;
import com.example.officeorder.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String id_User = null;
        id_User = UserData.getInstance().getIdUser();

        // Lưu trạng thái đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userID", id_User);
        editor.apply();


        replaceFragment(new HomeFragment());


        binding.bottomNavView.setOnItemSelectedListener( item -> {
            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.support:
                    replaceFragment(new AnnounceFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
                case R.id.holder:
                    swipeFragmentLeft(new CartFragment());
                    break;

            }
            return true;
        });

    }


    public void swipeFragmentLeft(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()

                .setCustomAnimations(R.anim.silde_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.main_Container, fragment,"home")
                .addToBackStack("home")
                .commit();


    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment,"home");
        fragmentTransaction.addToBackStack("home");
        fragmentTransaction.commit();
    }

}