# TipsterSearch

This information retrieval system is part of the 2014 Information Retrieval course at ETH. It searches and ranks documents in the tipster dataset according to a set of queries. Additionally it performs several metrics on the found results and displays these at the end of the search.

## Search models

This project uses two different search models. One term-based and one language-based.

### Term-based model
The term-based model uses a logarithmically scaled TFIDF model and is defined in the file `src/main/scala/scoring/TfidfModel.scala`. The score is computed as the sum of the scores of the individial words in the query, which are logarithmically scaled:

          ∑
      w∈query     (1.0 + log2(tf(w)))
    ∧ w∈document

Where

    tf(w) = the term frequency of a word in the document

### Language-based model
The language-based model uses Jelinek-Mercer smoothing and is defined in the file `src/main/scala/scoring/LanguageModel.scala`. The score is computed by summing, for every word in the query, the log of the probability of the word given a document p(w|d) divided by the probability of the word over the entire collection p(w):

          ∑                    (1.0-λ) * p(w|d)
      w∈query     log2( 1.0 + ---------------- )
    ∧ w∈document                 λ    * p(w)

Where

    p(w|d) = tf(w) / |d|
    p(w) = cf(w) / |∑ cf(v)|
    λ = 0.1
    cf(w) = the collection frequency of a word

## Instructions

The system is build using the Scala Build Tool (sbt) which can be found [here](http://www.scala-sbt.org/). Please make sure you have sbt installed and can execute `sbt` from the command line before continuing.

### Compilation

To compile, browse to the directory containing `build.sbt` and run:

    sbt compile
    
### Running

To run the software:

    sbt run

You can supply the following command line parameters:

    -n <value> | --n <value>
        The number of results to return per query (default: 100)
    -d <value> | --tipsterDirectory <value>
        The directory where the tipster zips are placed (default: 'dataset/tipster')
    -t <value> | --topicsFile <value>
        The topics file (default: 'dataset/topics')
    -q <value> | --qrelsFile <value>
        The qrels file (default: 'dataset/qrels')
    -m <value> | --model <value>
        The model to use, valid values: [language|tfidf] (default: 'tfidf')

For example, to specify the directory where the tipster zip files can be found use the `-d` parameter:

    sbt "run -d /path/to/tipsterdataset/"
    
In order to specify which queries to execute, you have to create a `topics` file which contains them. This file should match the format of the sample `topics` file that is provided. Similarly, the truth values will have to be specified in a `qrels` file which should match the format of the `qrels` file that is provided. You can pass the location of both these files as parameters to the program:

    sbt "run -d /path/to/tipsterdataset/ -t /path/to/topics -q /path/to/qrels"

In order to use the language model instead of the (default) tfidf model, you can use the `-m` parameter:
    
    sbt "run -d /path/to/tipsterdataset/ -t /path/to/topics -q /path/to/qrels -m language"

