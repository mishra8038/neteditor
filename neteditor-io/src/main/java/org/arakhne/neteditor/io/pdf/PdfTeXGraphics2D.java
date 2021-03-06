/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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

package org.arakhne.neteditor.io.pdf ;

import java.io.IOException;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.FontMetrics;
import org.arakhne.neteditor.io.tex.TexGenerator;


/** This graphic context permits to create a PDF file
 * with combined TeX macros, from a graphic context.
 * <p>
 * This exporter supports the 
 * <a href="http://partners.adobe.com/public/developer/en/pdf/PDFReference.pdf">PDF 1.4 Reference Document</a>.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 15.0
 */
public class PdfTeXGraphics2D extends PdfGraphics2D {

	private final StringBuilder texMacros = new StringBuilder();
	private final Rectangle2f drawingArea;
	
	/** Construct a new PdfGraphics2D.
	 * 
	 * @param drawingArea is the size of the drawing area.
	 */
	public PdfTeXGraphics2D(Rectangle2f drawingArea) {
		assert(drawingArea!=null);
		this.drawingArea = drawingArea;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		this.texMacros.setLength(0);
	}
	
	@Override
	public void reset() {
		super.reset();
		this.texMacros.setLength(0);
	}
	
	@Override
	public void prolog() throws IOException {
		super.prolog();
		this.texMacros.setLength(0);
	}
	
	/** Replies the generated TeX.
	 * 
	 * @return the generated TeX.
	 */
	public String getGeneratedTeX() {
		return this.texMacros.toString();
	}
	
	@Override
	protected void drawPdfString(float x, float y, String str, Color color) {
		Font font = getFont();
		FontMetrics fm = getFontMetrics(font);
		float tx = TexGenerator.toTeXX(x, this.drawingArea);
		float ty = TexGenerator.toTeXY(y, this.drawingArea);
		String lastFontDefinition = TexGenerator.buildFontString(font, fm);
		this.texMacros.append(TexGenerator.buildTeXTString(tx, ty, str, lastFontDefinition));
		this.texMacros.append("\n"); //$NON-NLS-1$
	}

}
