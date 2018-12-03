package com.example.johanmorales.colorpictures;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
    public static final int MAX_DURATION_VIDEO_APP = 10;
    public static final int CAMERA_WRITE_PERMISSION = 11;
    public static final int VIDEO_WRITE_PERMISSION = 12;

    private Uri mediaUri;

    //psfi o psfs para autogenerar declaraciones de constatntes tipo string o int

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Fix error
        //Caused by: android.os.FileUriExposedException: file:///storage/emulated/0/Pictures/... exposed beyond app through ClipData.Item.getUri()
        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());
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

            if(requestCode == PETICION_VIDEO){

                //este intent abre el view necesari opara poder ejecutar la accion necesaria para
                //poder ver el contenido de la forma necesaria
                Intent verVideo = new Intent(Intent.ACTION_VIEW, mediaUri);
                verVideo.setDataAndType(mediaUri,"video/*");
                startActivity(verVideo);
            }

            if(requestCode == PETICION_GALERIA_FOTOS){

                Intent verGaleriaFotos = new Intent(this, ImageActivity.class);
                verGaleriaFotos.setData(data.getData());
                startActivity(verGaleriaFotos);
            }

            if(requestCode == PETICION_GALERIA_VIDEOS){
                //envia la data que toma el result a la actividad de video
                Intent verGaleriaVideos = new Intent(this, VideoActivity.class);
                verGaleriaVideos.setData(data.getData());
                startActivity(verGaleriaVideos);
            }

        }else{
            //algo salio mal y toca notificar al usuario
            Toast.makeText(this, "No se pudo manejar el resultado.", Toast.LENGTH_SHORT).show();
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

        //en caso de que no halla almacenamiento disponible no se ejecuta este metodo
        if(!almacenamientoExternoDisponible()){
            return null;
        }

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

        Toast.makeText(this, "Ejecutando método tomarFoto sobre la version sdk "+Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();

        String permisoStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        //verificar la version de android para pedir permisos en tiempo de ejecucion
        //para la version 23 marshmallow hay que pedir permisos en tiempo de ejecución

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            Toast.makeText(this, "Necesito permisos!", Toast.LENGTH_SHORT).show();

            //verificar si ya tiene permiso requerido en este caso el WRITE_EXTERNAL_STORAGE que se definió en el
            //manifest
            if(checkSelfPermission(permisoStorage) != PackageManager.PERMISSION_GRANTED){
                //pedir el permiso

                //verificar si ya se pidio el persimo antes para informa porque
                //se necesita en caso de que se halla denegado previamente
                if(shouldShowRequestPermissionRationale(permisoStorage)){
                    mostrarExplicacion(PETICION_FOTO,permisoStorage);
                }else{
                    //para pedir el permiso se solicita el array de strings de los permisos, con las constantes
                    //definidas en Manifest.permission y un codigo para poderlo identificar definido por uno mismo
                    requestPermissions(new String[]{permisoStorage}, CAMERA_WRITE_PERMISSION);
                }

            }else{
                //ya tiene el permiso
                capturar(PETICION_FOTO,MEDIA_FOTO);
            }

        }else{
            capturar(PETICION_FOTO,MEDIA_FOTO);
        }

    }

    private void mostrarExplicacion(final int tipoPeticion, final String permisoStorage) {

        new AlertDialog.Builder(this)
                .setTitle("Permiso de Almacenamiento")
                .setMessage("Se necesita tu permiso para poder guardar las fotos en el dispositivo.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //pedir el permiso requerido

                        if(tipoPeticion == PETICION_FOTO){
                            requestPermissions(new String[]{permisoStorage}, CAMERA_WRITE_PERMISSION);
                        }else if(tipoPeticion == PETICION_VIDEO){
                            requestPermissions(new String[]{permisoStorage}, VIDEO_WRITE_PERMISSION);
                        }else{
                            throw new IllegalArgumentException();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //una lastima
                        Toast.makeText(MainActivity.this, "Que lástima, la aplicación no se puede utilizar.", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //este metodo se ejecuta en respuesta a lo que resulte del pedido de permisos

        //requestCode es el codigo que se definio por defecto dentro de nuestra app
        //public static final int CAMERA_WRITE_PERMISSION = 11;
        //public static final int CAMERA_WRITE_PERMISSION = 12;

        //permissions es el arreglo de perimisos solicitados
        //grantResults devuelte entero de si se dio el permiso o no

        if(requestCode == CAMERA_WRITE_PERMISSION){
            //se debe iniciar la camara con foto
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                capturar(PETICION_FOTO,MEDIA_FOTO);
            }
        }else if(requestCode == VIDEO_WRITE_PERMISSION){
            //iniciar video
            capturar(PETICION_VIDEO,MEDIA_VIDEO);
        }
    }

    public void tomarVideo(View view) {

        Toast.makeText(this, "Ejecutando método tomarVideo sobre la version sdk "+Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();

        capturar(PETICION_VIDEO,MEDIA_VIDEO);
    }

    public void capturar(int peticionArchivo, int tipoArchivo){

        try {
            mediaUri = crearArchivo(tipoArchivo);

            if(mediaUri == null){
                Toast.makeText(this, "Por favor revisar almacenamiento.", Toast.LENGTH_LONG).show();
            }else{

                iniciarCamara(mediaUri,peticionArchivo);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void iniciarCamara(Uri mediaUri, int tipoPeticion){

        Intent intent;

        if(tipoPeticion == PETICION_VIDEO){

            //se crea el intent que tiene por parametro tomar video
            intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //en el intent manda el extra con el path del archivo
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            //para limitar la duracion del video se envia en el intent de la siguiente forma
            //tambien se puede especificar la calidad del video
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAX_DURATION_VIDEO_APP);
            //se ejecuta startActivityForResult parametros intent de la foto y el numero de peticion que esta guardado en la constante
            //este metodo me permite guardar el resultado para despues hacer algo con el
            startActivityForResult(intent,tipoPeticion);

        }else if(tipoPeticion == PETICION_FOTO){

            //se crea el intent que tiene por parametro tomar imagen
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //en el intent manda el extra con el path del archivo
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mediaUri);
            //se ejecuta startActivityForResult parametros intent de la foto y el numero de peticion que esta guardado en la constante
            //este metodo me permite guardar el resultado para despues hacer algo con el
            startActivityForResult(intent,tipoPeticion);

        }else{

            Toast.makeText(this, "Se intenta inicializar la cámara de forma incorrecta.", Toast.LENGTH_LONG).show();

            throw new IllegalArgumentException();
        }
        
    }

    public void verGaleriaFotos(View view) {
        Toast.makeText(this, "Ejecutando método verGaleriaFotos", Toast.LENGTH_LONG).show();

        inicializarGaleria("image/*",PETICION_GALERIA_FOTOS);
    }

    public void verGaleriaVideos(View view) {
        Toast.makeText(this, "Ejecutando método verGaleriaVideos", Toast.LENGTH_LONG).show();

        inicializarGaleria("video/*",PETICION_GALERIA_VIDEOS);
    }

    public void inicializarGaleria(String tipo, int peticion){

        //para ver la galeria de fotos se hace con un intent
        Intent verGaleria = new Intent(Intent.ACTION_GET_CONTENT);
        //se dice el tipo de contenido que se va a ver con setType
        verGaleria.setType(tipo);
        //se inicia la activity pero por result para darle manejo en el evento de esta misma actividad
        startActivityForResult(verGaleria, peticion);
    }
}
