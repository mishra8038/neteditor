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
package org.arakhne.neteditor.fig.figure.node;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.neteditor.fig.figure.ResizeDirection;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Node;

/** Node figure that is displaying a circle.
 *
 * @param <N> is the type of the model node supported by this figure.
 * @param <A> is the type of the model anchor supported by this figure.
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CircleNodeFigure<N extends Node<?,? super N,? super A,?>,A extends Anchor<?,? super N,? super A,?>> extends EllipseNodeFigure<N,A> {

	private static final long serialVersionUID = -672917035148252078L;

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public CircleNodeFigure(UUID viewUUID) {
		this(viewUUID, 0, 0, DEFAULT_MINIMAL_SIZE/2f );
	}

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 */
	public CircleNodeFigure(UUID viewUUID, float x, float y) {
		this(viewUUID, x, y, DEFAULT_MINIMAL_SIZE/2f );
	}

	/** Construct a new figure.
	 * <p>
	 * The specified width and height are set inconditionally.
	 * The minimal width becomes the min between the specified 2*radius and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The minimal height becomes the min between the specified 2*radius and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The maximal width becomes the max between the specified 2*radius and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 * The maximal height becomes the max between the specified 2*radius and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 * @param radius is the radius of the figure.
	 */
	public CircleNodeFigure(UUID viewUUID, float x, float y, float radius) {
		super(viewUUID, x, y, radius*2f, radius*2f );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected NodeShadowPainter createShadowPainter() {
		return new SPainter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(float x, float y) {
		float cx = getX() + getWidth()/2f;
		float cy = getY() + getHeight()/2f;
		float radius = getRadius();
		cx -= x;
		cy -= y;
		return cx*cx+cy*cy <= radius*radius;
	}
	
	@Override
	public boolean contains(Rectangle2f r) {
		return Circle2f.containsCircleRectangle(
				getX()+getWidth()/2f, getY()+getHeight()/2f, getRadius(),
				r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean intersects(Shape2f r) {
		float radius = getWidth()/2f;
		Circle2f circle = new Circle2f(getX()+radius, getY()+radius, radius);
		return r.intersects(circle);
	}

	/** Replies the radius of this circle.
	 * 
	 * @return the radius.
	 */
	public float getRadius() {
		return this.getWidth()/2f;
	}

	/** Set the radius of this circle.
	 * 
	 * @param radius is the new radius.
	 */
	public void setRadius(float radius) {
		setSize(radius*2f, radius*2f);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMinimalDimension(float width, float height) {
		float s = Math.min(width, height);
		super.setMinimalDimension(s, s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaximalDimension(float width, float height) {
		float s = Math.max(width, height);
		super.setMaximalDimension(s, s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(float width) {
		super.setSize(width, width);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHeight(float height) {
		super.setSize(height, height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBounds(float x, float y, float width, float height) {
		float s = Math.max(width, height);
		super.setBounds(x, y, s, s);
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class SPainter extends NodeFigure<N,A>.SPainter {

		/**
		 */
		public SPainter() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void resize(float dx, float dy, ResizeDirection direction) {
			float nx1 = getX();
			float ny1 = getY();
			float nx2 = nx1 + getWidth();
			float ny2 = ny1 + getHeight();
			
			float deltax, tmp, c;
			if (Math.abs(dx)>=Math.abs(dy)) {
				deltax = dx;
			}
			else {
				deltax = ((dx>=0f)?1f:-1f) * Math.abs(dy);
			}
			
			switch(direction) {
			case NORTH_WEST:
				nx1 += deltax;
				tmp = nx2 - nx1;
				ny1 = ny2 - tmp;
				break;
			case WEST:
				nx1 += dx;
				tmp = Math.abs(nx2 - nx1) / 2;
				c = (ny1+ny2)/2;
				ny1 = c-tmp;
				ny2 = c+tmp;
				break;
			case SOUTH_WEST:
				nx1 += deltax;
				tmp = nx2 - nx1;
				ny2 = ny1 + tmp;
				break;
			case NORTH:
				ny1 += dy;
				tmp = Math.abs(ny2 - ny1) / 2;
				c = (nx1+nx2)/2;
				nx1 = c-tmp;
				nx2 = c+tmp;
				break;
			case SOUTH:
				ny2 += dy;
				tmp = Math.abs(ny2 - ny1) / 2;
				c = (nx1+nx2)/2;
				nx1 = c-tmp;
				nx2 = c+tmp;
				break;
			case NORTH_EAST:
				nx2 += deltax;
				tmp = nx2 - nx1;
				ny1 = ny2 - tmp;
				break;
			case EAST:
				nx2 += dx;
				tmp = Math.abs(nx2 - nx1) / 2;
				c = (ny1+ny2)/2;
				ny1 = c-tmp;
				ny2 = c+tmp;
				break;
			case SOUTH_EAST:
				nx2 += deltax;
				tmp = nx2 - nx1;
				ny2 = ny1 + tmp;
				break;
			default:
				throw new IllegalStateException();
			}
			
			if (nx1>nx2) {
				float t = nx1;
				nx1 = nx2;
				nx2 = t;
			}
			
			if (ny1>ny2) {
				float t = ny1;
				ny1 = ny2;
				ny2 = t;
			}
			
			this.bounds.setFromCorners(nx1, ny1, nx2, ny2);
			this.damagedBounds = null;
		}

	}

}
