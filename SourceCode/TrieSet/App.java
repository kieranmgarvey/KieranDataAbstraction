/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package csci2320;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class App {
    record SetListPair(TrieSet set, List<String> list) {}

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            String testType = sc.next();
            if (testType.equals("speed")) {
                speed(1000000);
                return;
            }
            Random rand = new Random(sc.nextLong());
            int size1 = sc.nextInt();
            int size2 = sc.nextInt();

            var slp1 = buildSet(rand, size1);
            TrieSet set1 = slp1.set;
            List<String> words1 = slp1.list;
            var slp2 = buildSet(rand, size2);
            TrieSet set2 = slp2.set;
            List<String> words2 = slp2.list;
            switch(testType) {
                case "basic":
                    contains(rand, set1, words1);
                    iterator(rand, set1, words1);
                    remove(rand, set1, words1);
                    contains(rand, set2, words2);
                    iterator(rand, set2, words2);
                    remove(rand, set2, words2);
                    break;
                case "pre-suffix":
                    prefix(rand, set1, words1);
                    suffix(rand, set1, words1);
                    prefix(rand, set2, words2);
                    suffix(rand, set2, words2);
                    break;
            }
        }
    }

    static String randomString(Random rand, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            sb.append((char)('a'+rand.nextInt(6)));
        }
        return sb.toString();
    }

    static SetListPair buildSet(Random rand, int size) {
        TrieSet set = new TrieSet();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            String str = randomString(rand, 8);
            while (set.contains(str)) str = randomString(rand, 5 + rand.nextInt(4));
            set.add(str);
            list.add(str);
        }
        return new SetListPair(set, list);
    }
    
    static void contains(Random rand, TrieSet set, List<String> words) {
        System.out.println("Start contains test.");
        for (var word: words) {
            if (!set.contains(word)) {
                System.out.println("Didn't find " + word);
                return;
            }
        }
        System.out.println("Existing words found.");
        for (int i = 0; i < 10; ++i) {
            String newWord = randomString(rand, 5 + rand.nextInt(4));
            while (words.contains(newWord)) {
                newWord = randomString(rand, 5 + rand.nextInt(4));
            }
            if (set.contains(newWord)) {
                System.out.println("Found " + newWord + " that isn't there.");
                return;
            }
        }
        System.out.println("Contains passed: " + words.size());
    }

    static void remove(Random rand, TrieSet set, List<String> words) {
        System.out.println("Start remove test.");
        List<String> removed = new ArrayList<>();
        var cnt = 0;
        for (var word: words) {
            if (rand.nextDouble() < 0.4) {
                if (!set.contains(word)) {
                    System.out.println("Houston, we have a problem. Can't remove word that should be there: " + word);
                    return;
                }
                removed.add(word);
                if (!set.remove(word)) {
                    System.out.println("Remove returned false when it should have worked.");
                    return;
                }
                ++cnt;
            }
        }
        System.out.println("Removals complete.");
        for (var word: removed) {
            if (set.contains(word)) {
                System.out.println("After removal we found " + word);
                return;
            }
        }
        System.out.println("Removed items not found. " + cnt);
    }

    static void iterator(Random rand, TrieSet set, List<String> words) {
        System.out.println("Start iterator test.");
        Set<String> wordSet = new HashSet<>(words);
        Set<String> trieSet = new HashSet<>();
        var cnt = 0;
        for (var word: set) {
            trieSet.add(word);
            ++cnt;
        }
        System.out.println("Iterator: " + cnt);
        if (cnt != words.size()) {
            System.out.println("Iterator count wrong.");
            return;
        }
        if (!wordSet.equals(trieSet)) {
            System.out.println("Iterator didn't get all values: " + words.size() + " != " + trieSet.size());
        }
        System.out.println("Iterator built set matches.");
    }

    static void prefix(Random rand, TrieSet set, List<String> words) {
        System.out.println("Start prefix test.");
        var cnt = 0;
        for (var word: words) {
            if (rand.nextDouble() < 0.4) {
                var newWord = word + randomString(rand, 3);
                String prefix = set.longestPrefix(newWord);
                if (!set.contains(prefix)) {
                    System.out.println("Got a prefix that isn't in the set. " + prefix);
                    return;
                }
                if (!words.contains(prefix)) {
                    System.out.println("Got a prefix that isn't in the list of words. " + prefix);
                    return;
                }
                ++cnt;
            }
        }
        System.out.println("Prefix tests passed. " + cnt);
    }

    static void suffix(Random rand, TrieSet set, List<String> words) {
        System.out.println("Start suffix test.");
        var cnt = 0;
        for (int i = 0; i < words.size() / 5; ++i) {
            var word = words.get(i);
            var prefix = word.substring(0, word.length() - 3);
            var suffixes = set.validSuffixes(prefix);
            if (!suffixes.contains(word.substring(prefix.length()))) {
                System.out.println("Original not found in suffixes.");
                return;
            }
            for (var w: words) {
                if (w.startsWith(prefix)) {
                    ++cnt;
                    if (!suffixes.contains(word.substring(prefix.length()))) {
                        System.out.println("Didn't get suffix of " + w + " with prefix of " + prefix);
                        return;
                    }
                }
            }
        }
        System.out.println("Suffix tests passed. " + cnt);
    }

    static void speed(int size) {
        var start = System.nanoTime();
        Random rand = new Random(45283);
        var slp = buildSet(rand, size);
        TrieSet set = slp.set;
        List<String> words = slp.list;

        contains(rand, set, words);
        System.out.println("First time: " + (System.nanoTime() - start)*1e-9);
        remove(rand, set, words);
        System.out.println("Second time: " + (System.nanoTime() - start)*1e-9);

        var slp2 = buildSet(rand, size / 20);
        TrieSet set2 = slp2.set;
        List<String> words2 = slp2.list;
        prefix(rand, set2, words2);
        System.out.println("Third time: " + (System.nanoTime() - start)*1e-9);
        suffix(rand, set2, words2);
        System.out.println("Final time: " + (System.nanoTime() - start)*1e-9);
    }
}