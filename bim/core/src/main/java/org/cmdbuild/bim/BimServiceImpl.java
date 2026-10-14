/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.config.BimConfiguration;
import org.cmdbuild.dao.beans.CMRelation;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeService;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.ifc.inner.Ifc2XktHelperImpl;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link BimService} interface, providing business logic
 * for BIM project and object management.
 * <p>
 * This service handles creation, update, deletion, and retrieval of BIM
 * projects and objects,
 * as well as conversion between IFC and XKT formats, file uploads/downloads,
 * and navigation tree integration.
 * It also implements the {@link MinionComponent} interface for runtime status
 * and configuration reload support.
 * </p>
 *
 * <p>
 * Key features:
 * <ul>
 * <li>Manages BIM projects and objects, including ownership and
 * relationships</li>
 * <li>Supports conversion of IFC files to XKT format using external
 * helpers</li>
 * <li>Handles file uploads and downloads for IFC and XKT files</li>
 * <li>Integrates with navigation tree for class and card lookups</li>
 * <li>Implements runtime status and configuration reload via MinionHandler</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is registered as a Spring component and depends on
 * {@link DaoService}, {@link BimConfiguration},
 * {@link NavTreeService}, {@link BimObjectRepository}, and
 * {@link BimProjectRepository}.
 * </p>
 */
