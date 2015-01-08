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

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 *
 * @author patrice
 */
public class MyEdgeShape extends EdgeShape
{
    
    /**
     * An edge shape that renders as a bent-line between the
     * vertex endpoints.
     */
    public static class BentLine<V,E> 
             extends AbstractEdgeShapeTransformer<V,E> implements IndexedRendering<V,E> {
        
        /**
         * singleton instance of the BentLine shape
         */
       // private static GeneralPath instance = new GeneralPath();
        
        private static Path2D instance = new Path2D.Double();
        
        protected EdgeIndexFunction<V,E> parallelEdgeIndexFunction;

        @SuppressWarnings("unchecked")
		public void setEdgeIndexFunction(EdgeIndexFunction<V,E> parallelEdgeIndexFunction) {
            this.parallelEdgeIndexFunction = parallelEdgeIndexFunction;
            loop.setEdgeIndexFunction(parallelEdgeIndexFunction);
        }
        
        

        /**
		 * @return the parallelEdgeIndexFunction
		 */
		public EdgeIndexFunction<V, E> getEdgeIndexFunction() {
			return parallelEdgeIndexFunction;
		}



		/**
         * Get the shape for this edge, returning either the
         * shared instance or, in the case of self-loop edges, the
         * Loop shared instance.
         */
        @SuppressWarnings("unchecked")
		public Shape transform(edu.uci.ics.jung.graph.util.Context<Graph<V,E>,E> context) {
        	Graph<V,E> graph = context.graph;
        	E e = context.element;
            Pair<V> endpoints = graph.getEndpoints(e);
            if(endpoints != null) {
            	boolean isLoop = endpoints.getFirst().equals(endpoints.getSecond());
            	if (isLoop) {
            		return loop.transform(context);
            	}
            }
            
            boolean isClosed = graph.findEdge(endpoints.getSecond(), endpoints.getFirst()) != null ;
            int index = 1;
            if(parallelEdgeIndexFunction != null) {
                index = parallelEdgeIndexFunction.getIndex(graph, e);
            }
            float controlY = control_offset_increment + control_offset_increment*index;
            instance.reset();
            instance.moveTo(0.0f, 0.0f);
            if ( isClosed) // v1->v2 et v2->v1 : une courbe de bezier
            {
                instance.curveTo(0.0f, 0.0f, 0.5f, controlY, 1.f,1.f);
            }
            else // sinon une ligne
            {
              // instance.lineTo(0.5f, controlY);
               instance.lineTo(1.0f, 1.0f);
            }

            return instance;
        }

    }
    
}

