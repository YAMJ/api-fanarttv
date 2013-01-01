/*
 *      Copyright (c) 2004-2013 Stuart Boston
 *
 *      This file is part of the FanartTV API.
 *
 *      The FanartTV API is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      The FanartTV API is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with the FanartTV API.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.omertron.fanarttvapi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omertron.fanarttvapi.FanartTvException.FanartTvExceptionType;
import com.omertron.fanarttvapi.model.FTArtworkSort;
import com.omertron.fanarttvapi.model.FTArtworkType;
import com.omertron.fanarttvapi.model.FanartTvArtwork;
import com.omertron.fanarttvapi.model.WrapperMovie;
import com.omertron.fanarttvapi.model.WrapperMusic;
import com.omertron.fanarttvapi.model.WrapperSeries;
import com.omertron.fanarttvapi.tools.FilteringLayout;
import com.omertron.fanarttvapi.tools.WebBrowser;
import java.io.IOException;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This is the main class for the API to connect to Fanart.TV
 * http://fanart.tv/api-info/
 *
 * @author Stuart.Boston
 * @version 1.1
 *
 * TODO Allow a selection of the artwork types to be selected rather than 1 or
 * ALL
 */
public class FanartTvApi {

    private static final Logger logger = Logger.getLogger(FanartTvApi.class);
    private String apiKey;
    private static final String API_FORMAT = "json";
    private static final String ERROR_JSON_TEXT = "Failed processing JSON Node";
    /*
     * URL for the API
     */
    private static final String API_SITE = "http://api.fanart.tv/webservice/";
    private static final String API_SERIES = "series/";
    private static final String API_MOVIE = "movie/";
    private static final String API_MUSIC = "artist/";
    /*
     * Defaults for the API
     */
    private static final int DEFAULT_ARTWORK_LIMIT = 2;
    private static final FTArtworkType DEFAULT_ARTWORK_TYPE = FTArtworkType.ALL;

    /*
     * Jackson JSON configuration
     */
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Create a new API instance with the given API Key
     *
     * @param apiKey
     */
    public FanartTvApi(String apiKey) {
        this.apiKey = apiKey;
        FilteringLayout.addApiKey(apiKey);
    }

