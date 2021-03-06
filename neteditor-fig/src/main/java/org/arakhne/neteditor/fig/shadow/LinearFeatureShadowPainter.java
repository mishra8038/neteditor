/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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
package org.arakhne.neteditor.fig.shadow;

import org.arakhne.neteditor.fig.shadow.ShadowPainter;

/** This interface represents any figure
 * that may be drawn in a shadow mode.
 * The shadow mode may be used when moving
 * or resizing the figure.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface LinearFeatureShadowPainter extends ShadowPainter {

	/** Recompute the edge path with the specified translation
	 * of the control point.
	 * This function does not change the original element.
	 * 
	 * @param index is the position of the control point.
	 * @param dx is the translation along x
	 * @param dy is the translation along y
	 */
	public void moveControlPointTo(int index, float dx, float dy);
	
}
