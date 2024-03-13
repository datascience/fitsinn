package rocks.artur.api;

import rocks.artur.api_impl.utils.ByteFile;
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
    String getVersion();

    /***
     *
     * This method extracts metadata properties from a given digital object.
     *
     * @param file Input File
     * @return A list of @CharacterisationResult
     * @throws IOException
     */
    List<CharacterisationResult> processFile(File file);


    /***
     *
     * This method extracts metadata properties from a given digital object passed as a byte array.
     *
     * @param file Input File
     * @return A list of @CharacterisationResult
     * @throws IOException
     */
    List<CharacterisationResult> processFile(ByteFile file);
}