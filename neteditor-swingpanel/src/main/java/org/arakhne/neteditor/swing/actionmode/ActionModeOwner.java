/* 
 * $Id$
 * 
 * Copyright (C) 2012 Stephane GALLAND.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
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
package org.arakhne.neteditor.swing.actionmode ;

import org.arakhne.afc.ui.actionmode.ActionModeManagerOwner;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.fig.factory.FigureFactory;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.swing.graphics.SwingViewGraphics2D;

/** This interface describes a container of modes.
 * 
 * @param <G> is the type of the supported graph.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ActionModeOwner<G extends Graph<?,?,?,?>> extends ActionModeManagerOwner<Figure,SwingViewGraphics2D,Color> {
	
	/** Returns the graph.
	 * 
	 * @return the graph.
	 */
	public G getGraph();
	
	/** Replies the factory of figures.
	 * 
	 * @return the factory.
	 */
	public FigureFactory<G> getFigureFactory();
	
}
