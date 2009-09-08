package com.mockey.runner;

import org.mortbay.jetty.runner.Runner;

import java.io.File;

public class Main {


    public static void main(String args[]) throws Exception {
        String port = "8080";

        for (int i = 0; i < args.length; i++) {
            if ("--port".equals(args[i])) {
                port = args[++i];
            }
        }

        String[] argv = {"--port", port, "Mockey.war"};

        new Thread(new BrowserThread("http://127.0.0.1:", port, 10)).start();

        Runner runner = new Runner();
        runner.configure(argv);
        runner.run();

        new File("Mockey.war").deleteOnExit();
    }

}

