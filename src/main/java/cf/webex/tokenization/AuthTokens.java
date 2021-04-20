package cf.webex.tokenization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AuthTokens {
    public static List<Token> tokens = new ArrayList<>();
    public static void loadTokens(File tokensFile) {

    }
    public static boolean isTokenValid(Token token) {
        return tokens.contains(token);
    }
}
