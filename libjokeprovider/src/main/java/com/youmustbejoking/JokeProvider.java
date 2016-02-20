package com.youmustbejoking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.joningi.icndb.ICNDBClient;
import net.joningi.icndb.Joke;

import org.jsoup.Jsoup;


public class JokeProvider {

    private static final Logger log = Logger.getLogger(JokeProvider.class.getName());

    private static final ICNDBClient client = new ICNDBClient();

    public static void main(String [] args) {
        log.log(Level.INFO, "LEE: main");
        String joke = getJoke("and now for something completely different..", "Chuck", "Norris", false);
        System.out.println("---> JOKE: "+joke);
    }

    public static String getJoke(String defaultJoke, String firstname, String lastname, @SuppressWarnings("SameParameterValue") boolean useFortune) {
        log.log(Level.INFO, "===> LEE: getJoke <===");
        final Random rand = new Random();
        int number = rand.nextInt(10000) + 1; // uniformly distributed int from 1 to 100000
        String theJoke;
        if (useFortune) {
            theJoke = getFortune();
        }
        else {
            theJoke = Jsoup.parse(getJokeFromInternet(firstname, lastname)).text();
        }
        if (theJoke == null) theJoke = "Random Joke #" + number + ":\n\n" + defaultJoke;

        return theJoke;
    }

    // get a joke from the Internet Chuck Norris Database..
    private static String getJokeFromInternet(String firstname, String lastname) {
        log.log(Level.INFO, "===> LEE: getJokeFromInternet <===");
        try {
            client.setFirstName(firstname);
            client.setLastName(lastname);
            Joke randomJoke = client.getRandom();
            return randomJoke.getJoke();
        }
        catch (Exception e) {
            log.log(Level.WARNING, "problem accessing the Internet Chuck Norris Database - e="+e);
            return null;
        }
    }

    // get a joke from the local 'fortune' program
    private static String getFortune() {
        log.log(Level.INFO, "===> LEE: getFortune <===");
        try {
            StringBuilder jokeBuffer = new StringBuilder();
            String[] command =
                {
                        "C:\\cygwin64\\bin\\fortune.exe", // FIXME: of course your 'fortune' program may not be installed here
                                                          // you can check this by running './gradlew runJar' from the project root
                };

            // from: http://stackoverflow.com/questions/5711084/java-runtime-getruntime-getting-output-from-executing-a-command-line-program
            Process proc = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            String s;
            while ((s = stdInput.readLine()) != null) {
                jokeBuffer.append(s);
                jokeBuffer.append("\n");
            }
            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            return jokeBuffer.toString();
        }
        catch (Exception e) {
            log.log(Level.WARNING, "ERROR: e=" + e);
            return null;
        }
    }

}
