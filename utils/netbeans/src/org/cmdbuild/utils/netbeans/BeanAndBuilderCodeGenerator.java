/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.netbeans;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.ModificationResult;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

public class BeanAndBuilderCodeGenerator implements CodeGenerator {

	private final Logger logger = Logger.getLogger("CMNDBuild code plugin");

	JTextComponent textComp;

	private BeanAndBuilderCodeGenerator(Lookup context) {
		textComp = context.lookup(JTextComponent.class);
	}

	@MimeRegistration(mimeType = "text/x-java", service = CodeGenerator.Factory.class)
	public static class Factory implements CodeGenerator.Factory {

		@Override
		public List<? extends CodeGenerator> create(Lookup context) {
			return Collections.singletonList(new BeanAndBuilderCodeGenerator(context));
		}
	}

	@Override
	public String getDisplayName() {
		return "Generate CMDBuild-stype bean getters/builder";
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
						FileObject fileObject = workingCopy.getFileObject();
						String content = fileObject.asText();
						String newContent = ClassRewriter.rewrite(content);
						try (OutputStream outputStream = fileObject.getOutputStream(); Writer writer = new OutputStreamWriter(outputStream)) {
							writer.write(newContent);
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
