package sh.isaac.solor.direct.clinvar.writers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sh.isaac.api.AssemblageService;
import sh.isaac.api.Get;
import sh.isaac.api.IdentifierService;
import sh.isaac.api.LookupService;
import sh.isaac.api.bootstrap.TermAux;
import sh.isaac.api.chronicle.Chronology;
import sh.isaac.api.chronicle.VersionType;
import sh.isaac.api.commit.StampService;
import sh.isaac.api.component.concept.ConceptService;
import sh.isaac.api.index.IndexBuilderService;
import sh.isaac.api.task.TimedTaskWithProgressTracker;
import sh.isaac.model.concept.ConceptChronologyImpl;
import sh.isaac.model.semantic.SemanticChronologyImpl;
import sh.isaac.model.semantic.version.ComponentNidVersionImpl;
import sh.isaac.model.semantic.version.StringVersionImpl;
import sh.isaac.solor.direct.clinvar.model.ConceptArtifact;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 2019-03-07
 * aks8m - https://github.com/aks8m
 */
public class GenomicConceptWriter extends TimedTaskWithProgressTracker<Void> {

    private final Set<ConceptArtifact> conceptArtifacts;
    private final Semaphore writeSemaphore;
    private final StampService stampService;
    private final ConceptService conceptService;
    private final AssemblageService assemblageService;
    private final List<IndexBuilderService> indexers;
    private final int batchSize = 10000;
    private final IdentifierService identifierService;
    private static final Logger LOG = LogManager.getLogger();

    public GenomicConceptWriter(Set<ConceptArtifact> conceptArtifacts, Semaphore writeSemaphore) {

        this.conceptArtifacts = conceptArtifacts;
        this.writeSemaphore = writeSemaphore;

        this.stampService = Get.stampService();
        this.conceptService = Get.conceptService();
        this.assemblageService = Get.assemblageService();
        this.identifierService = Get.identifierService();
        this.indexers = LookupService.get().getAllServices(IndexBuilderService.class);

        this.writeSemaphore.acquireUninterruptibly();
        updateTitle("Importing concept batch of size: " + this.conceptArtifacts.size());
        updateMessage("Solorizing concepts");
        addToTotalWork(this.conceptArtifacts.size() / this.batchSize);
        Get.activeTasks().add(this);
    }

    @Override
    protected Void call() throws Exception {

        final AtomicInteger batchCount = new AtomicInteger(0);

        try{

            this.conceptArtifacts.stream()
                    .forEach(conceptArtifact -> {

                        batchCount.incrementAndGet();

                        try {

                            //Create concept
                            ConceptChronologyImpl conceptToWrite = new ConceptChronologyImpl(
                                    conceptArtifact.getComponentUUID(),
                                    TermAux.SOLOR_CONCEPT_ASSEMBLAGE.getNid()
                            );

                            index(conceptToWrite);

                            int stamp = stampService.getStampSequence(
                                    conceptArtifact.getStatus(),
                                    conceptArtifact.getTime(),
                                    conceptArtifact.getAuthorNid(),
                                    conceptArtifact.getModuleNid(),
                                    conceptArtifact.getPathNid()
                            );

                            conceptToWrite.createMutableVersion(stamp);
                            conceptService.writeConcept(conceptToWrite);

                            //Create definition status semantic
                            SemanticChronologyImpl defStatusToWrite = new SemanticChronologyImpl(VersionType.COMPONENT_NID,
                                    conceptArtifact.getDefinitionStatusComponentUUID(),
                                    this.identifierService.getNidForUuids(conceptArtifact.getDefinitionStatusAssemblageUUID()),
                                    conceptToWrite.getNid()
                            );

                            ComponentNidVersionImpl defStatusVersion = defStatusToWrite.createMutableVersion(stamp);
                            defStatusVersion.setComponentNid(conceptArtifact.getDefinitionStatusNid());
                            index(defStatusToWrite);
                            assemblageService.writeSemanticChronology(defStatusToWrite);

                            //Create concept identifier semantic
                            SemanticChronologyImpl identifierToWrite = new SemanticChronologyImpl(VersionType.STRING,
                                    conceptArtifact.getIdentifierComponentUUID(),
                                    this.identifierService.getNidForUuids(conceptArtifact.getIdentifierAssemblageUUID()),
                                    conceptToWrite.getNid()
                            );

                            StringVersionImpl idVersion = identifierToWrite.createMutableVersion(stamp);
                            idVersion.setString(conceptArtifact.getIdentifierValue());
                            index(identifierToWrite);
                            this.assemblageService.writeSemanticChronology(identifierToWrite);

                            if (batchCount.get() % this.batchSize == 0)
                                completedUnitOfWork();

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    });

        }finally {

            this.writeSemaphore.release();
            Get.activeTasks().remove(this);
        }

        return null;
    }

    private void index(Chronology chronicle) {
        for (IndexBuilderService indexer: indexers) {
            indexer.indexNow(chronicle);
        }
    }
}
