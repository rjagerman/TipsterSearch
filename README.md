# TipsterSearch

This information retrieval system is part of the 2014 Information Retrieval course at ETH. It searches and ranks documents in the tipster dataset according to a set of queries. Additionally it performs several metrics on the found results and displays these at the end of the search.

## Compilation

This system is build using `sbt`. To compile, browse to the directory containing `build.sbt` and run:

    sbt compile
    
## Running

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

For example, to specify the directory where the tipster dataset can be found use the `-d` parameter:

    sbt "run -d /path/to/tipsterdataset/"
    
An additional example specifying all the parameters:

    sbt "run -n 50 -d /path/to/tipsterdataset/ -t /path/to/topics -q /path/to/qrels -m language"
    
