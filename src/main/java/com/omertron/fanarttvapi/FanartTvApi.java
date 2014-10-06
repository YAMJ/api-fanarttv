/*
 *      Copyright (c) 2004-2014 Stuart Boston
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.omertron.fanarttvapi.FanartTvException.FanartTvExceptionType;
import com.omertron.fanarttvapi.enumeration.BaseType;
import com.omertron.fanarttvapi.model.FTLatest;
import com.omertron.fanarttvapi.model.FTMovie;
import com.omertron.fanarttvapi.model.FTMusicAlbum;
import com.omertron.fanarttvapi.model.FTMusicArtist;
import com.omertron.fanarttvapi.model.FTMusicLabel;
import com.omertron.fanarttvapi.model.FTSeries;
import com.omertron.fanarttvapi.tools.ApiBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamj.api.common.http.CommonHttpClient;
import org.yamj.api.common.http.DefaultPoolingHttpClient;

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

    private static final Logger LOG = LoggerFactory.getLogger(FanartTvApi.class);
    private ApiBuilder API;
    private static final String DEFAULT_CHARSET = "UTF-8";
    private CommonHttpClient httpClient;
    private static final String ERROR_JSON_TEXT = "Failed processing JSON Node";

    /*
     * Jackson JSON configuration
     */
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Create a new API instance with the given API Key.
     *
     * A null client key will be used.
     *
     * @param apiKey
     * @throws FanartTvException
     */
    public FanartTvApi(String apiKey) throws FanartTvException {
        this(apiKey, null, new DefaultPoolingHttpClient());
    }

    /**
     * Create a new API instance with the given API keys.
     *
     * @param apiKey
     * @param clientKey
     * @throws FanartTvException
     */
    public FanartTvApi(String apiKey, String clientKey) throws FanartTvException {
        this(apiKey, clientKey, new DefaultPoolingHttpClient());
    }

    /**
     * Create a new API instance with the given API Key and specific
     * CommonHttpClient
     *
     * @param apiKey
     * @param clientKey
     * @param httpClient
     * @throws FanartTvException
     */
    public FanartTvApi(String apiKey, String clientKey, CommonHttpClient httpClient) throws FanartTvException {
        if (StringUtils.isBlank(apiKey)) {
            throw new FanartTvException(FanartTvExceptionType.UNKNOWN_CAUSE, "Invalid API Key");
        }

        this.API = new ApiBuilder(apiKey, clientKey);
        this.httpClient = httpClient;

    }

    /**
     * Provide access to the JSON mapper object
     *
     * @return
     */
    public static ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Get images for TV
     *
     * @param id
     * @return
     * @throws FanartTvException
     */
    public FTSeries getTvArtwork(String id) throws FanartTvException {
        URL url = API.getImageUrl(BaseType.TV, id);
        String page = requestWebPage(url);

        FTSeries series = null;

        try {
            series = mapper.readValue(page, FTSeries.class);
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
        }

        if (series.isError()) {
            LOG.info("Error: {}", series.getErrorMessage());
            throw new FanartTvException(FanartTvExceptionType.ID_NOT_FOUND, series.getErrorMessage());
        }

        return series;
    }

    /**
     * Latest Shows
     *
     * @param date
     * @return
     * @throws FanartTvException
     */
    public List<FTLatest> getTvLatest(String date) throws FanartTvException {
        URL url = API.getLatestUrl(BaseType.TV, date);
        String page = requestWebPage(url);

        List<FTLatest> latest = null;

        try {
            latest = mapper.readValue(page, new TypeReference<List<FTLatest>>() {
            });
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
        }

        return latest;
    }

    /**
     * Get images for Movie
     *
     * @param id
     * @return
     * @throws FanartTvException
     */
    public FTMovie getMovieArtwork(String id) throws FanartTvException {
        URL url = API.getImageUrl(BaseType.MOVIE, id);
        String page = requestWebPage(url);

        FTMovie movie = null;

        try {
            movie = mapper.readValue(page, FTMovie.class);
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
        }

        if (movie.isError()) {
            LOG.info("Error: {}", movie.getErrorMessage());
            throw new FanartTvException(FanartTvExceptionType.ID_NOT_FOUND, movie.getErrorMessage());
        }

        return movie;
    }

    /**
     * Latest Movies
     *
     * @param date
     * @return
     * @throws FanartTvException
     */
    public List<FTLatest> getMovieLatest(String date) throws FanartTvException {
        URL url = API.getLatestUrl(BaseType.MOVIE, date);
        String page = requestWebPage(url);

        List<FTLatest> latest = null;

        try {
            latest = mapper.readValue(page, new TypeReference<List<FTLatest>>() {
            });
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
        }

        return latest;
    }

    /**
     * Get images for artist
     *
     * @param id
     * @return
     * @throws FanartTvException
     */
    public FTMusicArtist getMusicArtist(String id) throws FanartTvException {
        URL url = API.getImageUrl(BaseType.ARTIST, id);
        String page = requestWebPage(url);

        FTMusicArtist artist = null;

        try {
            artist = mapper.readValue(page, FTMusicArtist.class);
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
        }

        return artist;
    }

    /**
     * Get Album
     *
     * @param id
     * @return
     * @throws FanartTvException
     */
    public FTMusicAlbum getMusicAlbum(String id) throws FanartTvException {
        URL url = API.getImageUrl(BaseType.ALBUM, id);
        String page = requestWebPage(url);

        FTMusicAlbum album = null;

        try {
            album = mapper.readValue(page, FTMusicAlbum.class);
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
        }

        return album;
    }

    /**
     * Get Label
     *
     * @param id
     * @return
     * @throws FanartTvException
     */
    public FTMusicLabel getMusicLabel(String id) throws FanartTvException {
        URL url = API.getImageUrl(BaseType.LABEL, id);
        String page = requestWebPage(url);

        FTMusicLabel label = null;

        try {
            label = mapper.readValue(page, FTMusicLabel.class);
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
        }

        return label;
    }

    /**
     * Latest Artists
     *
     * @param date
     * @return
     * @throws FanartTvException
     */
    public List<FTLatest> getMusicArtistLatest(String date) throws FanartTvException {
        URL url = API.getLatestUrl(BaseType.ARTIST, date);
        String page = requestWebPage(url);

        List<FTLatest> latest = null;

        try {
            latest = mapper.readValue(page, new TypeReference<List<FTLatest>>() {
            });
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.MAPPING_FAILED, null, ex);
        }

        return latest;
    }

    /**
     * Set proxy parameters.
     *
     * @param host proxy host URL
     * @param port proxy port
     * @param username proxy username
     * @param password proxy password
     */
    public void setProxy(String host, int port, String username, String password) {
        httpClient.setProxy(host, port, username, password);
    }

    /**
     * Set web browser timeout.
     *
     * @param webTimeoutConnect
     * @param webTimeoutRead
     */
    public void setTimeout(int webTimeoutConnect, int webTimeoutRead) {
        httpClient.setTimeouts(webTimeoutConnect, webTimeoutRead);
    }

    /**
     * Download the URL into a String
     *
     * @param url
     * @return
     * @throws FanartTvException
     */
    private String requestWebPage(URL url) throws FanartTvException {
        try {
            HttpGet httpGet = new HttpGet(url.toURI());
            httpGet.addHeader("accept", "application/json");
            return httpClient.requestContent(httpGet, Charset.forName(DEFAULT_CHARSET));
        } catch (URISyntaxException ex) {
            throw new FanartTvException(FanartTvExceptionType.CONNECTION_ERROR, "Invalid URL: " + url, ex);
        } catch (IOException ex) {
            throw new FanartTvException(FanartTvExceptionType.CONNECTION_ERROR, "Error retrieving URL: " + url, ex);
        }
    }
}