@Component
public class BimServiceImpl implements BimService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final BimConfiguration configuration;
    private final NavTreeService navTreeService;
    private final BimObjectRepository objectRepository;
    private final BimProjectRepository projectRepository;
    private final MinionHandler minionHandler;

    /**
     * Constructs the BIM service implementation with required dependencies.
     *
     * @param dao               the DAO service for database access
     * @param configuration     the BIM configuration
     * @param navTreeService    the navigation tree service
     * @param objectRepository  the BIM object repository
     * @param projectRepository the BIM project repository
     */
    public BimServiceImpl(DaoService dao, BimConfiguration configuration, NavTreeService navTreeService,
            BimObjectRepository objectRepository, BimProjectRepository projectRepository) {
        this.configuration = checkNotNull(configuration);
        this.navTreeService = checkNotNull(navTreeService);
        this.objectRepository = checkNotNull(objectRepository);
        this.projectRepository = checkNotNull(projectRepository);
        this.dao = checkNotNull(dao);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("BIM Service")
                .withConfigEnabler("org.cmdbuild.bim.enabled")
                .withEnabledChecker(configuration::isEnabled)
                .withStatusChecker(() -> configuration.isEnabled() ? MRS_READY : MRS_NOTRUNNING)
                .reloadOnConfigs(BimConfiguration.class)
                .build();
    }

    /**
     * Returns the MinionHandler for runtime status and configuration reload.
     */
    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    /**
     * Returns whether the BIM service is enabled.
     */
    @Override
    public boolean isEnabled() {
        return MinionComponent.super.isEnabled();
    }

    /**
     * Sets the owner for a BIM project.
     *
     * @param data  the BIM project extension data
     * @param owner the owner card, or {@code null}
     * @return the updated BIM object, or {@code null}
     */
    @Override
    @Nullable
    public BimObject setOwnerForProject(BimProjectExt data, @Nullable CardIdAndClassName owner) {
        return objectRepository.setOwnerForProject(data, owner);
    }

    /**
     * Retrieves a BIM object for a project and global ID, or {@code null} if not
     * found.
     *
     * @param bimProject the BIM project
     * @param globalId   the global ID of the BIM object
     * @return the BIM object, or {@code null} if not found
     */
    @Override
    @Nullable
    public BimObject getBimObjectForProjectGlobalIdOrNull(BimProject bimProject, String globalId) {
        return objectRepository.getBimObjectForProjectGlobalIdOrNull(bimProject, globalId);
    }

    /**
     * Retrieves a BIM object for a global ID, or {@code null} if not found.
     * 
     * @param globalId the global ID of the BIM object
     * @return the BIM object, or {@code null} if not found
     * @deprecated Use projectId + globalId instead.
     */
    @Override
    @Nullable
    @Deprecated // global id may be duplicate, use projectid+globalid
    public BimObject getBimObjectForGlobalIdOrNull(String globalId) {
        return objectRepository.getBimObjectForGlobalIdOrNull(globalId);
    }

    /**
     * Returns all BIM projects.
     *
     * @return a collection of all BIM projects
     */
    @Override
    public Collection<BimProject> getAllProjects() {
        return projectRepository.getAllProjects();
    }

    /**
     * Returns a BIM project by its ID.
     *
     * @param id the project ID
     * @return the BIM project
     */
    @Override
    public BimProject getProjectById(long id) {
        return projectRepository.getProjectById(id);
    }

    /**
     * Returns the IFC data for a project by its project ID, or {@code null} if not
     * found.
     *
     * @param projectId the project ID
     * @return the BIM project IFC data, or {@code null}
     */
    @Override
    @Nullable
    public BimProjectIfc getProjectIfcByProjectId(String projectId) {
        return projectRepository.getProjectIfcByProjectId(projectId);
    }

    /**
     * Deletes the IFC data for a project by its project ID.
     *
     * @param projectId the project ID
     */
    @Override
    public void deleteProjectIfcByProjectId(String projectId) {
        projectRepository.deleteProjectIfcByProjectId(projectId);
    }

    /**
     * Deletes a BIM project by its ID.
     *
     * @param id the project ID
     */
    @Override
    public void deleteProjectById(long id) {
        projectRepository.deleteProjectById(id);
    }

    /**
     * Deletes a BIM object.
     *
     * @param bimObject the BIM object to delete
     */
    @Override
    public void delete(BimObject bimObject) {
        objectRepository.delete(bimObject);
    }

    /**
     * Creates a new BIM object.
     *
     * @param bimObject the BIM object to create
     * @return the created BIM object
     */
    @Override
    public BimObject create(BimObject bimObject) {
        return objectRepository.create(bimObject);
    }

    /**
     * Creates a new BIM project, assigning a random project ID.
     *
     * @param bimProject the BIM project to create
     * @return the created BIM project
     */
    @Override
    public BimProject createProject(BimProject bimProject) {
        String projId = randomId();
        bimProject = BimProjectImpl.copyOf(bimProject).withProjectId(projId).build();
        return projectRepository.createProject(bimProject);
    }

    /**
     * Updates a BIM project with new data.
     *
     * @param bimProject the BIM project with updated data
     * @return the updated BIM project
     */
    @Override
    public BimProject updateProject(BimProject bimProject) {
        BimProject current = getProjectById(bimProject.getId());
        bimProject = BimProjectImpl.copyOf(current)
                .withActive(bimProject.isActive())
                .withDescription(bimProject.getDescription())
                .withImportMapping(bimProject.getImportMapping())
                .build();
        // TODO handle active/inactive on bim server
        return projectRepository.updateProject(bimProject);
    }

    /**
     * Retrieves a BIM project extension by its code, or {@code null} if not found.
     *
     * @param projectCode the project code
     * @return the BIM project extension, or {@code null}
     */
    @Override
    @Nullable
    public BimProjectExt getProjectByCodeOrNull(String projectCode) {
        return projectRepository.getAllProjects().stream().filter(p -> equal(p.getName(), projectCode))
                .collect(toOptional()).map(p -> getProjectExt(p.getId())).orElse(null);
    }

    /**
     * Creates a new BIM project extension, including owner object if present.
     *
     * @param data the BIM project extension data
     * @return the created BIM project extension
     */
    @Override
    public BimProjectExt createProjectExt(BimProjectExt data) {
        BimProject created = createProject(data);
        if (data.hasOwner()) {
            objectRepository.createBimObjectForProject(created, data.getOwner());
        }
        return getProjectExt(created.getId());
    }

    /**
     * Updates a BIM project extension and its owner.
     *
     * @param data the BIM project extension data
     * @return the updated BIM project extension
     */
    @Override
    public BimProjectExt updateProjectExt(BimProjectExt data) {
        updateProject(data);
        objectRepository.setOwnerForProject(data, data.getOwnerOrNull());
        return getProjectExt(data.getId());
    }

    /**
     * Checks if a class has BIM data in the navigation tree.
     *
     * @param classe the class to check
     * @return {@code true} if the class has BIM data, {@code false} otherwise
     */
    @Override
    public boolean hasBim(Classe classe) {
        NavTree navTree = getNavTreeOrNull();
        return navTree != null && navTree.getData().getThisNodeAndAllDescendants().stream()
                .anyMatch(n -> equal(n.getTargetClassName(), classe.getName()));
    }

    /**
     * Retrieves a BIM object for a card, or {@code null} if not found.
     *
     * @param card the card ID and class name
     * @return the BIM object, or {@code null}
     */
    @Override
    @Nullable
    public BimObject getBimObjectForCardOrNull(CardIdAndClassName card) {
        return objectRepository.getBimObjectForCardOrNull(card);
    }

    /**
     * Retrieves a BIM object for a card, searching via navigation tree if not found
     * directly.
     *
     * @param card the card ID and class name
     * @return the BIM object, or {@code null}
     */
    @Override
    @Nullable
    public BimObject getBimObjectForCardOrViaNavTreeOrNull(CardIdAndClassName card) {
        BimObject obj = getBimObjectForCardOrNull(card);
        if (obj != null) {
            return obj;
        } else {
            NavTree navTree = getNavTreeOrNull();
            if (navTree != null) {
                NavTreeNode node = navTree.getData().getThisNodeAndAllDescendants().stream()
                        .filter(n -> dao.getType(card).equalToOrDescendantOf(n.getTargetClassName()))
                        .collect(toOptional()).orElse(null);
                if (node != null && node.hasParent()) {
                    CMRelation relation = dao.selectAll().from(dao.getDomain(node.getDomainName()))
                            .where(node.getDirect() ? ATTR_IDOBJ2 : ATTR_IDOBJ1, EQ, card.getId()).getRelationOrNull();
                    if (relation != null) {
                        return getBimObjectForCardOrViaNavTreeOrNull(
                                relation.getRelationWithSource(card.getId()).getTargetCard());
                    }
                }
            }
            return null;
        }
    }

    /**
     * Retrieves a BIM object for a project, or {@code null} if not found.
     *
     * @param bimProject the BIM project
     * @return the BIM object, or {@code null}
     */
    @Override
    @Nullable
    public BimObject getBimObjectForProjectOrNull(BimProject bimProject) {
        return objectRepository.getBimObjectForProjectOrNull(bimProject);
    }

    /**
     * Downloads the IFC file for a project as a {@link DataHandler}.
     *
     * @param projectId the project ID
     * @param ifcFormat the IFC format (optional)
     * @return a {@link DataHandler} for the IFC file
     */
    @Override
    public DataHandler downloadIfcFile(long projectId, @Nullable String ifcFormat) {
        BimProject project = projectRepository.getProjectById(projectId);
        BimProjectIfc projectIfcByProjectId = projectRepository.getProjectIfcByProjectId(project.getProjectId());
        return newDataHandler(projectIfcByProjectId.getIfcDecompressedFile(), "text/plain",
                format("%s.ifc", project.getName()));
    }

    /**
     * Downloads the XKT file for a project as a {@link DataHandler}.
     *
     * @param projectId the project ID
     * @return a {@link DataHandler} for the XKT file
     */
    @Override
    public DataHandler downloadXktFile(long projectId) {
        BimProject project = projectRepository.getProjectById(projectId);
        return newDataHandler(project.getXktFile(), "application/octet-stream", format("%s.xkt", project.getName()));
    }

    /**
     * Converts the IFC file of a project to XKT format and updates the project.
     *
     * @param projectId the project ID
     * @return the updated BIM project with XKT file
     */
    @Override
    public BimProject convertIfcProjectToXkt(long projectId) {
        BimProject project = projectRepository.getProjectById(projectId);
        BimProjectIfc projectIfcByProjectId = projectRepository.getProjectIfcByProjectId(project.getProjectId());
        byte[] downloadedIfc = projectIfcByProjectId.getIfcDecompressedFile();
        if (downloadedIfc == null) {
            throw runtime("Unable to convert project with id %s ifc not found", projectId);
        } else {
            byte[] xktFile = new Ifc2XktHelperImpl(configuration).ifc2Xkt(downloadedIfc);
            return dao.update(BimProjectImpl.copyOf(project).withXktFile(xktFile).withLastCheckin(now()).build());
        }
    }

    /**
     * Uploads an XKT file for a project, converting from IFC and handling errors
     * for new projects.
     *
     * @param projectId    the project ID
     * @param dataHandler  the data handler for the XKT file
     * @param isNewProject whether this is a new project
     * @return the updated BIM project
     */
    @Override
    public BimProject uploadXktFile(long projectId, DataHandler dataHandler, boolean isNewProject) {
        BimProject project = projectRepository.getProjectById(projectId);
        BimProjectIfc projectIfcByProjectId = projectRepository.getProjectIfcByProjectId(project.getProjectId());
        uploadIfcToDatabase(projectIfcByProjectId, dataHandler, project.getProjectId());
        try {
            return convertIfcProjectToXkt(projectId);
        } catch (Exception ex) {
            if (isNewProject) {
                objectRepository.delete(objectRepository.getBimObjectForProjectOrNull(project));
                projectRepository.deleteProjectById(projectId);
                projectRepository.deleteProjectIfcByProjectId(project.getProjectId());
                logger.error("Unable to convert project with id {}, removed bim project", projectId);
            } else {
                logger.error("There was a problem with the project {} conversion", projectId);
            }
            throw new EtlException(ex, "error converting ifc project to xkt");
        }
    }

    /**
     * Retrieves a BIM project extension by its ID.
     *
     * @param projectId the project ID
     * @return the BIM project extension
     */
    @Override
    public BimProjectExt getProjectExt(long projectId) {
        BimProject project = getProjectById(projectId);
        BimObject bimObject = getBimObjectForProjectOrNull(project);
        return new BimProjectExtImpl(project, bimObject);
    }

    /**
     * Returns all BIM projects and their associated objects.
     *
     * @return a list of all BIM project extensions
     */
    @Override
    public List<BimProjectExt> getAllProjectsAndObjects() {
        return getAllProjects().stream().map((p) -> {
            BimObject bimObject = getBimObjectForProjectOrNull(p);// TODO this is inefficent, just a quick fix; change
                                                                  // bim service to run a join query or something else
            return new BimProjectExtImpl(p, bimObject);
        }).collect(toImmutableList());
    }

    /**
     * Deletes a BIM project and all associated objects and IFC data.
     *
     * @param id the project ID
     */
    @Override
    public void deleteProject(long id) {
        String globalProjectId = projectRepository.getProjectById(id).getProjectId();
        dao.selectAll().from(BimObject.class).where("ProjectId", EQ, checkNotBlank(toStringOrNull(globalProjectId)))
                .getCards().forEach(e -> {
                    dao.delete(e);
                });
        projectRepository.deleteProjectById(id);
        projectRepository.deleteProjectIfcByProjectId(globalProjectId);
    }

    /**
     * Creates a BIM object for a project and card.
     *
     * @param bimProject the BIM project
     * @param card       the card ID and class name
     * @return the created BIM object
     */
    @Override
    public BimObject createBimObjectForProject(BimProject bimProject, CardIdAndClassName card) {
        return objectRepository.createBimObjectForProject(bimProject, card);
    }

    /**
     * Updates a BIM object, replacing existing objects if necessary.
     *
     * @param bimObject the BIM object to update
     * @return the updated BIM object
     */
    @Override
    public BimObject updateBimObject(BimObject bimObject) {
        BimObject current = objectRepository.getBimObjectForCardOrNull(bimObject.getOwnerCard());
        if (current != null && equal(current.getProjectId(), bimObject.getProjectId())
                && equal(current.getGlobalId(), bimObject.getGlobalId())) {
            // nothing to do
            return current;
        } else {
            Optional.ofNullable(current).ifPresent(objectRepository::delete);
            Optional.ofNullable(objectRepository.getBimObjectForGlobalIdOrNull(bimObject.getGlobalId()))
                    .ifPresent(objectRepository::delete);
            return objectRepository.create(bimObject);
        }
    }

    /**
     * Returns the navigation tree for BIM, or {@code null} if not available.
     *
     * @return the navigation tree, or {@code null}
     */
    @Nullable
    private NavTree getNavTreeOrNull() {
        return navTreeService.getTreeOrNull(BIM_NAV_TREE);
    }

    /**
     * Uploads an IFC file to the database for a project, creating or updating as
     * needed.
     *
     * @param bimProjectIfc the BIM project IFC data (may be {@code null} if not
     *                      existing)
     * @param ifcFile       the IFC file data handler
     * @param projectId     the project ID
     */
    private void uploadIfcToDatabase(BimProjectIfc bimProjectIfc, DataHandler ifcFile, String projectId) {
        if (bimProjectIfc == null) {
            logger.info(format("Ifc for project id %s not existing, creating", projectId));
            dao.create(BimProjectIfcImpl.builder().withProjectId(projectId).withIfcFile(toByteArray(ifcFile)).build());
        } else {
            logger.info(format("Ifc for project id %s already existing, updating", projectId));
            dao.update(BimProjectIfcImpl.copyOf(bimProjectIfc).withIfcFile(toByteArray(ifcFile)).build());
        }
    }

}
