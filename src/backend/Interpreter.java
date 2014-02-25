package backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commands.Command;
import commands.CommandFactory;
import exceptions.*;

public class Interpreter {
	/**
	 * TODO: change the methods so that this looks like a real API TODO: Make
	 * documentation for all public methods and vars (all classes)
	 */
	final static String[] MATH_COMMANDS = { "sum", "+", "difference", "-",
			"product", "*", "quotient", "/", "remainder", "%", "minus", "~",
			"random", "sin", "cos", "tan", "atan", "log", "pow", "less?",
			"lessp", "greater?", "greaterp", "equal?", "equalp", "notequal?",
			"notequalp", "and", "or", "not?" };
	/**
	 * TODO: add in advanced commands
	 */
	CommandFactory commandFactory;
	public Engine engine;

	public Interpreter() throws IOException {
		engine = new Engine();
		commandFactory = new CommandFactory(this, engine);
	}

	public ArrayList<Double> interpret(String text)
			throws PluralityOfValuesException, InvalidCommandStringException,
			InvalidWordException, NotEnoughParametersException,
			InvalidCommandException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		ArrayList<String> wordList = listOutCommands(text);
		ArrayList<Double> evaluatedValues = new ArrayList<Double>();
		while (wordList.size() > 0) {
			evaluatedValues.add(evaluateCommand(wordList));
		}
		return evaluatedValues;
	}

	public ArrayList<String> listOutCommands(String commands) {
		ArrayList<String> listOfWords = new ArrayList<String>();
		ArrayList<String> listOfCommandBlocks = new ArrayList<String>();
		String[] words = commands.split("\\s+");
		Pattern p = Pattern.compile("\\[(.*?)\\]");
		Matcher m = p.matcher(commands);
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (words[i].startsWith("[")) {
				while (!words[i].endsWith("]")) {
					i++;
				}
				m.find();
				word = m.group(1);
			}
			listOfWords.add(word);
			System.out.println("added beginning[" + word + "]end");
		}
		return listOfWords;
	}

	private Double evaluateCommand(ArrayList<String> wordList)
			throws InvalidCommandStringException, InvalidWordException,
			NotEnoughParametersException, InvalidCommandException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		String firstWord = wordList.remove(0);
		if (isConstantValue(firstWord)) {
			return Double.parseDouble(firstWord);
		} else if (isCommand(firstWord)) {
			firstWord = firstWord.toLowerCase();
			ArrayList<Double> parameters = new ArrayList<Double>();
			Command newCommand = commandFactory.createCommand(firstWord);
			for (int i = 0; i < newCommand.NUM_OF_PARAMETERS; i++) {
				if (wordList.size() > 0) {
					parameters.add(evaluateCommand(wordList));
				} else {
					throw new NotEnoughParametersException();
				}
			}
			newCommand.loadParameters(parameters);
			return engine.obey(newCommand);
		} else {
			throw new InvalidWordException();
		}
	}

	private boolean isCommand(String word) {
		return commandFactory.commands.containsKey(word.toLowerCase());
	}

	private boolean isConstantValue(String word) {
		try {
			Double.parseDouble(word);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
