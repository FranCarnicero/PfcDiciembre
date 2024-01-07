package com.example.pfcdiciembre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registrar_usu extends AppCompatActivity {


    EditText UsuarioRegis;
    EditText Passwd1;
    EditText Passwd2;


    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usu);

        firebaseAuth = FirebaseAuth.getInstance();
        UsuarioRegis=(EditText) findViewById(R.id.UsuarioRegis);
        Passwd1 =(EditText) findViewById(R.id.passwd1Regis);
        Passwd2 =(EditText) findViewById(R.id.passwdRegis2);


        Button RegistrarUsu= (Button) findViewById(R.id.ini_sesion);

        RegistrarUsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = UsuarioRegis.getText().toString();
                String password = Passwd1.getText().toString();
                String passwordconfirm = Passwd2.getText().toString();

                if (!password.equals(passwordconfirm)) {
                    Toast.makeText(Registrar_usu.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return; // Salir del método si las contraseñas no coinciden
                }

                registerUser(email, password);

            }
        });

    }


    private void registerUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, puedes redirigir a la siguiente actividad o realizar otras acciones
                            Toast.makeText(Registrar_usu.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Registrar_usu.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Si el registro falla, muestra un mensaje al usuario.
                            Toast.makeText(Registrar_usu.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}