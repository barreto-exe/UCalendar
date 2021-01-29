package com.teamihc.ucalendar.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.teamihc.ucalendar.R;
import com.teamihc.ucalendar.adapters.CalendarioRVAdapter;
import com.teamihc.ucalendar.adapters.FeedRVAdapter;
import com.teamihc.ucalendar.backend.Herramientas;
import com.teamihc.ucalendar.backend.entidades.Evento;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class CalendarioFragment extends Fragment implements MuestraEventos
{
    private CompactCalendarView calendarView;
    private TextView txtMesActual, calendarioIzquierda, calendarioDerecha;
    private RecyclerView recyclerView;
    private CalendarioRVAdapter adapter;
    public ArrayList<Evento> eventos;
    
    public void setEventos(ArrayList<Evento> eventos)
    {
        this.eventos = eventos;
        adapter = new CalendarioRVAdapter(this.eventos);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
        for(Evento e : eventos)
        {
            //Agregar puntito en el calendario con su respectivo color
            calendarView.addEvent(new Event(Color.parseColor(e.getColor()), e.getFechaInicio().getTime()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        return view;
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        inicializarComponentes();
    }
    
    private void inicializarComponentes()
    {
        //Datos calendario
        txtMesActual = getView().findViewById(R.id.txtMesActual);
        calendarView = getView().findViewById(R.id.calendarView);
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setUseThreeLetterAbbreviation(true);
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener()
        {
            @Override
            public void onDayClick(Date dateClicked)
            {
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth)
            {
                //Actualizar nombre de mes y año
                txtMesActual.setText(Herramientas.formatearMesYearCalendario(firstDayOfNewMonth));
            }
        });
    
        //Botón izquierdo calendario
        calendarioIzquierda = getView().findViewById(R.id.calendarioIzquierda);
        calendarioIzquierda.setText("<");
        calendarioIzquierda.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendarView.scrollLeft();
            }
        });
        //Botón derecho calendario
        calendarioDerecha = getView().findViewById(R.id.calendarioDerecha);
        calendarioDerecha.setText(">");
        calendarioDerecha.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendarView.scrollRight();
            }
        });
        
        //Colocar nombre de mes y año actual
        txtMesActual.setText(Herramientas.formatearMesYearCalendario(calendarView.getFirstDayOfCurrentMonth()));
        
        //RecyclerView
        recyclerView = getView().findViewById(R.id.eventosDelmes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);

        //Datos Recycler
        eventos = new ArrayList<>();
        adapter = new CalendarioRVAdapter(eventos);
        recyclerView.setAdapter(adapter);
    
        //Aquí se inicializa ArrayList eventos
        Evento.obtenerEventos(getActivity(), this, false);
    }
}