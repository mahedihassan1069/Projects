package e.user.madiambu.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIKey {

    @GET
    Call<String> getPath(@Url String url);
}
