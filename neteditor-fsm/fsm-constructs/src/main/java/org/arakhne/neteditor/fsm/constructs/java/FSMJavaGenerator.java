/* 
 * $Id$
 * 
 * Copyright (C) 2012 Stephane GALLAND.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * This program is free software; you can redistribute it and/or modify
 */
package org.arakhne.neteditor.fsm.constructs.java ;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.arakhne.neteditor.fsm.constructs.AbstractFSMNode;
import org.arakhne.neteditor.fsm.constructs.FSMEndPoint;
import org.arakhne.neteditor.fsm.constructs.FSMStartPoint;
import org.arakhne.neteditor.fsm.constructs.FSMState;
import org.arakhne.neteditor.fsm.constructs.FSMTransition;
import org.arakhne.neteditor.fsm.constructs.FiniteStateMachine;

/** Generator of Java code for a FSM.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMJavaGenerator {

	private final String packageName;
	private final String className;
	private final Set<String> actions = new TreeSet<String>();

	/**
	 * @param className is the name of the class to generate. The name of the class
	 * may be qualified by the package name.
	 */
	public FSMJavaGenerator(String className) {
		int idx = className.lastIndexOf('.');
		if (idx>=0) {
			this.packageName = className.substring(0, idx);
			this.className = className.substring(idx+1);
		}
		else {
			this.packageName = null;
			this.className = className;
		}
	}

	/** Generate the Java code for the given FSM.
	 * 
	 * @param fsm
	 * @return the java code.
	 */
	public String generate(FiniteStateMachine fsm) {
		this.actions.clear();
		StringBuilder javaCode = new StringBuilder();

		//----

		Map<String,FSMState> nameToState = new TreeMap<String,FSMState>();
		Map<FSMState,String> stateToName = new TreeMap<FSMState,String>();

		for(AbstractFSMNode node : fsm.getNodes()) {
			if (node instanceof FSMState) {
				FSMState state = (FSMState)node;
				String name = "STATE_"+nameToState.size(); //$NON-NLS-1$
				nameToState.put(name, state);
				stateToName.put(state, name);
			}
		}

		Set<FSMTransition> startTransitions = new TreeSet<FSMTransition>();
		Map<FSMState,Set<FSMTransition>> transitionsPerState = new TreeMap<FSMState,Set<FSMTransition>>();
		for(FSMTransition transition : fsm.getEdges()) {
			if (transition.getStartAnchor().getNode() instanceof FSMStartPoint) {
				startTransitions.add(transition);
			}
			else {
				Set<FSMTransition> trans = transitionsPerState.get(transition.getStartAnchor().getNode());
				if (trans==null) {
					trans = new TreeSet<FSMTransition>();
					transitionsPerState.put((FSMState)transition.getStartAnchor().getNode(), trans);
				}
				trans.add(transition);
			}
		}

		//----

		javaCode.append("/* Generated by: "); //$NON-NLS-1$
		javaCode.append(FSMJavaGenerator.class.getName());
		javaCode.append("\n   Generation date: "); //$NON-NLS-1$
		javaCode.append(new Date().toString());
		javaCode.append("*/\n"); //$NON-NLS-1$

		if (this.packageName!=null && !this.packageName.isEmpty()) {
			javaCode.append("package "); //$NON-NLS-1$
			javaCode.append(this.packageName);
			javaCode.append(";\n"); //$NON-NLS-1$
		}

		javaCode.append("public class "); //$NON-NLS-1$
		javaCode.append(this.className);
		javaCode.append(" implements Runnable {\n"); //$NON-NLS-1$

		javaCode.append("\tpublic enum State {\n"); //$NON-NLS-1$
		if (!nameToState.isEmpty()) {
			boolean firstState = true;
			for(Entry<String,FSMState> entry : nameToState.entrySet()) {
				if (firstState) firstState = false;
				else javaCode.append(",\n"); //$NON-NLS-1$
				javaCode.append("\t\t/** State: "); //$NON-NLS-1$
				javaCode.append(entry.getValue().toString());
				javaCode.append("\n\t\t */\n\t\t"); //$NON-NLS-1$
				javaCode.append(entry.getKey());
				javaCode.append("("); //$NON-NLS-1$
				javaCode.append(Boolean.toString(entry.getValue().isAccepting()));
				javaCode.append(")"); //$NON-NLS-1$
			}
			javaCode.append(";\n"); //$NON-NLS-1$
		}
		else {
			javaCode.append("\t\t//FIXME: Add states"); //$NON-NLS-1$
		}
		javaCode.append("\t\tprivate final boolean isAccepting;\n"); //$NON-NLS-1$
		javaCode.append("\t\tpublic State(boolean isAccepting) {\n"); //$NON-NLS-1$
		javaCode.append("\t\t\tthis.isAccepting = isAccepting;\n"); //$NON-NLS-1$
		javaCode.append("\t\t}\n"); //$NON-NLS-1$
		javaCode.append("\t\tpublic boolean isAcceptiong() {\n"); //$NON-NLS-1$
		javaCode.append("\t\t\treturn this.isAccepting;\n"); //$NON-NLS-1$
		javaCode.append("\t\t}\n"); //$NON-NLS-1$
		javaCode.append("\t} // enum State\n"); //$NON-NLS-1$

		javaCode.append("\tprivate final Random random = new Random();\n"); //$NON-NLS-1$
		javaCode.append("\tprivate State currentState;\n"); //$NON-NLS-1$

		javaCode.append("\tpublic "); //$NON-NLS-1$
		javaCode.append(this.className);
		javaCode.append("() {\n"); //$NON-NLS-1$
		String cons = generateTransitions(null, startTransitions, stateToName);
		if (cons!=null && !cons.isEmpty()) {
			javaCode.append(cons);
		}
		else {
			javaCode.append("\t\tState[] allStates = State.values();\n"); //$NON-NLS-1$
			javaCode.append("\t\tthis.currentState = allStates[this.random.nextInt(allStates.length)];\n"); //$NON-NLS-1$
		}
		javaCode.append("\t}\n"); //$NON-NLS-1$

		javaCode.append("\t/** Replies the current state of the simulated FSM.\n"); //$NON-NLS-1$
		javaCode.append("\t * @return the current state of the simulated FSM.\n\t */\n"); //$NON-NLS-1$
		javaCode.append("\tpublic State getCurrentState() {\n"); //$NON-NLS-1$
		javaCode.append("\t\treturn this.currentState;\n"); //$NON-NLS-1$
		javaCode.append("\t}\n"); //$NON-NLS-1$

		javaCode.append("\t/** Run one step of the simulation of the FSM.\n\t */\n"); //$NON-NLS-1$
		javaCode.append("\tpublic void run() {\n"); //$NON-NLS-1$
		javaCode.append("\t\tif (this.currentState==null) return;\n"); //$NON-NLS-1$
		javaCode.append("\t\tswitch(this.currentState) {\n"); //$NON-NLS-1$
		for(Entry<String,FSMState> entry : nameToState.entrySet()) {
			FSMState s = entry.getValue();
			javaCode.append("\t\tcase "); //$NON-NLS-1$
			javaCode.append(entry.getKey());
			javaCode.append(": // "); //$NON-NLS-1$
			javaCode.append(s.toString());
			javaCode.append("\n\t\t{\n"); //$NON-NLS-1$
			Set<FSMTransition> trans = transitionsPerState.get(s);
			if (trans!=null && !trans.isEmpty()) {
				javaCode.append(generateTransitions(s, trans, stateToName));
			}
			else {
				javaCode.append(generateInAction(s));
			}
			javaCode.append("\t\t\tbreak;\n\t\t}\n"); //$NON-NLS-1$
		}
		javaCode.append("\t\tdefault: throw new IllegalStateException();\n"); //$NON-NLS-1$
		javaCode.append("\t\t} // end of current-state switch\n\t}\n"); //$NON-NLS-1$

		
		for(Entry<String,String> action : fsm.getActionCodes().entrySet()) {
			javaCode.append("\tprotected action_"); //$NON-NLS-1$
			javaCode.append(action.getKey());
			javaCode.append("() {\n\t"); //$NON-NLS-1$
			javaCode.append(action.getValue());
			javaCode.append("\n\t}\n"); //$NON-NLS-1$
			this.actions.remove(action.getKey());
		}
		
		for(String actionName : this.actions) {
			javaCode.append("\tprotected action_"); //$NON-NLS-1$
			javaCode.append(actionName);
			javaCode.append("() {\n\t}\n"); //$NON-NLS-1$
		}
		

		javaCode.append("} // class "); //$NON-NLS-1$
		javaCode.append(this.className);
		javaCode.append("\n"); //$NON-NLS-1$

		return javaCode.toString();
	}

	/** Generate the code that permits to pass through transitions.
	 * 
	 * @param state is the initial state
	 * @param transitions is the set of transitions to consider.
	 * @param stateToName is the map from the states' names to the states' enums.
	 * @return the Java source code that permits to pass through the transitions.
	 */
	private String generateTransitions(FSMState state, Set<FSMTransition> transitions, Map<FSMState,String> stateToName) {
		StringBuilder javaCode = new StringBuilder();

		Set<FSMTransition> unguardedTransitions = new TreeSet<FSMTransition>();
		Set<FSMTransition> guardedTransitions = new TreeSet<FSMTransition>();
		Set<FSMTransition> elseTransitions = new TreeSet<FSMTransition>();
		for(FSMTransition tr : transitions) {
			String guard = tr.getGuard();
			if (guard==null || guard.isEmpty()) {
				unguardedTransitions.add(tr);
			}
			else if ("else".equalsIgnoreCase(guard)) { //$NON-NLS-1$
				elseTransitions.add(tr);
			}
			else {
				guardedTransitions.add(tr);
			}
		}

		javaCode.append(generateGuardedTransitions(state, guardedTransitions, stateToName, unguardedTransitions));
		boolean elseCond = javaCode.length()>0;
		if (elseCond) javaCode.append("\t\telse {\n"); //$NON-NLS-1$
		if (elseTransitions.isEmpty()) {
			if (unguardedTransitions.isEmpty()) {
				javaCode.append(generateInAction(state));
			}
			else {
				javaCode.append(generateRandomTransitions(state, unguardedTransitions, stateToName, null));
			}
		}
		else {
			javaCode.append(generateRandomTransitions(state, elseTransitions, stateToName, unguardedTransitions));
		}
		if (elseCond) javaCode.append("\t\t} // end of else transitions\n"); //$NON-NLS-1$

		return javaCode.toString();
	}

	/** Generate the code that permits to pass through a transition that is randomly selected.
	 * 
	 * @param state is the initial state
	 * @param transitions is the set of transitions to consider.
	 * @param stateToName is the map from the states' names to the states' enums.
	 * @param additionalTransitions is an additional set of transitions that may also be considered
	 * @return the Java source code that permits to pass through the transitions.
	 */
	private String generateRandomTransitions(FSMState state, Set<FSMTransition> transitions, Map<FSMState,String> stateToName, Set<FSMTransition> additionalTransitions) {
		Set<FSMTransition> allTrans;
		if (additionalTransitions==null || additionalTransitions.isEmpty()) {
			allTrans = transitions;
		}
		else {
			allTrans = new TreeSet<FSMTransition>();
			allTrans.addAll(transitions);
			allTrans.addAll(additionalTransitions);
		}
		StringBuilder javaCode = new StringBuilder();
		if (!allTrans.isEmpty()) {
			if (allTrans.size()==1) {
				javaCode.append(generateUnguardedTransition(state, allTrans.iterator().next(), stateToName));
			}
			else {
				javaCode.append("\t\tswitch(random.nextInt("); //$NON-NLS-1$
				javaCode.append(allTrans.size());
				javaCode.append(")) {\n"); //$NON-NLS-1$
				int i = 0;
				for(FSMTransition tr : allTrans) {
					javaCode.append("\t\tcase "); //$NON-NLS-1$
					javaCode.append(Integer.toString(i));
					javaCode.append(":\n{\n\t\t\t"); //$NON-NLS-1$
					javaCode.append(generateUnguardedTransition(state, tr, stateToName));
					javaCode.append("break;\n\t\t} // end of random case\n"); //$NON-NLS-1$
					++i;
				}
				javaCode.append("\t\tdefault: throw new IllegalStateException();\n"); //$NON-NLS-1$
				javaCode.append("\t\t} // end of random switch\n"); //$NON-NLS-1$
			}
		}
		return javaCode.toString();
	}

	/** Generate the source code that permits to select a transition according to
	 * its guard, or to randomly select a transition if no guarded transition is matching.
	 * 
	 * @param state is the initial state
	 * @param guardedTransitions is the set of transitions with guards to consider.
	 * @param stateToName is the map from the states' names to the states' enums.
	 * @param randomTransitions is a collection of transitions to consider only if no guarded transition match.
	 * @return the Java source code that permits to pass through the transitions.
	 */
	private String generateGuardedTransitions(FSMState state, Set<FSMTransition> guardedTransitions, Map<FSMState,String> stateToName, Set<FSMTransition> randomTransitions) {
		StringBuilder javaCode = new StringBuilder();
		for(FSMTransition tr : guardedTransitions) {
			javaCode.append(generateGuardedTransition(state, tr, stateToName, randomTransitions));
		}
		return javaCode.toString();
	}

	/** Generate the source code that permits to pass through a guarded transition,
	 * or to randomly select a transition if no guarded transition is matching.
	 * 
	 * @param state is the initial state
	 * @param guardedTransition is the guarded transition to consider.
	 * @param stateToName is the map from the states' names to the states' enums.
	 * @param randomTransitions is a collection of transitions to consider only if no guarded transition match.
	 * @return the Java source code that permits to pass through the transition.
	 */
	private String generateGuardedTransition(FSMState state, FSMTransition guardedTransition, Map<FSMState,String> stateToName, Set<FSMTransition> randomTransitions) {
		StringBuilder javaCode = new StringBuilder();
		String guard = guardedTransition.getGuard();
		if (javaCode.length()==0) {
			javaCode.append("\t\tif ("); //$NON-NLS-1$
		}
		else {
			javaCode.append("\t\telse if ("); //$NON-NLS-1$
		}
		javaCode.append(guard);
		javaCode.append(") {\n"); //$NON-NLS-1$
		if (!randomTransitions.isEmpty()) {
			javaCode.append(generateRandomTransitions(state,
					Collections.singleton(guardedTransition), stateToName, randomTransitions));
		}
		else {
			javaCode.append(generateUnguardedTransition(state, guardedTransition, stateToName));
		}
		javaCode.append("\t\t} // end of transition "); //$NON-NLS-1$
		javaCode.append(guard.toString());
		javaCode.append("\n"); //$NON-NLS-1$
		return javaCode.toString();
	}

	/**
	 * @param state is the initial state
	 * @param transition is the unguarded transitions to consider.
	 * @param stateToName is the map from the states' names to the states' enums.
	 * @return the Java source code that permits to pass through the transition.
	 */
	private String generateUnguardedTransition(FSMState state, FSMTransition transition, Map<FSMState,String> stateToName) {
		StringBuilder javaCode = new StringBuilder(); 
		AbstractFSMNode targetNode = transition.getEndAnchor().getNode();
		if (state!=null) javaCode.append(generateExitAction(state));
		javaCode.append(generateAction(transition));
		if (targetNode instanceof FSMEndPoint) {
			javaCode.append("this.currentState = null;\n"); //$NON-NLS-1$
		}
		else if (targetNode instanceof FSMState) {
			FSMState target = (FSMState)targetNode;
			javaCode.append(generateEnterAction(target));
			String targetName = stateToName.get(target);
			javaCode.append("this.currentState = State."); //$NON-NLS-1$
			javaCode.append(targetName);
			javaCode.append(";\n"); //$NON-NLS-1$
		}
		return javaCode.toString();
	}

	/** Generate the source code for the "enter action".
	 * 
	 * @param state is the state to consider
	 * @return the source code.
	 */
	private String generateEnterAction(FSMState state) {
		StringBuilder javaCode = new StringBuilder();
		if (state!=null) {
			String action = state.getEnterAction();
			if (action!=null && !action.isEmpty()) {
				javaCode.append(action);
				javaCode.append("();\n"); //$NON-NLS-1$
				this.actions.add(action);
			}
		}
		return javaCode.toString();
	}

	/** Generate the source code for the "inside action".
	 * 
	 * @param state is the state to consider
	 * @return the source code.
	 */
	private String generateInAction(FSMState state) {
		StringBuilder javaCode = new StringBuilder();
		if (state!=null) {
			String action = state.getAction();
			if (action!=null && !action.isEmpty()) {
				javaCode.append("action_"); //$NON-NLS-1$
				javaCode.append(action);
				javaCode.append("();\n"); //$NON-NLS-1$
				this.actions.add(action);
			}
		}
		return javaCode.toString();
	}

	/** Generate the source code for the "exit action".
	 * 
	 * @param state is the state to consider
	 * @return the source code.
	 */
	private String generateExitAction(FSMState state) {
		StringBuilder javaCode = new StringBuilder();
		if (state!=null) {
			String action = state.getExitAction();
			if (action!=null && !action.isEmpty()) {
				javaCode.append("action_"); //$NON-NLS-1$
				javaCode.append(action);
				javaCode.append("();\n"); //$NON-NLS-1$
				this.actions.add(action);
			}
		}
		return javaCode.toString();
	}

	private String generateAction(FSMTransition transition) {
		StringBuilder javaCode = new StringBuilder();
		String action = transition.getAction();
		if (action!=null && !action.isEmpty()) {
			javaCode.append("action_"); //$NON-NLS-1$
			javaCode.append(action);
			javaCode.append("();\n"); //$NON-NLS-1$
			this.actions.add(action);
		}
		return javaCode.toString();
	}

}