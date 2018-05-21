package sh.komet.gui.exportation;

import sh.isaac.api.Get;
import sh.isaac.api.progress.PersistTaskResult;
import sh.isaac.api.task.TimedTaskWithProgressTracker;
import sh.komet.gui.manifold.Manifold;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/*
 * aks8m - 5/15/18
 */
public class ExportContentAndZipTask extends TimedTaskWithProgressTracker<Void> implements PersistTaskResult {

    private final File exportDirectory;
    private final ExportFormatType exportFormatType;
    private final Manifold manifold;

    public ExportContentAndZipTask(Manifold manifold, File exportDirectory, ExportFormatType exportFormatType){
        this.manifold = manifold;
        this.exportDirectory = exportDirectory;
        this.exportFormatType = exportFormatType;
        updateTitle("Export " + this.exportFormatType.toString());

        addToTotalWork(4);
        Get.activeTasks().add(this);
    }

    @Override
    protected Void call() throws Exception {

        try {
            Map<ExportComponentType, List<String>> exportLineseMap = new HashMap<>();

            completedUnitOfWork();

            final ExportLookUpCache exportLookUpCache = new ExportLookUpCache();
            exportLookUpCache.generateCache();
            final ReaderSpecification readerSpecification =
                    new ReaderSpecification(this.exportFormatType, this.manifold, exportLookUpCache);

            completedUnitOfWork();

            if (true) { //TODO Remove this when done debugging
                System.out.println("Start Concept================" + LocalDateTime.now());
                //Concepts
                completedUnitOfWork();
                updateMessage("Reading Concepts...");

                readerSpecification.setExportComponentType(ExportComponentType.CONCEPT);
                BatchReader conceptBatchReader = new BatchReader(readerSpecification, 102400);

                Future<List<String>> conceptReadTask = Get.executor().submit(conceptBatchReader, new ArrayList<>());
                exportLineseMap.put(ExportComponentType.CONCEPT, conceptReadTask.get());
                completedUnitOfWork();
                System.out.println("Finish Concept================" + LocalDateTime.now());
            }

            completedUnitOfWork();

            if (true) { //TODO Remove this when done debugging
                System.out.println("Start Descriptions================" + LocalDateTime.now());
                //Descriptions
                completedUnitOfWork();
                updateMessage("Reading Descriptions...");

                readerSpecification.setExportComponentType(ExportComponentType.DESCRIPTION);
                BatchReader descriptionBatchReader = new BatchReader(readerSpecification, 102400);

                Future<List<String>> descriptionReadTask = Get.executor().submit(descriptionBatchReader, new ArrayList<>());
                exportLineseMap.put(ExportComponentType.DESCRIPTION, descriptionReadTask.get());
                System.out.println("Finish Descriptions================" + LocalDateTime.now());
            }

            if(true){
                System.out.println("Start ZipingExport================" + LocalDateTime.now());
                //Zipping
                completedUnitOfWork();
                updateMessage("Zipping SOLOR Export...");

                ZipExportFiles zipExportFiles = new ZipExportFiles(this.exportFormatType, this.manifold, this.exportDirectory, exportLineseMap);
                Future zipExportFilesTask = Get.executor().submit(zipExportFiles);
                zipExportFilesTask.get();
                System.out.println("Finish ZipingExport================" + LocalDateTime.now());
            }

            completedUnitOfWork();

        }finally {
            Get.activeTasks().remove(this);
        }

        return null;
    }

}
