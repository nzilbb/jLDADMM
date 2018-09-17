//
// (c) 2018, New Zealand Institute of Language, Brain and Behaviour,
//           Univeriosty of Canterbury.
//
//    This module is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This module is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this module; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
package models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintStream;
import java.io.IOException;

/**
 * Interface for all models to implement.
 * <p>This interface abstracts away from the specific model implementations, allowing
 * LDA and DMM models to be processed in the same way.
 * <p>It also abtracts away from the file system, so that the corpus can be streamed from
 * a source other than a file, and outputs can be accessed directly, rather than by file-name
 * convention
 * @author Robert Fromont robert.fromont@canterbury.ac.nz
 */

public interface IJLDADMMModel
{
   // Methods:
   
   /**
    * Initialize the model.
    * @param corpusReader Reader that supplies the corpus, one document per line.
    * @param inNumTopics The number of topics.
    * @param inAlpha The hyper-parameter <var>alpha</var>.
    * @param inBeta The hyper-parameter <var>beta</var>.
    * @param inNumIterations Number of Gibbs sampling iterations.
    * @param inTopWords Number of most probable words for each topic.
    * @param inExpName Experiment name (if any).
    * @param topicAssignmentReader Reader that supplies topic assignments.
    * @param inSaveStep A step to save the sampling outputs. A value of 0 only saves the output from the last sample.
    * @param parametersWriter Writer for parameters output.
    * @param dictionaryWriter Writer for dictionary.
    * @param topicAssignmentsWriter Writer for topic assigments.
    * @param topTopicalWordsWriter Writer for top topical words.
    * @param topicWordProsWriter Writer for word pros.
    * @param docTopicProsWriter Writer for doc pros.
    * @throws Exception If an error occurs.
    */
   public void initialize(BufferedReader corpusReader, int inNumTopics,
			  double inAlpha, double inBeta, int inNumIterations, int inTopWords,
			  String inExpName, BufferedReader topicAssignmentReader, int inSaveStep,
			  BufferedWriter parametersWriter, BufferedWriter dictionaryWriter,
			  BufferedWriter topicAssignmentsWriter, BufferedWriter topTopicalWordsWriter,
			  BufferedWriter topicWordProsWriter, BufferedWriter docTopicProsWriter)
      throws IOException;

   /**
    * Run inference.
    * @throws IOException If an IO error occurs.
    */
   public void inference() throws IOException;
   
   /**
    * Sets the destination for logging.
    * @param logWriter The writer for receiving log messages, or null for no logging.
    */
   public void setLogStream(PrintStream logWriter);

} // end of class IJLDADMMModel
