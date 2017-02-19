package console;

import com.badlogic.gdx.utils.Array;

import console.Console.Type;

/**
 * 
 * @author timtrussner
 *
 */
public class Cmd {
	String command;
	public Array<CommandListener> cmdListeners = new Array<CommandListener>();
	public Type type;
	
	public Cmd(String command, Type type) {
		this.command = command;
		this.type = type;
	}
	
	public void executed(String arg) {
		for(CommandListener cmdl : cmdListeners) {
			if(type==Type.NONE) {
				cmdl.executed();
			} else if(type==Type.INT) {
				int result = 0;
				try {
				      result = Integer.parseInt(arg);
				} catch (NumberFormatException e) {
					System.out.println("Couldn't parse argument");
					return;
				}
				cmdl.executed(result);
			} else if(type==Type.FLOAT) {
				float result = 0;
				try {
				      result = Float.parseFloat(arg);
				} catch (NumberFormatException e) {
					System.out.println("Couldn't parse argument");
					return;
				}
				cmdl.executed(result);
				
			} else if(type==Type.STRING) {
				cmdl.executed(arg);
			}
		}
	}
	
	public void addListener(CommandListener cmdListener) {
		this.cmdListeners.add(cmdListener);
	}
}
