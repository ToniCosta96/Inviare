package com.example.prova.inviare.db_adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;

/**
 * Created by 2dam on 17/01/2017.
 */

public class DBAdapter {
    private static final String DATABASE_NAME = "db_inviare.db";
    //private static final String TABLE_PERFIL = "perfil"; // ID - NOMBRE - TELEFONO - EMAIL - ESTADO - IMAGE
    private static final String TABLE_CONTACTOS = "contactos"; // ID - NOMBRE - IMAGEN
    private static final String TABLE_MENSAJES = "mensajes"; // ID_CONTACTOS <-> ID - MENSAJE - DATETIME - TIPO - HORA - DIA - FRECUENCIA
    private static final int DATABASE_VERSION = 1;

    private static final String NOMBRE = "nombre";
    private static final String TELEFONO = "telefono";
    private static final String EMAIL = "email";
    private static final String ESTADO  = "estado";
    private static final String IMAGEN = "imagen";

    private static final String MENSAJE = "mensaje";
    private static final String DATE_TIME = "date";

    private static final String TIPO = "tipo";
    private static final String HORA = "hora";
    private static final String DIA = "dia";
    private static final String FRECUENCIA = "frecuencia";

    private static final String DATABASE_CREATE_CONTACTOS = "CREATE TABLE "+TABLE_CONTACTOS+" (_id integer primary key autoincrement, nombre text, imagen text, bloqueado integer, silenciado integer);";
    private static final String DATABASE_CREATE_MENSAJES = "CREATE TABLE "+TABLE_MENSAJES+
            " (_id integer primary key autoincrement, mensaje text, date text, tipo text, hora text, dia text, frecuencia text, contacto_id integer, FOREIGN KEY(contacto_id) REFERENCES contactos(_id));";

    private static final String DATABASE_DROP_CONTACTOS = "DROP TABLE IF EXISTS "+TABLE_CONTACTOS+";";
    private static final String DATABASE_DROP_MENSAJES = "DROP TABLE IF EXISTS "+TABLE_MENSAJES+";";

    private static final String DROP_DATABASE="";

    // Contexto de la aplicación que usa la base de datos
    private final Context context;
    // Clase SQLiteOpenHelper para crear/actualizar la base de datos
    private MyDbHelper dbHelper;
    // Instancia de la base de datos
    private SQLiteDatabase db;

    public DBAdapter(Context c) {
        context = c;
        dbHelper = new MyDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        try{
            db = dbHelper.getWritableDatabase();
        }catch(SQLiteException e){
            db = dbHelper.getReadableDatabase();
        }
    }

    public void insertarContacto(String nom, String img){
        //Creamos un nuevo registro de valores a insertar
        ContentValues newValues = new ContentValues();
        //Asignamos los valores de cada campo
        newValues.put(NOMBRE,nom);
        newValues.put(IMAGEN,img);
        db.insert(TABLE_CONTACTOS,null,newValues);
    }
    public void insertarMensaje(String m, String dt, String tip, String h, String d, String f){
        //Creamos un nuevo registro de valores a insertar
        ContentValues newValues = new ContentValues();
        //Asignamos los valores de cada campo
        newValues.put(MENSAJE,m);
        newValues.put(DATE_TIME,dt);
        newValues.put(TIPO,tip);
        newValues.put(HORA,h);
        newValues.put(DIA,d);
        newValues.put(FRECUENCIA,f);
        db.insert(TABLE_MENSAJES,null,newValues);
    }

    public void eliminarContactoPorId(String id){
        ContentValues newValues = new ContentValues();
        int n=db.delete(TABLE_CONTACTOS,"_id=?",new String[]{id});
        Toast.makeText(this.context,n+" registros eliminados",Toast.LENGTH_SHORT).show();
    }
    public void eliminarContactoPorNombre(String nom){
        ContentValues newValues = new ContentValues();
        int n=db.delete(TABLE_CONTACTOS,"nombre LIKE ?",new String[]{nom});
        Toast.makeText(this.context,n+" registros eliminados",Toast.LENGTH_SHORT).show();
    }
    public void eliminarMensajes(String[] id){
        ContentValues newValues = new ContentValues();
        int n=db.delete(TABLE_MENSAJES,"_id=?",id);
        Toast.makeText(this.context,n+" registros eliminados",Toast.LENGTH_SHORT).show();
    }

    public void eliminarDB(){
        //context.deleteDatabase(DATABASE_NAME);
        //SQLiteOpenHelp.close();
        if(SQLiteDatabase.deleteDatabase(new File(DATABASE_NAME)))
            Toast.makeText(context,"La base de datos ha sido eliminada correctamente",Toast.LENGTH_SHORT).show();
    }

    private static class MyDbHelper extends SQLiteOpenHelper {

        public MyDbHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_CONTACTOS);
            db.execSQL(DATABASE_CREATE_MENSAJES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DATABASE_DROP_CONTACTOS);
            db.execSQL(DATABASE_DROP_MENSAJES);
            onCreate(db);
        }
    }
}
