import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class WordNet {

    private final ArrayList<String> synsets = new ArrayList<>();
    private final Map<String, HashSet<Integer>> nouns = new HashMap<>();
//    private final Digraph digraph;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        In in = new In(synsets);
        while(in.hasNextLine()) {
            String line = in.readLine();
            String[] fields = line.split(",");
            this.synsets.add(fields[1]);
            for(String s : fields[1].split(" ")) {
                HashSet<Integer> set = nouns.getOrDefault(s, new HashSet<>());
                set.add(Integer.parseInt(fields[0]));
                nouns.put(s, set);
            }
        }
        Digraph digraph = new Digraph(this.synsets.size());
        In in1 = new In(hypernyms);
        while (in1.hasNextLine()) {
            String line = in1.readLine();
            String[] fields = line.split(",");
            for(int i = 1; i < fields.length; i++) {
                digraph.addEdge(Integer.parseInt(fields[0]), Integer.parseInt(fields[i]));
            }
        }
        DirectedCycle directedCycle = new DirectedCycle(digraph);
        if(directedCycle.hasCycle()) {
            throw new IllegalArgumentException("Cycle detected!");
        }
        int root = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if(digraph.outdegree(i) == 0) {
                root++;
            }
        }
        if(root != 1) {
            throw new IllegalArgumentException("Not a rooted DAG!");
        }
        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if(word == null) throw new IllegalArgumentException("word can not be null!");
        return nouns.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        int ancestor = sap.ancestor(nouns.get(nounA), nouns.get(nounB));
        return this.synsets.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {

        WordNet wordNet1 = new WordNet("synsets15.txt", "hypernyms15Tree.txt");
        WordNet wordNet2 = new WordNet("synsets15.txt", "hypernyms15Path.txt");
        StdOut.printf("sap=%s", wordNet1.sap("c", "d"));
        StdOut.printf("sap=%s", wordNet2.sap("a", "f"));
        StdOut.println(wordNet1.isNoun("k"));
        StdOut.println(wordNet1.nouns());
    }
}