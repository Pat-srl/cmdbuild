/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.netbeans;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.VariableTree;
import java.io.IOException;
import static java.lang.String.format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Modifier;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.util.Lookup;

public class ConstructorCodeGenerator implements CodeGenerator {

	private final Logger logger = Logger.getLogger("CMNDBuild code plugin");

	JTextComponent textComp;

	private ConstructorCodeGenerator(Lookup context) {
		textComp = context.lookup(JTextComponent.class);
	}

	@MimeRegistration(mimeType = "text/x-java", service = CodeGenerator.Factory.class)
	public static class Factory implements CodeGenerator.Factory {

		@Override
		public List<? extends CodeGenerator> create(Lookup context) {
			return Collections.singletonList(new ConstructorCodeGenerator(context));
		}
	}

	@Override
	public String getDisplayName() {
		return "Generate CMDBuild-stype service constructor";
	}

	private static String memberSelectToString(Tree mst) {
		if (mst instanceof MemberSelectTree) {
			String name = ((MemberSelectTree) mst).getIdentifier().toString();
			return memberSelectToString(((MemberSelectTree) mst).getExpression()) + "." + name;
		} else if (mst instanceof IdentifierTree) {
			return ((IdentifierTree) mst).getName().toString();
		} else {
			return "[" + mst.getKind() + "]";// for debug
		}
	}

	@Override
	public void invoke() {
		try {
			Document doc = textComp.getDocument();
			JavaSource javaSource = JavaSource.forDocument(doc);
			CancellableTask task = new CancellableTask<WorkingCopy>() {
				@Override
				public void run(WorkingCopy workingCopy) throws IOException {
					logger.info("running");
					try {
						workingCopy.toPhase(Phase.RESOLVED);
						CompilationUnitTree cut = workingCopy.getCompilationUnit();
						TreeMaker make = workingCopy.getTreeMaker();

						Set<String> imports = new HashSet<>();
						for (ImportTree imp : cut.getImports()) {
							imports.add(memberSelectToString(imp.getQualifiedIdentifier()));
						}

						for (Tree typeDecl : cut.getTypeDecls()) {
							if (Tree.Kind.CLASS == typeDecl.getKind()) {
								ClassTree classe = (ClassTree) typeDecl;

								String body = "";
								List<VariableTree> vars = new ArrayList<>();

								int pos = 0, lastVarPos = 0;

								for (Tree t : classe.getMembers()) {
									if (t.getKind() == Tree.Kind.VARIABLE) {
										VariableTree variable = (VariableTree) t;

										String varName = variable.getName().toString();
										ExpressionTree expressionTree = variable.getInitializer();
										ModifiersTree modifiers = variable.getModifiers();

										boolean notInitialized = expressionTree == null;
										boolean isFinal = modifiers != null && modifiers.getFlags() != null && modifiers.getFlags().contains(Modifier.FINAL);

										if (notInitialized && isFinal) {
											body += format("\t\tthis.%s = checkNotNull(%s);\n", varName, varName);

											IdentifierTree identifier = (IdentifierTree) variable.getType();

											VariableTree parameter = make.Variable(make.Modifiers(Collections.<Modifier>emptySet(), Collections.<AnnotationTree>emptyList()),
													varName,
													make.Identifier(identifier.getName()),
													null);

											vars.add(parameter);

										}
										lastVarPos = pos;
									}
									pos++;
								}

								ModifiersTree methodModifiers = make.Modifiers(Collections.<Modifier>singleton(Modifier.PUBLIC), Collections.<AnnotationTree>emptyList());

								MethodTree newMethod = make.Constructor(methodModifiers,
										Collections.<TypeParameterTree>emptyList(),
										vars,
										Collections.<ExpressionTree>emptyList(),
										format("{\n%s\n\t}", body));

								ClassTree modifiedClazz = make.insertClassMember(classe, lastVarPos + 1, newMethod);
								workingCopy.rewrite(classe, modifiedClazz);

								if (!vars.isEmpty() && !imports.contains("com.google.common.base.Preconditions.checkNotNull")) {
//									ImportTree importTree = make.Import(typeDecl, true); TODO auto add import 
////									cut.getImports().add((ImportTree) importTree);
//									workingCopy.
								}
							}
						}

					} catch (Exception ex) {
						logger.log(Level.SEVERE, "error", ex);
					}
				}

				@Override
				public void cancel() {
				}
			};
			ModificationResult result = javaSource.runModificationTask(task);

			result.commit();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "error", ex);
		}
	}
}
