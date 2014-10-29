package com.example.lighthousecontroller.view;

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
import com.androidplot.xy.XYStepMode;
import com.example.lighthousecontroller.R;
import com.example.lighthousecontroller.model.ConsumptionEvent;

public class ConsumptionGraphFragment extends Fragment{
	private XYPlot consumptionGraph;
	private final Map<Long, List<ConsumptionEvent> > consumptionEventsBySource;
	private final Map<Long, SimpleXYSeries > graphSeries;
	
	private boolean viewsReady;
	
	private TimestampPlotFormat domainPlotFormat;
	private long timeRangeInMilliseconds;
	private int domainDivisions;
	/** Indica que cada evento de consumo recebido representa uma mudança no estado do valor de consumo.
	 * Ou seja, que até o momento anterior, o valor de consumo era o mesmo indicado pelo último evento 
	 * de consumo.*/
	private boolean eventIsChange = true;
	
	public ConsumptionGraphFragment() {
		consumptionEventsBySource = new HashMap<>();
		graphSeries = new HashMap<>();
		
		domainPlotFormat = new TimestampPlotFormat();
		
		timeRangeInMilliseconds = 60000; 
		domainDivisions = 10;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_consumption_graph,
				container, false);
		
		setupViews(rootView);
		
		return rootView;
	}	
	
	public long getTimeRangeInMilliseconds() {
		return timeRangeInMilliseconds;
	}

	public void setTimeRangeInMilliseconds(long timeRangeInMilliseconds) {
		this.timeRangeInMilliseconds = timeRangeInMilliseconds;
	}
	public int getDomainDivisions() {
		return domainDivisions;
	}

	public void setDomainDivisions(int domainDivisions) {
		this.domainDivisions = domainDivisions;
	}

	public void clearGraph(){
		for(SimpleXYSeries serie: graphSeries.values()){
			this.consumptionGraph.removeSeries(serie);
		}
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
	
	

	private void setupViews(View rootView) {
        consumptionGraph = (XYPlot) rootView.findViewById(R.id.consumptionGraph_graph);
        setupGraph();
		viewsReady = true;
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
			
			SimpleXYSeries serie = graphSeries.get(sourceId);
			if(eventIsChange && serie.size() > 0){
				double lastConsumption = serie.getY(serie.size()-1).doubleValue();
				serie.addLast(event.getTimestamp()-1, lastConsumption);
			}
			serie.addLast(event.getTimestamp(), event.getConsumption());
//			if(graphSeries.get(sourceId).size() > this.maxRangeSize){
//				graphSeries.get(sourceId).removeFirst();
//			}
			removeGraphsByTimeRange(event.getTimestamp());
		}
	}

	private void removeGraphsByTimeRange(long lastTimestamp) {
		boolean moreGraphsOutRange = true;
		do
		{
			moreGraphsOutRange = false;
			for(SimpleXYSeries serie: graphSeries.values()){
				if(serie.size() > 0){
					//moreGraphsOutRange só será falso se todos retornarem falso
					moreGraphsOutRange = moreGraphsOutRange || removeLastSeriePointIfOutdated(lastTimestamp, serie);
				}
			}
		}while(moreGraphsOutRange );
	}

	private boolean removeLastSeriePointIfOutdated(long lastTimestamp, SimpleXYSeries serie) {
		Number timestamp = serie.getX(0);
		if(lastTimestamp - timestamp.longValue() > timeRangeInMilliseconds){
			Number nextTimestamp = serie.getX(0);
			serie.removeFirst();
			if(lastTimestamp - nextTimestamp.longValue() < timeRangeInMilliseconds){
				//Próximo passo é maior que ponto anterior, cria ponto médio:
				addMediumSeriePoint(lastTimestamp, serie);
				return false;
			}
			else{
				return true;
			}
		}
		return false;
	}

	private void addMediumSeriePoint(long lastTimestamp, SimpleXYSeries serie) {
		Number nextTimestamp = serie.getX(0);
		long boundaryTimestep = lastTimestamp - timeRangeInMilliseconds;
		double boundaryValue = serie.getY(0).doubleValue() * (boundaryTimestep/nextTimestamp.doubleValue());
		
		serie.addFirst(boundaryTimestep, boundaryValue);
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
        consumptionGraph.setDomainValueFormat(this.domainPlotFormat);
        
        consumptionGraph.setDomainStep(XYStepMode.SUBDIVIDE, domainDivisions);
        
        // reduce the number of range labels
        consumptionGraph.setTicksPerRangeLabel(3);
	}
}
