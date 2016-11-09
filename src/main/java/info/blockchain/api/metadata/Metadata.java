package info.blockchain.api.metadata;

import info.blockchain.api.metadata.request.MessagePostRequest;
import info.blockchain.api.metadata.response.AuthNonceResponse;
import info.blockchain.api.metadata.response.AuthTokenResponse;
import info.blockchain.api.metadata.response.AuthTrustedResponse;
import info.blockchain.api.metadata.response.MessagePostResponse;
import info.blockchain.api.metadata.response.StatusResponse;
import info.blockchain.api.metadata.response.TrustedPutResponse;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.spongycastle.util.encoders.Base64;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Potentially move this to Android
 */
public class Metadata {

    MetadataService mds;

    public Metadata() {

        //Setup retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MetadataService.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mds = retrofit.create(MetadataService.class);
    }

    /**
     * Get nonce generated by the server (auth challenge).
     * @return
     * @throws Exception
     */
    public String getNonce() throws Exception {

        Call<AuthNonceResponse> response = mds.getNonce();

        Response<AuthNonceResponse> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body().getNonce();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Get JSON Web Token if signed nonce is correct. Signed.
     *
     * @param key
     * @return
     * @throws Exception
     */
    public String getToken(ECKey key) throws Exception {

        String mdid = key.toAddress(MainNetParams.get()).toString();
        String nonce = getNonce();
        String sig = key.signMessage(nonce);

//        System.out.println("mdid: "+mdid);
//        System.out.println("sig: "+sig);
//        System.out.println("nonce: "+nonce);
//        System.out.println("curl -X POST http://localhost:8080/auth -H \"Content-Type: application/json\" -d '{\"mdid\": \""+mdid+"\", \"signature\":\""+sig+"\",\"nonce\":\""+nonce+"\"}'\n");

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("mdid", mdid);
        map.put("signature", sig);
        map.put("nonce", nonce);
        Call<AuthTokenResponse> response = mds.getToken(map);

        Response<AuthTokenResponse> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body().getToken();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Get list of all trusted MDIDs. Authenticated.
     *
     * @param token
     * @return
     * @throws Exception
     */
    public String getTrustedList(String token) throws Exception {

        Call<AuthTrustedResponse> response = mds.getTrustedList("Bearer " + token);
//        System.out.println("curl -X GET http://localhost:8080/trusted -H \"Authorization: Bearer "+token+"\"");
        Response<AuthTrustedResponse> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body().getMdid();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Check if a contact is on trusted list of mdid. Authenticated.
     *
     * @param token
     * @param mdid
     * @return
     * @throws Exception
     */
    public String getTrusted(String token, String mdid) throws Exception {

        Call<AuthTrustedResponse> response = mds.getTrusted("Bearer " + token, mdid);

        Response<AuthTrustedResponse> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body().getMdid();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Add a contact to trusted list of mdid. Authenticated.
     *
     * @param token
     * @param mdid
     * @return
     * @throws Exception
     */
    public String putTrusted(String token, String mdid) throws Exception {

        Call<TrustedPutResponse> response = mds.putTrusted("Bearer " + token, mdid);
//        System.out.println("curl -X PUT http://localhost:8080/trusted/"+mdid+" -H \"Authorization: Bearer "+token+"\"");
        Response<TrustedPutResponse> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body().getContact();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Delete a contact from trusted list of mdid. Authenticated.
     *
     * @param token
     * @param mdid
     * @return
     * @throws Exception
     */
    public boolean deleteTrusted(String token, String mdid) throws Exception {

        Call<StatusResponse> response = mds.deleteTrusted("Bearer " + token, mdid);
//        System.out.println("curl -X DELETE http://localhost:8080/trusted/"+mdid+" -H \"Authorization: Bearer "+token+"\"");
        Response<StatusResponse> exe = response.execute();

        if (exe.isSuccessful()) {
            return true;
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Add new shared metadata entry. Signed. Authenticated.
     *
     * @param token
     * @param key
     * @param recipientMdid
     * @param message
     * @param type
     * @return
     * @throws Exception
     */
    public String postMessage(String token, ECKey key, String recipientMdid, String message, int type) throws Exception {

        String b64Msg = new String(Base64.encode(message.getBytes()));

        String signature = key.signMessage(b64Msg);

        MessagePostRequest request = new MessagePostRequest();
        request.setRecipient(recipientMdid);
        request.setType(type);
        request.setPayload(b64Msg);
        request.setSignature(signature);

        Call<MessagePostResponse> response = mds.postMessage("Bearer " + token, request);
        Response<MessagePostResponse> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body().toString();
        } else {
            throw new Exception(exe.message());
        }
    }

    /**
     * Get messages sent to my MDID. Authenticated.
     *
     * @param token
     * @param onlyProcessed
     * @return
     * @throws Exception
     */
    public String getMessage(String token, boolean onlyProcessed) throws Exception {

        Call<List<MessagePostResponse>> response = mds.getMessages("Bearer " + token, onlyProcessed);

        System.out.println(response.request().url());

//        System.out.println("curl -X GET http://localhost:8080/messages?new=true -H \"Authorization: Bearer "+token+"\"");

        Response<List<MessagePostResponse>> exe = response.execute();

        if (exe.isSuccessful()) {
            return exe.body().toString();
        } else {
            throw new Exception(exe.message());
        }
    }
}