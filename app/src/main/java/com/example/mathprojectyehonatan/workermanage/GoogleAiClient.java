package com.example.mathprojectyehonatan.workermanage;




import android.graphics.Bitmap;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;

public class GoogleAiClient {

    public interface ResponseCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    // הופך את הפונקציה לסטטית כדי שיהיה אפשר לקרוא לה מכל מקום בלי ליצור Object
    public static void extractIdStatic(String apiKey, Bitmap idImage, ResponseCallback callback) {

        GenerativeModel gm =
                new GenerativeModel("gemini-pro", apiKey);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addImage(idImage)
                .addText("Extract only the 9-digit ID number from this ID card. Return only the digits.")
                .build();

        Executor mainExecutor = new android.os.Handler(android.os.Looper.getMainLooper())::post;
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (result.getText() != null) {
                    callback.onSuccess(result.getText().trim());
                } else {
                    callback.onError("לא נמצא טקסט");
                }
            }

            @Override
            public void onFailure(Throwable t) {

                t.printStackTrace();

                callback.onError(t.toString());
            }
        }, mainExecutor);
    }
}