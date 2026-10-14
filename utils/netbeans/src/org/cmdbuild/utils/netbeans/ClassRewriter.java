/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.netbeans;

import static java.lang.String.format;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassRewriter {

	private final String content;
	private String importToAdd, contentToAdd, output, className, builderClassName;
	private final List<String[]> attrs = new ArrayList<>();

	public ClassRewriter(String content) {
		this.content = content;
	}

	public static String rewrite(String content) {
		return new ClassRewriter(content).rewrite();

	}

	private String rewrite() {
		parse();
		build();
		write();
		return output;
	}

	private void parse() {
		{
			Matcher m1 = Pattern.compile("^[^;]*private +(final +)?([^ ]+) +([^;]+);", Pattern.MULTILINE).matcher(content);
			while (m1.find()) {
				String type = m1.group(2);
				String val = m1.group(3);
				Matcher m2 = Pattern.compile("([^, ]+)").matcher(val);
				while (m2.find()) {
					String varName = m2.group(1);
					String capVarname = varName.substring(0, 1).toUpperCase() + varName.substring(1);
					attrs.add(new String[]{type, varName, capVarname});
				}
			}
		}
		{
			Matcher matcher = Pattern.compile("class +([^ {]+)").matcher(content);
			matcher.find();
			className = matcher.group(1);
		}
	}

	private void build() {
		importToAdd = "\nimport static org.cmdbuild.utils.lang.MyPreconditions.checkNotBlank;\nimport static com.google.common.base.Preconditions.checkNotNull;\nimport org.cmdbuild.utils.lang.Builder;\n\n";

		builderClassName = className + "Builder";

		StringBuilder builder = new StringBuilder();

		builder.append(format("\n\n// processed vars:%s\n\n", attrs));

//		constructor
		builder.append("\nprivate ").append(className).append("(").append(builderClassName).append(" builder) {\n");
		attrs.forEach((attr) -> {
			builder.append("         this.").append(attr[1]).append(" = checkNotNull(builder.").append(attr[1]).append(");\n");
		});
		builder.append("    }\n");

		//getters
		attrs.forEach((attr) -> {
			builder.append("\n").append("    public ").append(attr[0]).append(" get").append(attr[2]).append("() {\n")
					.append("        return ").append(attr[1]).append(";\n    }\n");
		});

		//static build()
		builder.append("\n    public static ").append(builderClassName).append(" builder() {\n        return new ").append(builderClassName).append("();\n    }\n");

		//statuc copyOf()
		builder.append("\n    public static ").append(builderClassName).append(" copyOf(").append(className).append(" source) {\n        return new ").append(builderClassName).append("()");
		attrs.forEach((attr) -> {
			builder.append(format("\n        .with%s(source.get%s())", attr[2], attr[2]));
		});
		builder.append(";\n    }\n");

		//builder
		builder.append(format("\n        public static class %s implements Builder<%s,%s> {\n\n", builderClassName, className, builderClassName));
		attrs.forEach((attr) -> {
			builder.append(format("            private %s %s;\n", attr[0], attr[1]));
		});
		builder.append("\n");
		attrs.forEach((attr) -> {
			builder.append(format("\n            public %s with%s(%s %s) {\n                this.%s = %s;\n                return this;            }\n", builderClassName, attr[2], attr[0], attr[1], attr[1], attr[1]));
		});
		builder.append(format("\n            @Override\n            public %s build(){\n               return new %s(this);\n           }\n", className, className));
		builder.append("\n           }\n");

		contentToAdd = builder.toString();
	}

	private void write() {
		int importIndex, contentIndex;
		{
			Matcher matcher = Pattern.compile("public +class", Pattern.DOTALL).matcher(content);
			matcher.find();
			importIndex = matcher.start();
		}
		{
			Matcher matcher = Pattern.compile("[}]", Pattern.DOTALL).matcher(content);
			contentIndex = 0;
			while (matcher.find()) {
				contentIndex = matcher.start();
			}
		}

		StringBuilder builder = new StringBuilder();
		builder.append(content.substring(0, importIndex));
		builder.append(importToAdd);
		builder.append(content.substring(importIndex, contentIndex));
		builder.append(contentToAdd);
		builder.append(content.substring(contentIndex, content.length()));
		output = builder.toString();
	}

}