    /**
     * Get the artwork for a specific TV Series
     *
     * @param searchUrl
     * @return
     */
    private List<FanartTvArtwork> readTvArtwork(String searchUrl) throws FanartTvException {
        JsonNode jn = getJsonNode(searchUrl);
        Iterator<String> ftNode = jn.fieldNames();

        while (ftNode.hasNext()) {
            WrapperSeries wrapper;
            try {
                wrapper = mapper.treeToValue(jn.get(ftNode.next()), WrapperSeries.class);
            } catch (JsonParseException ex) {
                throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
            } catch (JsonMappingException ex) {
                throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
            } catch (IOException ex) {
                throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
            }

            ArrayList<FanartTvArtwork> artworkList = new ArrayList<FanartTvArtwork>();

            // Get the artwork and apply the correct FTArtworkType to it
            for (Map.Entry<FTArtworkType, List<FanartTvArtwork>> entry : wrapper.getArtwork().entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    for (FanartTvArtwork ftSingle : entry.getValue()) {
                        ftSingle.setType(entry.getKey());
                        artworkList.add(ftSingle);
                    }
                }
            }

            return artworkList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Get the artwork for a specific movie
     *
     * @param searchUrl
     * @return
     */
    private List<FanartTvArtwork> readMovieArtwork(String searchUrl) throws FanartTvException {
        JsonNode jn = getJsonNode(searchUrl);
        Iterator<String> ftNode = jn.fieldNames();

        while (ftNode.hasNext()) {
            WrapperMovie wrapper;
            try {
                wrapper = mapper.treeToValue(jn.get(ftNode.next()), WrapperMovie.class);
            } catch (JsonParseException ex) {
                throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
            } catch (JsonMappingException ex) {
                throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
            } catch (IOException ex) {
                throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
            }

            ArrayList<FanartTvArtwork> artworkList = new ArrayList<FanartTvArtwork>();

            // Get the artwork and apply the correct FTArtworkType to it
            for (Map.Entry<FTArtworkType, List<FanartTvArtwork>> entry : wrapper.getArtwork().entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    for (FanartTvArtwork ftSingle : entry.getValue()) {
                        ftSingle.setType(entry.getKey());
                        artworkList.add(ftSingle);
                    }
                }
            }

            return artworkList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Get the artwork for a specific music ID
     *
     * @param searchUrl
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> readMusicArtwork(String searchUrl) throws FanartTvException {
        JsonNode jn = getJsonNode(searchUrl);
        Iterator<String> ftNode = jn.fieldNames();

        while (ftNode.hasNext()) {
            WrapperMusic wrapper;
            try {
                wrapper = mapper.treeToValue(jn.get(ftNode.next()), WrapperMusic.class);
            } catch (JsonParseException ex) {
                throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
            } catch (JsonMappingException ex) {
                throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
            } catch (IOException ex) {
                throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
            }

            ArrayList<FanartTvArtwork> artworkList = new ArrayList<FanartTvArtwork>();

            // Get the artwork and apply the correct FTArtworkType to it
            for (Map.Entry<FTArtworkType, List<FanartTvArtwork>> entry : wrapper.getArtwork().entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    for (FanartTvArtwork ftSingle : entry.getValue()) {
                        ftSingle.setType(entry.getKey());
                        artworkList.add(ftSingle);
                    }
                }
            }

            return artworkList;
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Get all the artwork of a type for a TVDB ID
     *
     * @param tvdbId
     * @param artworkType
     * @param artworkSortBy
     * @param artworkLimit
     * @return
     */
    public List<FanartTvArtwork> getTvArtwork(int tvdbId, FTArtworkType artworkType, FTArtworkSort artworkSortBy, int artworkLimit) throws FanartTvException {
        return readTvArtwork(buildSeriesUrl(tvdbId, artworkType, artworkSortBy, artworkLimit));
    }

    /**
     * Get all the artwork of a type for a TVDB ID
     *
     * @param tvdbId
     * @param artworkType
     * @param artworkSortBy
     * @return
     */
    public List<FanartTvArtwork> getTvArtwork(int tvdbId, FTArtworkType artworkType, FTArtworkSort artworkSortBy) throws FanartTvException {
        return getTvArtwork(tvdbId, artworkType, artworkSortBy, DEFAULT_ARTWORK_LIMIT);
    }

    /**
     * Get all the artwork of a type for a TVDB ID
     *
     * @param tvdbId
     * @param artworkType
     * @return
     */
    public List<FanartTvArtwork> getTvArtwork(int tvdbId, FTArtworkType artworkType) throws FanartTvException {
        return getTvArtwork(tvdbId, artworkType, FTArtworkSort.DEFAULT);
    }

    /**
     * Get all the artwork for a specific TVDB ID
     *
     * @param tvdbId
     * @return
     */
    public List<FanartTvArtwork> getTvArtwork(int tvdbId) throws FanartTvException {
        return getTvArtwork(tvdbId, DEFAULT_ARTWORK_TYPE);
    }

    /**
     * Get all the artwork for a specific TheMovieDb ID
     *
     * @param tmdbId
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMovieArtwork(int tmdbId) throws FanartTvException {
        return getMovieArtwork(tmdbId, DEFAULT_ARTWORK_TYPE);
    }

    /**
     * Get all the artwork for a specific TheMovieDb ID
     *
     * @param tmdbId
     * @param artworkType
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMovieArtwork(int tmdbId, FTArtworkType artworkType) throws FanartTvException {
        return getMovieArtwork(tmdbId, artworkType, FTArtworkSort.DEFAULT);
    }

    /**
     * Get all the artwork for a specific TheMovieDb ID
     *
     * @param tmdbId
     * @param artworkType
     * @param artworkSortBy
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMovieArtwork(int tmdbId, FTArtworkType artworkType, FTArtworkSort artworkSortBy) throws FanartTvException {
        return getMovieArtwork(tmdbId, artworkType, artworkSortBy, DEFAULT_ARTWORK_LIMIT);
    }

    /**
     * Get all the artwork for a specific TheMovieDb ID
     *
     * @param tmdbId
     * @param artworkType
     * @param artworkSortBy
     * @param artworkLimit
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMovieArtwork(int tmdbId, FTArtworkType artworkType, FTArtworkSort artworkSortBy, int artworkLimit) throws FanartTvException {
        return readMovieArtwork(buildMovieUrl(tmdbId, artworkType, artworkSortBy, artworkLimit));
    }

    /**
     * Get all the artwork for a specific IMDB ID
     *
     * @param imdbId
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMovieArtwork(String imdbId) throws FanartTvException {
        return getMovieArtwork(imdbId, DEFAULT_ARTWORK_TYPE);
    }

    /**
     * Get all the artwork for a specific IMDB ID
     *
     * @param imdbId
     * @param artworkType
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMovieArtwork(String imdbId, FTArtworkType artworkType) throws FanartTvException {
        return getMovieArtwork(imdbId, artworkType, FTArtworkSort.DEFAULT);
    }

    /**
     * Get all the artwork for a specific IMDB ID
     *
     * @param imdbId
     * @param artworkType
     * @param artworkSortBy
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMovieArtwork(String imdbId, FTArtworkType artworkType, FTArtworkSort artworkSortBy) throws FanartTvException {
        return getMovieArtwork(imdbId, artworkType, artworkSortBy, DEFAULT_ARTWORK_LIMIT);
    }

    /**
     * Get all the artwork for a specific IMDB ID
     *
     * @param imdbId
     * @param artworkType
     * @param artworkSortBy
     * @param artworkLimit
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMovieArtwork(String imdbId, FTArtworkType artworkType, FTArtworkSort artworkSortBy, int artworkLimit) throws FanartTvException {
        return readMovieArtwork(buildMovieUrl(imdbId, artworkType, artworkSortBy, artworkLimit));
    }

    /**
     * get all the artwork for a specific Music Brainz ID
     *
     * @param musicId
     * @param artworkType
     * @param artworkSort
     * @param artworkLimit
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMusicArtwork(String musicId, FTArtworkType artworkType, FTArtworkSort artworkSort, int artworkLimit) throws FanartTvException {
        return readMusicArtwork(buildMusicUrl(musicId, artworkType, artworkSort, artworkLimit));
    }

    /**
     * get all the artwork for a specific Music Brainz ID
     *
     * @param musicId
     * @param artworkType
     * @param artworkSort
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMusicArtwork(String musicId, FTArtworkType artworkType, FTArtworkSort artworkSort) throws FanartTvException {
        return getMusicArtwork(musicId, artworkType, artworkSort, DEFAULT_ARTWORK_LIMIT);
    }

    /**
     * get all the artwork for a specific Music Brainz ID
     *
     * @param musicId
     * @param artworkType
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMusicArtwork(String musicId, FTArtworkType artworkType) throws FanartTvException {
        return getMusicArtwork(musicId, artworkType, FTArtworkSort.DEFAULT);
    }

    /**
     * get all the artwork for a specific Music Brainz ID
     *
     * @param musicId
     * @return
     * @throws FanartTvException
     */
    public List<FanartTvArtwork> getMusicArtwork(String musicId) throws FanartTvException {
        return getMusicArtwork(musicId, DEFAULT_ARTWORK_TYPE);
    }

    /**
     * Set proxy parameters.
     *
     * @param host proxy host URL
     * @param port proxy port
     * @param username proxy username
     * @param password proxy password
     */
    public void setProxy(String host, String port, String username, String password) {
        WebBrowser.setProxyHost(host);
        WebBrowser.setProxyPort(port);
        WebBrowser.setProxyUsername(username);
        WebBrowser.setProxyPassword(password);
    }

    /**
     * Set web browser timeout.
     *
     * @param webTimeoutConnect
     * @param webTimeoutRead
     */
    public void setTimeout(int webTimeoutConnect, int webTimeoutRead) {
        WebBrowser.setWebTimeoutConnect(webTimeoutConnect);
        WebBrowser.setWebTimeoutRead(webTimeoutRead);
    }

    /**
     * Build the URL that is used to get the XML from Fanart.tv
     *
     * @param baseUrl
     * @param artworkId The id for the video/tv/music, one of TheMovieDB, IMDB,
     * TheTVDB or MusicBrainz
     * @param artworkType The type of the artwork to limit the search too.
     * "all"/Blank/null gets all artwork
     * @param artworkSortBy Added for completeness, but not used
     * @return The search URL
     *
     */
    private String buildUrl(String baseUrl, String artworkId, FTArtworkType artworkType, FTArtworkSort artworkSortBy, int artworkLimit) {
        //http://fanart.tv/webservice/series/apikey/thetvdb_id/format/type/sort/limit/
        StringBuilder searchUrl = new StringBuilder(API_SITE);

        searchUrl.append(baseUrl);

        searchUrl.append(apiKey).append("/");
        searchUrl.append(artworkId).append("/");
        searchUrl.append(API_FORMAT).append("/");

        searchUrl.append(artworkType.toString().toLowerCase()).append("/");

        searchUrl.append(artworkSortBy.getValue()).append("/");

        if (artworkLimit > 0) {
            searchUrl.append(Math.max(artworkLimit, 2)).append("/");
        }

        logger.trace("Search URL: " + searchUrl);
        return searchUrl.toString();
    }

    private String buildSeriesUrl(int tvdbId, FTArtworkType artworkType, FTArtworkSort artworkSortBy, int artworkLimit) {
        String idString = String.valueOf(tvdbId < 0 ? 0 : tvdbId);
        return buildUrl(API_SERIES, idString, artworkType, artworkSortBy, artworkLimit);
    }

    private String buildMovieUrl(int tmdbId, FTArtworkType artworkType, FTArtworkSort artworkSortBy, int artworkLimit) {
        String idString = String.valueOf(tmdbId < 0 ? 0 : tmdbId);
        return buildMovieUrl(idString, artworkType, artworkSortBy, artworkLimit);
    }

    private String buildMovieUrl(String imdbId, FTArtworkType artworkType, FTArtworkSort artworkSortBy, int artworkLimit) {
        return buildUrl(API_MOVIE, imdbId, artworkType, artworkSortBy, artworkLimit);
    }

    private String buildMusicUrl(String musicId, FTArtworkType artworkType, FTArtworkSort artworkSort, int artworkLimit) {
        return buildUrl(API_MUSIC, musicId, artworkType, artworkSort, artworkLimit);
    }

    /**
     * Method to get JSON Node from the search URL
     *
     * @param searchUrl
     * @return
     * @throws FanartTvException
     */
    private JsonNode getJsonNode(String searchUrl) throws FanartTvException {
        String webPage;
        try {
            webPage = WebBrowser.request(searchUrl);
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.INVALID_URL, searchUrl, ex);
        }

        // Strip the wrapper from the json returned
        try {
            return mapper.readTree(webPage);
        } catch (JsonProcessingException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, ERROR_JSON_TEXT, ex);
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, ERROR_JSON_TEXT, ex);
        }
    }
}
