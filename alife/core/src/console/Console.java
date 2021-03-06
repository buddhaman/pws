package console;

import java.util.Scanner;

import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.bcel.internal.generic.Type;

public class Console implements Runnable {

	/**
	 * quick and very dirty console where everything is static
	 */
	
	public static Array<Cmd> commands = new Array<Cmd>();
	
	public enum Type {
		NONE, INT, FLOAT, STRING
	}
	
	public Console() {
		
	}
	
	@Override
	public void run() {
		
		Scanner input = new Scanner(System.in);
		String parserArgs = "";
		
		while(parserArgs != "quit") {
			parserArgs = input.nextLine();
			
			//parse
			String[] split = parserArgs.split("\\s+");
			//max length 2
			if(split.length >2 || split.length==0)
				continue;
			for(Cmd cmd : commands) {
				if(cmd.command.equals(split[0])) {
					//last word in line is argument
					if(cmd.type==Type.NONE)
						cmd.executed(split[split.length-1]);
					else {
						if(split.length==1) {
							print("missing argument");
							break;
						} else{
							cmd.executed(split[split.length-1]);
						}
					}
				}
			}
		}

		print(parserArgs);
		input.close();
	}
	
	public static void print(String text) {
		System.out.println(text);
	}
	
	public static void printCommands() { 
		for(int i = 0; i < commands.size; i++) {
			Cmd command = commands.get(i);
			print(command.command + " [" + command.type + "]");
		}
	}
	
	public static void createCommand(String command, Type argType, CommandListener listener) {
		Cmd cmd = addCommand(command, argType);
		cmd.addListener(listener);
	}
	
	public static Cmd addCommand(String command, Type argType) {
		Cmd cmd = new Cmd(command, argType);
		commands.add(cmd);
		return cmd;
	}
}
