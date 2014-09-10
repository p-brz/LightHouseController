package com.example.lighthousecontroller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import com.androidplot.xy.XYSeries;

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
			addSerie(sourceId);
		}
		this.consumptionEventsBySource.get(sourceId).add(event);
		graphSeries.get(sourceId).addLast(event.getTimestamp(), event.getConsumption());
	}

	private void addSerie(long sourceId) {
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
//		Number[] series1Numbers = {50, 10, 0, 20, 30,30};
//        Number[] timestamps     = {
//        						getTimestamp(9,9,2014,10,15,20), 
//    							getTimestamp(9,9,2014,10,20,35),
//    							getTimestamp(9,9,2014,10,40,0),
//    							getTimestamp(9,9,2014,11,6,0),
//    							getTimestamp(9,9,2014,11,35,20),
//    							getTimestamp(9,9,2014,11,70,40)};
////        Number[] series2Numbers = {4, 6, 3, 8, 2, 10};
// 
//        // Turn the above arrays into XYSeries':
//        XYSeries series1 = new SimpleXYSeries(
//        		Arrays.asList(timestamps),          // SimpleXYSeries takes a List so turn our array into a List
//        		Arrays.asList(series1Numbers), // Y_VALS_ONLY means use the element index as the x value
//                "Series1");                             // Set the display title of the series
// 
//        // Create a formatter to use for drawing a series using LineAndPointRenderer
//        // and configure it from xml:
//        LineAndPointFormatter series1Format = new LineAndPointFormatter();
//        series1Format.setPointLabelFormatter(new PointLabelFormatter());
//        series1Format.configure(getActivity().getApplicationContext(), R.xml.consumption_graph_line_formatter);
// 
//        // add a new series' to the xyplot:
//        consumptionGraph.addSeries(series1, series1Format);
        
        consumptionGraph.setRangeValueFormat(new DecimalFormat());
        consumptionGraph.setDomainValueFormat(new TimestampPlotFormat());
        
        // reduce the number of range labels
        consumptionGraph.setTicksPerRangeLabel(3);
	}


	private Number getTimestamp(int day, int month, int year, int hourOfDay, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, day, hourOfDay, minute, second);
		return calendar.getTimeInMillis();
	}
}
