package org.apache.jmeter.libcopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class LibraryCopy {
	private String srcPath = null;
	private String desPath = null;
	private File srcFile = null;
	private String m2path = null;

	// private Set libs = new HashSet();

	public LibraryCopy() {
		m2path = System.getProperty("user.home") + File.separator + ".m2";
	}

	public void setSrcPath(String path) {
		this.srcPath = path;
	}

	public void setDesPath(String path) {
		this.desPath = path;
	}

	public void copyLibrary() {
		File desFile = new File(desPath);
		if (!desFile.exists()) {
			desFile.mkdirs();
		}
		srcFile = new File(srcPath);
		if (srcFile.isDirectory()) {
			copyDirectory(srcFile);
		} else {
			System.out.println("Please select a folder!");
		}
	}

	private void copyDirectory(File file) {
		File[] fs = file.listFiles();
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isDirectory()) {
				copyDirectory(fs[i]);
			} else {
				if (fs[i].getName().equals(".classpath")) {
					parseClassPathFile(fs[i]);
				} else {
					continue;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parseClassPathFile(File cpFile) {
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(cpFile);
			Element root = document.getRootElement();
			for (Iterator i = root.elementIterator("classpathentry"); i
					.hasNext();) {
				Element classpathentry = (Element) i.next();
				// do something
				String type = classpathentry.attributeValue("kind");
				if (type.equals("var")) {
					String path = classpathentry.attributeValue("path");
					String res=m2path+File.separator+"repository"+File.separator;
					path = path.replace("M2_REPO", res);
					path = path.replace("/", File.separator);
					path = path.replace("\\", File.separator);
					copyFile(path);
				} else if (type.equals("lib")) {
					String path = classpathentry.attributeValue("path");
					if (!path.contains(":")) {
						path = srcPath + File.separator + path;
						path = path.replace("/", File.separator);
						path = path.replace("\\", File.separator);
					}
					copyFile(path);
				} else {

				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private void copyFile(String path) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			File f = new File(path);
			fis = new FileInputStream(f);
			fos = new FileOutputStream(this.desPath + File.separator
					+ f.getName());
			int byteread = 0;
			byte[] buffer = new byte[1024];
			while ((byteread = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, byteread);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		// String path = "D:\\Project\\Project01\\Rialto\\rialto";
		// String path =
		// "D:\\Project\\Project01\\Rialto\\rialto\\rialto.container.admin";
		// String path = "D:\\Project\\Project01\\Otter\\Otter3.0.0\\Otter3.0";
		// String path =
		// "D:\\Project\\Project01\\Napoli1.3\\maven.1273199094711\\napoli.client";
		// String path =
		// "D:\\Project\\Project01\\NapoliTest\\NapoliClient1.3.0";
		String path = "D:\\Project\\DemoSpace\\XMLDemo";
		LibraryCopy lc = new LibraryCopy();
		lc.setSrcPath(path);
		lc.setDesPath("D:\\Project\\lib");
		lc.copyLibrary();
		System.out.println("Mission Over");
	}
}
