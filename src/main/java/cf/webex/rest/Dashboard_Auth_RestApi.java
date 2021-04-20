package cf.webex.rest;

import cf.webex.fileutils.WebExFiles;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;
import sun.misc.IOUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class Dashboard_Auth_RestApi {

    public String loginpagecontent() {
        try {
            return WebExFiles.getContentOfFile(new File("webcontent/dashboard/loginpage.html"))
                    .replaceAll("%stylesheet%", WebExFiles.getContentOfFile(new File("webcontent/dashboard/loginpagestyle.css")));
        } catch (FileNotFoundException fileNotFoundException) {

            fileNotFoundException.printStackTrace();
        }
        return null;
    }

//    static {
//
//            try {
//                loginpagecontent = WebExFiles.getContentOfFile(new File("webcontent/dashboard/loginpage.html"))
//                        .replaceAll("%stylesheet%", WebExFiles.getContentOfFile(new File("webcontent/dashboard/loginpagestyle.css")));
//            } catch (FileNotFoundException fileNotFoundException) {
//
//                fileNotFoundException.printStackTrace();
//            }
//    }

    public static YamlConfiguration yamlConfiguration = new YamlConfiguration();

    @RequestMapping(value = "/auth")
    public Object api(HttpServletResponse response, WebRequest request, @RequestParam String type, @RequestParam String username, @RequestParam String password) {

        if (type == null) {
            //return "The type was not provided!";
        } else {
            if (type.equals("login")) {
                if (yamlConfiguration.contains("auth.users." + username)) {
                    if (yamlConfiguration.contains("auth.users." + username + ".password")) {
                        String pass = yamlConfiguration.getString("auth.users." + username + ".password");
                        pass = new String(Base64.getDecoder().decode(pass));
                        String rank = "Default";
                        if (yamlConfiguration.contains("auth.users." + username + ".rank")) {

                            rank = yamlConfiguration.getString("auth.users." + username + ".rank");
                        }
                        if (pass.equals(password)) {

                            RedirectView redirectView = new RedirectView();
                            redirectView.setUrl("/dashboard");
                            response.addCookie(new Cookie("loggedInUsername", username));
                            response.addCookie(new Cookie("loggedInPassword", password));
                            return redirectView;
                        } else {

                            RedirectView redirectView = new RedirectView();
                            redirectView.setUrl("/auth/failed");
                            return redirectView;
                        }
                    }
                } else {

                    RedirectView redirectView = new RedirectView();
                    redirectView.setUrl("/auth/failed");
                    return redirectView;
                }
//                if(username.equals("admin")) {
//                    if (password.equals("kek")) {
//                        return "valid!";
//                    }
//                }
//                return "invalid login credentials!";
            }
        }
        return loginpagecontent().replaceAll("%msg%", "<div id=\"msg\"><p class=\"i1\">No Content was provided!</p></div>");
    }

    @RequestMapping(value = "/auth/login")
    public Object auth(@Nullable @CookieValue String loggedInUsername) {
        if(loggedInUsername != null) {
            return new RedirectView("/dashboard");
        }
        return loginpagecontent().replaceAll("%msg%", "");
    }

    @RequestMapping(value = "/auth/failed")
    public String backToLogin() {
        return loginpagecontent().replaceAll("%msg%", "<div id=\"msg\"><p class=\"i1\">Invalid Login Credentials!</p></div>");
    }

    @RequestMapping(value = "/dashboard")
    public Object getDashboard(HttpServletResponse response, @Nullable @CookieValue String loggedInUsername, @Nullable @CookieValue String loggedInPassword) {

        if (loggedInUsername != null) {

            if (loggedInPassword != null) {

                if (yamlConfiguration.contains("auth.users." + loggedInUsername)) {

                    if (yamlConfiguration.contains("auth.users." + loggedInUsername + ".password")) {

                        String pass = yamlConfiguration.getString("auth.users." + loggedInUsername + ".password");
                        pass = new String(Base64.getDecoder().decode(pass));
                        String rank = "Default";
                        if (yamlConfiguration.contains("auth.users." + loggedInUsername + ".rank")) {

                            rank = yamlConfiguration.getString("auth.users." + loggedInUsername + ".rank");
                        }
                        if (pass.equals(loggedInPassword)) {

                            String pageContent = "";

                            try {
                                 pageContent = WebExFiles.getContentOfFile(new File("webcontent/dashboard/dashboard.html"))
                                        .replaceAll("%stylesheet%", WebExFiles.getContentOfFile(new File("webcontent/dashboard/dashboardstyle.css")))
                                         .replaceAll("%username%", yamlConfiguration.getString("auth.users." + loggedInUsername + ".name"))
                                         .replaceAll("%rank%", rank);
                            } catch (FileNotFoundException fileNotFoundException) {

                                fileNotFoundException.printStackTrace();
                            }

                            return pageContent;

//                            return "<pre>\n" +
//                                    "<span style=\"font-size:14px;\"><span style=\"font-family:georgia,serif;\"><tt><var>User Dashboard </var></tt></span></span>\n" +
//                                    "</pre>\n" +
//                                    "\n" +
//                                    "<hr />\n" +
//                                    "<p style=\"text-align: right;\"><span style=\"font-family:verdana,geneva,sans-serif;\">Your Rank: " + rank + "</span></p>" +
//                                    "<form action=\"/auth/logout\" method=\"post\" name=\"login\" target=\"_self\">\n" +
//                                    "<p><input name=\"Login\" type=\"submit\" value=\"Logout\" /></p>\n" +
//                                    "</form>";
                        } else {

                            RedirectView redirectView = new RedirectView();
                            redirectView.setUrl("/auth/login");
                            return redirectView;
                        }
                    }
                } else {

                    RedirectView redirectView = new RedirectView();
                    redirectView.setUrl("/auth/login");
                    return redirectView;
                }
            }
        }

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/auth/login");
        return redirectView;
    }

    @RequestMapping(value = "/auth/logout")
    public Object logout(HttpServletRequest request, HttpServletResponse response, @Nullable @CookieValue String loggedInUsername, @Nullable @CookieValue String loggedInPassword) {

        if(loggedInUsername == null) {
            return new RedirectView("/auth/login");
        }
        String pageContent = "";

        try {
            pageContent = WebExFiles.getContentOfFile(new File("webcontent/dashboard/loggedout.html"))
                    .replaceAll("%stylesheet%", WebExFiles.getContentOfFile(new File("webcontent/dashboard/loginpagestyle.css")));
        } catch (FileNotFoundException fileNotFoundException) {

            fileNotFoundException.printStackTrace();
        }

        Cookie cookie1 = new Cookie("loggedInUsername", null);
        cookie1.setMaxAge(0);
        cookie1.setSecure(false);
        cookie1.setHttpOnly(true);
        cookie1.setPath("/");
        response.addCookie(cookie1);
        Cookie cookie2 = new Cookie("loggedInPassword", null);
        cookie2.setMaxAge(0);
        cookie2.setSecure(false);
        cookie2.setHttpOnly(true);
        cookie2.setPath("/");
        response.addCookie(cookie2);
        try {
            response.getOutputStream().write(pageContent.getBytes(StandardCharsets.US_ASCII));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        request.getSession().invalidate();
//        RedirectView redirectView = new RedirectView();
//        redirectView.setUrl("/auth/login");
//        return redirectView;
        return null;
    }

    @RequestMapping(value = "/auth/resetpassword")
    public Object resetPassword(HttpServletRequest request, HttpServletResponse response, @Nullable @RequestParam String oldpassword, @Nullable @RequestParam String newpassword, @Nullable @RequestParam String type, @Nullable @RequestParam String username) {

        if(type == null) {
            String pageContent = "";

            try {
                pageContent = WebExFiles.getContentOfFile(new File("webcontent/dashboard/accountresetpassword.html"))
                        .replaceAll("%stylesheet%", WebExFiles.getContentOfFile(new File("webcontent/dashboard/loginpagestyle.css")));
            } catch (FileNotFoundException fileNotFoundException) {

                fileNotFoundException.printStackTrace();
            }

            return pageContent;
        } else {
            System.out.println(username);
            System.out.println(oldpassword);
            System.out.println(newpassword);
            if (oldpassword != null) {
                if (yamlConfiguration.contains("auth.users." + username)) {
                    if (yamlConfiguration.contains("auth.users." + username + ".password")) {
                        String pass = yamlConfiguration.getString("auth.users." + username + ".password");
                        pass = new String(Base64.getDecoder().decode(pass));
                        if (pass.equals(oldpassword) && !pass.equals(newpassword)) {
                            {
                                yamlConfiguration.set("auth.users." + username + ".password", Base64.getEncoder().encodeToString(newpassword.getBytes()));
                                try {
                                    yamlConfiguration.save(new File("credentials.yml"));
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                                Cookie cookie1 = new Cookie("loggedInUsername", null);
                                cookie1.setMaxAge(0);
                                cookie1.setSecure(true);
                                cookie1.setHttpOnly(true);
                                cookie1.setPath("/");
                                response.addCookie(cookie1);
                                Cookie cookie2 = new Cookie("loggedInPassword", null);
                                cookie2.setMaxAge(0);
                                cookie2.setSecure(true);
                                cookie2.setHttpOnly(true);
                                cookie2.setPath("/");
                                response.addCookie(cookie2);
                                System.out.println("DEBUG!");
                                return new RedirectView("/auth/login");
                            }
                        } else {
                            return new RedirectView("/auth/resetpassword");
                        }
                    } else {
                        return new RedirectView("/auth/resetpassword");
                    }
                } else {
                    return new RedirectView("/auth/resetpassword");
                }
            } else {
                return new RedirectView("/auth/resetpassword");
            }
        }
    }

    @RequestMapping(value = "/api/getpassword")
    public Object getUserPassword(@RequestParam String username) {

        if (username != null) {

            if (yamlConfiguration.contains("auth.users." + username)) {

                if (yamlConfiguration.contains("auth.users." + username + ".password")) {

                    return yamlConfiguration.getString("auth.users." + username + ".password");
                }
            }
        }
        return "null";
    }

    @RequestMapping(value = "/api/getrank")
    public Object getUserRank(@RequestParam String username) {

        if (username != null) {

            if (yamlConfiguration.contains("auth.users." + username)) {

                if (yamlConfiguration.contains("auth.users." + username + ".rank")) {

                    return yamlConfiguration.getString("auth.users." + username + ".rank");
                }
            }
        }
        return "null";
    }
    @RequestMapping(value = "/public/**")
    public void getPublicContent(HttpServletRequest request, HttpServletResponse response) {

        String path = "webcontent" + request.getServletPath();

        //System.out.println(path);

        if(path.endsWith(".png")) {

            response.setContentType(MediaType.IMAGE_PNG_VALUE);

            try {

                response.getOutputStream().write(IOUtils.readAllBytes(new FileInputStream(path)));

            } catch (IOException ioException) {

                //TODO Handle Exception without masssive Console Output!
                //ioException.printStackTrace();
            }

        }
        if(path.endsWith(".jpg")) {

            response.setContentType(MediaType.IMAGE_JPEG_VALUE);

            try {

                response.getOutputStream().write(IOUtils.readAllBytes(new FileInputStream(path)));

            } catch (IOException ioException) {

                //TODO Handle Exception without masssive Console Output!
                //ioException.printStackTrace();
                try {

                    response.getOutputStream().write("null".getBytes());
                } catch (IOException ioException1) {

                    ioException1.printStackTrace();
                }
            }

        }
    }
}
