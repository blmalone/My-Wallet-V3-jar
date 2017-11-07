package info.blockchain.wallet.api.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletOptions {

    @JsonProperty("showBuySellTab")
    private List<String> buySellCountries = new ArrayList<>();

    @JsonProperty("partners")
    private Partners partners;

    @JsonProperty("androidBuyPercent")
    private double rolloutPercentage;

    @JsonProperty("android")
    private Map<String, Boolean> androidFlags = new HashMap<>();

    //Temp for hard fork
    @JsonProperty("mobile_notice")
    private Map<String, String> mobileNotice = new HashMap<>();

    @JsonProperty("shapeshift")
    private ShapeShiftOptions shapeshift;

    @JsonProperty("androidUpgrade")
    private Map<String, Integer> androidUpgrade = new HashMap<>();

    @JsonProperty("mobileInfo")
    private Map<String, String> mobileInfo = new HashMap<>();

    public List<String> getBuySellCountries() {
        return buySellCountries;
    }

    public Partners getPartners() {
        return partners;
    }

    public double getRolloutPercentage() {
        return rolloutPercentage;
    }

    public Map<String, Boolean> getAndroidFlags() {
        return androidFlags;
    }

    public Map<String, String> getMobileNotice() {
        return mobileNotice;
    }

    public ShapeShiftOptions getShapeshift() {
        return shapeshift;
    }

    public Map<String, Integer> getAndroidUpgrade() {
        return androidUpgrade;
    }

    public Map<String, String> getMobileInfo() {
        return mobileInfo;
    }
}
