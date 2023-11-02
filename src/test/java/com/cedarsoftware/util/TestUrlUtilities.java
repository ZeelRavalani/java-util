package com.cedarsoftware.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UrlUtilities class.
 */
public class TestUrlUtilities {

    @Test
    /**
     * Test case for the setCookies method.
     */
    public void testSetCookies() {
        // Creating a mock URLConnection
        URLConnection mockConnection = mock(URLConnection.class);

        try {
            when(mockConnection.getURL()).thenReturn(new URL("https://github.com"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Map<String, Map<String, Object>> outCookies = new HashMap<>();
        Map<String, Object> domainStore = new ConcurrentHashMap<>();
        outCookies.put("github.com", domainStore);

        try {
            UrlUtilities.setCookies(mockConnection, outCookies);
        } catch (IOException e) {
            fail("Exception should not be thrown");
        }
    }

    @Test
    /**
     * Test case for the getContentFromUrl method.
     */
    public void testGetContentFromUrl() {
        try {
            // Defining a dummy URL for testing
            URL url = new URL("https://github.com");

            // Creating empty cookie stores for input and output cookies
            Map<String, Map<String, Object>> inCookies = new HashMap<>();
            Map<String, Map<String, Object>> outCookies = new HashMap<>();

            // Calling getContentFromUrl to fetch content
            byte[] content = UrlUtilities.getContentFromUrl(url, inCookies, outCookies, true);

            assertNotNull(content);
            assertTrue(content.length > 0);
        } catch (MalformedURLException e) {
            Assertions.fail("MalformedURLException should not be thrown");
        }
    }

    @Test
    /**
     * Test case for the getConnection method.
     */
    public void testGetConnection() {
        try {
            // Creating a URL for testing.
            URL testUrl = new URL("https://github.com");

            // Calling the method to get a connection.
            URLConnection connection = UrlUtilities.getConnection(testUrl, false, false, false);

            assertNotNull(connection);
        } catch (IOException e) {
            fail("Exception occurred while getting a connection: " + e.getMessage());
        }
    }
}