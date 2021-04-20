package cf.webex.fileutils;

public class StringAppender {

    private StringBuilder stringBuilder = null;

    StringAppender() {
        stringBuilder = new StringBuilder();
    }
    public StringAppender appendLine(String line) {

        stringBuilder.append(line);
        return this;
    }
}
