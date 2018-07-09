package javapkg;

import java.io.IOException;
import java.util.ArrayList;

public class Util {
	public static int forceInt(ABC_compiler compiler, String str) throws IOException {
		if (str.contains(".")) {
			System.out.println("Warning: force cast from " +
					"double[" + str + "] to int.");
			compiler.sendErrorMsg(new ErrorType(ABC_Constant.WARNING,
					"Warning: force cast from "
					+ "double[" + str + "] to int."));
			return (int) Double.parseDouble(str);
		} else {
			return Integer.parseInt(str);
		}
	}

	public static void addFuncSentence(
			ABC_compiler compiler, String funcName, int objectName, int objectID)
			throws ParseException, IOException {
		if (compiler.funcMap.containsKey(funcName)) {
			ArrayList<Command> list = compiler.funcMap.get(funcName);
			for (int i = 0; i < list.size(); i++) {
				Command command = list.get(i);
				if (command.getFunctionName() != ABC_Constant.FUNCTION) {
					Sentence sentence = command.toSentence(compiler);
					sentence.setObjectId(objectName);
					sentence.setObjectnum(objectID);
//					sentence.display();
					compiler.sentences.add(sentence);
				} else {
					addFuncSentence(compiler, command.getMyFuncName(), objectName, objectID);
				}
			}
		} else {
			throw new ParseException("Error: function '"+funcName+"()' isn't defined.");
		}
	}

	public static void addExecStmt(
			ABC_compiler compiler, ASTIfConditionStatement conditionStmt,
			String funcName, int objectName, int objectID)
			throws ParseException, IOException {
		if (compiler.funcMap.containsKey(funcName)) {
			ArrayList<Command> list = compiler.funcMap.get(funcName);
			for (int i = 0; i < list.size(); i++) {
				Command command = list.get(i);
				if (command.getFunctionName() != ABC_Constant.FUNCTION) {
					Sentence sentence = command.toSentence(compiler);
					sentence.setObjectId(objectName);
					sentence.setObjectnum(objectID);
//					sentence.display();
					conditionStmt.addExecStmt(sentence);
				} else {
					addExecStmt(compiler, conditionStmt,
							command.getMyFuncName(), objectName, objectID);
				}
			}
		} else {
			throw new ParseException("Error: function '"+funcName+"()' isn't defined.");
		}
	}

	public static void addExecStmt(
			ABC_compiler compiler, ASTWhileConditionStatement conditionStmt,
			String funcName, int objectName, int objectID)
			throws ParseException, IOException {
		if (compiler.funcMap.containsKey(funcName)) {
			ArrayList<Command> list = compiler.funcMap.get(funcName);
			for (int i = 0; i < list.size(); i++) {
				Command command = list.get(i);
				if (command.getFunctionName() != ABC_Constant.FUNCTION) {
					Sentence sentence = command.toSentence(compiler);
					sentence.setObjectId(objectName);
					sentence.setObjectnum(objectID);
//					sentence.display();
					conditionStmt.addExecStmt(sentence);
				} else {
					addExecStmt(compiler, conditionStmt,
							command.getMyFuncName(), objectName, objectID);
				}
			}
		} else {
			throw new ParseException("Error: function '"+funcName+"()' isn't defined.");
		}
	}

	public static void closeScope(ABC_compiler compiler) {
//		System.out.println("variable table (before):");
//		for (Variable variable : compiler.variables) {
//			System.out.println(variable.getName());
//		}
		compiler.variables.removeIf(variable -> variable.getScope() >= compiler.scope);
//		System.out.println("variable table (after):");
//		for (Variable variable : compiler.variables) {
//			System.out.println(variable.getName());
//		}
	}
}
