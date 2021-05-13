package it.polito.tdp.borders.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	
	BordersDAO dao;
	Map<Integer,Country> idMap;
	SimpleGraph<Country,DefaultEdge> grafo;
	String CountryAndBorders="";
	Map<Country,Country> predecessore;

	public Model() {	
		dao = new BordersDAO();
		idMap = new HashMap<>();
	}
	
	public void creaGrafo(int anno) {
		
		// Aggiungo i vertici	
		dao.loadAllCountries(idMap, anno);
		this.grafo = new SimpleGraph<Country, DefaultEdge>(DefaultEdge.class);
		Graphs.addAllVertices(this.grafo, dao.getVertices(idMap, anno));
		
		// Aggiungo gli archi
		for(Border b : dao.getCountryPairs(idMap, anno)) {
			if(this.grafo.containsVertex(b.getState1()) && this.grafo.containsVertex(b.getState2())) {
				DefaultEdge e = this.grafo.getEdge(b.getState1(), b.getState2());
				if(e == null) {
					Graphs.addEdgeWithVertices(grafo, b.getState1(), b.getState2());
				}
			}
		}
		
		for(Country c : dao.getVertices(idMap, anno)) {
			CountryAndBorders = CountryAndBorders + c.getName() +" " +this.grafo.degreeOf(c) +"\n";
		}
	
	}
	
	public String getVerticesAndEdges() {
		return "Grafo creato con "+this.grafo.vertexSet().size() +" vertici e " +this.grafo.edgeSet().size() +" archi\n";
	}
	
	public String getNumberOfConnectedComponents() {
		ConnectivityInspector<Country, DefaultEdge> ci = new ConnectivityInspector<>(this.grafo);
		return "Numero componenti connesse = " +ci.connectedSets().size()+"\n";
	}
	
	public String getCountryAndBorders() {
		return this.CountryAndBorders;
	}
	
	public Collection<Country> getAllCountries() {
		return this.idMap.values();
	}
	
	// Metodo con BreadthFirstIterator
	
	/**
	 * Fermate raggiungibili tramite BreadthFirstIterator
	 * @param partenza
	 * @return
	 */
	public String fermateRaggiungibiliBFV(Country partenza) {
		if(!this.grafo.containsVertex(partenza)) {
			return "Il vertice selezionato non è presente nel grafo!";
		}
		
		BreadthFirstIterator<Country,DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo,partenza);
		
		predecessore = new HashMap<Country,Country>();
		predecessore.put(partenza, null);
		
		bfv.addTraversalListener(new TraversalListener<Country,DefaultEdge>() {

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				DefaultEdge arco = e.getEdge();
				Country a = grafo.getEdgeSource(arco);
				Country b = grafo.getEdgeTarget(arco);
				
				// Se b lo conoscevo già e a no --> ho scoperto a attraverso b
				if(predecessore.containsKey(b) && !predecessore.containsKey(a)) {
					predecessore.put(a, b);
				}
				// Se a lo conoscevo già e b no --> ho scoperto b attraverso a
				if(predecessore.containsKey(a) && !predecessore.containsKey(b)) {
					predecessore.put(b, a);
				}
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Country> e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Country> e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		List<Country> statiRaggiungibili = new LinkedList<>();
		
		while(bfv.hasNext()) {
			Country c = bfv.next();
			statiRaggiungibili.add(c);
		}
		
		String risultato="";
		
		if(statiRaggiungibili.isEmpty()) {
			return risultato;
		}
		
		risultato = risultato +"Numero stati raggiungibili = " +statiRaggiungibili.size() +"\n";
		for(Country c : statiRaggiungibili)
			risultato = risultato +c +"\n";
		
		return risultato;
	}
	
	
	// Metodo ricorsivo
	
	/**
	 * Fermate raggiungibili tramite algortimo ricorsivo
	 * @param partenza
	 */
	public String fermateRaggiungibiliRI(Country partenza) {
		
		if(!this.grafo.containsVertex(partenza)) {
			return "Il vertice selezionato non è presente nel grafo!";
		}
		
		List<Country> parziale = new LinkedList<>();
		parziale.add(partenza);
		ricorsivo(parziale);
		
		String risultato="";
		
		risultato = risultato +"Numero stati raggiungibili = " +parziale.size() +"\n";
		for(Country c : parziale)
			risultato = risultato +c +"\n";
		
		return risultato;
	}
	
	public void ricorsivo (List<Country> parziale) {
		
		for(Country vicino : Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1)))
			if(!parziale.contains(vicino)) {
				parziale.add(vicino);
				ricorsivo(parziale);
			}
	}
	
	
	// Metodo iterativo
	/**
	 * Fermate raggiungibili tramite metodo iterativo
	 * @param partenza
	 * @return
	 */
	public String fermateRggiungibiliIT(Country partenza) {
		
		if(!this.grafo.containsVertex(partenza)) {
			return "Il vertice selezionato non è presente nel grafo!";
		}
		
		List<Country> visitati = new LinkedList<>();
		List<Country> daVisitare = new LinkedList<>();
		Set<DefaultEdge> archi;
		
		int cont = 0;
		
		daVisitare.add(partenza);
		while(cont<daVisitare.size()) {
			Country c = daVisitare.get(cont);
			
			archi = this.grafo.edgesOf(c);
			for(DefaultEdge e: archi) {
				// Controllo se devo aggiungere l'arco di arrivo
				if(!visitati.contains(this.grafo.getEdgeTarget(e)) || !daVisitare.contains(this.grafo.getEdgeTarget(e))) {
					daVisitare.add(this.grafo.getEdgeTarget(e));
				}
				
				// Controllo se devo aggiungere l'arco di partenza
                if(!visitati.contains(this.grafo.getEdgeSource(e)) || !daVisitare.contains(this.grafo.getEdgeSource(e))) {
                	daVisitare.add(this.grafo.getEdgeSource(e));
				}
			}
			
			// Aggiungo c alla lista dei visitati (se non è già presente)
			if(!visitati.contains(c))
				visitati.add(c);
			
			cont++;
		}
		
		// Stampo il risultato
		String risultato = "";
		
		if(visitati.isEmpty())
			return risultato;
		
		risultato = risultato +"Numero stati raggiungibili = " +visitati.size() +"\n";
		for(Country cc : visitati)
			risultato = risultato +cc +"\n";
		
		return risultato;
		
	}
	
	

}
