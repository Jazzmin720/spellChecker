import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;

public class SpellChecker {

    static void testMisspelledWords(HashSet<String> dictionary) {
        String[] testWords = { "example", "word", "test", "spell", "checker" };
        for (String word : testWords) {
            String misspelledWord = MisspelledWordGenerator.generateMisspelledWord(word);
            System.out.println("Original: " + word + ", Misspelled: " + misspelledWord);
            TreeSet<String> corrections = corrections(misspelledWord, dictionary);
            System.out.println("Corrections for '" + misspelledWord + "': "
                    + (corrections.isEmpty() ? "(no suggestions)" : corrections));
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        HashSet<String> dictionary = loadDictionary("words.txt");
        System.out.println("Dictionary loaded. Size: " + dictionary.size());

        File inputFile = getInputFileNameFromUser();
        if (inputFile != null) {
            System.out.println("Selected file: " + inputFile.getAbsolutePath()); // Debugging line
            checkSpelling(inputFile, dictionary);
        } else {
            System.out.println("No file was selected."); // Debugging line
        }
    }

    static HashSet<String> loadDictionary(String filePath) throws FileNotFoundException {
        HashSet<String> dictionary = new HashSet<>();
        Scanner filein = new Scanner(new File(filePath));
        while (filein.hasNext()) {
            String word = filein.next().toLowerCase();
            dictionary.add(word);
        }
        filein.close();
        return dictionary;
    }

    static File getInputFileNameFromUser() {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogTitle("Select File for Input");
        int option = fileDialog.showOpenDialog(null);
        if (option != JFileChooser.APPROVE_OPTION) {
            return null;
        } else {
            return fileDialog.getSelectedFile();
        }
    }

    static void checkSpelling(File inputFile, HashSet<String> dictionary) throws FileNotFoundException {
        Scanner in = new Scanner(inputFile);
        in.useDelimiter("[^a-zA-Z]+");
        HashSet<String> misspelledWords = new HashSet<>();

        while (in.hasNext()) {
            String word = in.next().toLowerCase();
            // System.out.println("Checking word: " + word); // Debugging line
            if (!dictionary.contains(word) && !misspelledWords.contains(word)) {
                misspelledWords.add(word);
                TreeSet<String> corrections = corrections(word, dictionary);
                System.out.println(word + ": " + (corrections.isEmpty() ? "(no suggestions)" : corrections));
            }
        }

        in.close();
    }

    static TreeSet<String> corrections(String badWord, HashSet<String> dictionary) {
        TreeSet<String> suggestions = new TreeSet<>();

        // Deleting each character
        for (int i = 0; i < badWord.length(); i++) {
            String temp = badWord.substring(0, i) + badWord.substring(i + 1);
            if (dictionary.contains(temp)) {
                suggestions.add(temp);
            }
        }

        // Changing each character
        for (int i = 0; i < badWord.length(); i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                String temp = badWord.substring(0, i) + ch + badWord.substring(i + 1);
                if (dictionary.contains(temp)) {
                    suggestions.add(temp);
                }
            }
        }

        // Inserting a letter at each position
        for (int i = 0; i <= badWord.length(); i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                String temp = badWord.substring(0, i) + ch + badWord.substring(i);
                if (dictionary.contains(temp)) {
                    suggestions.add(temp);
                }
            }
        }

        // Swapping adjacent characters
        for (int i = 0; i < badWord.length() - 1; i++) {
            char[] chars = badWord.toCharArray();
            char temp = chars[i];
            chars[i] = chars[i + 1];
            chars[i + 1] = temp;
            String swappedString = new String(chars);
            if (dictionary.contains(swappedString)) {
                suggestions.add(swappedString);
            }
        }

        // Splitting the word into two
        for (int i = 1; i < badWord.length(); i++) {
            String part1 = badWord.substring(0, i);
            String part2 = badWord.substring(i);
            if (dictionary.contains(part1) && dictionary.contains(part2)) {
                suggestions.add(part1 + " " + part2);
            }
        }

        return suggestions;
    }

    class MisspelledWordGenerator {
        private static final Random random = new Random();

        public static String generateMisspelledWord(String word) {
            int method = random.nextInt(5); // There are 5 methods

            switch (method) {
                case 0:
                    return insertRandomCharacter(word);
                case 1:
                    return deleteRandomCharacter(word);
                case 2:
                    return swapAdjacentCharacters(word);
                case 3:
                    return replaceWithSimilarCharacter(word);
                case 4:
                    return typingError(word);
                default:
                    return word; // In case of an unexpected value
            }
        }

        private static String insertRandomCharacter(String word) {
            int position = random.nextInt(word.length() + 1);
            char randomChar = (char) ('a' + random.nextInt(26));
            return word.substring(0, position) + randomChar + word.substring(position);
        }

        private static String deleteRandomCharacter(String word) {
            if (word.length() < 2) {
                return word;
            }
            int position = random.nextInt(word.length());
            return word.substring(0, position) + word.substring(position + 1);
        }

        private static String swapAdjacentCharacters(String word) {
            if (word.length() < 2) {
                return word;
            }
            int position = random.nextInt(word.length() - 1);
            char[] chars = word.toCharArray();
            char temp = chars[position];
            chars[position] = chars[position + 1];
            chars[position + 1] = temp;
            return new String(chars);
        }

        private static String replaceWithSimilarCharacter(String word) {
            if (word.isEmpty()) {
                return word;
            }
            int position = random.nextInt(word.length());
            char[] similarChars = { 'e', 'i', 'r', 't', 'o', 'p', 'a', 's' }; // Example set of characters
            char similarChar = similarChars[random.nextInt(similarChars.length)];
            char[] chars = word.toCharArray();
            chars[position] = similarChar;
            return new String(chars);
        }

        private static String typingError(String word) {
            if (word.length() < 2) {
                return word;
            }
            int position = random.nextInt(word.length() - 1);
            char[] chars = word.toCharArray();
            // Simulate adjacent key press
            if (random.nextBoolean() && position > 0) {
                char temp = chars[position];
                chars[position] = chars[position - 1];
                chars[position - 1] = temp;
            } else if (position < word.length() - 2) {
                char temp = chars[position];
                chars[position] = chars[position + 1];
                chars[position + 1] = temp;
            }
            return new String(chars);
        }
    }

}
