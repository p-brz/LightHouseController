package com.example.lighthousecontroller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

public class ConsumptionGraphFragment extends Fragment{
	private XYPlot consumptionGraph;
	private final Map<Long, List<ConsumptionEvent> > consumptionEventsBySource;
	private final Map<Long, SimpleXYSeries > graphSeries;
	
	private boolean viewsReady;
	
	public ConsumptionGraphFragment() {
		consumptionEventsBySource = new HashMap<>();
		graphSeries = new HashMap<>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_consumption_graph,
				container, false);
		
		setupViews(rootView);
		
		return rootView;
	}

	private void setupViews(View rootView) {
        consumptionGraph = (XYPlot) rootView.findViewById(R.id.consumptionGraph_graph);
        setupGraph();
		viewsReady = true;
	}

	public void clearGraph(){
		//TODO: not implemented yet
	}
	public void addConsumptionHistory(List<ConsumptionEvent> list) {
		if(list != null){
			addEvents(list);
		}
		if(viewsReady){
			setupGraph();
		}
	}

	public void plotConsumption(ConsumptionEvent event) {
		addConsumptionEvent(event);
		redraw();
	}

	private void addEvents(List<ConsumptionEvent> list) {
		for(ConsumptionEvent event : list){
			addConsumptionEvent(event);
		}

		redraw();
	}

	private void redraw() {
		if(viewsReady){
			consumptionGraph.redraw();
		}
	}

	private void addConsumptionEvent(ConsumptionEvent event) {
		long sourceId = event.getSourceId();
		if(!consumptionEventsBySource.containsKey(sourceId)){
			consumptionEventsBySource.put(sourceId, new ArrayList<ConsumptionEvent>());
		}
		this.consumptionEventsBySource.get(sourceId).add(event);
		if(viewsReady){
			if(!graphSeries.containsKey(sourceId)){
				addSerie(sourceId);
			}
			graphSeries.get(sourceId).addLast(event.getTimestamp(), event.getConsumption());
		}
	}

	private void addSerie(long sourceId) {
		if(!viewsReady){
			return;
		}
		
		SimpleXYSeries xySeries = new SimpleXYSeries(String.valueOf(sourceId));
		graphSeries.put(sourceId, xySeries);
		
		//Formato da série
        LineAndPointFormatter formatter = new LineAndPointFormatter();
        formatter.setPointLabelFormatter(new PointLabelFormatter());

		//TODO: definir configurações dinâmicas
        formatter.configure(getActivity().getApplicationContext(), R.xml.consumption_graph_line_formatter);
		
        if(viewsReady){
        	consumptionGraph.addSeries(xySeries, formatter);
        }
	}
	


	private void setupGraph() {        
        consumptionGraph.setRangeValueFormat(new DecimalFormat());
        consumptionGraph.setDomainValueFormat(new TimestampPlotFormat());
        
        // reduce the number of range labels
        consumptionGraph.setTicksPerRangeLabel(3);
	}
}
