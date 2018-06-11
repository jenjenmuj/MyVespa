package cz.uhk.fim.umte.myvespa;

import cz.uhk.fim.umte.myvespa.Remote.IGoogleAPIService;
import cz.uhk.fim.umte.myvespa.Remote.RetrofitClient;

public class Common {
    private  static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleApiService() {
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
