package com.example.prova.inviare.db_adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.prova.inviare.elementos.Contacto;
import com.example.prova.inviare.elementos.Mensaje;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 2dam on 17/01/2017.
 */

public class DBAdapter {
    private static final String DATABASE_NAME = "db_inviare.db";
    //private static final String TABLE_PERFIL = "perfil"; // ID - NOMBRE - TELEFONO - EMAIL - ESTADO - IMAGE
    public static final String TABLE_CONTACTOS = "contactos"; // ID - NOMBRE - IMAGEN - BLOQUEADO_SILENCIADO (0,1[silenciado],2[bloqueado])
    public static final String TABLE_MENSAJES = "mensajes"; // ID_CONTACTOS <-> ID - MENSAJE - DATETIME - TIPO - HORA - DIA - FRECUENCIA
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
    public static final String ID_CONTACTO = "contacto_id";

    public static final String TIPO_TEXTO = "text";

    private static final String DATABASE_CREATE_CONTACTOS = "CREATE TABLE "+TABLE_CONTACTOS+" (_id integer primary key autoincrement, nombre text, estado text, imagen text, bloqueado_silenciado integer);";
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

    public void seleccionarMensaje(ArrayList<Mensaje> arrayElementos, int valor, String columna, String tabla){
        String selectQuery = "SELECT * FROM "+tabla+" WHERE "+columna+" = "+valor+";";
        Cursor cursor= db.rawQuery(selectQuery, null);

        boolean propietario;
        if(cursor.moveToFirst()){
            do {
                //Se crea un objeto 'Elemento' con los datos de la DB recogidos por el cursor
                Log.d("aaaa",""+cursor.getInt(7));
                arrayElementos.add(new Mensaje(cursor.getString(1),cursor.getString(2),true));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void seleccionar(ArrayList<Mensaje> arrayElementos, String valor, String columna, String tabla){
        String selectQuery = "SELECT * FROM "+tabla+" WHERE "+columna+" LIKE '"+valor+"';";
        Cursor cursor= db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                //Se crea un objeto 'Elemento' con los datos de la DB recogidos por el cursor
                arrayElementos.add(new Mensaje(cursor.getString(1),cursor.getString(2),true));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void insertarContacto(String nom, String estado, String img){
        String selectQuery = "SELECT * FROM contactos;";
        StringBuilder insertQuery = new StringBuilder("INSERT INTO contactos (nombre, estado, imagen, bloqueado_silenciado) VALUES ");
        Cursor cursor= db.rawQuery(selectQuery, null);
        //Eliminamos la tabla contactos
        db.delete(TABLE_CONTACTOS,null,null);
        //Introducimos los valores de nuevo
        insertQuery.append("('");
        insertQuery.append(nom);//Nombre del contacto
        insertQuery.append("', '");
        insertQuery.append(estado);//Nombre del contacto
        insertQuery.append("', '");
        insertQuery.append(img);//Imagen del contacto
        insertQuery.append("', '");
        insertQuery.append("0");//No esta bloqueado al añadirlo
        insertQuery.append("', '),");
        if(cursor.moveToFirst()){
            int i=0;
            do {
                //Se añaden los datos del cursor a la consulta insert "InsertQuery"
                insertQuery.append("('");
                insertQuery.append(cursor.getString(1));
                insertQuery.append("', '");
                insertQuery.append(cursor.getString(2));
                insertQuery.append("', '");
                insertQuery.append(cursor.getString(3));
                insertQuery.append("', '");
                insertQuery.append(cursor.getString(4));
                insertQuery.append("', '),");
            } while (cursor.moveToNext());
            insertQuery.deleteCharAt(insertQuery.length()-1);
        }
        cursor.close();
        db.execSQL(insertQuery.toString());
    }
    public void insertarMensaje(String m, String dt, String tip, String h, String d, String f, int contacto_id){
        //Creamos un nuevo registro de valores a insertar
        ContentValues newValues = new ContentValues();
        //Asignamos los valores de cada campo
        newValues.put(MENSAJE,m);
        newValues.put(DATE_TIME,dt);
        newValues.put(TIPO,tip);
        newValues.put(HORA,h);
        newValues.put(DIA,d);
        newValues.put(FRECUENCIA,f);
        newValues.put(ID_CONTACTO,contacto_id);
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

    public void eliminarMensajesPorContacto(int contacto_id){
        ContentValues newValues = new ContentValues();
        int n=db.delete(TABLE_MENSAJES,"contacto_id="+contacto_id,null);
        Toast.makeText(this.context,n+" mensajes eliminados.",Toast.LENGTH_SHORT).show();
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
