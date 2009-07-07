package com.mockey.util;


public class Url {
    String scheme;
    String port;
    String host;
    String path;

    public Url(String url) {
        // extract the scheme
        if(url.matches("(?i)^https?://.*")) {
            this.scheme = url.substring(0,url.indexOf(":"));
            url = url.substring(url.indexOf("://")+3, url.length());
        }else{
            this.scheme = "http";
        }

        // extract the host and port
        String hostAndPort;
        if(url.indexOf("/") > 0) {
             hostAndPort =  url.substring(0,url.indexOf("/"));
        }else{
            hostAndPort = url;
        }
        if(hostAndPort.indexOf(":") > 0) {
            this.host = hostAndPort.substring(0, hostAndPort.indexOf(":"));
            this.port = hostAndPort.substring(hostAndPort.indexOf(":")+1,hostAndPort.length());
        }else{
            this.host = hostAndPort;
            if(this.scheme.equalsIgnoreCase("https")) {
                this.port = "443";
            }else{
                this.port = "80";
            }
        }

        if(url.indexOf("/") > 0) {
            this.path = url.substring(url.indexOf("/") +1, url.length());
        }
    }

    public String getScheme() {
        return scheme;
    }

    public String getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }
}
