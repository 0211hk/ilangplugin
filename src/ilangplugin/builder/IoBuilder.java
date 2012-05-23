package ilangplugin.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class IoBuilder extends IncrementalProjectBuilder {

	class IoDeltaVisitor implements IResourceDeltaVisitor {
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				checkIo(resource);
				break;
			case IResourceDelta.REMOVED:
				break;
			case IResourceDelta.CHANGED:
				checkIo(resource);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class IoResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkIo(resource);
			// return true to continue visiting children.
			return true;
		}
	}

	class XMLErrorHandler extends DefaultHandler {

		private IFile file;

		public XMLErrorHandler(IFile file) {
			this.file = file;
		}

		private void addMarker(SAXParseException e, int severity) {
			IoBuilder.this.addMarker(file, e.getMessage(), e.getLineNumber(),
					severity);
		}

		public void error(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void warning(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "ilangplugin.IlanguageBuilder";

	private static final String MARKER_TYPE = "ilangplugin.xmlProblem";

	private SAXParserFactory parserFactory;

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	@Override
	protected IProject[] build(int kind,
			@SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void checkIo(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".io")) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			try {
				System.out.println("in");
				execCommand(new String[]{"/usr/local/bin/io", file.getFullPath().toOSString()});
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			XMLErrorHandler reporter = new XMLErrorHandler(file);
			try {
				getParser().parse(file.getContents(), reporter);
			} catch (Exception e1) {
			}
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new IoResourceVisitor());
		} catch (CoreException e) {
		}
	}

	private SAXParser getParser() throws ParserConfigurationException,
			SAXException {
		if (parserFactory == null) {
			parserFactory = SAXParserFactory.newInstance();
		}
		return parserFactory.newSAXParser();
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		delta.accept(new IoDeltaVisitor());
	}

	private String[] execCommand(String[] cmds) throws IOException,
			InterruptedException {
		String[] returns = new String[3];
		String LINE_SEPA = System.getProperty("line.separator");
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(cmds);
		InputStream in = null;
		BufferedReader br = null;
		try {
			in = p.getInputStream();
			StringBuffer out = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				out.append(line + LINE_SEPA);
			}
			returns[0] = out.toString();
			br.close();
			in.close();
			in = p.getErrorStream();
			StringBuffer err = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
				err.append(line + LINE_SEPA);
			}
			returns[1] = err.toString();
			returns[2] = Integer.toString(p.waitFor());
			return returns;
		} finally {
			if (br != null) {
				br.close();
			}
			if (in != null) {
				in.close();
			}
		}
	}
}
