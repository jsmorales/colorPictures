package com.example.johanmorales.colorpictures;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AlertDialogLayout;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    /*
    * Los recursos de tipo icono se encuentran en las siguientes url
    * https://material.io/tools/icons/?style=baseline
    * */

    public static final int PETICION_FOTO = 1;
    public static final int PETICION_VIDEO = 2;
    public static final int PETICION_GALERIA_FOTOS = 3;
    public static final int PETICION_GALERIA_VIDEOS = 4;

    public static final int MEDIA_FOTO = 5;
    public static final int MEDIA_VIDEO = 6;
    public static final String YYYY_MM_DD_MM_SS = "yyyy-MM-DD:mm:ss";

    private Uri mediaUri;

    //psfi o psfs para autogenerar declaraciones de constatntes tipo string o int

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Metodo para manejar los resultados de actividades
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //la constante predefinida de buen resultado es RESULT_OK
        if(resultCode == RESULT_OK){
            //manipular informacion

            if(requestCode == PETICION_FOTO){
                //ver la foto
                //se pasa a la otra activity con un nuevo intent que pasa de esta activity Main a
                //la actividad ImageActivity
                Intent verFoto = new Intent(this, ImageActivity.class);
                //a este intent se le agregan los datos que se acaban de capturar
                //en este caso lo que se captura el la Uri de la imagen recien tomada
                verFoto.setData(mediaUri);
                //se inicializa el intent inicializado
                startActivity(verFoto);
            }

        }else{
            //algo salio mal y toca notificar al usuario
            Toast.makeText(this, "No se pudo manejar el reseultado.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * Metodos de clase
    * */

    private boolean almacenamientoExternoDisponible(){

        //La clase Environment es el ambiente del sistema operativo

        String estadoAlmacenamiento = Environment.getExternalStorageState();

        return estadoAlmacenamiento.equals(Environment.MEDIA_MOUNTED);
    }

    //Este metodo regresa el path al cual el archivo va a quedar almacenado
    private Uri crearArchivo(int tipoMedio) throws IOException {

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat(YYYY_MM_DD_MM_SS).format(new Date());
        String nombreArchivo;

        File archivo;


        //se valida el tipo de archivo que se quiere crear por medio de la variable tipoMedio
        //por medio de las constantes definidas

        if(tipoMedio == MEDIA_FOTO){

            nombreArchivo = "IMG_"+timeStamp+"appColorPictures";

            //se obtiene directorio de imagenes
            File directorioAlmacenamiento = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            //se crea el archivo
            archivo = File.createTempFile(nombreArchivo,".jpg",directorioAlmacenamiento);

            //esta linea refresca el contenido de la galeria para que lo podamos ver
            MediaScannerConnection.scanFile(this, new String[]{archivo.getPath()}, new String [] {"image/jpeg", "video/mp4"}, null);

            Toast.makeText(this, "Archivo guardado en: "+archivo.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            return Uri.fromFile(archivo);

        }else if(tipoMedio == MEDIA_VIDEO){

            nombreArchivo = "VID_"+timeStamp+"appColorPictures";

            //se obtiene directorio de imagenes
            File directorioAlmacenamiento = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

            //se crea el archivo
            archivo = File.createTempFile(nombreArchivo,".mp4",directorioAlmacenamiento);

            //esta linea refresca el contenido de la galeria para que lo podamos ver
            MediaScannerConnection.scanFile(this, new String[]{archivo.getPath()}, new String [] {"image/jpeg", "video/mp4"}, null);

            Toast.makeText(this, "Archivo guardado en: "+archivo.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            return Uri.fromFile(archivo);

        }else{
            return null;
        }

    }

    /*
    * Metodos on click de la view
    *
    * se utiliza la libreria para las imagenes de github
    * https://github.com/square/picasso
    * */
    public void tomarFoto(View view) {
        Toast.makeText(this, "Ejecutando método tomarFoto", Toast.LENGTH_LONG).show();

        try {
            mediaUri = crearArchivo(MEDIA_FOTO);
            
            if(mediaUri == null){
                Toast.makeText(this, "Por favor revisar almacenamiento.", Toast.LENGTH_SHORT).show();
            }else{

                //se crea el intent que tiene por parametro tomar imagen
                Intent intentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //en el intent manda el extra con el path del archivo
                intentFoto.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);

                //se ejecuta startActivityForResult parametros intent de la foto y el numero de peticion que esta guardado en la constante
                //este metodo me permite guardar el resultado para despues hacer algo con el
                startActivityForResult(intentFoto,PETICION_FOTO);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void tomarVideo(View view) {
        Toast.makeText(this, "Ejecutando método tomarVideo", Toast.LENGTH_LONG).show();
    }

    public void verGaleriaFotos(View view) {
        Toast.makeText(this, "Ejecutando método verGaleriaFotos", Toast.LENGTH_LONG).show();
    }

    public void verGaleriaVideos(View view) {
        Toast.makeText(this, "Ejecutando método verGaleriaVideos", Toast.LENGTH_LONG).show();
    }
}
