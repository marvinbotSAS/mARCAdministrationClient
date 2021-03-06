/*
 * Copyright (C) 2015 Marvinbot S.A.S
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marcadministrationclient;


import edu.uci.ics.jung.algorithms.layout.FRLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.transform.LensSupport;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;

import java.util.ArrayDeque;
import java.util.Arrays;
/**
 * Shows a RadialTreeLayout view of a Forest.
 * A hyperbolic projection lens may also be applied
 * to the view
 * 
 * @author Tom Nelson
 * 
 */
@SuppressWarnings("serial")

class Vertex
{
    public boolean isActive;
    public String  content;
    Vertex() {
        this.isActive = false;
        init();
    }
    
    Vertex(String s)
    {
        this.content = s;
        init();
        
    }
    
    void init()
    {
        this.isActive = false;
    }
    public String toString()
    {
        return this.content;
    }
} 
    class Edge
    {
        static int i = 0;
        int index;
        Edge()
        {
            index = i++;
        }
        
    }

public class KnowLedgeGraph extends javax.swing.JPanel {
	
    ArrayList<String> vertexToRemove;
    
    ArrayList<String> roots;
    
	DirectedGraph<String,Integer> graph;

	Factory<DirectedGraph<String,Integer>> graphFactory = 
		new Factory<DirectedGraph<String,Integer>>() {

                    @Override
		public DirectedGraph<String,Integer> create() {
			return new DirectedSparseGraph<>();
		}
	};

	Factory<Tree<String,Integer>> treeFactory =
		new Factory<Tree<String,Integer>> () {

                    @Override
		public Tree<String,Integer> create() {
			return new DelegateTree<>(graphFactory);
		}
	};
	Factory<Integer> edgeFactory = new Factory<Integer>() 
        {
                int i =0;
                
                @Override
		public Integer create() 
                {
                    return i++;
		}

	};

	Factory<String> vertexFactory = new Factory<String>() {
		int i=0;
                @Override
		public String create() {
			return "V"+i++;
		}
	};

	VisualizationServer.Paintable rings;

	String root;

	FRLayout<String,Integer> layout;

	

	/**
	 * the visual component and renderer for the graph
	 */
	VisualizationViewer<String,Integer> vv;

    /**
     * provides a Hyperbolic lens for the view
     */
    LensSupport hyperbolicViewSupport;
    
    ScalingControl scaler;
   
    /**
     * create an instance of a simple graph with controls to
     * demo the zoomand hyperbolic features.
     * 
     */
    
    final VisualizationModel<String,Integer> visualizationModel;
    
    public KnowLedgeGraph() {
        
        super();
        this.roots = null;
        this.vertexToRemove = null;
        // create a simple graph for the demo
        // create a simple graph for the demo
        graph = new DirectedSparseMultigraph();


        createTree();
        
        


        Dimension preferredSize = new Dimension(500,500);
        
        layout = new FRLayout<>(graph,preferredSize );
        ((FRLayout<String,Integer>)layout).setMaxIterations(1000);
        
         this.visualizationModel = 
            new DefaultVisualizationModel<>(layout, preferredSize);
        
        vv =  new VisualizationViewer<>(visualizationModel, preferredSize);

        
        PickedState<String> ps = vv.getPickedVertexState();
        PickedState<Integer> pes = vv.getPickedEdgeState();
        vv.getRenderContext().setVertexFillPaintTransformer(new PickableVertexPaintTransformer<>(ps, Color.red, Color.yellow));
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<>(pes, Color.black, Color.cyan));
        vv.setBackground(Color.white);
        
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
        vv.getRenderContext().setEdgeShapeTransformer(new MyEdgeShape.BentLine<String,Integer>());
        
        // add a listener for ToolTips
        vv.setVertexToolTipTransformer(new ToStringLabeller<String>());
        
       // Container content = this.getContentPane();
        GraphZoomScrollPane gzsp = new GraphZoomScrollPane(vv);
        this.add(gzsp);
        
        /**
         * the regular graph mouse for the normal view
         */
        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        vv.addKeyListener(graphMouse.getModeKeyListener());
 
        //rings = new Rings();
	//	vv.addPreRenderPaintable(rings);

