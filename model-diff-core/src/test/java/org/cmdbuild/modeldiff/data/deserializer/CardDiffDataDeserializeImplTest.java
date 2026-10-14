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
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author afelice
 */
public class CardDiffDataDeserializeImplTest {
    
    private final static String A_KNOWN_DATASET_NAME = "DatasetData1";
    private final static String A_KNOWN_DIFF_DATA_BASECLASS_GENERATED_STR_INFO_ONLY_FILE_NAME = Path.of(DatasetDataBase.TEST_RESOURCE_PATH.toString(), "mobileoffline_08_diffdata_baseclass_str.json").toString();
    private final static Path A_KNOWN_DIFF_DATA_BASECLASS_GENERATED_FILE_PATH = Path.of(DatasetDataBase.TEST_RESOURCE_PATH.toString(), "mobileoffline_08_diffdata_baseclass_str.json");    
    private final static Path A_KNOWN_DIFF_DATA_BASECLASS_GENERATED_ZIPPED_FILE_PATH = Path.of(DatasetDataBase.TEST_RESOURCE_PATH.toString(), "mobileoffline_08_diffdata_baseclass_str_file.zip");    

    /**
     * Test of deserialize method, from String, of class CardDiffDataDeserializeImpl.
     */
    @Test
    public void testDeserialize_String() {
        System.out.println("deserialize_String");
        
        //arrange:
        String serializedJson = readToString(getClass().getResourceAsStream(A_KNOWN_DIFF_DATA_BASECLASS_GENERATED_STR_INFO_ONLY_FILE_NAME));
        SerializationHandle_String serializedData = new SerializationHandle_String(serializedJson);
                
        CardDiffDataDeserializerImpl instance = new CardDiffDataDeserializerImpl();
        
        //act:
        GeneratedDiffData result = instance.deserialize(serializedData);
        
        //assert:
        assertEquals(A_KNOWN_DATASET_NAME, result.name);
        assertEquals(2, result.insertedCards.size());
        assertEquals(1, result.removedCards.size());
        assertEquals(2, result.changedCards.size());
    }
    
    /**
     * Test of deserialize method, from File containing JSON String, of class CardDiffDataDeserializeImpl.
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testDeserialize_File() throws URISyntaxException {
        System.out.println("deserialize_File");
        
        //arrange:
        File dataFile = getTestFile(A_KNOWN_DIFF_DATA_BASECLASS_GENERATED_FILE_PATH);
        SerializationHandle_FileSystem serializedData = new SerializationHandle_FileSystem(dataFile);
                
        CardDiffDataDeserializerImpl_OnFileSystem instance = new CardDiffDataDeserializerImpl_OnFileSystem();
        
        //act:
        GeneratedDiffData result = instance.deserialize(serializedData);
        
        //assert:
        assertEquals(A_KNOWN_DATASET_NAME, result.name);
        assertEquals(2, result.insertedCards.size());
        assertEquals(1, result.removedCards.size());
        assertEquals(2, result.changedCards.size());
    } 

    /**
     * Test of deserialize method, from zipped File containing JSON String, of class CardDiffDataDeserializeImpl.
     * @throws java.net.URISyntaxException
     */
    @Test
    @Ignore("Needs a properly build zip file")
    public void testDeserialize_ZippedFile() throws URISyntaxException {
        System.out.println("deserialize_ZippedFile");
        
        //arrange:
        SerializationHandle_FileSystem_Zipped serializedData = new SerializationHandle_FileSystem_Zipped(TestHelper_Model.getTestFile(A_KNOWN_DIFF_DATA_BASECLASS_GENERATED_ZIPPED_FILE_PATH));
                
        CardDiffDataDeserializerImpl_OnFileSystem instance = new CardDiffDataDeserializerImpl_OnFileSystem();
        
        //act:
        GeneratedDiffData result = instance.deserialize(serializedData);
        
        //assert:
        assertEquals(A_KNOWN_DATASET_NAME, result.name);
        assertEquals(A_KNOWN_DATASET_NAME, result.name);
        assertEquals(2, result.insertedCards.size());
        assertEquals(1, result.removedCards.size());
        assertEquals(2, result.changedCards.size());       
    }    
    
}
