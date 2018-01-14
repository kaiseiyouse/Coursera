import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wordNet;

    public Outcast(WordNet wordnet)         // constructor takes a WordNet object
    {
        this.wordNet = wordnet;
    }
    public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
    {
        int maxDist = Integer.MIN_VALUE;
        int outcastId = -1;
        for (int i = 0; i < nouns.length; i++) {
            int dist = 0;
            for (int j = 0; j < nouns.length; j++) {
                dist += wordNet.distance(nouns[i], nouns[j]);
            }
            if(dist > maxDist) {
                maxDist = dist;
                outcastId = i;
            }
        }
        return nouns[outcastId];
    }
    public static void main(String[] args)  // see test client below
    {
        WordNet wordNet = new WordNet("synsets.txt", "hypernyms.txt");
        Outcast outcast = new Outcast(wordNet);
        In in = new In("outcast11.txt");
        String[] nouns = in.readAllStrings();
        StdOut.print(outcast.outcast(nouns));
    }
}