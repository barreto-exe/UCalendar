package com.teamihc.ucalendar.backend.entidades;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamihc.ucalendar.activities.MainActivity;
import com.teamihc.ucalendar.backend.Herramientas;
import com.teamihc.ucalendar.backend.SolicitudHTTP;
import com.teamihc.ucalendar.backend.basedatos.Configuraciones;
import com.teamihc.ucalendar.backend.entidades.enums.Sexo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Usuario extends Persona
{
    private int idUsuario;
    private String passEncriptada;

    
    public Usuario(int idUsuario, String correo, String passDesencriptada)
    {
        this.idUsuario = idUsuario;
        this.correo = correo;
        //this.passEncriptada = Herramientas.encriptarMd5(passDesencriptada);
        this.passEncriptada = passDesencriptada;
    }
    
    
    public Usuario(String correo, String passDesencriptada)
    {
        this.correo = correo;
        //this.passEncriptada = Herramientas.encriptarMd5(passDesencriptada);
        this.passEncriptada = passDesencriptada;
    }

    /**
     * Valida la combinación de correo y contraseña en la base de datos.
     * @param context objeto en donde se realiza la consulta (de tipo activity, view, etc).
     * @param destino clase del Activity que se abrirá si la combinación es correcta.
     */
    public void validar(Context context, Class<?> destino)
    {
        SolicitudHTTP solicitud = new SolicitudHTTP(context, "validar_usuario")
        {
            @Override
            public void eventoRespuestaHTTP(String response)
            {
                String mensaje;
                if(!response.isEmpty())
                {
                    //Abrir app
                    Intent i = new Intent(context, destino);
                    context.startActivity(i);
        
                    //Recibir info de usuario logueado
                    Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    Usuario u = g.fromJson(response, Usuario.class);
        
                    //Guardar datos de usuario
                    Configuraciones.setCorreoSesion(u.correo);
                    Configuraciones.setIdUsuarioSesion(u.idUsuario);
        
                    mensaje = "Inicio de sesión exitoso.";
                }
                else
                {
                    mensaje = "Usuario y contraseña incorrectos.";
                }
                Toast.makeText(context, mensaje,Toast.LENGTH_SHORT).show();
            }
    
            @Override
            public void eventoRespuestaErrorHTTP()
            {
        
            }
        };
        solicitud.getParametros().put("correo", correo);
        solicitud.getParametros().put("password", passEncriptada);
        solicitud.ejecutar();
    }
}
