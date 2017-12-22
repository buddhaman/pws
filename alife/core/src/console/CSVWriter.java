package console;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class CSVWriter {
	/*
	 * write comma seperated values to a text file
	 */
	
	StringBuilder stringBuilder;
	private FileHandle csvFolder;
	protected boolean writing = false;
	
	
	public static final String CSV_FOLDER = "experimentCSV";
	
	
	public CSVWriter() {
		this.csvFolder = Gdx.files.local(CSV_FOLDER);
		if(!csvFolder.exists())
			csvFolder.mkdirs();
	}
	
	public void beginWriting() {
		this.stringBuilder = new StringBuilder();
		writing = true;
	}
	
	
	/**
	 * @param path - path to write file to
	 * write all contents in the stringbuilder to a text file
	 */
	public void endWriting(String name) {
		//chek if writing and set to false
		if(!writing) {
			System.err.println("call beginWriting() first");
			return;
		}
		writing = false;
		
		//write to file
		FileHandle handle = Gdx.files.local(csvFolder+"/"+name+".csv");
		handle.writeString(this.stringBuilder.toString(), false);
	}
	
	/**
	 * @param linename name of line
	 * @param values values to write
	 */
	public void writeLine(String lineName, List<Float> values) {
		//check if writing to a file
		if(!writing) {
			System.err.println("call beginWriting() first");
			return;
		}
		
		stringBuilder.append(lineName);
		for(int i = 0; i < values.size(); i++) {
			stringBuilder.append(',').append(values.get(i));
		}
		stringBuilder.append('\n');
	}
}
