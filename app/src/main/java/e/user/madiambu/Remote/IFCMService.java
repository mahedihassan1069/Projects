package e.user.madiambu.Remote;




import e.user.madiambu.Model.FCMResponse;
import e.user.madiambu.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAJwI3OcE:APA91bEmj2ntjHFSkzpcgttGOPr2DvCvHR0dIkrnQyvVdJ6rYRa51BJhYSXxaPcRLn4QB-Z0VxOasUzx7_XOnbqi1lK3-hDwUbARWNs2zk1-K_EJKB8hQd5tthYn7P55xEPqlsGW-lCR"
    })

    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
