package com.example.pfcdiciembre;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthService  {
    private FirebaseAuth firebaseAuth;

    public FirebaseAuthService() {
        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void logout(@NonNull Context context) {
        firebaseAuth.signOut();

        // Puedes redirigir a la pantalla de inicio de sesión u otra pantalla después del logout
        Intent loginIntent = new Intent(context, MainActivity.class);
        context.startActivity(loginIntent);
    }

    public boolean isLoggedIn() {
        // Verificar si el usuario está actualmente autenticado
        return firebaseAuth.getCurrentUser() != null;
    }
}