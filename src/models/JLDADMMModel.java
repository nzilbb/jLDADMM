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

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

/**
 * Base class for all models to provide implementation for.
 * <p>This base class abstracts away from the specific model implementations, allowing
 * LDA and DMM models to be processed in the same way.
 * <p>It also abtracts away from the file system, so that the corpus can be streamed from
 * a source other than a file, and outputs can be accessed directly, rather than by file-name
 * convention
 * @author Robert Fromont robert.fromont@canterbury.ac.nz
 */

public abstract class JLDADMMModel
{
   // Attributes
   
   /** Hyper-parameter alpha */
   public double alpha;
   /** Hyper-parameter alpha */
   public double beta;
   /** Number of topics */
   public int numTopics;
   /** Number of Gibbs sampling iterations */
   public int numIterations;
   /** Number of most probable words for each topic */
   public int topWords;

   public int savestep = 0;
   public String expName = "model";
   public String tAssignsFilePath = "";

   /** Path to the directory containing the corpus */
   public File folderPath;

   /** Path to the topic modeling corpus */
   public String corpusPath;

   // input readers
   protected BufferedReader corpusReader;
   protected BufferedReader topicAssignmentReader;
   
   // output writers
   protected BufferedWriter parametersWriter;
   protected BufferedWriter dictionaryWriter;
   protected BufferedWriter topicAssignmentsWriter;
   protected BufferedWriter topTopicalWordsWriter;
   protected BufferedWriter topicWordProsWriter;
   protected BufferedWriter docTopicProsWriter;
   
   /**
    * Destination for logging, if any. Defaults to <var>System.out</var>.
    * @see #getLogWriter()
    * @see #setLogWriter(PrintStream)
    */
   protected PrintStream logStream = System.out;
   /**
    * Getter for {@link #logStream}: Destination for logging, if any.
    * @return Destination for logging, if any.
    */
   public PrintStream getLogStream() { return logStream; }
   /**
    * Setter for {@link #logStream}: Destination for logging, if any.
    * @param newLogStream Destination for logging, if any.
    */
   public void setLogStream(PrintStream newLogStream) { logStream = newLogStream; }   

   /**
    * Utility function that implementors can use.
    */
   public void initialize(String pathToCorpus, int inNumTopics,
			  double inAlpha, double inBeta, int inNumIterations, int inTopWords,
			  String inExpName, String pathToTAfile, int inSaveStep)
      throws IOException
   {
      if (expName == null) expName = "DMMmodel";
      File corpusFile = new File(pathToCorpus);
      corpusPath = pathToCorpus;
      folderPath = corpusFile.getParentFile();
      tAssignsFilePath = pathToTAfile;
      initialize(new BufferedReader(
		    new InputStreamReader(new FileInputStream(corpusFile), "UTF-8")),
		 inNumTopics, inAlpha, inBeta, inNumIterations, inTopWords,
		 inExpName,
		 pathToTAfile==null||pathToTAfile.length()==0?null:new BufferedReader(new InputStreamReader(new FileInputStream(pathToTAfile), "UTF-8")),
		 inSaveStep,
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(folderPath, expName + ".paras")), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(folderPath, expName + ".vocabulary")), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(folderPath, expName + ".topicAssignments")), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(folderPath, expName + ".topWords")), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(folderPath, expName + ".phi")), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(folderPath, expName + ".theta")), "UTF-8")));
   }

   /**
    * Utility function that implementors can use.
    */
   public void initialize(File corpusFile, int inNumTopics,
			  double inAlpha, double inBeta, int inNumIterations, int inTopWords,
			  String inExpName, String pathToTAfile, int inSaveStep,
			  File parametersFile, File dictionaryFile,
			  File topicAssignmentsFile, File topTopicalWordsFile,
			  File topicWordProsFile, File docTopicProsFile)
      throws IOException
   {
      if (expName == null) expName = "DMMmodel";
      corpusPath = corpusFile.getPath();
      folderPath = corpusFile.getParentFile();
      tAssignsFilePath = pathToTAfile;
      initialize(new BufferedReader(
		    new InputStreamReader(new FileInputStream(corpusFile), "UTF-8")),
		 inNumTopics, inAlpha, inBeta, inNumIterations, inTopWords,
		 inExpName,
		 pathToTAfile==null||pathToTAfile.length()==0?null:new BufferedReader(new InputStreamReader(new FileInputStream(pathToTAfile), "UTF-8")),
		 inSaveStep,
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(parametersFile), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictionaryFile), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(topicAssignmentsFile), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(topTopicalWordsFile), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(topicWordProsFile), "UTF-8")),
		 new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docTopicProsFile), "UTF-8")));
   }

   /**
    * Initialize the model.
    * @param corpusReader Reader that supplies the corpus, one document per line.  This can be a subclassed implementation, and needn't correctly implement all methods; the only method called is <code>readLine</code>.
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
   public abstract void initialize(BufferedReader corpusReader, int inNumTopics,
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
   public abstract void inference() throws IOException;
   
} // end of class JLDADMMModel
