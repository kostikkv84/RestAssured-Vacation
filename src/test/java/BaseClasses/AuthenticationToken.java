package BaseClasses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationToken {
    private String access_token;
    private Integer expires_in;
    private Integer refresh_expires_in;
    private String refresh_token;
    private String token_type;
    @JsonProperty("not-before-policy")
    public Integer not_before_policy;
    private String session_state;
    private String scope;

    public AuthenticationToken() {
        super();
    }

    public AuthenticationToken(String access_token, Integer expires_in, Integer refresh_expires_in, String refresh_token, String token_type, Integer not_before_policy, String session_state, String scope) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.refresh_expires_in = refresh_expires_in;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
        this.not_before_policy = not_before_policy;
        this.session_state = session_state;
        this.scope = scope;
    }
}
