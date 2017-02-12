package com.example.prova.inviare.db_adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.prova.inviare.R;
import com.example.prova.inviare.elementos.Alarma;
import com.example.prova.inviare.elementos.Contacto;
import com.example.prova.inviare.elementos.Mensaje;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBAdapter {
    private static final String DATABASE_NAME = "db_inviare.db";
    //private static final String TABLE_PERFIL = "perfil"; // ID - NOMBRE - TELEFONO - EMAIL - ESTADO - IMAGE
    public static final String TABLE_CONTACTOS = "contactos"; // _ID - NOMBRE - ESTADO - IMAGEN - PERMISOS (0,1[silenciado],2[bloqueado])
    public static final String TABLE_MENSAJES = "mensajes"; //  _ID - MENSAJE - DATETIME - TIPO - HORA_INICIO - HORA - DIA - FRECUENCIA - ID_CONTACTO
    private static final int DATABASE_VERSION = 1;

    public static final String ID = "_id";
    private static final String NOMBRE = "nombre";
    private static final String TELEFONO = "telefono";
    private static final String EMAIL = "email";
    private static final String ESTADO  = "estado";
    private static final String IMAGEN = "imagen";
    private static final String PERMISOS = "permisos";

    private static final String MENSAJE = "mensaje";
    private static final String DATE_TIME = "fecha";
    private static final String TIPO = "tipo";
    private static final String HORA_INICIO = "hora_inicio";
    private static final String HORA_DURACION = "hora_duracion";
    private static final String FRECUENCIA = "frecuencia";
    private static final String CURSO_TAREA = "curso_tarea";
    public static final String ID_CONTACTO = "contacto_id";

    public static final int TIPO_TEXTO = 0; //Texto
    public static final int TIPO_FANTASMA = 1; //Fecha sin texto
    public static final int TIPO_ALARMA_REPETITIVA = 2; //Alarma repetitiva
    public static final int TIPO_ALARMA_PERSISTENTE = 3; //Alarma persistente
    public static final int TIPO_ALARMA_FIJA = 4; //Alarma fija

    public static final int PERMISOS_TODOS = 0; // Contacto sin restricciones
    public static final int PERMISOS_SILENCIADO = 1; // Contacto silenciado (Sin sonido al recibir notificación)
    public static final int PERMISOS_BLOQUEADO = 2; // Contacto bloqueado
    public static final int PERMISOS_ELIMINADO = 3; // Contacto eliminado

    private static final String DATABASE_CREATE_CONTACTOS = "CREATE TABLE "+TABLE_CONTACTOS+" (_id integer primary key AUTOINCREMENT, nombre text, estado text, imagen text, permisos integer);";
    private static final String DATABASE_CREATE_MENSAJES = "CREATE TABLE "+TABLE_MENSAJES+
            " (_id integer primary key AUTOINCREMENT, mensaje text, fecha text, tipo integer, hora_inicio text, hora_duracion text, frecuencia text, curso_tarea text, contacto_id integer, FOREIGN KEY(contacto_id) REFERENCES contactos(_id));";

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

    public void seleccionarMensaje(ArrayList<Object> arrayElementos, int valor, String columna, String tabla){
        //Se introducen los mensajes de la base de datos en el arrayElementos
        String selectQuery = "SELECT * FROM "+tabla+" WHERE "+columna+" = "+valor+";";
        Cursor cursor= db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                //Se crea un objeto 'Elemento' con los datos de la DB recogidos por el cursor
                SimpleDateFormat df = new SimpleDateFormat(context.getResources().getString(R.string.simple_date_format_DB), java.util.Locale.getDefault());
                SimpleDateFormat df2 = new SimpleDateFormat(context.getResources().getString(R.string.simple_date_format_MENSAJE), java.util.Locale.getDefault());
                Date dateSegundos=null;
                try {
                    dateSegundos = df.parse(cursor.getString(2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Se averigua si el mensaje es del propietario y el tipo de mensaje
                final boolean mensajePropietario = cursor.getInt(9) == context.getResources().getInteger(R.integer.id_propietario);
                final String fechaFinal=df2.format(dateSegundos);
                if(cursor.getInt(3)==TIPO_TEXTO){
                    arrayElementos.add(new Mensaje(cursor.getString(1),fechaFinal,mensajePropietario));
                }else if(cursor.getInt(3)==TIPO_ALARMA_REPETITIVA || cursor.getInt(3)==TIPO_ALARMA_PERSISTENTE){
                    arrayElementos.add(new Alarma(cursor.getString(1),fechaFinal,cursor.getInt(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),mensajePropietario));
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    /**
     * Retorna en un int los permisos de un contacto haciendo una consulta de tipo
     * "SELECT permisos FROM tabla WHERE 'columna'='valor';" o -1 si la consulta no devuelve ningún contacto.
     * @param valor valor de la columna
     * @param columna nombre de una columna que contenga un valor numérico
     * @return int Permiso del Contacto seleccionado
     * <p>PERMISOS_TODOS = 0<br>
     * PERMISOS_SILENCIADO = 1<br>
     * PERMISOS_BLOQUEADO = 2<br>
     * PERMISOS_ELIMINADO = 3</p>
     */
    public int seleccionarPermisoContacto(int valor, String columna){
        String selectQuery = "SELECT "+PERMISOS+" FROM "+TABLE_CONTACTOS+" WHERE "+columna+" = "+valor+";";
        Cursor cursor= db.rawQuery(selectQuery, null);
        //Se asigna
        int permisosContacto=-1;
        if(cursor.moveToFirst()){
            //Se guarda el valor int de la consulta en la variable permisosContacto
            permisosContacto = cursor.getInt(0);
        }
        cursor.close();
        return permisosContacto;
    }

    /**
     * Retorna en un String la fecha del último mensaje del Contacto seleccionado mediante una consulta
     * de tipo "SELECT MAX(fecha) FROM mensajes WHERE columna = 'valor';". Si no hay ningún mensaje este método retornará null.
     * @param valor valor de la cláusula WHERE
     * @param columna columna a seleccionar de la tabla 'mensajes'
     * @param formatoFecha formato de la fecha en string (Ej.: "dd MMMM yyyy, HH:mm")
     * @return String - Fecha del último mensaje recibido
     */
    public String seleccionarUltimaFechaContacto(int valor, String columna, String formatoFecha){
        //Se introducen los contactos de la base de datos en el arrayElementos y se intercala Contacto si no es null
        String ultimaFecha=null;
        String selectQuery = "SELECT MAX(fecha) FROM "+TABLE_MENSAJES+" WHERE "+columna+" = "+valor+";";
        Cursor cursor= db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            SimpleDateFormat df = new SimpleDateFormat(context.getResources().getString(R.string.simple_date_format_DB), java.util.Locale.getDefault());
            SimpleDateFormat df2 = new SimpleDateFormat(formatoFecha, java.util.Locale.getDefault());
            Date dateSegundos;
            final String parsearFecha=cursor.getString(0);
            if(parsearFecha!=null) {
                try {
                    dateSegundos = df.parse(parsearFecha);
                    ultimaFecha = df2.format(dateSegundos);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        return ultimaFecha;
    }

    public void seleccionarContactos(ArrayList<Contacto> arrayElementos, String valor, String columna, String tabla){
        //Se introducen los contactos de la base de datos en el arrayElementos y se intercala Contacto si no es null
        String selectQuery = "SELECT c.nombre,c.estado,c.imagen,m.fecha FROM contactos c INNER JOIN mensajes m ON c._id=m.contacto_id GROUP BY c._id ORDER BY m.fecha;";
        Cursor cursor= db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                //Se crea un objeto 'Elemento' con los datos de la DB recogidos por el cursor
                arrayElementos.add(new Contacto(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),null));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void insertarContacto(String nom, String estado, String img){
        //Creamos un nuevo registro de valores a insertar
        ContentValues newValues = new ContentValues();
        //Asignamos los valores de cada campo
        newValues.put(NOMBRE,nom);//Nombre del contacto
        newValues.put(ESTADO,estado);//Estado del contacto
        newValues.put(IMAGEN,img);//Imagen del contacto
        newValues.put(PERMISOS,0);//Permisos ([0] por defecto porque no esta bloqueado ni silenciado al añadirlo)
        //Ejecutamos el INSERT
        db.insert(TABLE_MENSAJES,null,newValues);
    }

    /*public void insertarContacto(String nom, String estado, String img){
        String selectQuery = "SELECT * FROM contactos;";
        StringBuilder insertQuery = new StringBuilder("INSERT INTO contactos (nombre, estado, imagen, pwermisos) VALUES ");
        Cursor cursor= db.rawQuery(selectQuery, null);
        //Eliminamos la tabla contactos
        db.delete(TABLE_CONTACTOS,null,null);
        //Introducimos los valores de nuevo
        insertQuery.append("('");
        insertQuery.append(nom);//Nombre del contacto
        insertQuery.append("', '");
        insertQuery.append(estado);//Estado del contacto
        insertQuery.append("', '");
        insertQuery.append(img);//Imagen del contacto
        insertQuery.append("', '");
        insertQuery.append("0");//Permisos (no esta bloqueado al añadirlo)
        insertQuery.append("', '),");
        if(cursor.moveToFirst()){
            do {
                //Se añaden los datos del cursor anterior a la consulta insert "InsertQuery"
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
    }*/
    public void insertarMensaje(String m, String dt, int tipo, String h_i, String h_d, String f, String ct, int contacto_id){
        //Creamos un nuevo registro de valores a insertar
        ContentValues newValues = new ContentValues();
        //Asignamos los valores de cada campo
        newValues.put(MENSAJE,m);
        newValues.put(DATE_TIME,dt);
        newValues.put(TIPO,tipo);
        newValues.put(HORA_INICIO,h_i);
        newValues.put(HORA_DURACION,h_d);
        newValues.put(FRECUENCIA,f);
        newValues.put(CURSO_TAREA,ct);
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
        if(context.deleteDatabase(DATABASE_NAME))
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
