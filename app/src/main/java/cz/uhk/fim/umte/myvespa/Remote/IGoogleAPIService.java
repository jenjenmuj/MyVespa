package cz.uhk.fim.umte.myvespa.Remote;

import cz.uhk.fim.umte.myvespa.Model.MyPlaces;
import cz.uhk.fim.umte.myvespa.Model.PlaceDetail;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

    @GET
    Call<PlaceDetail> getDetail(@Url String url);
}
