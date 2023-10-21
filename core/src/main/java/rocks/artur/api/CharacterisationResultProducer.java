package rocks.artur.api;

import rocks.artur.domain.CharacterisationResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * this interface helps to build a wrapper for a characterisation tool.
 */
public interface CharacterisationResultProducer {
    /***
     * This method returns the version of the used tool
     * @return A version of the tool
     * @throws IOException
     */
    String getVersion() throws IOException;

    /***
     *
     * This method extracts metadata properties from a given digital object.
     *
     * @param file Input File
     * @return A list of @CharacterisationResult
     * @throws IOException
     */
    List<CharacterisationResult> processFile(File file) throws IOException;


    /***
     *
     * This method extracts metadata properties from a given digital object passed as a byte array.
     *
     * @param file Input File
     * @param filename
     * @return A list of @CharacterisationResult
     * @throws IOException
     */
    List<CharacterisationResult> processFile(byte[] file, String filename) throws IOException;
}