package net.parostroj.timetable.model.ls;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.*;
import net.parostroj.timetable.utils.Pair;

/**
 * Factory for loading/saving train diagram.
 * 
 * @author jub
 */
public class LSFileFactory {

    private static final String METADATA = "metadata.properties";
    private static final String METADATA_KEY_MODEL_VERSION = "model.version";
    private static final LSFileFactory instance = new LSFileFactory();

    public static LSFileFactory getInstance() {
        return instance;
    }
    private Map<Integer, Class<?>> lss = new HashMap<Integer, Class<?>>();
    private Pair<Integer, Class<?>> latest;

    public synchronized void registerLS(ModelVersion version, Class<?> lsClazz) {
        if (!FileLoadSave.class.isAssignableFrom(lsClazz))
            throw new RuntimeException("Error registering LS, wrong class: " + lsClazz);
        if (latest == null || version.getMajorVersion() > latest.first) {
            latest = new Pair<Integer, Class<?>>(version.getMajorVersion(), lsClazz);
        }
        lss.put(version.getMajorVersion(), lsClazz);
    }

    public synchronized FileLoadSave createLatest() throws LSException {
        try {
            if (latest == null)
                throw new LSException("No FileLoadSave registered.");
            return (FileLoadSave)latest.second.newInstance();
        } catch (InstantiationException ex) {
            throw new LSException(ex);
        } catch (IllegalAccessException ex) {
            throw new LSException(ex);
        }
    }
    
    public synchronized FileLoadSave create(ZipInputStream is) throws LSException {
        try {
            ZipEntry entry = is.getNextEntry();
            if (entry == null || !entry.getName().equals(METADATA))
                throw new LSException("Metadata was now the first entry.");
            Properties metadata = new Properties();
            if (entry != null) {
                // load metadata
                metadata.load(is);
            }
            return this.createFLSInstance(metadata);
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    public synchronized FileLoadSave create(File file) throws LSException {
        try {
            ZipFile zipFile = new ZipFile(file);
            ZipEntry entry = zipFile.getEntry(METADATA);
            Properties metadata = new Properties();
            if (entry != null) {
                // load metadata
                metadata.load(zipFile.getInputStream(entry));
            }

            return this.createFLSInstance(metadata);
        } catch (ZipException ex) {
            throw new LSException(ex);
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }
    
    private FileLoadSave createFLSInstance(Properties metadata) throws LSException {
        try {
            // set model version
            ModelVersion modelVersion = null;
            if (metadata.getProperty(METADATA_KEY_MODEL_VERSION) == null) {
                modelVersion = new ModelVersion("1.0");
            } else {
                modelVersion = new ModelVersion(metadata.getProperty(METADATA_KEY_MODEL_VERSION));
            }

            Class<?> clazz = lss.get(modelVersion.getMajorVersion());
            if (clazz == null) {
                throw new LSException("No FileLoadSave registered for version: " + modelVersion.getVersion());
            }
            return (FileLoadSave) clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new LSException(ex);
        } catch (IllegalAccessException ex) {
            throw new LSException(ex);
        }
    }
}
