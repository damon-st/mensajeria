package com.damon.messenger;

public interface iConstants {

    public static final String[] DISABLE_DOWNLOADING = {};
    public  static final String SEARCH_ENGINE="https://www.google.com/search?q=%1$s"; //Search Engine
    public  static  final String API_URL = "https://www.saveitoffline.com/process/?url=%1$s&type=json";
    public static final String API_URL2 = "https://savevideo.tube/";
    public static final String WEB_DISABLE = "We cannot allow to download videos form this website.";
    public static final String PREF_APPNAME = "xmatevidedownloader";
    public static final String DOWNLOADING_MSG = "Generating Download links";
    public  static  final String URL_NOT_SUPPORTED = "This url not supported or no media found!";
    public static final String DOWNLOAD_DIRECTORY="in_video_dwonloader";
    // public Integer[] HomePageThumbs = {R.drawable.img_logo_facebook,R.drawable.img_logo_instagram,R.drawable.img_logo_twitter,R.drawable.img_logo_pinterest,R.drawable.dailymotion,R.drawable.img_logo_tumblr,R.drawable.img_logo_vimeo,R.drawable.img_logo_vine,R.drawable.img_logo_keek};
    //  public String[] HomePageURI = {"http://facebook.com","http://youtube.com","http://twitter.com","http://kotalipara.com","http://youtube.com/kotalipara","http://tumblr.com","http://vimeo.com","https://vine.co","https://k.to"};

    public String[] HomePageURI = {"http://youtube.com"};

    public Integer[] HomePageThumbs = {R.mipmap.ic_launcher};

}
