package cf.webex.fileutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class WebExFiles {

    public static String getContentOfFile(File file) throws FileNotFoundException {

        FileInputStream fileReader = new FileInputStream(file);
        Scanner scanner = new Scanner(fileReader).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
