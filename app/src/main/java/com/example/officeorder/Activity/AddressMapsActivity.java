package com.example.officeorder.Activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;

import com.example.officeorder.R;
import com.example.officeorder.databinding.ActivityAddressMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class AddressMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityAddressMapsBinding binding;
    private EditText et_diachi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        et_diachi = binding.etNgo;

        et_diachi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address = s.toString();
                fetchAddressSuggestions(address);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.clear();
        LatLng defaultLocation = new LatLng(10.7769, 106.7009);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
        String address = "ngõ 20 phường phúc xá , ba đình, hà nội";
        fetchAddressSuggestions(address);
    }

    private void fetchAddressSuggestions(String address) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName(address, 5);
            if (addresses != null && !addresses.isEmpty()) {

                mMap.clear();

                for (Address addressLocation : addresses) {
                    LatLng location = new LatLng(addressLocation.getLatitude(), addressLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title("Marker at " + addressLocation.getAddressLine(0))
                    );
                }

                Address firstAddress = addresses.get(0);
                LatLng firstLocation = new LatLng(firstAddress.getLatitude(), firstAddress.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10));
            }
        } catch (IOException e) {
            Log.e("MapsActivity", "Failed to fetch address suggestions: " + e.getMessage());
        }
    }
}