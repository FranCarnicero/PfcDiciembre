package com.example.pfcdiciembre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {


    private EditText usuario;
    private EditText passwd;
    private CheckBox showPasswordCheckbox;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        usuario=(EditText) findViewById(R.id.Usuario);
        passwd =(EditText) findViewById(R.id.passwd);
        showPasswordCheckbox = findViewById(R.id.showPasswordCheckbox);


        Button registrar_usu = (Button) findViewById(R.id.Crear_Usu);
        Button inicio_sesion = (Button) findViewById(R.id.ini_sesion);

        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Mostrar u ocultar la contraseña según el estado del checkbox
            int inputType = isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            passwd.setInputType(inputType);
            passwd.setSelection(passwd.length());
        });

        registrar_usu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrarUsu();
            }
        });

        inicio_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usuario.getText().toString();
                String password = passwd.getText().toString();

                loginUser(email, password);
            }
        });


    }
    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login exitoso, puedes redirigir a la siguiente actividad
                            Toast.makeText(MainActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Si el login falla, muestra un mensaje al usuario.
                            Toast.makeText(MainActivity.this, "Error en el login: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void  RegistrarUsu() {
        Intent intent = new Intent(MainActivity.this, Registrar_usu.class);
        startActivity(intent);
    }




}