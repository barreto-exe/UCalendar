package com.teamihc.ucalendar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.teamihc.ucalendar.R;
import com.teamihc.ucalendar.adapters.FeedRVAdapter;
import com.teamihc.ucalendar.backend.entidades.Evento;

import java.util.ArrayList;

public class FeedFragment extends Fragment implements MuestraEventos
{
    //Views
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    
    //Info
    private FeedRVAdapter adapter;
    public ArrayList<Evento> eventos;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        return view;
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        inicializarComponentes();
    }
    
    @Override
    public void setEventos(ArrayList<Evento> eventos)
    {
        this.eventos = eventos;
        adapter = new FeedRVAdapter(this.eventos);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public void inicializarComponentes()
    {
        //Swipe-refresh
        swipeRefresh = getView().findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener
        (
            new SwipeRefreshLayout.OnRefreshListener()
            {
                @Override
                public void onRefresh()
                {
                    actualizarEventosMostrados();
                }
            }
        );
    
        //Recycler
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);
        
        eventos = new ArrayList<>();
        adapter = new FeedRVAdapter(eventos);
        recyclerView.setAdapter(adapter);
        
        //Aquí se inicializa ArrayList eventos
        Evento.obtenerEventos(getActivity(), this, false);
    }
    
    @Override
    public void actualizarEventosMostrados()
    {
        //Mostrar cargar por 1seg
        Thread t = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                swipeRefresh.setRefreshing(false);
            }
        });
        t.start();
        
        Evento.obtenerEventos(getActivity(), this, false);
    }
}