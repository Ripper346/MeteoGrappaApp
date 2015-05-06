/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alessandro.meteograppa;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This class downloads the content of a web page.
 *
 * @author Alessandro
 */
public class ResourceDownloader {

    private String page;

    /**
     * It downloads the web page and store in a String variable.
     *
     * @param link url of the page to download
     */
    public ResourceDownloader(String link) {
        try {
            page = Jsoup.connect(link).ignoreContentType(true).execute().body();
        } catch (IOException ex) {
            page = null;
            Logger.getLogger(ResourceDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPage() {
        return page;
    }
}
