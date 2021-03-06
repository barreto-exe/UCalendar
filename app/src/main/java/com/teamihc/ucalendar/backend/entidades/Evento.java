package com.teamihc.ucalendar.backend.entidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamihc.ucalendar.R;
import com.teamihc.ucalendar.backend.Herramientas;
import com.teamihc.ucalendar.backend.Notificaciones;
import com.teamihc.ucalendar.backend.SolicitudHTTP;
import com.teamihc.ucalendar.backend.basedatos.Configuraciones;
import com.teamihc.ucalendar.backend.basedatos.DBMatriz;
import com.teamihc.ucalendar.backend.basedatos.SqliteOp;
import com.teamihc.ucalendar.fragments.MuestraEventos;
import com.teamihc.ucalendar.notificaciones.AlarmCreator;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Evento implements Serializable
{
    private int posicionLista;
    private int idEvento;
    private String nombre;
    private String descripcion;
    private int cantidadLikes;
    private int cantidadGuardados;
    private Date fechaInicio;
    private Date fechaFinal;
    private String lugar;
    private String color;
    private String foto;
    private String fotoCreador;
    private String nombreCreador;
    private Boolean tieneLike, tieneGuardado;
    
    public Evento(int idEvento, String nombre, String descripcion, int cantidadLikes, int cantidadGuardados, Date fechaInicio, Date fechaFinal, String lugar, String color, String foto, String fotoCreador, String nombreCreador, Boolean tieneLike, Boolean tieneGuardado)
    {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidadLikes = cantidadLikes;
        this.cantidadGuardados = cantidadGuardados;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.lugar = lugar;
        this.color = color;
        this.foto = foto;
        this.fotoCreador = fotoCreador;
        this.nombreCreador = nombreCreador;
        this.tieneLike = tieneLike;
        this.tieneGuardado = tieneGuardado;
    }
    
    public Evento()
    {
    }
    
    //<editor-fold desc="Getters & Setters">
    public Boolean getTieneLike()
    {
        return tieneLike;
    }
    
    public Boolean getTieneGuardado()
    {
        return tieneGuardado;
    }
    
    public int getPosicionLista()
    {
        return posicionLista;
    }
    
    public void setPosicionLista(int posicionLista)
    {
        this.posicionLista = posicionLista;
    }
    
    public int getIdEvento()
    {
        return idEvento;
    }
    
    public void setIdEvento(int idEvento)
    {
        this.idEvento = idEvento;
    }
    
    public String getNombre()
    {
        return nombre;
    }
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
    public String getDescripcion()
    {
        return descripcion;
    }
    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }
    public Date getFechaInicio()
    {
        return fechaInicio;
    }
    public void setFechaInicio(Date fechaInicio)
    {
        this.fechaInicio = fechaInicio;
    }
    public Date getFechaFinal()
    {
        return fechaFinal;
    }
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }
    public String getLugar()
    {
        return lugar;
    }
    public void setLugar(String lugar)
    {
        this.lugar = lugar;
    }
    public String getColor()
    {
        return color;
    }
    public void setColor(String color)
    {
        this.color = color;
    }
    public int getCantidadLikes()
    {
        return cantidadLikes;
    }
    public void setCantidadLikes(int cantidadLikes)
    {
        this.cantidadLikes = cantidadLikes;
    }
    public String getFoto()
    {
        return foto;
    }
    public void setFoto(String foto)
    {
        this.foto = foto;
    }
    public int getCantidadGuardados()
    {
        return cantidadGuardados;
    }
    public void setCantidadGuardados(int cantidadGuardados)
    {
        this.cantidadGuardados = cantidadGuardados;
    }
    public String getFotoCreador()
    {
        return fotoCreador;
    }
    public void setFotoCreador(String fotoCreador)
    {
        this.fotoCreador = fotoCreador;
    }
    public String getNombreCreador()
    {
        return nombreCreador;
    }
    public void setNombreCreador(String nombreCreador)
    {
        this.nombreCreador = nombreCreador;
    }
    //</editor-fold>
    
    public void toggleLike(Context context)
    {
        tieneLike ^= true;
        
        String servicio;
        if(tieneLike)
        {
            cantidadLikes++;
            servicio = "recibir_like";
        }
        else
        {
            cantidadLikes--;
            servicio = "recibir_like_borrado";
        }
    
        SolicitudHTTP solicitud = new SolicitudHTTP(context, servicio)
        {
            @Override
            public void eventoRespuestaHTTP(String response)
            {
                //No hacer nada
            }
    
            @Override
            public void eventoRespuestaErrorHTTP()
            {
        
            }
    
            @Override
            public void eventoPostRespuesta()
            {
        
            }
        };
        solicitud.getParametros().put("idUsuario", Configuraciones.getIdUsuarioSesion() + "");
        solicitud.getParametros().put("idEvento", idEvento + "");
        solicitud.ejecutar();
    }
    
    public void toggleGuardar(Context context)
    {
        tieneGuardado ^= true;
        String servicio;
        
        if(tieneGuardado)
        {
            cantidadGuardados++;
            servicio = "recibir_guardar";
            guardarInteres();
            
            crearNotificacion(context);
        }
        else
        {
            cantidadGuardados--;
            servicio = "recibir_guardar_borrado";
            eliminarGuardado();
        }
        
        
        SolicitudHTTP solicitud = new SolicitudHTTP(context, servicio);
        solicitud.getParametros().put("idUsuario", Configuraciones.getIdUsuarioSesion() + "");
        solicitud.getParametros().put("idEvento", idEvento + "");
        solicitud.ejecutar();
    }
    
    private void guardarInteres()
    {
        registrarBBDD();
    
        String query =
            "UPDATE eventos SET tieneGuardado = 1 WHERE id_evento = ?";
        SqliteOp op = new SqliteOp(query);
        op.pasarParametro(idEvento);
        op.ejecutar();
    }
    private void eliminarGuardado()
    {
        String query =
            "UPDATE eventos SET tieneGuardado = 0 WHERE id_evento = ?";
        SqliteOp op = new SqliteOp(query);
        op.pasarParametro(idEvento);
        op.ejecutar();
    }
    
    public void registrarBBDD()
    {
        String query =
            "INSERT INTO eventos " +
            "(id_evento, nombre, descripcion, fecha_inicio, fecha_final, lugar, color, " +
            "cantidad_likes, cantidad_guardados, foto, fotoCreador, nombreCreador, tieneLike, tieneGuardado) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
        SqliteOp op = new SqliteOp(query);
        op.pasarParametro(idEvento);
        op.pasarParametro(nombre);
        op.pasarParametro(descripcion);
        op.pasarParametro(Herramientas.formatearFechaTiempoBBDD(fechaInicio));
        op.pasarParametro(Herramientas.formatearFechaTiempoBBDD(fechaFinal));
        op.pasarParametro(lugar);
        op.pasarParametro(color);
        op.pasarParametro(cantidadLikes);
        op.pasarParametro(cantidadGuardados);
        op.pasarParametro(foto);
        op.pasarParametro(fotoCreador);
        op.pasarParametro(nombreCreador);
        op.pasarParametro(tieneLike? 1:0);
        op.pasarParametro(tieneGuardado? 1:0);
        op.ejecutar();
    }
    public void eliminarBBDD()
    {
        String query = "DELETE FROM eventos WHERE id_evento = ?";
        SqliteOp op = new SqliteOp(query);
        op.pasarParametro(idEvento);
        op.ejecutar();
    }
    
    public void actualizarBBDD()
    {
        eliminarBBDD();
        registrarBBDD();
    }
    private static void borrarEventosBBDD()
    {
        String query = "DELETE FROM eventos";
        SqliteOp op = new SqliteOp(query);
        op.ejecutar();
    }
    public void crearNotificacion(Context context)
    {
        Toast.makeText(context, "Recibirás un recordatorio 10 minutos antes de este evento.", Toast.LENGTH_LONG).show();
    
        /*Calendar c = Calendar.getInstance();
        //c.setTime(getFechaInicio());
        c.add(Calendar.SECOND, 10);
        
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = settings.edit();
        //SAVE ALARM TIME TO USE IT IN CASE OF REBOOT
        edit.putInt("alarmID", getIdEvento());
        edit.putLong("alarmTime", c.getTimeInMillis());

        edit.commit();

        AlarmCreator.setAlarm(getIdEvento(), c.getTimeInMillis(), Notificaciones.getContext(), this);*/
    }
    
    
    public static void obtenerEventos(Context context, MuestraEventos muestraEventos, Boolean soloGuardados)
    {
        String servicio = "obtener_eventos";
        if(soloGuardados)
        {
            servicio += "_guardados";
        }
        
        SolicitudHTTP solicitud = new SolicitudHTTP(context, servicio)
        {
            @Override
            public void eventoRespuestaHTTP(String respuesta)
            {
                //Recibir arreglo de eventos
                Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                Evento[] test = g.fromJson(respuesta, Evento[].class);
                
                //Convertirlo en lista
                ArrayList<Evento> listaEventos = new ArrayList<>();
                for (Evento evento : test)
                {
                    listaEventos.add(evento);
                    
                    //Refrescar eventos locales
                    evento.actualizarBBDD();
                    
                    //Si tiene guardado, refrescar en BBDD local
                    if(evento.tieneGuardado)
                    {
                        evento.guardarInteres();
                    }
                }
                
                //Actualizar eventos mostrados en el inicio
                muestraEventos.setEventos(listaEventos);
            }
            
            @Override
            public void eventoRespuestaErrorHTTP()
            {
                //No hay conexión, mostrar sólo los guardados offline
                obtenerEventosOffline(muestraEventos, soloGuardados);
            }
        };
        solicitud.getParametros().put("id_usuario_sesion", Configuraciones.getIdUsuarioSesion() + "");
        solicitud.ejecutar();
    }
    
    private static void obtenerEventosOffline(MuestraEventos muestraEventos, Boolean soloGuardados)
    {
        //Realizar consulta en BBDD local
        String query = "SELECT * FROM eventos e ";
        if(soloGuardados)
        {
            query += "WHERE tieneGuardado = 1 ";
        }
        query += "ORDER BY id_evento DESC";
        SqliteOp op = new SqliteOp(query);
        DBMatriz result = op.consultar();
        
        //Leer datos
        ArrayList<Evento> eventos = new ArrayList<>();
        while (result.leer())
        {
            eventos.add(eventoParsear(result));
        }
        
        //Actualizar recycler
        muestraEventos.setEventos(eventos);
    }
    
    public static ArrayList<Date> obtenerFechasEventosGuardados()
    {
        String query =
            "SELECT SUBSTR(fecha_inicio,0,11) AS fecha " +
            "FROM eventos " +
            "WHERE tieneGuardado = 1 " +
            "GROUP BY SUBSTR(fecha_inicio,0,11)";
        SqliteOp op = new SqliteOp(query);
        DBMatriz resultado = op.consultar();
        
        ArrayList<Date> fechas = new ArrayList<>();
        while (resultado.leer())
        {
            try
            {
                Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse((String)resultado.getValor("fecha"));
                Date hoy = Calendar.getInstance().getTime();
                
                //Si fecha agregada es >= que hoy
                if(fecha.after(hoy)
                    || (fecha.getYear() == hoy.getYear() && fecha.getMonth() == hoy.getMonth() &&fecha.getDate() == hoy.getDate()))
                {
                    //Agregar fecha a eventos guardados
                    fechas.add(fecha);
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        return fechas;
    }
    
    public static ArrayList<Evento> obtenerEventosGuardadosPorDia(Date dia)
    {
        String query =
                "SELECT * FROM eventos " +
                "WHERE " +
                "tieneGuardado = 1 AND " +
                "SUBSTR(fecha_inicio,0,11) = ?";
        SqliteOp op = new SqliteOp(query);
        op.pasarParametro(new SimpleDateFormat("yyyy-MM-dd").format(dia));
        DBMatriz resultado = op.consultar();
        
        ArrayList<Evento> eventos = new ArrayList<>();
        while (resultado.leer())
        {
            eventos.add(eventoParsear(resultado));
        }
        
        return eventos;
    }
    
    public static Evento eventoParsear(DBMatriz lectura)
    {
        Evento e = new Evento();
        e.idEvento = (int)lectura.getValor("id_evento");
        e.nombre = (String) lectura.getValor("nombre");
        e.descripcion = (String) lectura.getValor("descripcion");
        e.cantidadLikes = (int) lectura.getValor("cantidad_likes");
        e.cantidadGuardados = (int) lectura.getValor("cantidad_guardados");
        e.fechaInicio = Herramientas.parsearFechaTiempoBBDD((String) lectura.getValor("fecha_inicio"));
        e.fechaFinal = Herramientas.parsearFechaTiempoBBDD((String) lectura.getValor("fecha_final"));
        e.lugar = (String) lectura.getValor("lugar");
        e.color = (String) lectura.getValor("color");
        e.foto = (String) lectura.getValor("foto");
        e.fotoCreador = (String) lectura.getValor("fotoCreador");
        e.nombreCreador = (String) lectura.getValor("nombreCreador");
        e.tieneLike = (int) lectura.getValor("tieneLike") == 1;
        e.tieneGuardado = (int) lectura.getValor("tieneGuardado") == 1;
    
        return e;
    }
    
    @Override
    public String toString()
    {
        return "Evento{" +
                "nombre='" + nombre + '\'' +
                ", fechaInicio=" + fechaInicio +
                '}';
    }
    
}
