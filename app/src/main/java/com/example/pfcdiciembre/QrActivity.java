package com.example.pfcdiciembre;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrActivity extends AppCompatActivity {

    private FirebaseAuthService firebaseAuthService;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        Button scanButton = findViewById(R.id.scanButton);

        TextView textView = findViewById(R.id.showQRResult);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar el escáner QR
                IntentIntegrator integrator = new IntentIntegrator(QrActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setCameraId(0);  // Use camera with ID 0 (rear camera)
                integrator.setBeepEnabled(false);  // Disable beep sound
                integrator.setOrientationLocked(false);  // Permitir la rotación de la cámara
                integrator.initiateScan();
            }
        });

        // Configurar el botón de Log Out
        firebaseAuthService = new FirebaseAuthService();
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agrega la lógica para el log out aquí
                firebaseAuthService.logout(QrActivity.this);
            }
        });
        // Configurar clics para cada botón del footer
        ImageButton btnMaps = findViewById(R.id.btnMaps);
        ImageButton btnQRCode = findViewById(R.id.btnQRCode);
        ImageButton btnUser = findViewById(R.id.btnUser);

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Agrega la lógica para la opción de Maps aquí
                Intent intent = new Intent(QrActivity.this, MapsActivity.class);
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

    // Manejar el resultado del escaneo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                // Escaneo cancelado o fallido
                // Puedes agregar lógica adicional aquí si es necesario
            } else {
                // Escaneo exitoso
                String scannedData = result.getContents();
                // Puedes hacer algo con los datos escaneados, como mostrarlos en un TextView

                 textView.setText(scannedData);
            }
        }
    }
}