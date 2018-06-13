package cz.uhk.fim.umte.myvespa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import cz.uhk.fim.umte.myvespa.Model.PlaceDetail;
import cz.uhk.fim.umte.myvespa.Remote.IGoogleAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPlace extends AppCompatActivity {

    ImageView photo;
    RatingBar ratingBar;
    TextView opening_hours, place_address, place_name;
    Button btnViewOnMap;

    IGoogleAPIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        mService = Common.getGoogleApiService();

        photo = (ImageView) findViewById(R.id.photo);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        opening_hours = (TextView) findViewById(R.id.place_open_hours);
        place_address = (TextView) findViewById(R.id.place_address);
        place_name = (TextView) findViewById(R.id.place_name);
        btnViewOnMap = (Button) findViewById(R.id.btn_show_map);


        // Photos - Check if photo is not null
        if (Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length > 0) {
            // getPhotos is array, we will use onlz first image
            Picasso.with(this)
                    .load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(), 1000))
                    .into(photo);
        }

        // RAting bar
        if (Common.currentResult.getRating() != null && !TextUtils.isEmpty(Common.currentResult.getRating())) {
            ratingBar.setRating((Float.parseFloat(Common.currentResult.getRating())));
        } else {
            ratingBar.setVisibility(View.GONE);
        }

        //Opening hours
        if (Common.currentResult.getOpening_hours() != null) {
            opening_hours.setText("Open now : "Common.currentResult.getOpening_hours().getOpen_now());
        } else {
            opening_hours.setVisibility(View.GONE);
        }


    }

    private String getPhotoOfPlace(String photo_reference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?")
                .append("maxwidth=" + maxWidth)
                .append("&photoreference=" + photo_reference)
                .append("&key=" + getResources().getString(R.string.browser_key));
        return url.toString();
    }
}
