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
package net.automatalib.automata.transout;

import net.automatalib.automata.MutableDeterministic;
import net.automatalib.automata.concepts.MutableTransitionOutput;

/**
 *
 * @author fh
 */
public interface MutableMealyMachine<S,I,T,O> extends MealyMachine<S,I,T,O>, MutableDeterministic<S,I,T,Void,O>,
		MutableTransitionOutput<T,O> {
	
	@Override
	default public void setStateProperty(S state, Void property) {}
	
	@Override
	default public void setTransitionProperty(T transition, O property) {
		setTransitionOutput(transition, property);
	}
	
}