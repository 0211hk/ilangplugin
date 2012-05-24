package ilangplugin.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

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
import org.hk.io.language.plugin.util.SystemPropertyUtil;

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
			return true;
		}
	}

	class IoResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkIo(resource);
			return true;
		}
	}

	public static final String BUILDER_ID = "ilangplugin.IlanguageBuilder";

	private final Parse p = new Parse();

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(IMarker.PROBLEM);
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
			p.clear();
			try {
				String[] returns = execCommand(new String[] {
						"/usr/local/bin/io", file.getLocation().toOSString() });
				p.parse(returns[0], file.getName());
				if (p.isError) {
					System.out.println(p.errorMessage);
					addMarker(file, p.errorMessage, p.lineNumber,
							IMarker.SEVERITY_ERROR);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static class Parse {
		private boolean isError = false;
		private int lineNumber = 0;
		private String errorMessage = "";

		public boolean isError() {
			return isError;
		}

		public void clear() {
			isError = false;
			lineNumber = 0;
			errorMessage = "";
		}

		public void parse(final String message, final String fileName) {
			if (message == null || message.length() == 0) {
				isError = false;
				return;
			}
			isError = true;
			String[] messages = message.split(SystemPropertyUtil
					.getLineSeparator());
			if (messages.length != 0) {
				this.errorMessage = messages[1];
			}
			for (String m : messages) {
				int index = m.indexOf(fileName);
				if (index != -1) {
					String s = m.substring(index, m.length());
					String[] str = s.split(" ");
					if (str.length == 2) {
						lineNumber = Integer.parseInt(str[1]);
					}
				}
			}
			if (lineNumber == 0) {
				isError = false;
			}
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
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

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		delta.accept(new IoDeltaVisitor());
	}

	private String[] execCommand(String[] cmds) throws IOException,
			InterruptedException {
		String[] returns = new String[3];
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
				out.append(line + SystemPropertyUtil.getLineSeparator());
			}
			returns[0] = out.toString();
			br.close();
			in.close();
			in = p.getErrorStream();
			StringBuffer err = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
				err.append(line + SystemPropertyUtil.getLineSeparator());
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
