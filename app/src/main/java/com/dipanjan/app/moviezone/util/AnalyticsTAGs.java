package com.dipanjan.app.moviezone.util;

/**
 * Created by LENOVO on 16-01-2019.
 */

public interface AnalyticsTAGs {

    interface Category{
        String CATEGORY_BUTTON_CLICK="Button Click";
        String CATEGORY_SNACKBAR_CLICK="Snackbar Click";
        String CATEGORY_MENU_CLICK="Menu Click";
        String CATEGORY_MOVIE_CLICK="Open Movie";
        String CATEGORY_MOVIE_SERIES__CLICK="Open Movie Series";
        String CATEGORY_MOVIE_SERIES_NAME="Open Movie Series Named AS";
        String CATEGORY_MOVIE_FROM_SERIES="Open Movie From Series";
        String CATEGORY_SEARCH_MOVIE="Search Movie";
        String CATEGORY_MOVIE_ITEM_CLICK="MOVIE CLICK";
        String CATEGORY_MOVIE_LIKE_CLICK="LIKE MOVIE";
        String CATEGORY_DISLIKE_MOVIE_CLICK="DISLIKE MOVIE";
    }

    interface Events{
        String EVENT_LIKE_MOVIE = "Like a Movie";
        String EVENT_DISLIKE_MOVIE = "Dislike a Movie";
        String EVENT_WATCH_TRAILER = "Watch Trailer";
        String EVENT_VIEW_MOVIE_IMDB = "View IMDB";
        String EVENT_DOWNLOAD_TORRENT = "DOWNLOAD TORRENT";
        String EVENT_DOWNLOAD_SUBTITLE = "DOWNLOAD SUBTITLE";
        String EVENT_ACTOR_IMDB_PROFILE = "View Actor IMDB";
        String EVENT_MAGNET_TORRENT_LINK_CLICK = "Download Torrent Magnet Link ";
        String EVENT_SNACKBAR_DISPLAY_NO_TORRENT_CLIENT = "Snackbar Display for no torrent client install";
        String EVENT_VIEW_TUTORIAL_CLICK = "View Tutorial Click";
        String EVENT_DOWNLOAD_TORRENT_CLIENT_CLICK = "Download Torrent Client ";
        String EVENT_RATE_THE_APP = "Rate App";
        String EVENT_OPEN_DISCLAIMER = "Open Disclaimer";
        String EVENT_OPEN_MOVIE = "Open Movie";
        String EVENT_OPEN_MOVIE_SERIES = "Open Movie Series";
        String EVENT_OPEN_SEARCH_MOVIE = "Search Movie";

    }
}
