package org.cmdbuild.workflow.xpdl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.util.List;
import org.cmdbuild.utils.io.BigByteArrayOutputStream;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.workflow.dao.RiverPlanRepositoryImpl.ATTR_BIND_TO_CLASS;
import org.cmdbuild.workflow.model.Process;
import static org.cmdbuild.workflow.model.WorkflowConstants.XPDL_ATTRIBUTES;
import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.BasicType;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Created;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.DataField;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.DataFields;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.DataType;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.DeclaredType;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ExtendedAttribute;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ExtendedAttributes;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ExternalReference;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.PackageHeader;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Package_v10;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Participant;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ParticipantType;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Participants;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.ProcessHeader;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Script;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.TypeDeclaration;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.TypeDeclarations;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.Vendor;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.WorkflowProcess_v10;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.WorkflowProcesses_v10;
import org.cmdbuild.workflow.river.engine.xpdl.schema.xpdl_21.XPDLVersion;

public class XpdlDocumentHelper {

    private static final String GRAMMAR_JAVA = "text/java";
//    private static final String GRAMMAR_JAVA_SCRIPT = "text/javascript";
//    private static final String GRAMMAR_PYTHON_SCRIPT = "text/pythonscript";
//    private static final String GRAMMAR_GROOVY = "text/groovy";

    private static final String DEFAULT_XPDL_VERSION = "2.1";

    private static final String DEFAULT_SYSTEM_PARTICIPANT = "System";

    private final Package_v10 xpdlPackage;

    public XpdlDocumentHelper() {
        xpdlPackage = new Package_v10();
    }

    public XpdlDocumentHelper createXpdlTemplateForProcess(Process process, List<String> groupNamesForRoleParticipant) {
        try {
            setPackage(process);
            setPackageHeader();
            setScript();
            setTypeDeclarations();
            setParticipants(groupNamesForRoleParticipant);
            setWorkflowProcesses(process);
            return this;
        } catch (Exception ex) {
            throw new WorkflowException(ex, "unable to create template");
        }
    }

    public byte[] xpdlByteArray() {
        try {
            BigByteArrayOutputStream bigByteArrayOutputStream = new BigByteArrayOutputStream();
            bigByteArrayOutputStream.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>".getBytes());

            Marshaller marshaller = JAXBContext.newInstance(Package_v10.class).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            marshaller.marshal(xpdlPackage, bigByteArrayOutputStream);
            return bigByteArrayOutputStream.toByteArray();
        } catch (Exception ex) {
            throw new WorkflowException(ex, "unable to create template");
        }
    }

    private void setPackage(Process process) {
        xpdlPackage.setId(getStandardPackageId(process));
    }

    private void setPackageHeader() {
        XPDLVersion xpdlVersion = new XPDLVersion();
        xpdlVersion.setValue(DEFAULT_XPDL_VERSION);
        PackageHeader packageHeader = new PackageHeader();
        packageHeader.setXPDLVersion(xpdlVersion);
        packageHeader.setVendor(new Vendor());
        packageHeader.setCreated(new Created());
        xpdlPackage.setPackageHeader(packageHeader);
    }

    private void setScript() {
        Script script = new Script();
        script.setType(GRAMMAR_JAVA);
        xpdlPackage.setScript(script);
    }

    private void setTypeDeclarations() {
        TypeDeclarations typeDeclarations = new TypeDeclarations();
        XPDL_ATTRIBUTES.forEach((k, v) -> {
            TypeDeclaration typeDeclaration = new TypeDeclaration();
            typeDeclaration.setId(k);
            ExternalReference externalReference = new ExternalReference();
            externalReference.setLocation(v);
            typeDeclaration.setExternalReference(externalReference);
            typeDeclarations.getTypeDeclaration().add(typeDeclaration);
        });
        xpdlPackage.setTypeDeclarations(typeDeclarations);
    }

    private void setParticipants(List<String> groupNamesForRoleParticipant) {
        Participants participants = new Participants();
        list(DEFAULT_SYSTEM_PARTICIPANT).with(groupNamesForRoleParticipant).forEach(role -> {
            Participant participant = new Participant();
            participant.setId(role);
            ParticipantType participantType = new ParticipantType();
            participantType.setType(role.equals(DEFAULT_SYSTEM_PARTICIPANT) ? "SYSTEM" : "ROLE");
            participant.setParticipantType(participantType);
            participants.getParticipant().add(participant);
        });
        xpdlPackage.setParticipants(participants);
    }

    private void setWorkflowProcesses(Process process) {
        WorkflowProcesses_v10 workflowProcesses = new WorkflowProcesses_v10();
        WorkflowProcess_v10 workflowProcess = new WorkflowProcess_v10();
        workflowProcess.setId(getStandardProcessDefinitionId(process));
        workflowProcess.setProcessHeader(new ProcessHeader());
        DataFields dataFields = new DataFields();
        DaoToXpdlAttributeTypeConverter typeConverter = new DaoToXpdlAttributeTypeConverter();
        process.getActiveServiceAttributes().stream().filter(a -> typeConverter.convertType(a.getType()) != null).forEach(a -> {
            StandardAndCustomTypes attributeConverted = typeConverter.convertType(a.getType());
            DataField dataField = new DataField();
            dataField.setId(a.getName());
            dataField.setIsArray(false);
            DataType dataType = new DataType();
            switch (attributeConverted.getElement()) {
                case BASICTYPE -> {
                    BasicType basicType = new BasicType();
                    basicType.setType(attributeConverted.name());
                    dataType.setBasicType(basicType);
                }
                case DECLAREDTYPE -> {
                    DeclaredType declaredType = new DeclaredType();
                    TypeDeclaration declaredTypeId = xpdlPackage.getTypeDeclarations().getTypeDeclaration().stream().filter(td -> td.getId().equals(attributeConverted.getType())).collect(onlyElement());
                    declaredType.setId(declaredTypeId);
                    dataType.setDeclaredType(declaredType);
                }
            }
            dataField.setDataType(dataType);
            dataFields.getDataField().add(dataField);
        });
        workflowProcess.setDataFields(dataFields);
        ExtendedAttributes extendedAttributes = new ExtendedAttributes();
        ExtendedAttribute extendedAttribute = new ExtendedAttribute();
        extendedAttribute.setName(ATTR_BIND_TO_CLASS);
        extendedAttribute.setValue(process.getName());
        extendedAttributes.getExtendedAttribute().add(extendedAttribute);
        workflowProcess.setExtendedAttributes(extendedAttributes);
        workflowProcesses.getWorkflowProcess().add(workflowProcess);
        xpdlPackage.setWorkflowProcesses(workflowProcesses);
    }

    private static String getStandardProcessDefinitionId(Process process) {
        return "Process_" + process.getName().toLowerCase();
    }

    private static String getStandardPackageId(Process process) {
        return "Package_" + process.getName().toLowerCase();
    }
}
