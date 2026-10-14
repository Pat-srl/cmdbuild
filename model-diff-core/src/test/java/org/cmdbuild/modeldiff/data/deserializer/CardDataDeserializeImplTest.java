/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data.deserializer;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.cmdbuild.modeldiff.DatasetDataBase;
import org.cmdbuild.modeldiff.TestHelper_Model;
import static org.cmdbuild.modeldiff.TestHelper_Model.getTestFile;
import org.cmdbuild.modeldiff.core.SerializationHandle_FileSystem;
import org.cmdbuild.modeldiff.core.SerializationHandle_FileSystem_Zipped;
import org.cmdbuild.modeldiff.core.SerializationHandle_String;
import org.cmdbuild.modeldiff.dataset.data.GeneratedData;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class CardDataDeserializeImplTest {

    private final static String A_KNOWN_DATASET_NAME = "DatasetData1";
    private final static String A_KNOWN_DATA_BASECLASS_GENERATED_STR_INFO_ONLY_FILE_NAME = Path.of(DatasetDataBase.TEST_RESOURCE_PATH.toString(), "mobileoffline_05_data_baseclass_str_infoOnly.json").toString();
    private final static Path A_KNOWN_DATA_BASECLASS_GENERATED_FILE_PATH = Path.of(DatasetDataBase.TEST_RESOURCE_PATH.toString(), "mobileoffline_06_data_baseclass_file.json");
    private final static Path A_KNOWN_DATA_BASECLASS_GENERATED_ZIPPED_FILE_PATH = Path.of(DatasetDataBase.TEST_RESOURCE_PATH.toString(), "mobileoffline_07_data_baseclass_file.zip");

    /**
     * Test of deserialize method, from String, of class
     * CardDataDeserializeImpl.
     */
    @Test
    public void testDeserialize_String() {
        System.out.println("deserialize_String");

        //arrange:
        String serializedJson = readToString(getClass().getResourceAsStream(A_KNOWN_DATA_BASECLASS_GENERATED_STR_INFO_ONLY_FILE_NAME));
        SerializationHandle_String serializedData = new SerializationHandle_String(serializedJson);

        CardDataDeserializerImpl instance = new CardDataDeserializerImpl();

        //act:
        GeneratedData result = instance.deserialize(serializedData);

        //assert:
        assertEquals(A_KNOWN_DATASET_NAME, result.name);
        assertEquals(3, result.data.classes.size());
    }

    /**
     * Test of deserialize method, from File containing JSON String, of class
     * CardDataDeserializeImpl.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testDeserialize_File() throws URISyntaxException {
        System.out.println("deserialize_File");

        //arrange:
        File dataFile = getTestFile(A_KNOWN_DATA_BASECLASS_GENERATED_FILE_PATH);
        SerializationHandle_FileSystem serializedData = new SerializationHandle_FileSystem(dataFile);

        CardDataDeserializerImpl_OnFileSystem instance = new CardDataDeserializerImpl_OnFileSystem();

        //act:
        GeneratedData result = instance.deserialize(serializedData);

        //assert:
        assertEquals(A_KNOWN_DATASET_NAME, result.name);
        assertEquals(3, result.data.classes.size());
        assertEquals(2, result.data.classes.get(0).getValues().size());
        assertEquals(3, result.data.classes.get(1).getValues().size());
        assertEquals(1, result.data.classes.get(2).getValues().size());
    }

    /**
     * Test of deserialize method, from zipped File containing JSON String, of
     * class CardDataDeserializeImpl.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testDeserialize_ZippedFile() throws URISyntaxException {
        System.out.println("deserialize_ZippedFile");

        //arrange:
        SerializationHandle_FileSystem_Zipped serializedData = new SerializationHandle_FileSystem_Zipped(TestHelper_Model.getTestFile(A_KNOWN_DATA_BASECLASS_GENERATED_ZIPPED_FILE_PATH));

        CardDataDeserializerImpl_OnFileSystem instance = new CardDataDeserializerImpl_OnFileSystem();

        //act:
        GeneratedData result = instance.deserialize(serializedData);

        //assert:
        assertEquals(A_KNOWN_DATASET_NAME, result.name);
        assertEquals(3, result.data.classes.size());
        assertEquals(2, result.data.classes.get(0).getValues().size());
        assertEquals(3, result.data.classes.get(1).getValues().size());
        assertEquals(1, result.data.classes.get(2).getValues().size());
    }

}
