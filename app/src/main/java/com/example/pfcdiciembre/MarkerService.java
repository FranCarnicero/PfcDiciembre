package com.example.pfcdiciembre;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;



public class MarkerService extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private static final Map<Marker, MarkerInfo> markerInfoMap = new HashMap<>();




    public MarkerService() {
        // Configura la referencia a la base de datos de Firebase

    }

    public interface OnMarkerInfoListener {
        void onMarkerInfoSubmitted(MarkerInfo markerInfo);
    }



    public static void addMarker(GoogleMap map, LatLng position) {
        // Añade un marcador al mapa
        Marker marker = map.addMarker(new MarkerOptions().position(position).title("Nuevo Marcador"));


        // Guarda el marcador en el mapa de información
        markerInfoMap.put(marker, new MarkerInfo());

    }

    public static void setMarkerClickListener(Context context, GoogleMap map, OnMarkerInfoListener listener) {
        // Establece un listener de clic en el marcador
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Muestra el diálogo editable de información del marcador al hacer clic
                showEditableMarkerInfoDialog(context, marker, listener);
                return true;
            }
        });
    }


    private static void showEditableMarkerInfoDialog(Context context, Marker marker, OnMarkerInfoListener listener) {
        // Obtén la información personalizada del marcador
        MarkerInfo markerInfo = markerInfoMap.get(marker);

        // Inflar el diseño del diálogo editable
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_editable_marker_info, null);

        // Obtén referencias a las vistas en el diálogo
        EditText editTextCountry = view.findViewById(R.id.editTextCountry);
        EditText editTextCity = view.findViewById(R.id.editTextCity);
        EditText editTextPlace = view.findViewById(R.id.editTextPlace);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        Button buttonAddPhoto = view.findViewById(R.id.buttonAddPhoto);
        Button buttonSend = view.findViewById(R.id.buttonSend);

        // Configurar el contenido del diálogo editable
        editTextCountry.setText(markerInfo.getCountry());
        editTextCity.setText(markerInfo.getCity());
        editTextPlace.setText(markerInfo.getPlace());
        editTextDescription.setText(markerInfo.getDescription());

        // Configurar el botón para añadir foto desde la galería
        buttonAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(context);
            }
        });

        // Crear el diálogo editable
        Dialog dialog = new Dialog(context);
        dialog.setContentView(view);

        // Configurar el botón de enviar
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualizar la información del marcador con los valores editados
                markerInfo.setCountry(editTextCountry.getText().toString());
                markerInfo.setCity(editTextCity.getText().toString());
                markerInfo.setPlace(editTextPlace.getText().toString());
                markerInfo.setDescription(editTextDescription.getText().toString());
                markerInfo.setPosition(marker.getPosition());

                // Notificar al listener con la información actualizada del marcador
                if (listener != null) {
                    listener.onMarkerInfoSubmitted(markerInfo);
                }

                // Generar el código QR con la información del diálogo
                try {
                    generateQRCode(context, markerInfo);

                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }

                // Cerrar el diálogo después de enviar
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo editable
        dialog.show();
    }

    private static void openGallery(Context context) {
        // Abre la galería para seleccionar una imagen
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((FragmentActivity) context).startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public static Bitmap handleGalleryResult(Context context, Intent data) {
        // Maneja el resultado de la galería y obtiene la imagen seleccionada
        Bitmap bitmap = null;
        if (data != null) {
            Uri selectedImage = data.getData();
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(selectedImage);
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private static void generateQRCode(Context context, MarkerInfo markerInfo) throws WriterException {

        // Genera el código QR con la información del marcador
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmap = null;
        try {
            String qrData = String.format("País: %s\nCiudad: %s\nLugar: %s\nDescripción: %s",
                    markerInfo.getCountry(), markerInfo.getCity(), markerInfo.getPlace(), markerInfo.getDescription());
            bitmap = barcodeEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 400, 400);

            if (bitmap != null) {
                saveImageToGallery(context, bitmap);
                Toast.makeText(context, "Código QR guardado en la galería", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al generar el código QR", Toast.LENGTH_SHORT).show();
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private static void saveImageToGallery(Context context, Bitmap bitmap) {
        OutputStream fos = null;
        try {

            String fileName = "QRImage_" + System.currentTimeMillis() + ".png";
            // Añadir la imagen a la galería utilizando MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

            Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try {
                // Obtener un OutputStream para el URI de la imagen
                OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri);

                // Comprimir y guardar el bitmap en el OutputStream como formato PNG
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                // Cerrar el OutputStream
                outputStream.close();

            } catch (IOException e) {
                    e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class MarkerInfo {

        private LatLng position;
        private String country = "";
        private String city = "";
        private String place = "";
        private String description = "";
        private Bitmap photo;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Bitmap getPhoto() {
            return photo;
        }

        public void setPhoto(Bitmap photo) {
            this.photo = photo;
        }

        public LatLng getPosition() {
            return position;
        }

        public void setPosition(LatLng position) {
            this.position = position;
        }

        public MarkerInfo() {
            // Constructor vacío requerido para Firebase
        }

    }
}
