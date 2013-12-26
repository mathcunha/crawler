package br.mia.unifor.crawlerenvironment.engine.view.rest.resources;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import br.mia.unifor.crawlerenvironment.Main;
import br.mia.unifor.crawlerenvironment.engine.view.JSONHelper;
import br.mia.unifor.crawlerenvironment.view.model.FileStore;

@Path("/v1/filestore")
public class FileStoreResource extends CloudCrawlerEnvironmentResource {

	public final String baseDir;

	public FileStoreResource() {
		baseDir = Main.properties.getProperty("fs.base.dir")+ System.getProperty("file.separator");;
	}

	@POST
	@Produces("application/json")
	@Consumes("text/plain")
	public String insertFileResource(String resource) {
		File file = insertFileResource(resource, baseDir) ;
		
		FileStore fs = new FileStore();
		
		if(file != null){
			fs.setName(file.getName());
			fs.setStatus("saved");
		}else{
			fs.setStatus("error");
		}
		
		return JSONHelper.getJSON(fs);
	}

	public File insertFileResource(String resource, String path) {
		String fileName = getFileName(resource);
		String fileContent = getFileContent(resource).toString();

		FileWriter fWriter;
		try {
			logger.info("Saving file " + path + fileName);
			File file = new File(path + fileName);
			if (createFile(file)) {
				fWriter = new FileWriter(file);

				fWriter.write(fileContent);

				fWriter.flush();

				fWriter.close();

				logger.info("file saved");

				return file;
			} else {
				logger.log(Level.WARNING, "file already exists");
				return file;
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, "failure saving the resource file", e);
		}
		return null;
	}

	private boolean createFile(File file) throws IOException {
		if(file.createNewFile()){
			return true;
		}else{
			if(file.delete()){
				return createFile(file);
			}else{
				logger.info("File already exists, but could not be deleted");
				return false;
			}
			
		}
	}
	
	private List<FileStore> getFiles(File directory){
		logger.fine("directory "+directory.getName());
		List<FileStore> list = new ArrayList<FileStore>();
		
		for (File file : directory.listFiles()) {
			logger.fine("list "+file.getName());
			FileStore fs = new FileStore();
			list.add(fs);
			
			fs.setName(file.getName());
			fs.setStatus("saved");
			if (file.isDirectory()){
				fs.setFiles(getFiles(file));
			}
		}
		
		return list;
	}

	@GET
	@Produces("application/json")
	public String listFileResources() {

		File directory = new File(baseDir);
		
		return JSONHelper.getJSON(getFiles(directory));		
	}

	@GET
	@Path("{fileName}")
	@Produces("text/plain")
	public String getFileResource(@PathParam("fileName") String fileName) {
		logger.info("file name is " + fileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(baseDir
					+ fileName));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}

			br.close();

			return sb.toString();

		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "resource " + fileName + " not found", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "failure loading the resource file", e);
		}

		return null;
	}

	@DELETE
	@Path("{fileName}")
	@Produces("application/json")
	public String deleteFileResource(@PathParam("fileName") String fileName) {
		FileStore fs = new FileStore();
		
		File file = new File(baseDir + fileName);
		fs.setName(file.getName());
		file.delete();
		fs.setStatus("deleted");

		return JSONHelper.getJSON(fs);
	}

	public static String getFileName(String crawlFile) {
		String[] lines = crawlFile.split(System.getProperty("line.separator"));

		String name = null;

		String pattern = "; filename=";

		for (String line : lines) {

			if (line.indexOf("Content-Disposition") != -1) {
				name = line.substring(line.indexOf(pattern) + pattern.length()
						+ 1, line.length() - 2);
			}

		}

		return name;
	}

	public static StringBuffer getFileContent(String crawlFile) {
		String[] lines = crawlFile.split(System.getProperty("line.separator"));

		List<String> lstLines = new ArrayList<String>();
		String hash = null;
		boolean read = false;

		for (String line : lines) {
			if (hash == null) {
				hash = line.substring(line.lastIndexOf("-") + "-".length(),
						line.length() - 1);
				logger.info("file hash [" + hash + "]");
			}
			if (!read) {
				read = "".equals(line.trim());
				continue;
			}

			if (line.indexOf(hash) != -1) {
				logger.info("EOF");
				break;// End of file
			} else {
				lstLines.add(line);				
			}

		}
		StringBuffer buffer = new StringBuffer();
		for (String line : lstLines) {
			buffer.append(line+"\n");
		}
		
		return buffer;
	}

	public static void copyFile(File srcFile, File destFile) throws IOException {
		InputStream oInStream = new FileInputStream(srcFile);
		OutputStream oOutStream = new FileOutputStream(destFile);

		// Transfer bytes from in to out
		byte[] oBytes = new byte[1024];
		int nLength;
		BufferedInputStream oBuffInputStream = new BufferedInputStream(
				oInStream);
		while ((nLength = oBuffInputStream.read(oBytes)) > 0) {
			oOutStream.write(oBytes, 0, nLength);
		}
		oInStream.close();
		oOutStream.close();
	}

}
