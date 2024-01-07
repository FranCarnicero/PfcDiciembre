package com.example.pfcdiciembre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private FirebaseAuthService firebaseAuthService;
    private GoogleMap mMap;

    private static DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        this.databaseReference = FirebaseDatabase.getInstance("https://pfcdiciembre-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        // Configurar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Configurar el botón de Log Out
        firebaseAuthService = new FirebaseAuthService();
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agrega la lógica para el log out aquí
                firebaseAuthService.logout(MapsActivity.this);
            }
        });

        // Configurar clics para cada botón del footer
        ImageButton btnQRCode = findViewById(R.id.btnQRCode);
        ImageButton btnUser = findViewById(R.id.btnUser);


        btnQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agrega la lógica para la opción de Código QR aquí

                Intent intent = new Intent(MapsActivity.this, QrActivity.class);
                startActivity(intent);
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agrega la lógica para la opción de Usuario aquí
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Configura la lógica para agregar marcadores y manejar clics en ellos
        configureMapMarkers();

        LatLng initialLocation = new LatLng(40.4166445,-3.7031945); // Coordenadas de Madrid

        // Mueve la cámara a la ubicación inicial
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12));

    }

    private void configureMapMarkers() {
        // Establece un listener de clic en el mapa para agregar marcadores
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Añade marcadores al mapa cuando haces clic en él
                MarkerService.addMarker(mMap, latLng);
            }
        });

        // Establece un listener de clic en los marcadores para mostrar el diálogo editable
        MarkerService.setMarkerClickListener(this, mMap, new MarkerService.OnMarkerInfoListener() {
            @Override
            public void onMarkerInfoSubmitted(MarkerService.MarkerInfo markerInfo) {
                // Aquí puedes realizar acciones con la información actualizada del marcador
                // Por ejemplo, guardar en una base de datos o realizar alguna acción específica
                saveMarkerToFirebase(markerInfo);
            }
        });

        //loadMarkersFromFirebase();
    }
    private static void saveMarkerToFirebase(MarkerService.MarkerInfo markerInfo) {
        // Guarda la información del marcador en la base de datos de Firebase
        String key = databaseReference.push().getKey();
        if (key != null) {
            databaseReference.child(key).setValue(markerInfo);
        }
    }

    private void loadMarkersFromFirebase() {
        // Leer los marcadores desde Firebase y agregarlos al mapa
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MarkerService.MarkerInfo markerInfo= snapshot.getValue(MarkerService.MarkerInfo.class);
                    if (markerInfo != null) {

                        MarkerService.addMarker(mMap, markerInfo.getPosition());                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Error al cargar marcadores desde Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}