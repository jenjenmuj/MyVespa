package cz.uhk.fim.umte.myvespa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import cz.uhk.fim.umte.myvespa.Model.Photos;

public class ViewPlace extends AppCompatActivity {

    ImageView photo;
    String detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_place);

        photo = (ImageView)findViewById(R.id.photo);


        // Check if photo is not null
        if (Common.currentResult.getPhotos() != null && Common.currentResult.getPhotos().length > 0) {
            // getPhotos is array, we will use onlz first image
            Picasso.with(this)
                    .load(getPhotoOfPlace(Common.currentResult.getPhotos()[0].getPhoto_reference(), 1000))
                    .into(photo);
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