        hyperbolicViewSupport = 
            new ViewLensSupport<>(vv, new HyperbolicShapeTransformer(vv, 
            		vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)), 
                    new ModalLensGraphMouse());
        
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });
        
        final JRadioButton hyperView = new JRadioButton("Hyperbolic View");
        hyperView.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                hyperbolicViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        graphMouse.addItemListener(hyperbolicViewSupport.getGraphMouse().getModeListener());
        
        JMenuBar menubar = new JMenuBar();
        menubar.add(graphMouse.getModeMenu());
        gzsp.setCorner(menubar);

        JPanel controls = new JPanel();
        JPanel zoomControls = new JPanel(new GridLayout(2,1));
        zoomControls.setBorder(BorderFactory.createTitledBorder("Zoom"));
        JPanel hyperControls = new JPanel(new GridLayout(3,2));
        hyperControls.setBorder(BorderFactory.createTitledBorder("Examiner Lens"));
        zoomControls.add(plus);
        zoomControls.add(minus);
        JPanel modeControls = new JPanel(new BorderLayout());
        modeControls.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
        modeControls.add(graphMouse.getModeComboBox());
        hyperControls.add(hyperView);
        
        controls.add(zoomControls);
        controls.add(hyperControls);
        controls.add(modeControls);
        /*content*/this.add(controls, BorderLayout.NORTH);
        
    }

    public  void CheckContainsVertex(String s)
    {
        Collection<String> vertices = graph.getVertices();
        boolean found = false;
        for (String v : vertices)
        {
            if ( v.equals(s))
            {
                found = true;
                break;
            }
        }
        if ( found )
        {
            this.vertexToRemove.remove(s);
        }
    }
    
    public void AddNodes(java.util.Map<String,ArrayList<String>> theNodesMap, ArrayList<String> roots)
    {
        this.roots = roots;
        this.vertexToRemove = new ArrayList(Arrays.asList(this.graph.getVertices().toArray()) );
        
        synchronized(theNodesMap)
        {
            for (String s : theNodesMap.keySet() )
            {
                ArrayList<String> l = theNodesMap.get(s);

                this.Add(s,l);
            }

            for (String v : this.vertexToRemove )
            {
                boolean removeVertex = this.graph.removeVertex(v);
            }

            this.vertexToRemove.clear();
        }
        //layout = new TreeLayout<>(graph);
        layout.setGraph(graph);
        this.visualizationModel.setGraphLayout(layout);
        layout.setSize(new Dimension(500,500));
        this.vv.repaint();
    }
    public void Add(String s, ArrayList<String> l)
    {
        
            this.CheckContainsVertex(s);
            if ( this.roots.contains(s))
            {
                graph.addVertex(s);
            }

            for (String s2 : l)
            {
                this.CheckContainsVertex(s2);
                if ( roots.contains(s2))
                {
                    graph.addVertex(s2);
                }
                if ( this.graph.findEdge(s2, s) == null )
                {
                    this.graph.addEdge(this.edgeFactory.create(), s2, s );
                }

                
            }
        
    }
    
    private void createTree() {
    	graph.addVertex("V0");
    	graph.addEdge(edgeFactory.create(), "V0", "V1");
    	graph.addEdge(edgeFactory.create(), "V0", "V2");
    	graph.addEdge(edgeFactory.create(), "V1", "V4");
    	graph.addEdge(edgeFactory.create(), "V2", "V3");
    	graph.addEdge(edgeFactory.create(), "V2", "V5");
    	graph.addEdge(edgeFactory.create(), "V4", "V6");
    	graph.addEdge(edgeFactory.create(), "V4", "V7");
    	graph.addEdge(edgeFactory.create(), "V3", "V8");
    	graph.addEdge(edgeFactory.create(), "V6", "V9");
    	graph.addEdge(edgeFactory.create(), "V4", "V10");
    	
       	graph.addVertex("A0");
       	graph.addEdge(edgeFactory.create(), "A0", "A1");
       	graph.addEdge(edgeFactory.create(), "A0", "A2");
       	graph.addEdge(edgeFactory.create(), "A0", "A3");
       	
       	graph.addVertex("B0");
    	graph.addEdge(edgeFactory.create(), "B0", "B1");
    	graph.addEdge(edgeFactory.create(), "B0", "B2");
    	graph.addEdge(edgeFactory.create(), "B1", "B4");
    	graph.addEdge(edgeFactory.create(), "B2", "B3");
    	graph.addEdge(edgeFactory.create(), "B2", "B5");
    	graph.addEdge(edgeFactory.create(), "B4", "B6");
    	graph.addEdge(edgeFactory.create(), "B4", "B7");
    	graph.addEdge(edgeFactory.create(), "B3", "B8");
    	graph.addEdge(edgeFactory.create(), "B6", "B9");
        
        graph.addEdge(edgeFactory.create(), "A0", "B0");
       	graph.addEdge(edgeFactory.create(), "B0", "A0");
    }

 

}
