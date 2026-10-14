/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.netbeans.tester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.util.Arrays.asList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegextTester {

	public static void main(String[] args) throws IOException {
		String content = "";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(RegextTester.class.getResourceAsStream("file.txt")))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content += line + "\n";
			}
		}
		{
			Matcher matcher = Pattern.compile("^[^;]*private +([^ ]+) +([^;]+);", Pattern.MULTILINE).matcher(content);
			while (matcher.find()) {
//				System.out.println(matcher.group());
				String type = matcher.group(1);
				String val = matcher.group(2);
//				System.out.println(matcher.group(2));
//				for (int i = 2; i < matcher.groupCount(); i += 1) {
//				for (int i = 3; i < matcher.groupCount(); i += 2) {
				Matcher m2 = Pattern.compile("([^, ]+)").matcher(val);
				while (m2.find()) {
					String varName = m2.group(1);
					String capVarname = varName.substring(0, 1).toUpperCase() + varName.substring(1);
					System.out.println(asList(type, varName, capVarname).toString());
//					attrs.add(new String[]{type, varName, capVarname});
				}
			}
		}
	}
}
