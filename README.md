## jLDADMM: A Java package for the LDA and DMM topic models 

**jLDADMM** is released to provide alternatives for topic modeling on normal or short texts. Probabilistic topic models, such as Latent Dirichlet Allocation (LDA) [1] and related models [2], are widely used to discover latent topics in document collections. However, applying topic models for short texts (e.g. Tweets) is more challenging because of data sparsity and the limited contexts in such texts. One approach is to combine short texts into long pseudo-documents before training LDA. Another approach is to assume that there is only one topic per document [3].

jLDADMM provides implementations of the LDA topic model [1] and the one-topic-per-document Dirichlet Multinomial Mixture (DMM) model (i.e. mixture of unigrams) [4]. The implementations of LDA and DMM use the collapsed Gibbs sampling algorithms for inference as described in [5] and [6], respectively. Furthermore, jLDADMM supplies a document clustering evaluation to compare topic models, using two common metrics of Purity and normalized mutual information (NMI) [7].

Find more information about jLDADMM at [http://jldadmm.sourceforge.net/](http://jldadmm.sourceforge.net/). 

Please cite jLDADMM when jLDADMM is used to produce published results or incorporated into other software:

`Dat Quoc Nguyen. jLDADMM: A Java package for the LDA and DMM topic models. 2015. URL http://jldadmm.sourceforge.net/.` [[.bib]](http://jldadmm.sourceforge.net/jldadmm.bib)

Bug reports, comments and suggestions about jLDADMM are highly appreciated. As a free open-source package, jLDADMM is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

### Using jLDADMM for topic modeling

This section describes the usage of jLDADMM in command line or terminal, using a  pre-compiled file named `jLDADMM.jar`. Here, it is supposed that Java is already set to run in command line or terminal (e.g. adding Java to the environment variable `path` in Windows OS).

Users can find the pre-compiled file `jLDADMM.jar` and source codes in folders `jar` and `src`, respectively. The users can recompile the source codes by simply running `ant` (it is also expected that `ant` is already installed). In addition, the users can find input examples in `test` folder.

**File format of input corpus:**  Similar to file `corpus.txt`  in the `test` folder, jLDADMM assumes that each line in the input corpus represents a document. Here, a document is a sequence of words/tokens separated by white space characters. The users should preprocess the input corpus before training the LDA or DMM topic  models, for example: down-casing, removing non-alphabetic characters and stop-words, removing words shorter than 3 characters and words appearing less than a certain times.  

**Now, we can train LDA or DMM by executing:**

	$ java [-Xmx1G] -jar jar/jLDADMM.jar –model <LDA_or_DMM> -corpus <Input_corpus_file_path> [-ntopics <int>] [-alpha <double>] [-beta <double>] [-niters <int>] [-twords <int>] [-name <String>] [-sstep <int>]

where parameters in [ ] are optional.

`-model`: Specify the topic model LDA or DMM

`-corpus`: Specify the path to the input corpus file.

`-ntopics <int>`: Specify the number of topics. The default value is 20.

`-alpha <double>`: Specify the hyper-parameter `alpha`. Following [6, 8], the default  `alpha` value is 0.1.

`-beta <double>`: Specify the hyper-parameter `beta`. The default `beta` value is 0.01 which is a common setting in  literature [5]. Following [6], the users may consider to the `beta` value of 0.1 for short texts.

`-niters <int>`: Specify the number of Gibbs sampling iterations. The default value is 2000.

`-twords <int>`: Specify the number of the most probable topical words. The default value is 20.

`-name <String>`: Specify a name to the topic modeling experiment. The default value is `model`.

`-sstep <int>`: Specify a step to save the sampling outputs. The default value is 0 (i.e. only saving the output from the last sample).

**Examples:**

	$ java -jar jar/jLDADMM.jar -model LDA -corpus test/corpus.txt -name testLDA

The output files are saved in the same folder containing the input corpus file, in this case in the `test` folder. We have output files of `testLDA.theta`, `testLDA.phi`, `testLDA.topWords`, `testLDA.topicAssignments` and `testLDA.paras` referring to the document-to-topic distributions, topic-to-word distributions, top topical words, topic assignments and model parameters, respectively. Similarly, we perform:

	$ java -jar jar/jLDADMM.jar -model DMM -corpus test/corpus.txt -beta 0.1 -name testDMM

Output files `testDMM.theta`, `testDMM.phi`, `testDMM.topWords`, `testDMM.topicAssignments` and `testDMM.paras` are also in the `test` folder.

### Using jLDADMM for document clustering evaluation

Here, we treat each topic as a cluster, and we assign every document the topic with the highest probability given the document [8]. To get the Purity and NMI clustering scores, we perform:

	$ java –jar jar/jLDADMM.jar –model Eval –label <Golden_label_file_path> -dir <Directory_path> -prob <Document-topic-prob/Suffix>

`–label`: Specify the path to the ground truth label file. Each line in this label file contains the golden label of the corresponding document in the input corpus. See files `corpus.LABEL` and `corpus.txt` in the `test` folder.

`-dir`: Specify the path to the directory containing document-to-topic distribution files.

`-prob`: Specify a document-to-topic distribution file OR a group of document-to-topic distribution files in the specified directory.

**Examples:**

	$ java -jar jar/jLDADMM.jar -model Eval -label test/corpus.LABEL -dir test -prob testLDA.theta

	$ java -jar jar/jLDADMM.jar -model Eval -label test/corpus.LABEL -dir test -prob testDMM.theta

The above commands will produce the clustering scores for files `testLDA.theta` and `testDMM.theta`  in the `test` folder, separately. The following command

	$ java -jar jar/jLDADMM.jar -model Eval -label test/corpus.LABEL -dir test -prob theta

will produce the clustering scores for all document-to-topic distribution files with their names ending in `theta`. In this case, they are are `testLDA.theta` and `testDMM.theta`. The command also provides the mean and standard deviation of the clustering scores.

To improve evaluation scores, the users might consider combining the LDA and DMM topic models with word embeddings [3] \(source codes [HERE](https://github.com/datquocnguyen/LFTM)\).

### Topic inference on new/unseen corpus

To infer topics on a new/unseen corpus using a pre-trained LDA/DMM topic model, we perform:

`$ java -jar jar/jLDADMM.jar -model <LDAinf_or_DMMinf> -paras <Hyperparameter_file_path> -corpus <Unseen_corpus_file_path> [-niters <int>] [-twords <int>] [-name <String>] [-sstep <int>]`

* `-paras`: Specify the path to the hyper-parameter file produced by the pre-trained LDA/DMM topic model.

<b>Examples:</b>

`$ java -jar jar/jLDADMM.jar -model LDAinf -paras test/testLDA.paras -corpus test/unseenTest.txt -niters 100 -name testLDAinf`

`$ java -jar jar/jLDADMM.jar -model DMMinf -paras test/testDMM.paras -corpus test/unseenTest.txt -niters 100 -name testDMMinf`

### References

[1]   David M. Blei, Andrew Y. Ng, and Michael I. Jordan. 2003. Latent Dirichlet Allocation. Journal of Machine Learning Research, 3:993–1022.

[2]   David M. Blei. 2012. Probabilistic Topic Models. Communications of the ACM, 55(4):77–84.

[3]   Dat Quoc Nguyen, Richard Billingsley, Lan Du and Mark Johnson. 2015. [Improving Topic Models with Latent Feature Word Representations](https://tacl2013.cs.columbia.edu/ojs/index.php/tacl/article/view/582/158). Transactions of the Association for Computational Linguistics, vol. 3, pp. 299-313. [[CODE]](https://github.com/datquocnguyen/LFTM)

[4]   Kamal Nigam, AK McCallum, S Thrun, and T Mitchell. 2000. Text Classification from Labeled and Unlabeled Documents Using EM. Machine learning, 39:103– 134.

[5]   Thomas L. Griffiths and Mark Steyvers. 2004. Finding scientific topics. Proceedings of the National Academy of Sciences of the United States of America, 101(Suppl 1):5228–5235.

[6]   Jianhua Yin and Jianyong Wang. 2014. A Dirichlet Multinomial Mixture Model-based Approach for Short Text Clustering. In Proceedings of the 20th ACM SIGKDD International Conference on Knowledge Discovery and Data Mining, pages 233–242.

[7]   Christopher D. Manning, Prabhakar Raghavan, and Hinrich Sch¨utze. 2008. Introduction to Information Retrieval. Cambridge University Press.

[8]   Yue Lu, Qiaozhu Mei, and ChengXiang Zhai. 2011. Investigating task performance of probabilistic topic models: an empirical study of PLSA and LDA. Information Retrieval, 14:178–203.
