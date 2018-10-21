package com.company;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static int linkOk;
    private static int linkBroken;

    public static void main(String[] args) {
        try {
            //============Connexion=============

            HttpURLConnection conn = connection("https://perso.liris.cnrs.fr/pierre-antoine.champin/enseignement/apiweb/_static/test_liens.html");
            conn.connect();
            // =============Lecture==============

            InputStream bodyStream = conn.getInputStream();//récupération du stream
            InputStreamReader readerStream = new InputStreamReader(bodyStream);//traduction en format lisible
            BufferedReader bodyReader = new BufferedReader(readerStream);//lecture du stream
            String line = bodyReader.readLine();//lecture de chaque ligne
            while(line != null){
                linkReader(line);
                line = bodyReader.readLine();
            }
            System.out.println(linkBroken);
            System.out.println(linkOk);

            URL url = new URL("https://liris-ktbs01.insa-lyon.fr:8000/blogephem/");
            HttpURLConnection conn2 = (HttpURLConnection) url.openConnection();
            conn2.setRequestMethod("POST");
            conn2.addRequestProperty("content-type", "application/x-www-form-urlencoded");
            conn2.setDoOutput(true);
            OutputStream stream = conn2.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write("title=hello&body=world");
            writer.close();
            conn2.connect();

        }catch (Exception e){
        }
    }

    public static void linkReader(String line) {
        Pattern regex = Pattern.compile("(src|href)\\s*=\\s*[\"']([^\"']*)[\"']");
        Matcher matcher = regex.matcher(line);
        while (matcher.find()){
            linkTester(matcher.group(2));
            System.out.println(matcher.group(2));
        }
    }

    public static void linkTester(String linkBis) {
        try {
            HttpURLConnection conn2 = connection(linkBis);
            conn2.connect();
            //===================================

            int status = conn2.getResponseCode();
            System.out.println(status);
            //===================================

            if (status <= 399 && status >= 100){
                linkOk++;
            }else if(status <= 599 && status >= 400){
                linkBroken++;
            }

        }catch(Exception e){
            linkBroken++;
            System.out.println(e.getMessage());
        }
    }

    public static HttpURLConnection connection(String link){
        try {
            URL base = new URL ("https://perso.liris.cnrs.fr/pierre-antoine.champin/enseignement/apiweb/_static/test_liens.html");
            URL url = new URL(base, link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("Accept-language", "fr");
            return conn;
        }catch(Exception e){
            return null;
        }
    }
}
