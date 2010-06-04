package org.apache.jmeter.libcopy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class LibraryCopy {
	private String srcPath;
	private String desPath;
	private File srcFile;
	private Set libs = new HashSet();

	public LibraryCopy() {

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
					System.out.println(fs[i].getAbsolutePath());
					parseClassPathFile(fs[i]);
				} else {
					continue;
				}
			}
		}
	}

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
				String type=classpathentry.attributeValue("kind");
				if (type.equals("var")) {
					String path = classpathentry.attributeValue("path");
					String m2_repo = "C:\\Documents and Settings\\chenchao.yecc\\.m2\\repository";
					path = path.replace("M2_REPO", m2_repo);
					path = path.replace("/", "\\");
					copyFile(path);
				} else if (type.equals("lib")) {
					String path = classpathentry.attributeValue("path");
					if (!path.contains(":")) {
						path=srcPath+File.separator+path;
						path = path.replace("/", "\\");
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
		FileInputStream fis=null;
		FileOutputStream fos=null;
		try {
			File f=new File(path);
			fis=new FileInputStream(f);
			fos=new FileOutputStream(this.desPath+File.separator+f.getName());
			byte[] b =new byte[1024];
			while(fis.read(b)!=-1){
				fos.write(b);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		String path = "D:\\Project\\Project01\\Rialto\\rialto";
//		String path = "D:\\Project\\Project01\\Rialto\\rialto\\rialto.container.admin";
//		String path = "D:\\Project\\Project01\\Otter\\Otter3.0.0\\Otter3.0";
//		String path = "D:\\Project\\Project01\\Napoli1.3\\maven.1273199094711\\napoli.client";
		LibraryCopy lc = new LibraryCopy();
		lc.setSrcPath(path);
		lc.setDesPath("D:\\Project\\lib");
		lc.copyLibrary();
		System.out.println("Mission Over");
	}

}
