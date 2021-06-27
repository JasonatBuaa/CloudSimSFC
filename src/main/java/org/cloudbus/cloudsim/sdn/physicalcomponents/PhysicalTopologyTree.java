/*
 * Title:        CloudSimSDN
 * Description:  SDN extension for CloudSim
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2015, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.sdn.physicalcomponents;

import java.util.Collection;


/**
 * Network connection maps including switches, hosts, and links between them
 *  
 * @author Jungmin Son
 * @author Rodrigo N. Calheiros
 * @since CloudSimSDN 1.0
 */

 // Jason: Todo! Check this part again. May contains faulty code.
public class PhysicalTopologyTree extends PhysicalTopology {

	@Override
	public void buildDefaultRouting() {
		buildDefaultRoutingTree();
		printTopology();
	}
	
	protected void buildDefaultRoutingTree() {
		Collection<Node> nodes = getAllNodes();
		
		// For SDNHost: build path to edge switch
		// For Edge: build path to SDN Host
		for(Node sdnhost:nodes) {
			if(sdnhost.getRank() == RANK_HOST) {	// Rank3 = SDN Host
				Collection<Link> links = getAdjacentLinks(sdnhost);
				for(Link l:links) {
					if(l.getLowOrder().equals(sdnhost)) {
						sdnhost.addRoute(null, l);
						Node edge = l.getHighOrder();
						edge.addRoute(sdnhost, l);
					}
				}
			}
		}

		//Jason: the following block constructs the routing between core- and aggregation- switch
		// However, currently the routing information on aggigation switch is not finished yet 
		// (i.e., agg.getRoutingTable().getKnownDestination() is null), so add all children is non-sense.
		// This block should be placed after the next block ???
		
		// For Agg: build path to core switch
		// For Core: build path to aggregate switch
		for(Node agg:nodes) {
			if(agg.getRank() == RANK_AGGR) {	// Rank1 = Agg switch
				Collection<Link> links = getAdjacentLinks(agg);
				for(Link l:links) {
					if(l.getLowOrder().equals(agg)) {
						// Link is between Edge and Aggregate // Jason: Link is between Core and Aggregate
						agg.addRoute(null, l); // Jason: default routing on Aggregation switch to the Core switch
						Node core = l.getHighOrder();
						
						// Add all children hosts to
						for(Node destination: agg.getRoutingTable().getKnownDestination()) {
							if(destination != null)
								core.addRoute(destination, l);
						}
					}
				}
			}
		}


		// Jason: This block constructs the routing information between aggregation- and edge- switch
		// Some variable name may be misleading, e.g., Node core = l.getHighOrder(). 
		// A correct version: Node agg = l.getHighOrder ???
		// In addition, this block should appear before the last blcok ??? 



		// For Edge: build path to aggregate switch
		// For Aggregate: build path to edge switch
		for(Node edge:nodes) {
			if(edge.getRank() == RANK_EDGE) {	// Rank2 = Edge switch
				Collection<Link> links = getAdjacentLinks(edge);
				for(Link l:links) {
					if(l.getLowOrder().equals(edge)) {
						// Link is between Edge and Aggregate
						edge.addRoute(null, l);
						Node core = l.getHighOrder();
						
						// Add all children hosts to
						for(Node destination: edge.getRoutingTable().getKnownDestination()) {
							if(destination != null)
								core.addRoute(destination, l);
						}
					}
				}
			}
		}
	}
	
}
