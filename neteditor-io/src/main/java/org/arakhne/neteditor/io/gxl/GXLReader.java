/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io.gxl ;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.arakhne.afc.progress.ProgressionInputStream;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.afc.vmutil.FileSystem;
import org.arakhne.afc.vmutil.Resources;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.NetEditorContentType;
import org.arakhne.neteditor.io.gxl.readers.AbstractGXLReader;
import org.arakhne.neteditor.io.resource.ResourceRepository;
import org.arakhne.neteditor.io.xml.AbstractXMLToolReader;
import org.arakhne.neteditor.io.xml.DTDResolver;
import org.arakhne.neteditor.io.xml.XMLErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/** This class permits to read a
 *  <strong>graph-model</strong> from the GXL format.
 *  <p>
 *  GXL (Graph eXchange Language) is designed to be a standard exchange
 *  format for graphs. GXL is an XML sublanguage and the syntax is 
 *  given by a XML DTD (Document Type Definition). This exchange format 
 *  offers an adaptable and flexible means to support interoperability 
 *  between graph-based tools.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://www.gupro.de/GXL/"
 */
public class GXLReader extends AbstractXMLToolReader implements GXLConstants {

	/** Replies the public DTD for GraphML.
	 * 
	 * @return the public DTD.
	 */
	public static String getPublicDTD() {
		return C_GXL_DTD_URL;
	}

	/** Replies the system DTD for GraphML.
	 * 
	 * @return the system DTD.
	 */
	public static URL getSystemDTD() {
		return Resources.getResource(GXLReader.class, C_GXL_DTD_FILENAME);
	}

	private boolean isDtdValidation = true;
	private boolean connectFigures = true;

	/**
	 */
	public GXLReader() {
		//
	}
	
	/**
	 * Set the flag that permits to connect the model objects and
	 * the figures, or not.
	 * 
	 * @param connectFigures indicates if the figures should be linked to their model
	 * objects.
	 */
	public void setFigureConnection(boolean connectFigures) {
		this.connectFigures = connectFigures;
	}

	/**
	 * Replies the flag that permits to connect the model objects and
	 * the figures, or not.
	 * 
	 * @return <code>true</code> if the figures should be linked to their model
	 * objects; otherwise <code>false</code>.
	 */
	public boolean isFigureConnection() {
		return this.connectFigures;
	}

	/**
	 * Set the flag that permits to validate, or not, the DTD.
	 * 
	 * @param dtdValidation indicates if the DTD should be validated.
	 */
	public void setDTDValidation(boolean dtdValidation) {
		this.isDtdValidation = dtdValidation;
	}

	/**
	 * Replies if the DTD is validating when reading.
	 * 
	 * @return <code>true</code> if the DTD is validated; otherwise <code>false</code>.
	 */
	public boolean isDTDValidation() {
		return this.isDtdValidation;
	}

	/** {@inheritDoc}
	 */
	@Override
	public final NetEditorContentType getContentType() {
		return NetEditorContentType.GXL;
	}

	@Override
	public final <G extends Graph<?, ?, ?, ?>> G read(Class<G> type, File inputFile,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		ResourceRepository rr = getResourceRepository();
		if (rr!=null) rr.setRoot(FileSystem.dirname(inputFile));
		FileInputStream fis = new FileInputStream(inputFile);
		try {
			return read(type, fis, figures);
		}
		finally {
			fis.close();
		}
	}

	@Override
	public final <G extends Graph<?, ?, ?, ?>> G read(Class<G> type, URL inputURL,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		ResourceRepository rr = getResourceRepository();
		if (rr!=null) rr.setRoot(FileSystem.dirname(inputURL));
		InputStream is = inputURL.openStream();
		try {
			return read(type, is, figures);
		}
		finally {
			is.close();
		}
	}

	@SuppressWarnings("resource")
	@Override
	public <G extends Graph<?, ?, ?, ?>> G read(Class<G> type, InputStream is,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		ProgressionUtil.init(getProgression(), 0, 100000);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new IOException(e);
		}

		// Force the builder to use the entity resolver devoted
		// to the support of dtd.
		if (isDTDValidation()) {
			factory.setValidating(true);
			builder.setEntityResolver(new DTDResolver(
					getPublicDTD(), getSystemDTD()));
			builder.setErrorHandler(new XMLErrorHandler());
		}

		// Read the input stream and extract the XML tree
		try {
			Document document = builder.parse(new ProgressionInputStream(
					is,	ProgressionUtil.sub(getProgression(), 50000)));
			
			Element graphMLNode = extractNode(document, N_GXL);
			String spec = graphMLNode.getAttribute(A_SPECIFICATION_VERSION);
			
			AbstractGXLReader reader = AbstractGXLReader.createGXLReader(spec);
			reader.setDTDValidation(isDTDValidation());
			reader.setFigureConnection(isFigureConnection());
			reader.setResourceRepository(getResourceRepository());
			
			ProgressionUtil.ensureNoSubTask(getProgression());

			G g = reader.readGraph(type, document, figures, ProgressionUtil.subToEnd(getProgression()));
			
			ProgressionUtil.end(getProgression());
			
			return g;
		}
		catch (SAXException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected String extractType(Element node) throws IOException {
		Element typeN = extractNode(node, N_TYPE);
		String value = typeN.getAttribute(A_XLINK_HREF);
		if (value!=null && !value.isEmpty()) {
			if (value.startsWith(SCHEMA_URL+"#")) { //$NON-NLS-1$
				value = value.substring(SCHEMA_URL.length()+1);
				if (!value.startsWith("__internal_")) { //$NON-NLS-1$
					return value;
				}
			}
		}
		throw new GXLException(Locale.getString("UNSUPPORTED_XML_NODE", node.getNodeName())); //$NON-NLS-1$
	}

}
