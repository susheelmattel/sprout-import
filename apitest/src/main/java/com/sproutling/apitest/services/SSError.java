package com.sproutling.apitest.services;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bradylin on 11/29/16.
 */

public class SSError {

    /// Request timed out.
    public static final int InternalServerError = 1000;
    /// Server cannont fulfill the request.
    public static final int NotImplemented = 1001;
    /// Request timeout trying to reach the service.
    public static final int BadGateway = 1002;
    /// Service you're trying to reach is unavailable.
    public static final int ServiceUnavailable = 1003;
    /// Service timeout before request could finish.
    public static final int GatewayTimeout = 1004;
    /// It is not confirmed.
    public static final int Confirmation = 5000;
    /// Need to be checked.
    public static final int Accepted = 5001;
    /// Cannot be blank.
    public static final int Blank = 5002;
    /// Cannot be set.
    public static final int Present = 5003;
    /// Minimum characters requiered.
    public static final int TooShort = 5004;
    /// Maximum characters exceed.
    public static final int TooLong = 5005;
    /// It is not unique.
    public static final int Taken = 5006;
    /// It is the not correct format.
    public static final int Invalid = 5007;
    /// Must be one of the set.
    public static final int Inclusion = 5008;
    /// Must not be one of the set.
    public static final int Exclusion = 5009;
    /// Is is not a number.
    public static final int NotANumber = 5010;
    /// Must be greater.
    public static final int GreaterThan = 5011;
    /// Must be greater than or equal to.
    public static final int GreaterThanOrEqualTo = 5012;
    /// Must be equal to.
    public static final int EqualTo = 5013;
    /// Must be less than.
    public static final int LessThan = 5014;
    /// Must be less than or equal to.
    public static final int LessThanOrEqualTo = 5015;
    /// Must be an integer.
    public static final int NotAnInteger = 5016;
    /// Must be an odd number.
    public static final int Odd = 5017;
    /// Must be an even number.
    public static final int Even = 5018;
    /// The request is missing a required parameter includes an unsupported parameter value or is otherwise malformed.
    public static final int Unauthorized = 5019;
    /// The resource you are looking for could not be found.
    public static final int NotFound = 5021;
    /// You do not have access to view that resource.
    public static final int Forbidden = 5022;
    /// The request is missing a required parameter, includes an unsupported parameter value, or is otherwise malformed.
    public static final int InvalidRequest = 5023;
    /// The redirect uri included is not valid.
    public static final int InvalidRedirect_uri = 5024;
    /// The client is not authorized to perform this request using this method.
    public static final int UnauthorizedClient = 5025;
    /// The resource owner or authorization server denied the request.
    public static final int AccessDenied = 5026;
    /// The requested scope is invalid, unknown, or malformed.
    public static final int InvalidDcope = 5027;
    /// The authorization server encountered an unexpected condition which prevented it from fulfilling the request.
    public static final int ServerError = 5028;
    /// The authorization server is currently unable to handle the request due to a temporary overloading or maintenance of the server.
    public static final int TemporarilyUnavailable = 5029;
    /// Resource Owner Password Credentials flow failed due to Doorkeeper.configure.resource_owner_from_credentials being unconfigured.
    public static final int CredentialFlowNotConfigured = 5030;
    /// Resource Owner find failed due to Doorkeeper.configure.resource_owner_authenticator being unconfiged.
    public static final int ResourceOwnerAuthenticatorNotConfigured = 5031;
    /// The authorization server does not support this response type.
    public static final int UnsupportedResponseType = 5032;
    /// Client authentication failed due to unknown client, no client authentication included, or unsupported authentication method.
    public static final int InvalidClient = 5033;
    /// The provided authorization grant is invalid, expired, revoked, does not match the redirection URI used in the authorization request, or was issued to another client.
    public static final int InvalidGrant = 5034;
    /// The authorization grant type is not supported by the authorization server.
    public static final int UnsupportedGrantType = 5035;
    /// The provided resource owner credentials are not valid, or resource owner cannot be found.
    public static final int InvalidResourceOwner = 5036;
    /// The HTTP method you sent was not accepted by the server.
    public static final int MethodNotAllowed = 5037;
    /// The account was already confirmed, please try signing in.
    public static final int AlreadyConfirmed = 5038;
    /// The account is not locked.
    public static final int NotLocked = 5039;
    /// It is not a valid subclass please check that your value is within the known types.
    public static final int Subclass = 5040;
    /// Request could not be completed yet.
    public static final int NotReady = 5041;
    /// Parsing error.
    public static final int ParsingError = 6000;
    /// Missing one or more items expected in response.
    public static final int MissingResponseItems = 6001;
    /// Invalid date string format.
    public static final int InvaildDateString = 6002;
    /// Invalid enum type.
    public static final int InvalidEnumType = 6003;

    public int code;
    public String reason;
    public String message;
    public String path;

    public SSError() {}

    public SSError(JSONObject jsonObject) throws JSONException {
        JSONObject error;
        if (hasErrors(jsonObject)) {
            error = jsonObject.getJSONArray("errors").getJSONObject(0);
            code = error.getInt("logref");
            reason = error.optString("reason");
            message = error.getString("message");
            path = error.getString("path");
        } else {
            message = jsonObject.getString("error");
        }
    }

    public SSError(int code, String reason, String message, String path) {
        this.code = code;
        this.reason = reason;
        this.message = message;
        this.path = path;
    }

    public static boolean hasErrors(JSONObject jsonObject) {
        return jsonObject.toString().contains("errors");
    }

    public static boolean isError(JSONObject jsonObject) {
        return jsonObject.toString().contains("errors") || jsonObject.toString().contains("error");
    }

    @Override
    public String toString() {
        return "[code=" + code + ", reason=" + reason + "]: " + message;
    }
}
