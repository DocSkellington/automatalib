/* Copyright (C) 2013 TU Dortmund
 * This file is part of AutomataLib, http://www.automatalib.net/.
 * 
 * AutomataLib is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 3.0 as published by the Free Software Foundation.
 * 
 * AutomataLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with AutomataLib; if not, see
 * http://www.gnu.de/documents/lgpl.en.html.
 */
package net.automatalib.commons.util;

/**
 * The well-known disjoint-set forest data structure for dealing with partitions
 * on a fixed-range integer domain.
 * 
 * @author fhowar
 * @author Malte Isberner
 */
public final class UnionFind {

	private final int[] p;
	private final int[] rank;

	/**
	 * Initializes the disjoint-set data structure.
	 * @param n the overall size of the domain
	 */
	public UnionFind(int n) {
		p = new int[n];
		rank = new int[n];

		for(int i = 0; i < n; i++) {
			// primitive arrays are always zero initialized
			// rank[i] = 0;
			p[i] = i;
		}
	}

	public int size() {
		return p.length;
	}
	/**
	 * Unites the sets containing the two given elements.
	 * @param p the first element
	 * @param q the second element
	 * @return the identifier of the resulting set
	 */
	public int union(int p, int q) {
		return link(find(p), find(q));
	}

	/**
	 * Unites two given sets. Note that the behavior of this method is not specified
	 * if the given parameters are normal elements and no set identifiers.
	 * @param x the first set
	 * @param y the second set
	 * @return the identifier of the resulting set (either <tt>x</tt> or <tt>y</tt>)
	 */
	public int link(int x, int y) {
		int rx = rank[x], ry = rank[y];
		if(rx > ry) {
			p[y] = x;
			return x;
		}
		p[x] = y;
		if(rx == ry) {
			rank[y] = ry + 1;
		}
		return y;
	}

	/**
	 * Finds the set of a given element, and compresses the path to the root node.
	 * @param x the element
	 * @return the identifier of the set which contains the given element
	 */
	public int find(int x) {
		int curr = x;
		int currp = p[curr];
		while (curr != currp) {
			curr = currp;
			currp = p[curr];
		}
		int ancestor = curr;
		curr = x;
		while (curr != ancestor) {
			int next = p[curr];
			p[curr] = ancestor;
			curr = next;
		}
		
		return ancestor;
	}
}